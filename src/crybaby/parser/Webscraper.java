package crybaby.parser;

import java.util.*;
import java.util.regex.*;
import javax.xml.xpath.*;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.w3c.dom.*;

import javax.script.*;

import sun.org.mozilla.javascript.ScriptableObject;

public class Webscraper {
	private Document page;
	private XPath xpath;
	public Webscraper(String uri) throws Exception {
		page = new HtmlDocumentBuilder().parse(uri);
		xpath = XPathFactory.newInstance().newXPath();
	}
	
	// TODO: try using the code from http://lab.arc90.com/experiments/readability/js/readability.js
	private static class ScriptableNode extends ScriptableObject {
		private Node self;
		
		public ScriptableNode(Node repr) {
			this.self = repr;
			Class<? extends Node> clazz = repr.getClass();
		}

		@Override
		public String getClassName() {
			return "You suck";
		}
	}
	public void filterWithReadability() {
		ScriptEngine engine = new ScriptEngineManager().getEngineByMimeType("text/javascript");
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

	public List<String> getSentences() {
		String text = dumpText();
		Pattern p = Pattern.compile("([.?!])([ \n\r\t]+|$)|[ \n\r\t]{4,}", Pattern.MULTILINE);
	
		List<String> sentences = new ArrayList<String>();
		Matcher m = p.matcher(text);
		int last = 0;
		while (m.find()) {
			String s = text.substring(last, m.start() + 1).trim();
			if (s.length() > 0)
				sentences.add(s);
			last = m.end();
		}
		return sentences;
	}
	public static void main(String[] args) throws Exception{
		Webscraper scraper = new Webscraper(args[0]);
		scraper.filterPage();
		System.out.println(scraper.getSentences());
	}
}
