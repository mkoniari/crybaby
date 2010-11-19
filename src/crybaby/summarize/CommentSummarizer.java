package crybaby.summarize;

import crybaby.common.LexRanker;
import crybaby.common.LexRankResults;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * This class can be used to summarize a bunch of comments.
 * It computes the IDF for every term in the group of comments, re-represents
 * them as a tf-idf weighted bag of words, and runs LexRank on the whole thing.
 */
public class CommentSummarizer {
    List<String> commentTexts;

    public CommentSummarizer(List<String> comments) {
        commentTexts = comments;
    }

    /**
     * A little utility to tokenize a string's text, so that this and Comment
     * come up with the same result.
     * TODO: move that out so that this isn't duplicated
     */
    public static String[] tokens(String comment) {
        return comment.replaceAll("--", " ")
            .replaceAll("[^a-zA-Z0-9_'\\s]", "")
            .toLowerCase()
            .split("\\s+");
    }

    /**
     * Computes the inverse document frequency for each word in the corpus.
     * IDF is defined as log(N/t), where N is the number of documents (in this
     * case, comments), and t is the number of documents that term appears in.
     */
    protected Map<String, Double> idf(List<String> words) {
        Map<String, Double> idf = new HashMap<String, Double>();
        Map<String, Integer> df = new HashMap<String, Integer>();
        for (String word: words) {
            df.put(word, 0);
        }
        for (String comment: commentTexts) {
            Set<String> present = new HashSet<String>();
            for (String word: tokens(comment)) {
                present.add(word);
            }
            for (String word: present) {
                df.put(word, df.get(word) + 1);
            }
        }
        for (String word: words) {
            idf.put(word, Math.log(commentTexts.size() * 1.0 / df.get(word)));
        }
        return idf;
    }

    /**
     * Generates a summary of the comments passed into this CommentSummarizer.
     * The output is a list of salient comments, ordered from most salient to
     * least.
     * Currently, we just take any comment that is locally maximal in relevance,
     * so it's possible that the summary could have multiple entries
     * that mean the same thing. Anecdotally, though, it work pretty well.
     */
    public List<String> summarize() {
        Set<String> wordSet = new HashSet<String>();
        for (String s: commentTexts) {
            for (String word: tokens(s)) {
                wordSet.add(word);
            }
        }
        List<String> words = new ArrayList<String>();
        for (String s: wordSet) {
            words.add(s);
        }
        Map<String, Double> idf = idf(words);
        List<Comment> comments = new ArrayList<Comment>(commentTexts.size());
        for (String s: commentTexts) {
            comments.add(new Comment(s, idf, words));
        }
        LexRankResults<Comment> results = LexRanker.rank(comments, 0.1, false);

        List<String> finalResults = new ArrayList<String>();
        for (Comment c: results.rankedResults) {
            // Only return results that are local maxima
            boolean max = true;
            for (Comment neighbor: results.neighbors.get(c)) {
                if (results.scores.get(neighbor) > results.scores.get(c)) {
                    max = false;
                }
            }
            if (max) {
                finalResults.add(c.commentText);
            }
        }
        return finalResults;
    }
}