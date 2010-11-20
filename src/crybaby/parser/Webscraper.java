package crybaby.parser;

import java.util.*;
import javax.xml.xpath.*;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.w3c.dom.*;

public class Webscraper {
	private Document page;
	private XPath xpath;
	public Webscraper(String uri) throws Exception {
		page = new HtmlDocumentBuilder().parse(uri);
		xpath = XPathFactory.newInstance().newXPath();
	}
	
	public void filterPage() {
		// The following is a list of tags to remove
		removeList(page.getElementsByTagName("head"));
		removeList(page.getElementsByTagName("script"));
		removeList(page.getElementsByTagName("style"));
		removeList(page.getElementsByTagName("object"));
		removeList(page.getElementsByTagName("form"));
		
		// Guess for comment nodes?
		List<Node> comments = new LinkedList<Node>();
		try {
			Node content = page.getElementById("content");
			if (content != null)
				comments.add(content);
			addAll(comments, (NodeList)xpath.evaluate(
				"//*[contains(@class, \"comment\")]", page, XPathConstants.NODESET));
			addAll(comments, (NodeList)xpath.evaluate(
				"//*[contains(@class, \"review\")]", page, XPathConstants.NODESET));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (!comments.isEmpty()) {
			// Limit the page to just these nodes
			Node n, body = page.getElementsByTagName("body").item(0);
			while ((n = body.getFirstChild()) != null) {
				body.removeChild(n);
			}
			for (Node n2 : comments) {
				body.appendChild(n2);
			}
		}
	}
	
	private static void addAll(List<Node> nodes, NodeList toAdd) {
		for (int i = 0; i < toAdd.getLength(); i++)
			nodes.add(toAdd.item(i));
	}
	
	private void removeList(NodeList list) {
		for (int i = list.getLength() - 1; i >= 0; i--) {
			Node n = list.item(i);
			Node parent = n.getParentNode();
			parent.removeChild(n);
		}
	}
	
	public String dumpText() {
		if (this != null) {
			Element body = (Element)page.getElementsByTagName("body").item(0);
			return body.getTextContent();
		} else {
			StringBuilder output = new StringBuilder();
			serialize(page, output);
			return output.toString();
		}
	}
	
	private static void serialize(Node n, StringBuilder output) {
		if (n instanceof Element) {
			output.append("<" + n.getNodeName() + ">");
			NodeList children = n.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				serialize(children.item(i), output);
			}
			output.append("</" + n.getNodeName() + ">");
		} else if (n instanceof Text) {
			output.append(n.getNodeValue());
		} else {
			NodeList children = n.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				serialize(children.item(i), output);
			}
		}
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
		scraper.filterPage();
		System.out.println(scraper.dumpText());
	}
}
/**
javascript:(function(){
  readConvertLinksToFootnotes=false;readStyle='style-newspaper';readSize='size-medium';readMargin='margin-wide';_readability_script=document.createElement('script');_readability_script.type='text/javascript';_readability_script.src='http://lab.arc90.com/experiments/readability/js/readability.js?x='+(Math.random());document.documentElement.appendChild(_readability_script);_readability_css=document.createElement('link');_readability_css.rel='stylesheet';_readability_css.href='http://lab.arc90.com/experiments/readability/css/readability.css';_readability_css.type='text/css';_readability_css.media='all';document.documentElement.appendChild(_readability_css);_readability_print_css=document.createElement('link');_readability_print_css.rel='stylesheet';_readability_print_css.href='http://lab.arc90.com/experiments/readability/css/readability-print.css';_readability_print_css.media='print';_readability_print_css.type='text/css';document.getElementsByTagName('head')[0].appendChild(_readability_print_css);})();
*/