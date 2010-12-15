package crybaby;

import crybaby.jargon.JargonExtractor;
import crybaby.jargon.NPWordBag;
import crybaby.parser.CommentExtractor;
import crybaby.parser.ParseSentence;
import crybaby.parser.Webscraper;
import crybaby.parser.Websearcher;
import crybaby.summarize.CommentSummarizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The main class for crybaby (part 2)
 */
public final class Main2 {
	
	public static void main(String[] args) throws InterruptedException {
		if (args.length != 1 && args.length != 2) {
			System.err.println("Usage: java crybaby.Main " + 
                                           "<review file> <disable parsing>");
			System.exit(1);
		}

                boolean parse = (args.length == 1);

                String text = "";
                String line = "";
                try {
                    BufferedReader br =
                        new BufferedReader(new FileReader(args[0]));
                    while ((line = br.readLine()) != null) {
                        text += line;
                    }
                } catch (IOException e) {
                    System.out.println("Couldn't find file "+args[0]);
                    System.exit(1);
                }

                List<String> sentences = new ArrayList<String>();

                System.out.println("Sentences:");

                for (String sentence: text.split("([.?!])(\\s+|$)|\\s{2,}")) {
                    System.out.println("\"" + sentence + "\"");
                    sentences.add(sentence);
                }
		
                JargonExtractor jargonExtractor = new JargonExtractor();
                List<String> jargon = new ArrayList<String>();
                
                if (parse) {
                    System.out.println("\n\nParsing sentences (" +
                                       sentences.size() + " sentences)");
                    
                    for (String s: sentences) {
                        jargonExtractor.addParseSentence(new ParseSentence(s));
                    }
                    System.out.println("\n\nFinding jargon terms...");
                    jargonExtractor.removeSimilarity();
                    for (NPWordBag bag : jargonExtractor.getWordBags()) {
                        if (bag.getSimilars() < 15)
                            break;
                        jargon.add(bag.getNounPhrase());
                    }
                    
                    System.out.println("\n\nJargon phrases:");
                    for (String s: jargon) {
                        System.out.println("\"" + s + "\"");
                    }
                }

                System.out.println("\n\nSummarizing results...");
                List<String> strings = new LinkedList<String>();
                for (String s : sentences) {
                    if (s.split("\\s+").length >= 5) {
                        boolean good = false;
                        if (parse) {
                            for (String phrase : jargon) {
                                if (s.contains(phrase)) {
                                    good = true;
                                    break;
                                }
                            }
                        }
                        if (good || !parse) {
                            strings.add(s);
                        }
                    }
		}
		CommentSummarizer summarizer = new CommentSummarizer(strings);
		List<String> finalResults = summarizer.summarize();
		System.out.println("Final summary strings:");
		for (String s : finalResults) {
			System.out.println(s);
                }
	}
}
