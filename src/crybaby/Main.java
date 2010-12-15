package crybaby;

import crybaby.jargon.JargonExtractor;
import crybaby.jargon.NPWordBag;
import crybaby.parser.CommentExtractor;
import crybaby.parser.ParseSentence;
import crybaby.parser.Webscraper;
import crybaby.parser.Websearcher;
import crybaby.summarize.CommentSummarizer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
	
	private final static int NUM_THREADS = 5;
	
	private static Queue<String> pages, results;
	private static List<String> jargon = Collections.synchronizedList(new LinkedList<String>());
	private static BlockingQueue<ParseSentence> sentences = new LinkedBlockingQueue<ParseSentence>();
	
	private static volatile boolean finished = false;
	private static class Scraper implements Runnable {
		public void run() {
			String runner;
			while ((runner = pages.poll()) != null) {
				try {
					Webscraper scraper = new Webscraper(runner);
					scraper.filterPage();
					CommentExtractor extractor = new CommentExtractor(scraper);
					for (ParseSentence p : extractor.getSentences()) {
						sentences.put(p);
					}
					List<String> sentences = extractor.getText();
					System.err.println("Found " + sentences.size() + " sentences for "
						+ runner);
					results.addAll(sentences);
				} catch (Throwable e) {
					System.err.println("Error loading page " + runner);
					e.printStackTrace(System.err);
				}
			}
			// We couldn't get anything, so we must be done
			System.out.println("Scraper finished!");
			finished = true;
		}
	}

	private static class JargonThread implements Runnable {
		public void run() {
			JargonExtractor extractor = new JargonExtractor();
			System.out.println(sentences.size() + " sentences to look at.");
			while (!sentences.isEmpty()) {
				try {
					ParseSentence p = sentences.poll(1, TimeUnit.SECONDS);
					if (p == null)
						continue;
					extractor.addParseSentence(p);
				} catch (InterruptedException e) {} 
			}
			System.out.println("Finding jargon terms...");
			extractor.removeSimilarity();
			for (NPWordBag bag : extractor.getWordBags()) {
				if (bag.getSimilars() < 5)
					break;
				jargon.add(bag.getNounPhrase());
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
			} catch (Throwable e) {
				System.err.println("Error searching for " + query + ":");
				e.printStackTrace(System.err);
			}
		}
		System.out.println("Found " + resultPages.size() + " reviews.");
		
		// Grab all text
		System.out.println("Parsing reviews...");
		pages = new ConcurrentLinkedQueue<String>(resultPages);
		results = new ConcurrentLinkedQueue<String>();
		/*ThreadGroup group = new ThreadGroup("Parsers");
		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new Thread(group, new Scraper());
			threads[i].start();
		}*/
		new Scraper().run();
		/*Thread jargonThread = new Thread(new JargonThread());
		jargonThread.start();
		jargonThread.join();*/
		new JargonThread().run();
		
		// Wait for other threads to finish...
		// Maybe we'll clean up a lot of memory!
		/*for (int i = 0; i < NUM_THREADS; i++)
			threads[i].join();*/
		
		// Output review comments
		System.out.println("Summarizing results...");
		List<String> strings = new LinkedList<String>();
		for (String s : results) {
			boolean good = false;
			for (String phrase : jargon)
				if (s.contains(phrase)) {
					good = true;
					break;
				}
			if (good)
				strings.add(s);
		}
		System.out.println("Output strings: " + strings);
		System.out.println("Jargon phrases: " + jargon);
		CommentSummarizer summarizer = new CommentSummarizer(strings);
		List<String> finalResults = summarizer.summarize();
		System.out.println("Final summary strings:");
		for (String s : finalResults)
			System.out.println(s);
	}
}
