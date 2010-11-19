package crybaby.parser;

import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.trees.Tree;

public class ParseSentence {
	private String sentence;
	private Tree parseTree;

	/**
	 * Just pass in the sentence that you want parsed.  Then use getSentence() and getParseTree() to your heart's content.
	 * @param sentence
	 */
	public ParseSentence(String sentence) {
		super();
		this.sentence = sentence;
		List<? extends HasWord> sentenceTokens = Parser.tf.getTokenizer(new StringReader(sentence)).tokenize(); 
		Parser.parser.parse(sentenceTokens); // parse the tokens
		this.parseTree = Parser.parser.getBestParse();
	}

	public String getSentence() {
		return sentence;
	}

	public Tree getParseTree() {
		return parseTree;
	}
	
	
}
