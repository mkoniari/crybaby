package crybaby.jargon;

import java.util.Set;

import crybaby.common.Similar;

public class NPWordBag implements Similar<NPWordBag>, Comparable<NPWordBag>
{
	private String nounPhrase;
	private int similars;
	private Set<String> wordBag;

	public NPWordBag(String nounPhrase, Set<String> wordBag)
	{
		this.nounPhrase = nounPhrase;
		this.wordBag = wordBag;
		this.similars = 0;
	}
	
	// because Java doesn't have intersection built into the Set class...
	private int intersectionCardinality(Set<String> setA, Set<String> setB)
	{
		int intersectionCardinality;
		
		intersectionCardinality = 0;
		for (String x : setA)
			if (setB.contains(x))
			{
				++intersectionCardinality;
			}
		return intersectionCardinality;
	}
	
	public String toString()
	{
		return this.nounPhrase;
	}
	
	public String getNounPhrase() {
		return nounPhrase;
	}

	public void setNounPhrase(String nounPhrase) {
		this.nounPhrase = nounPhrase;
	}

	public Set<String> getWordBag() {
		return wordBag;
	}

	public void setWordBag(Set<String> wordBag) {
		this.wordBag = wordBag;
	}

	@Override
	public double similarity(NPWordBag other) {
		return (double) this.intersectionCardinality(this.getWordBag(), other.getWordBag()) / 
		(this.getWordBag().size() + other.getWordBag().size());
	}
	
	public void addSimilar()
	{
		++this.similars;
	}
	
	public int getSimilars()
	{
		return this.similars;
	}

	@Override
	public int compareTo(NPWordBag arg0) {
		if(this.getSimilars() > arg0.getSimilars())
			return -1;
		else
			return 1;
	}
}
