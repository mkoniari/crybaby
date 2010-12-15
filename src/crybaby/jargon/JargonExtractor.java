package crybaby.jargon;

import java.io.*;
import java.util.*;

import crybaby.parser.ParseSentence;

import edu.stanford.nlp.trees.Tree;

public class JargonExtractor 
{
	public JargonExtractor() {}
	private	List<NPWordBag> wordBags = new ArrayList<NPWordBag>();
	private	Map<String, Integer> nounPhrases = new HashMap<String, Integer>();

	public void addSentences(String[] sentences) {
		ParseSentence ps;
		for(int i = 0; i < sentences.length; ++i)
		{
			sentences[i] = sentences[i] + ".";
			System.out.println(sentences[i]);
			ps = new ParseSentence(sentences[i]);
			addParseSentence(ps);
		}
	}

	public void addParseSentence(ParseSentence ps) {
		String nounPhrase;
		NPWordBag newBag;
		String label;
		boolean commaNP;
		commaNP = false;
		List<Tree> nodes = ps.getParseTree().preOrderNodeList();

		for(Tree node : nodes)
		{
			if(node.size() > 5)
				continue;
			if(node.label().value().equals("NP"))
			{
				nounPhrase = "";
//				System.out.println(node);
				Set<String> bag = new HashSet<String>();
				for(Tree leaf : node.getLeaves())
				{
					label = leaf.label().toString();
					if(label.equals(","))
					{
						commaNP = true;
						System.out.println("COMMA NP");
						System.out.println(nounPhrase + label);
						break;
					}
					nounPhrase += label + " ";
//					System.out.println(nounPhrase);
					if(!stopWords.contains(label))
					{
						if (label.length() == 0)
							continue;
						label = label.toLowerCase();
//						System.out.println(label);
						bag.add(label);
						/* special case, we should either have a bunch of these in a 	*
						 * separate file or come up with a general rule 				*/
						for(Set<String> synSet : synonyms)
						{
							if(synSet.contains(label))
							{
								bag.addAll(synSet);
								break;
							}
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
				if(commaNP)
					continue;
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
	public void removeSimilarity() {
		Set<Integer> removed = new HashSet<Integer>();
		
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
		wordBags.removeAll(removed);
		Collections.sort(wordBags);		
	}
	public List<NPWordBag> getWordBags() {
		return wordBags;
	}
	private static Set<String> stopWords = null, synonymWords = null;
	private static List<Set<String>> synonyms = null;

	static {
		String stopWord, synonymLine;
		BufferedReader stopWordBuffer, synonymBuffer;
		stopWordBuffer = null;
		synonymBuffer = null;

		stopWords = new HashSet<String>();
		
		synonyms = new ArrayList<Set<String>>();
		try {
			stopWordBuffer = new BufferedReader(new FileReader("stopwords.txt"));
			synonymBuffer = new BufferedReader(new FileReader("synonyms.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			while((stopWord = stopWordBuffer.readLine()) != null)
				stopWords.add(stopWord);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			while((synonymLine = synonymBuffer.readLine()) != null)
			{
				synonymWords = new HashSet<String>();
				synonymWords.addAll(Arrays.asList(synonymLine.split(",")));
				synonyms.add(synonymWords);
			}
				
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
	public static void main(String[] args) 
	{
		String paragraph;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		
		JargonExtractor extractor = new JargonExtractor();
		
		try {
			while((paragraph = br.readLine()) != null)
			{
				if(paragraph.length() < 2)
					continue;
				String[] sentences = paragraph.split("(\\.|\\?|!) ");
				extractor.addSentences(sentences);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Word bags generated.");
		extractor.removeSimilarity();
		List<NPWordBag> wordBags = extractor.getWordBags();
		
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