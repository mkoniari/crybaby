package crybaby.summarize;

import crybaby.common.LexRanker;
import crybaby.common.LexRankResults;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class CommentSummarizer {
    List<String> commentTexts;

    public CommentSummarizer(List<String> comments) {
        commentTexts = comments;
    }

    public static String[] tokens(String comment) {
        return comment.replaceAll("--", " ")
            .replaceAll("[^a-zA-Z0-9_'\\s]", "")
            .toLowerCase()
            .split("\\s+");
    }

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