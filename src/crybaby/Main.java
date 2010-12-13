package crybaby;

import crybaby.parser.CommentExtractor;
import crybaby.parser.Webscraper;
import crybaby.parser.Websearcher;
import crybaby.summarize.CommentSummarizer;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The main class for crybaby
 */
public final class Main {
	
	/**
	 * The URI of the engine to use to search for results.
	 */
	private final static String SEARCH_ENGINE = "http://www.bing.com/search?q=";
	
	/**
	 * The query strings to search with.
	 * 
	 * Note that %s will be replaced with the product name.
	 */
	private final static String[] queries =
		new String[] {"%s reviews"};
	
	private static Queue<String> pages, results;
	private static class Scraper implements Runnable {
		public void run() {
			String runner;
			while ((runner = pages.poll()) != null) {
				try {
					Webscraper scraper = new Webscraper(runner);
					scraper.filterPage();
					CommentExtractor extractor = new CommentExtractor(scraper);
					results.addAll(extractor.getText());
				} catch (Exception e) {
					System.err.println("Error loading page " + runner);
					e.printStackTrace(System.err);
				}
			}
		}
	}
	public static void main(String[] args) throws InterruptedException {
		if (args.length < 1) {
			System.err.println("Usage: java crybaby.Main <product to search for>");
			System.exit(1);
		}
		
		// Compose the arguments into a string, in case the user forgot to quote
		String product = args[0];
		for (int i = 1; i < args.length; i++)
			product += " " + args[i];
		
		// Execute the web search for these terms
		System.out.println("Collecting reviews...");
		Websearcher searcher = new Websearcher(SEARCH_ENGINE);
		List<String> resultPages = new LinkedList<String>();
		for (String query : queries) {
			try {
				query = query.replace("%s", product);
				resultPages.addAll(searcher.searchResults(query, 100));
			} catch (Exception e) {
				System.err.println("Error searching for " + query + ":");
				e.printStackTrace(System.err);
			}
		}
		System.out.println("Found " + resultPages.size() + " reviews.");
		
		// Grab all text
		System.out.println("Parsing reviews...");
		pages = new ConcurrentLinkedQueue<String>(resultPages);
		results = new ConcurrentLinkedQueue<String>();
		ThreadGroup group = new ThreadGroup("Parsers");
		for (int i = 0; i < 5; i++)
			new Thread(group, new Scraper()).start();
		while (!pages.isEmpty()) {
			Thread.sleep(1000);
		}
		
		// Output review comments
		System.out.println("Summarizing results...");
		CommentSummarizer summarizer = new CommentSummarizer(new LinkedList<String>(results));
		List<String> finalResults = summarizer.summarize();
		System.out.println("Final summary strings:");
		for (String s : finalResults)
			System.out.println(s);
	}
}
