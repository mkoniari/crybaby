package crybaby.parser;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;

public class Parser {
	// Singleton design pattern FTW
	public static LexicalizedParser parser = new LexicalizedParser("englishPCFG.ser.gz");
	public static TokenizerFactory<? extends HasWord> tf = PTBTokenizer.factory(false, new WordTokenFactory());
}
