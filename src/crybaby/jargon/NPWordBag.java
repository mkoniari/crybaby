package crybaby.jargon;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/*
 * This is a somewhat naive way of doing this.
 * Really, each bag should be a vertex in a graph, 
 * and similarity between vertices should cause edges 
 * have smaller weights (or larger weights).
 */

public class NPWordBag
{
	String nounPhrase;
	Set<String> wordBag;
	ArrayList<NPWordBag> similarBags;
	
	public NPWordBag(String nounPhrase, Set<String> wordBag)
	{
		this.nounPhrase = nounPhrase;
		this.wordBag = wordBag;
		this.similarBags = new ArrayList<NPWordBag>();
	}

	public boolean addSimilarBag(NPWordBag newBag)
	{
		if(intersection(this.wordBag, newBag.wordBag).size() > 0)
		{
			this.similarBags.add(newBag);
			return true;
		}
		return false;
	}
	
	// because Java doesn't have intersection built into the Set class...
	private Set<String> intersection(Set<String> setA, Set<String> setB)
	{
		Set<String> temp = new HashSet<String>();
		for (String x : setA)
			if (setB.contains(x))
			{
				temp.add(x);
				return temp;
			}
		return temp;
	}
	
	public String toString()
	{
		String rep = this.nounPhrase + "\n";
		for(NPWordBag b : this.similarBags)
			rep += b.nounPhrase + ", ";
		return rep.substring(0, rep.length() - 2) + "\n";
	}
}
