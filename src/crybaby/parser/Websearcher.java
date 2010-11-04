package crybaby.parser;

import java.util.*;
import javax.xml.xpath.*;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.w3c.dom.*;

public class Websearcher {
	static {
		// We're a web browser! We swear!
		System.setProperty("http.agent",
			"Mozilla/5.0 (X11; Linux i686; rv:2.0b6pre) Gecko/20100101 Firefox/4.0b6pre");
	}
	private String engine;
	public Websearcher(String baseURI) {
		engine = baseURI;
	}
	
	public List<String> searchResults(String query, int num) throws Exception {
		String base = engine + query.replaceAll(" ", "+");
		XPath xpath = XPathFactory.newInstance().newXPath();
		List<String> results = new LinkedList<String>();
		buildResultsInternal(base, results, xpath);
		while (results.size() < num) {
			try {
				buildResultsInternal(base + "&first=" + results.size(), results, xpath);
			} catch (Exception e) {
				e.printStackTrace(System.err);
				break;
			}
		}
		return results;
	}
	
	private void buildResultsInternal(String uri, List<String> pages, XPath xpath) throws Exception {
		Document page = new HtmlDocumentBuilder().parse(uri);
		// I hate XPATH
		//NodeList list = (NodeList)xpath.evaluate("//*/*[@class=\"l\"]", page, XPathConstants.NODESET);
		NodeList list = (NodeList)xpath.evaluate("//*[@class=\"sb_tlst\"]/*/*", page, XPathConstants.NODESET);
		//NodeList list = page.getElementsByTagName("a");		
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element)list.item(i);
			pages.add(e.getAttribute("href"));
		}
	}
	
	public static void main(String... args) throws Exception {
		// So... bing likes bots better
		Websearcher google = new Websearcher("http://www.bing.com/search?q=");
		//Websearcher google = new Websearcher("http://www.google.com/search?hl=en&num=100&q=");
		List<String> pages = google.searchResults(args[0], 100);
		Set<String> allClasses = new HashSet<String>();
		Set<String> allIDs = new HashSet<String>();
		for (String page : pages) {
			try {
				System.out.println("From page " + page);
				Webscraper lists = new Webscraper(page);
				List<String> classes = lists.getClasses();
				allClasses.addAll(classes);
				System.out.println("Classes: " + classes);
				List<String> ids = lists.getIDs();
				allIDs.addAll(ids);
				System.out.println("IDs: " + ids);
			} catch (Exception e) {
				System.out.println("Failed: " + e);
			}
		}
		System.out.println("All classes: " + allClasses);
		System.out.println("All IDs:" + allIDs);
	}
}
