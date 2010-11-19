package crybaby.jargon;

import java.io.*;
import java.util.*;

import crybaby.parser.ParseSentence;

import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
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
		Tree parseTree;
		String[] stopWordArray = { "a",  "A",  "able",  "Able",  "about",  "About",  "above",  "Above",  "abroad",  "Abroad",  "according",  "According",  "accordingly",  "Accordingly",  "across",  "Across",  "actually",  "Actually",  "adj",  "Adj",  "after",  "After",  "afterwards",  "Afterwards",  "again",  "Again",  "against",  "Against",  "ago",  "Ago",  "ahead",  "Ahead",  "ain't",  "Ain't",  "all",  "All",  "allow",  "Allow",  "allows",  "Allows",  "almost",  "Almost",  "alone",  "Alone",  "along",  "Along",  "alongside",  "Alongside",  "already",  "Already",  "also",  "Also",  "although",  "Although",  "always",  "Always",  "am",  "Am",  "amid",  "Amid",  "amidst",  "Amidst",  "among",  "Among",  "amongst",  "Amongst",  "an",  "An",  "and",  "And",  "another",  "Another",  "any",  "Any",  "anybody",  "Anybody",  "anyhow",  "Anyhow",  "anyone",  "Anyone",  "anything",  "Anything",  "anyway",  "Anyway",  "anyways",  "Anyways",  "anywhere",  "Anywhere",  "apart",  "Apart",  "appear",  "Appear",  "appreciate",  "Appreciate",  "appropriate",  "Appropriate",  "are",  "Are",  "aren't",  "Aren't",  "around",  "Around",  "as",  "As",  "a's",  "A's",  "aside",  "Aside",  "ask",  "Ask",  "asking",  "Asking",  "associated",  "Associated",  "at",  "At",  "available",  "Available",  "away",  "Away",  "awfully",  "Awfully",  "back",  "Back",  "backward",  "Backward",  "backwards",  "Backwards",  "be",  "Be",  "became",  "Became",  "because",  "Because",  "become",  "Become",  "becomes",  "Becomes",  "becoming",  "Becoming",  "been",  "Been",  "before",  "Before",  "beforehand",  "Beforehand",  "begin",  "Begin",  "behind",  "Behind",  "being",  "Being",  "believe",  "Believe",  "below",  "Below",  "beside",  "Beside",  "besides",  "Besides",  "best",  "Best",  "better",  "Better",  "between",  "Between",  "beyond",  "Beyond",  "both",  "Both",  "brief",  "Brief",  "but",  "But",  "by",  "By",  "came",  "Came",  "can",  "Can",  "cannot",  "Cannot",  "cant",  "Cant",  "can't",  "Can't",  "caption",  "Caption",  "cause",  "Cause",  "causes",  "Causes",  "certain",  "Certain",  "certainly",  "Certainly",  "changes",  "Changes",  "clearly",  "Clearly",  "c'mon",  "C'mon",  "co",  "Co",  "co.",  "Co.",  "com",  "Com",  "come",  "Come",  "comes",  "Comes",  "concerning",  "Concerning",  "consequently",  "Consequently",  "consider",  "Consider",  "considering",  "Considering",  "contain",  "Contain",  "containing",  "Containing",  "contains",  "Contains",  "corresponding",  "Corresponding",  "could",  "Could",  "couldn't",  "Couldn't",  "course",  "Course",  "c's",  "C's",  "currently",  "Currently",  "dare",  "Dare",  "daren't",  "Daren't",  "definitely",  "Definitely",  "described",  "Described",  "despite",  "Despite",  "did",  "Did",  "didn't",  "Didn't",  "different",  "Different",  "directly",  "Directly",  "do",  "Do",  "does",  "Does",  "doesn't",  "Doesn't",  "doing",  "Doing",  "done",  "Done",  "don't",  "Don't",  "down",  "Down",  "downwards",  "Downwards",  "during",  "During",  "each",  "Each",  "edu",  "Edu",  "eg",  "Eg",  "eight",  "Eight",  "eighty",  "Eighty",  "either",  "Either",  "else",  "Else",  "elsewhere",  "Elsewhere",  "end",  "End",  "ending",  "Ending",  "enough",  "Enough",  "entirely",  "Entirely",  "especially",  "Especially",  "et",  "Et",  "etc",  "Etc",  "even",  "Even",  "ever",  "Ever",  "evermore",  "Evermore",  "every",  "Every",  "everybody",  "Everybody",  "everyone",  "Everyone",  "everything",  "Everything",  "everywhere",  "Everywhere",  "ex",  "Ex",  "exactly",  "Exactly",  "example",  "Example",  "except",  "Except",  "fairly",  "Fairly",  "far",  "Far",  "farther",  "Farther",  "few",  "Few",  "fewer",  "Fewer",  "fifth",  "Fifth",  "first",  "First",  "five",  "Five",  "followed",  "Followed",  "following",  "Following",  "follows",  "Follows",  "for",  "For",  "forever",  "Forever",  "former",  "Former",  "formerly",  "Formerly",  "forth",  "Forth",  "forward",  "Forward",  "found",  "Found",  "four",  "Four",  "from",  "From",  "further",  "Further",  "furthermore",  "Furthermore",  "get",  "Get",  "gets",  "Gets",  "getting",  "Getting",  "given",  "Given",  "gives",  "Gives",  "go",  "Go",  "goes",  "Goes",  "going",  "Going",  "gone",  "Gone",  "got",  "Got",  "gotten",  "Gotten",  "greetings",  "Greetings",  "had",  "Had",  "hadn't",  "Hadn't",  "half",  "Half",  "happens",  "Happens",  "hardly",  "Hardly",  "has",  "Has",  "hasn't",  "Hasn't",  "have",  "Have",  "haven't",  "Haven't",  "having",  "Having",  "he",  "He",  "he'd",  "He'd",  "he'll",  "He'll",  "hello",  "Hello",  "help",  "Help",  "hence",  "Hence",  "her",  "Her",  "here",  "Here",  "hereafter",  "Hereafter",  "hereby",  "Hereby",  "herein",  "Herein",  "here's",  "Here's",  "hereupon",  "Hereupon",  "hers",  "Hers",  "herself",  "Herself",  "he's",  "He's",  "hi",  "Hi",  "him",  "Him",  "himself",  "Himself",  "his",  "His",  "hither",  "Hither",  "hopefully",  "Hopefully",  "how",  "How",  "howbeit",  "Howbeit",  "however",  "However",  "hundred",  "Hundred",  "i",  "I",  "i'd",  "I'd",  "ie",  "Ie",  "if",  "If",  "ignored",  "Ignored",  "i'll",  "I'll",  "i'm",  "I'm",  "immediate",  "Immediate",  "in",  "In",  "inasmuch",  "Inasmuch",  "inc",  "Inc",  "inc.",  "Inc.",  "indeed",  "Indeed",  "indicate",  "Indicate",  "indicated",  "Indicated",  "indicates",  "Indicates",  "inner",  "Inner",  "inside",  "Inside",  "insofar",  "Insofar",  "instead",  "Instead",  "into",  "Into",  "inward",  "Inward",  "is",  "Is",  "isn't",  "Isn't",  "it",  "It",  "it'd",  "It'd",  "it'll",  "It'll",  "its",  "Its",  "it's",  "It's",  "itself",  "Itself",  "i've",  "I've",  "just",  "Just",  "k",  "K",  "keep",  "Keep",  "keeps",  "Keeps",  "kept",  "Kept",  "know",  "Know",  "known",  "Known",  "knows",  "Knows",  "last",  "Last",  "lately",  "Lately",  "later",  "Later",  "latter",  "Latter",  "latterly",  "Latterly",  "least",  "Least",  "less",  "Less",  "lest",  "Lest",  "let",  "Let",  "let's",  "Let's",  "like",  "Like",  "liked",  "Liked",  "likely",  "Likely",  "likewise",  "Likewise",  "little",  "Little",  "look",  "Look",  "looking",  "Looking",  "looks",  "Looks",  "low",  "Low",  "lower",  "Lower",  "ltd",  "Ltd",  "made",  "Made",  "mainly",  "Mainly",  "make",  "Make",  "makes",  "Makes",  "many",  "Many",  "may",  "May",  "maybe",  "Maybe",  "mayn't",  "Mayn't",  "me",  "Me",  "mean",  "Mean",  "meantime",  "Meantime",  "meanwhile",  "Meanwhile",  "merely",  "Merely",  "might",  "Might",  "mightn't",  "Mightn't",  "mine",  "Mine",  "minus",  "Minus",  "miss",  "Miss",  "more",  "More",  "moreover",  "Moreover",  "most",  "Most",  "mostly",  "Mostly",  "mr",  "Mr",  "mrs",  "Mrs",  "much",  "Much",  "must",  "Must",  "mustn't",  "Mustn't",  "my",  "My",  "myself",  "Myself",  "name",  "Name",  "namely",  "Namely",  "nd",  "Nd",  "near",  "Near",  "nearly",  "Nearly",  "necessary",  "Necessary",  "need",  "Need",  "needn't",  "Needn't",  "needs",  "Needs",  "neither",  "Neither",  "never",  "Never",  "neverf",  "Neverf",  "neverless",  "Neverless",  "nevertheless",  "Nevertheless",  "new",  "New",  "next",  "Next",  "nine",  "Nine",  "ninety",  "Ninety",  "no",  "No",  "nobody",  "Nobody",  "non",  "Non",  "none",  "None",  "nonetheless",  "Nonetheless",  "noone",  "Noone",  "no-one",  "No-one",  "nor",  "Nor",  "normally",  "Normally",  "not",  "Not",  "nothing",  "Nothing",  "notwithstanding",  "Notwithstanding",  "novel",  "Novel",  "now",  "Now",  "nowhere",  "Nowhere",  "obviously",  "Obviously",  "of",  "Of",  "off",  "Off",  "often",  "Often",  "oh",  "Oh",  "ok",  "Ok",  "okay",  "Okay",  "old",  "Old",  "on",  "On",  "once",  "Once",  "one",  "One",  "ones",  "Ones",  "one's",  "One's",  "only",  "Only",  "onto",  "Onto",  "opposite",  "Opposite",  "or",  "Or",  "other",  "Other",  "others",  "Others",  "otherwise",  "Otherwise",  "ought",  "Ought",  "oughtn't",  "Oughtn't",  "our",  "Our",  "ours",  "Ours",  "ourselves",  "Ourselves",  "out",  "Out",  "outside",  "Outside",  "over",  "Over",  "overall",  "Overall",  "own",  "Own",  "particular",  "Particular",  "particularly",  "Particularly",  "past",  "Past",  "per",  "Per",  "perhaps",  "Perhaps",  "placed",  "Placed",  "please",  "Please",  "plus",  "Plus",  "possible",  "Possible",  "presumably",  "Presumably",  "probably",  "Probably",  "provided",  "Provided",  "provides",  "Provides",  "que",  "Que",  "quite",  "Quite",  "qv",  "Qv",  "rather",  "Rather",  "rd",  "Rd",  "re",  "Re",  "really",  "Really",  "reasonably",  "Reasonably",  "recent",  "Recent",  "recently",  "Recently",  "regarding",  "Regarding",  "regardless",  "Regardless",  "regards",  "Regards",  "relatively",  "Relatively",  "respectively",  "Respectively",  "right",  "Right",  "round",  "Round",  "said",  "Said",  "same",  "Same",  "saw",  "Saw",  "say",  "Say",  "saying",  "Saying",  "says",  "Says",  "second",  "Second",  "secondly",  "Secondly",  "see",  "See",  "seeing",  "Seeing",  "seem",  "Seem",  "seemed",  "Seemed",  "seeming",  "Seeming",  "seems",  "Seems",  "seen",  "Seen",  "self",  "Self",  "selves",  "Selves",  "sensible",  "Sensible",  "sent",  "Sent",  "serious",  "Serious",  "seriously",  "Seriously",  "seven",  "Seven",  "several",  "Several",  "shall",  "Shall",  "shan't",  "Shan't",  "she",  "She",  "she'd",  "She'd",  "she'll",  "She'll",  "she's",  "She's",  "should",  "Should",  "shouldn't",  "Shouldn't",  "since",  "Since",  "six",  "Six",  "so",  "So",  "some",  "Some",  "somebody",  "Somebody",  "someday",  "Someday",  "somehow",  "Somehow",  "someone",  "Someone",  "something",  "Something",  "sometime",  "Sometime",  "sometimes",  "Sometimes",  "somewhat",  "Somewhat",  "somewhere",  "Somewhere",  "soon",  "Soon",  "sorry",  "Sorry",  "specified",  "Specified",  "specify",  "Specify",  "specifying",  "Specifying",  "still",  "Still",  "sub",  "Sub",  "such",  "Such",  "sup",  "Sup",  "sure",  "Sure",  "take",  "Take",  "taken",  "Taken",  "taking",  "Taking",  "tell",  "Tell",  "tends",  "Tends",  "th",  "Th",  "than",  "Than",  "thank",  "Thank",  "thanks",  "Thanks",  "thanx",  "Thanx",  "that",  "That",  "that'll",  "That'll",  "thats",  "Thats",  "that's",  "That's",  "that've",  "That've",  "the",  "The",  "their",  "Their",  "theirs",  "Theirs",  "them",  "Them",  "themselves",  "Themselves",  "then",  "Then",  "thence",  "Thence",  "there",  "There",  "thereafter",  "Thereafter",  "thereby",  "Thereby",  "there'd",  "There'd",  "therefore",  "Therefore",  "therein",  "Therein",  "there'll",  "There'll",  "there're",  "There're",  "theres",  "Theres",  "there's",  "There's",  "thereupon",  "Thereupon",  "there've",  "There've",  "these",  "These",  "they",  "They",  "they'd",  "They'd",  "they'll",  "They'll",  "they're",  "They're",  "they've",  "They've",  "thing",  "Thing",  "things",  "Things",  "think",  "Think",  "third",  "Third",  "thirty",  "Thirty",  "this",  "This",  "thorough",  "Thorough",  "thoroughly",  "Thoroughly",  "those",  "Those",  "though",  "Though",  "three",  "Three",  "through",  "Through",  "throughout",  "Throughout",  "thru",  "Thru",  "thus",  "Thus",  "till",  "Till",  "to",  "To",  "together",  "Together",  "too",  "Too",  "took",  "Took",  "toward",  "Toward",  "towards",  "Towards",  "tried",  "Tried",  "tries",  "Tries",  "truly",  "Truly",  "try",  "Try",  "trying",  "Trying",  "t's",  "T's",  "twice",  "Twice",  "two",  "Two",  "un",  "Un",  "under",  "Under",  "underneath",  "Underneath",  "undoing",  "Undoing",  "unfortunately",  "Unfortunately",  "unless",  "Unless",  "unlike",  "Unlike",  "unlikely",  "Unlikely",  "until",  "Until",  "unto",  "Unto",  "up",  "Up",  "upon",  "Upon",  "upwards",  "Upwards",  "us",  "Us",  "use",  "Use",  "used",  "Used",  "useful",  "Useful",  "uses",  "Uses",  "using",  "Using",  "usually",  "Usually",  "v",  "V",  "value",  "Value",  "various",  "Various",  "versus",  "Versus",  "very",  "Very",  "via",  "Via",  "viz",  "Viz",  "vs",  "Vs",  "want",  "Want",  "wants",  "Wants",  "was",  "Was",  "wasn't",  "Wasn't",  "way",  "Way",  "we",  "We",  "we'd",  "We'd",  "welcome",  "Welcome",  "well",  "Well",  "we'll",  "We'll",  "went",  "Went",  "were",  "Were",  "we're",  "We're",  "weren't",  "Weren't",  "we've",  "We've",  "what",  "What",  "whatever",  "Whatever",  "what'll",  "What'll",  "what's",  "What's",  "what've",  "What've",  "when",  "When",  "whence",  "Whence",  "whenever",  "Whenever",  "where",  "Where",  "whereafter",  "Whereafter",  "whereas",  "Whereas",  "whereby",  "Whereby",  "wherein",  "Wherein",  "where's",  "Where's",  "whereupon",  "Whereupon",  "wherever",  "Wherever",  "whether",  "Whether",  "which",  "Which",  "whichever",  "Whichever",  "while",  "While",  "whilst",  "Whilst",  "whither",  "Whither",  "who",  "Who",  "who'd",  "Who'd",  "whoever",  "Whoever",  "whole",  "Whole",  "who'll",  "Who'll",  "whom",  "Whom",  "whomever",  "Whomever",  "who's",  "Who's",  "whose",  "Whose",  "why",  "Why",  "will",  "Will",  "willing",  "Willing",  "wish",  "Wish",  "with",  "With",  "within",  "Within",  "without",  "Without",  "wonder",  "Wonder",  "won't",  "Won't",  "would",  "Would",  "wouldn't",  "Wouldn't",  "yes",  "Yes",  "yet",  "Yet",  "you",  "You",  "you'd",  "You'd",  "you'll",  "You'll",  "your",  "Your",  "you're",  "You're",  "yours",  "Yours",  "yourself",  "Yourself",  "yourselves",  "Yourselves",  "you've",  "You've",  "zero",  "Zero",  "iPad",  "IPad",  "ipad",  "Ipad" };
		List<String> stopWordList = Arrays.asList(stopWordArray);
		Set<String> stopWords = new HashSet<String>(stopWordList);
		NPWordBag newBag;
		String label;
		Map<NPWordBag, Integer> wordBags;
		ParseSentence ps;
		
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