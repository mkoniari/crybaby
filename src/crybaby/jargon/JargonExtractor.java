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
		boolean added;
		BufferedReader stopWordBuffer;
		Set<String> stopWords = null;
		NPWordBag newBag;
		String label;
		Map<NPWordBag, Integer> wordBags;
		ParseSentence ps;
		String stopWord;
		
		stopWordBuffer = null;
		stopWords = new HashSet<String>();
		try {
			stopWordBuffer = new BufferedReader(new FileReader("../../../stopwords.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			while((stopWord = stopWordBuffer.readLine()) != null)
				stopWords.add(stopWord);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		nounPhrases = new Hashtable<String, Integer>();
		wordBags = new Hashtable<NPWordBag, Integer>();
		
		try {
			while((paragraph = br.readLine()) != null)
			{
				if(paragraph.length() < 2)
					continue;
				sentences = paragraph.split("(\\.|\\?|!) ");
				for(int i = 0; i < sentences.length; ++i)
				{
					sentences[i] = sentences[i] + ".";
//					System.out.println(sentences[i]);
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
								nounPhrase += label + " ";
								if(!stopWords.contains(label))
								{
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
							added = false;
							newBag = new NPWordBag(nounPhrase, bag);
							for(NPWordBag b : wordBags.keySet())
							{
								if(b.addSimilarBag(newBag))
								{
									added = true;
									wordBags.put(b, wordBags.get(b) + 1);
									break;
								}
							}
							if(!added)
								wordBags.put(newBag, 1);
							if(nounPhrases.containsKey(nounPhrase))
								nounPhrases.put(nounPhrase, nounPhrases.get(nounPhrase) + 1);
							else
								nounPhrases.put(nounPhrase, 1);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(NPWordBag bag : wordBags.keySet())
		{
			if(wordBags.get(bag) > 3)
				System.out.println(wordBags.get(bag) + " : " + bag);
		}
	}

}