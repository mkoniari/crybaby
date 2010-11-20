package crybaby.jargon;

import java.io.*;
import java.util.*;

import crybaby.parser.ParseSentence;

import edu.stanford.nlp.trees.Tree;

public class JargonExtractor 
{
	public static void main(String[] args) 
	{
		Map<String, Integer> nounPhrases;
		String[] sentences;
		String paragraph;
		String nounPhrase;
		BufferedReader stopWordBuffer;
		Set<String> stopWords = null;
		NPWordBag newBag;
		String label;
		List<NPWordBag> wordBags;
		ParseSentence ps;
		String stopWord;
		Set<Integer> removed;
		
		stopWordBuffer = null;
		stopWords = new HashSet<String>();
		try {
			stopWordBuffer = new BufferedReader(new FileReader("/Users/ashwin/code/projects/crybaby/stopwords.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			while((stopWord = stopWordBuffer.readLine()) != null)
				stopWords.add(stopWord);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		nounPhrases = new Hashtable<String, Integer>();
		wordBags = new ArrayList<NPWordBag>();
		
		try {
			while((paragraph = br.readLine()) != null)
			{
				if(paragraph.length() < 2)
					continue;
				sentences = paragraph.split("(\\.|\\?|!) ");
				for(int i = 0; i < sentences.length; ++i)
				{
					sentences[i] = sentences[i] + ".";
					System.out.println(sentences[i]);
					ps = new ParseSentence(sentences[i]);
					List<Tree> nodes = ps.getParseTree().preOrderNodeList();

					for(Tree node : nodes)
					{
						if(node.label().value().equals("NP"))
						{
							nounPhrase = "";
							Set<String> bag = new HashSet<String>();
							for(Tree leaf : node.getLeaves())
							{
								label = leaf.label().toString();
								if(label.equals(","))
									
								nounPhrase += label + " ";
								System.out.println(label);
								if(!stopWords.contains(label))
								{
//									System.out.println(label);
									bag.add(label);
									/* special case, we should either have a bunch of these in a 	*
									 * separate file or come up with a general rule 				*/
									if(label.equalsIgnoreCase("wifi") || label.equalsIgnoreCase("wi-fi"))
									{
										bag.add("Wi-Fi");
										bag.add("wi-fi");
										bag.add("wifi");
										bag.add("WiFi");
									}
									if(label.charAt(label.length() - 1) != 's')
									{
										bag.add(label + "s");
										bag.add(label + "es");
									}
									else
									{
										bag.add(label.substring(0, label.length()-1));
									}
								}
							}
							
							nounPhrase = nounPhrase.substring(0, nounPhrase.length() - 1);
							if(stopWords.contains(nounPhrase))
								continue;
							newBag = new NPWordBag(nounPhrase, bag);
							wordBags.add(newBag);
							if(nounPhrases.containsKey(nounPhrase))
								nounPhrases.put(nounPhrase, nounPhrases.get(nounPhrase) + 1);
							else
								nounPhrases.put(nounPhrase, 1);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Word bags generated.");
		
		removed = new HashSet<Integer>();
		
		for(int i = 0; i < wordBags.size(); ++i)
		{
			for(int j = i+1; j < wordBags.size(); ++j)
			{
				if(!removed.contains(j) && wordBags.get(i).similarity(wordBags.get(j)) > 0)
				{
					removed.add(j);
					wordBags.get(i).addSimilar();
				}
			}
		}
		Collections.sort(wordBags);
		for(NPWordBag wb : wordBags)
		{
			System.out.println(wb.getSimilars() + " " + wb.getNounPhrase());
		}
		
//		for(NPWordBag bag : wordBags.keySet())
//		{
//			if(wordBags.get(bag) > 3)
//				System.out.println(wordBags.get(bag) + " : " + bag);
//		}
	}

}