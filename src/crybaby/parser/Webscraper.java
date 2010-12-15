package crybaby.parser;

/*import java.io.FileReader;
import java.lang.reflect.*;
import java.nio.ByteBuffer;*/
import java.util.*;
import java.util.regex.*;
//import javax.script.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
//import sun.org.mozilla.javascript.ScriptableObject;

public class Webscraper {
	private String uri;
	private Document page;
	private XPath xpath;
	public Webscraper(String uri) throws Exception {
		page = new HtmlDocumentBuilder().parse(uri);
		xpath = XPathFactory.newInstance().newXPath();
		this.uri = uri;
	}
	
	// TODO: try using the code from http://lab.arc90.com/experiments/readability/js/readability.js
	/**
	 * Magic classes to hook the DOM into Rhino.
	 * Do not try to understand this.
	 */
/*	@SuppressWarnings("serial")
	private static class ScriptableNode extends ScriptableObject implements InvocationHandler {
		private Object self;
		static ClassLoader dcl = new DynamicClassLoader();
		
		public ScriptableNode(Object repr) {
			this.self = repr;
			Class<?> clazz = repr.getClass();
			try {
				while (clazz != Object.class) {
					for (Class<?> iface : clazz.getInterfaces())
						addClassMethods(iface);
					clazz = clazz.getSuperclass();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		private void addClassMethods(Class<?> clazz) throws Exception {
			if (!clazz.getPackage().getName().equals("org.w3c.dom"))
				return;
			Class<?> counterpart = dcl.loadClass("Glue" + clazz.getSimpleName());
			for (Method m : clazz.getMethods()) {
				if (!m.getDeclaringClass().getName().startsWith("org.w3c.dom"))
					continue;
				Class<?> params[] = m.getParameterTypes();
				for (int i = 0; i < params.length; i++) {
					if (!params[i].isPrimitive() && params[i] != String.class)
						params[i] = ScriptableObject.class;
				}
				m = counterpart.getMethod(m.getName(), params);
				if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
					String varName = m.getName().substring(3);
					Method setter = null;
					try {
						setter = clazz.getMethod("set" + varName, m.getParameterTypes());
					} catch (NoSuchMethodException e) {
						// ignore
					}
					defineProperty(varName.substring(0, 1).toLowerCase() + varName.substring(1),
						null, m, setter, ScriptableObject.PERMANENT);
				} else if (m.getName().startsWith("set")) {
					// Do nothing--we did this with the getter
				} else {
					defineFunctionProperties(new String[] { m.getName() },
						m.getDeclaringClass(), ScriptableObject.PERMANENT);
				}
			}
			defineFunctionProperties(new String[] {"toString", "equals"}, Object.class, ScriptableObject.PERMANENT);
		}

		@Override
		public String getClassName() {
			return "You suck";
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			// Call the method on self instead of this.
			Class<?> srcClass = method.getDeclaringClass();
			Object ret = null;
			if (srcClass.getClassLoader() == dcl) {
				Method invokee = null;
				Class<?> realClass = Class.forName("org.w3c.dom." +
					srcClass.getName().substring(4));
				for (Method m2 : realClass.getMethods()) {
					if (m2.getName().equals(method.getName())) {
						assert method.getDeclaringClass().getClassLoader() == dcl;
						invokee = m2;
					}
				}
				ret = invokee.invoke(self, args);
			} else if (!srcClass.getName().startsWith("org.w3c.dom.")) {
				System.out.println("Invoking " + method + " with " + Arrays.toString(args));
				ret = this.getClass().getMethod(method.getName(), method.getParameterTypes()).invoke(this, args);
			} else {
				ret = method.invoke(self, args);
			}
			if (ret != null && method.getReturnType() == ScriptableObject.class)
				ret = new ScriptableNode(ret);
			System.out.println(ret == null ? "null" : ret.getClass());
			return ret;
		}
	}
	
	private static class DynamicClassLoader extends ClassLoader {
		public Class<?> findClass(String name) throws ClassNotFoundException {
			if (name.startsWith("Glue")) {
				Class<?> fake = Class.forName("org.w3c.dom." + name.substring(4));
				byte[] buf = constructClass(fake);
				return defineClass(name, buf, 0, buf.length);
			}
			throw new ClassNotFoundException("Can not construct class" + name);
		}
		private static byte[] constructClass(Class<?> iface) {
			ByteBuffer buffer = ByteBuffer.allocate(10000);
			buffer.putInt(0xCAFEBABE); // magic
			buffer.putShort((short) 0).putShort((short)49); // class version number
			// Constant pool management time!
			// Basic rule of thumb:
			// 1,2: <iface name> (ascii, classref)
			// 3,4: java/lang/Object (ascii, classref)
			// 5,6: sun/org/mozilla/javascript/Scriptable or superinterface (ascii, classref)
			// 7,9,11,etc.: method name
			// 8,10,12,etc.: descriptor
			Method[] methods = iface.getDeclaredMethods();
			buffer.putShort((short)(7 + 2 * methods.length));
			buffer.put(ascii2class("Glue" + iface.getSimpleName() + "")).put(new byte[] {7,0,1});
			buffer.put(ascii2class("java/lang/Object")).put(new byte[] {7,0,3});
			if (iface.getInterfaces().length == 0)
				buffer.put(ascii2class("sun/org/mozilla/javascript/Scriptable"));
			else
				buffer.put(ascii2class("Glue" + iface.getInterfaces()[0].getSimpleName()));
			buffer.put(new byte[] {7,0,5});
			
			for (Method m : methods) {
				buffer.put(ascii2class(m.getName()));
				// Construct the String for the descriptor
				StringBuilder sb = new StringBuilder();
				sb.append('(');
				for (Class<?> c : m.getParameterTypes()) {
					sb.append(type2string(c));
				}
				sb.append(')').append(type2string(m.getReturnType()));
				buffer.put(ascii2class(sb.toString()));
			}
			buffer.putShort((short)0x1601); // Flags
			buffer.putShort((short)0x0002).putShort((short)0x0004); // This, super
			buffer.putShort((short)0x0001).putShort((short)0x0006); // superinterface
			buffer.putShort((short)0x0000); // No fields
			buffer.putShort((short)methods.length); // Methods
			for (int i = 0; i < methods.length; i++) {
				buffer.putShort((short)0x1401); // Flags
				buffer.putShort((short)(i * 2 + 7)).putShort((short)(i * 2 + 8)); // name, descriptor
				buffer.putShort((short)0x0000); // No attributes
			}
			buffer.putShort((short)0x0000); // No attributes
			buffer.limit(buffer.position());
			buffer.rewind();
			
			byte[] data = new byte[buffer.limit()];
			buffer.get(data);
			return data;
		}

		private static String type2string(Class<?> clazz) {
			if (clazz.equals(int.class))
				return "I";
			else if (clazz.equals(boolean.class))
				return "Z";
			else if (clazz.equals(void.class))
				return "V";
			else if (clazz.equals(String.class))
				return "Ljava/lang/String;";
			return "Lsun/org/mozilla/javascript/ScriptableObject;";
		}

		private static byte[] ascii2class(String ascii) {
			int len = ascii.length();
			byte[] b = String.format("\u0001..%s", ascii).getBytes();
			b[1] = (byte)(len >> 8);
			b[2] = (byte)(len & 0xff);
			return b;
		}
	}
	public void filterWithReadability() {
		ScriptEngine engine = new ScriptEngineManager().getEngineByMimeType("text/javascript");
		try {
			Object d = Proxy.newProxyInstance(ScriptableNode.dcl,
				new Class<?>[] { Class.forName("GlueDocument", true, ScriptableNode.dcl) },
				new ScriptableNode(page));
			System.out.println(Arrays.toString(d.getClass().getInterfaces()));
			//engine.put("document", d);
			engine.put("document", page);
			engine.put("_href", uri);
			engine.eval(new FileReader("readability.js"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}*/
	
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
		//scraper.filterPage();
		//scraper.filterWithReadability();
		System.out.println(scraper.dumpText());
	}
}
