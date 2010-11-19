package crybaby.parser;

import java.util.*;
import javax.xml.xpath.*;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.w3c.dom.*;

public class Webscraper {
	private Document page; 
	public Webscraper(String uri) throws Exception {
		page = new HtmlDocumentBuilder().parse(uri);
	}
	
	public void filterPage() {
		// The following is a list of tags to remove
		removeList(page.getElementsByTagName("head"));
		removeList(page.getElementsByTagName("script"));
		removeList(page.getElementsByTagName("style"));
		removeList(page.getElementsByTagName("object"));
	}
	
	private void removeList(NodeList list) {
		System.out.println(list.getLength());
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			n.getParentNode().removeChild(n);
		}
	}
	
	public String dumpText() {
		Element body = (Element)page.getElementsByTagName("body").item(0);
		return body.getTextContent();
	}
	
	public List<String> getClasses() throws Exception {
		Set<String> classes = new HashSet<String>();
		XPath query = XPathFactory.newInstance().newXPath();
		NodeList list = (NodeList)query.evaluate("//@class", page, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			for (String s : list.item(i).getNodeValue().split(" "))
				classes.add("." + s);
		}
		return new LinkedList<String>(classes);
	}
	public List<String> getIDs() throws Exception {
		Set<String> classes = new HashSet<String>();
		XPath query = XPathFactory.newInstance().newXPath();
		NodeList list = (NodeList)query.evaluate("//@id", page, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			for (String s : list.item(i).getNodeValue().split(" "))
				classes.add("#" + s);
		}
		return new LinkedList<String>(classes);
	}

	public static void main(String[] args) throws Exception{
		Webscraper scraper = new Webscraper(args[0]);
		String before = scraper.dumpText();
		scraper.filterPage();
		String after = scraper.dumpText();
		System.out.println(before.equals(after));
		System.out.println(scraper.dumpText());
	}

}
