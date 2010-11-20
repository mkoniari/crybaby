package crybaby.parser;

import java.util.*;

import edu.stanford.nlp.ling.*;

public class CommentExtractor {
	private List<ParseSentence> sentences;
	
	public CommentExtractor(Webscraper webpage) {
		List<String> bag = webpage.getSentences();
		sentences = new LinkedList<ParseSentence>();
		for (String sentence : bag) {
			try {
				ParseSentence p = new ParseSentence(sentence);
				for (Label l : p.getParseTree().labels()) {
					if (l instanceof HasTag && "VP".equals(l.value())) {
						sentences.add(p);
						break;
					}
				}
			} catch (Exception e) {
				// Ignore this sentence.
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Webscraper scraper = new Webscraper(args[0]);
		scraper.filterPage();
		CommentExtractor extractor = new CommentExtractor(scraper);
		System.out.println(extractor.sentences);
	}
}
