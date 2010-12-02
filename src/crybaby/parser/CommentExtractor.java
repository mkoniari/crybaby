package crybaby.parser;

import java.util.*;

import crybaby.summarize.CommentSummarizer;

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
	
	public List<String> getText() {
		List<String> ret = new ArrayList<String>(sentences.size());
		for (ParseSentence sentence : sentences)
			ret.add(sentence.getSentence());
		return ret;
	}
	public static void main(String[] args) throws Exception {
		Webscraper scraper = new Webscraper(args[0]);
		scraper.filterPage();
		CommentExtractor extractor = new CommentExtractor(scraper);
		CommentSummarizer summary = new CommentSummarizer(extractor.getText());
		List<String> results = summary.summarize();
		System.out.println("Top results:");
		for (String s : results) {
			System.out.println(s);
		}
	}
}
