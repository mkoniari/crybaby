package crybaby.summarize;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class CommentSummarizer {
    List<String> comments;
    List<BagOfWords> bagsOfWords;

    public CommentSummarizer(List<String> comments) {
        this.comments = comments;
        Set<String> wordSet = new HashSet<String>();
        for (String comment: comments) {
            for (String word: comment.toLowerCase().split("\\s+")) {
                wordSet.add(word);
            }
        }
        List<String> words = new ArrayList<String>(wordSet.size());
        for (String word: wordSet) {
            words.add(word);
        }
        Map<String, Integer> wordPositions = new HashMap<String, Integer>();
        for (int i = 0; i < words.size(); ++i) {
            wordPositions.put(words.get(i), i);
        }
        bagsOfWords = new ArrayList<BagOfWords>();
        for (String c: comments) {
            bagsOfWords.add(new BagOfWords(wordPositions, words.size(), c));
        }
    }

    public List<String> summarize(int k) {
        KMeansClusterer kmeans = new KMeansClusterer(bagsOfWords, k);
        kmeans.runToConvergence();
        List<BagOfWords> means = kmeans.means();
        List<String> results = new ArrayList<String>(means.size());
        for (int i = 0; i < means.size(); ++i) {
            int best = 0;
            BagOfWords mean = means.get(i);
            for (int j = 0; j < bagsOfWords.size(); ++j) {
                if (mean.squared_distance(bagsOfWords.get(j)) <
                    mean.squared_distance(bagsOfWords.get(best))) {
                    best = j;
                }
            }
            results.add(comments.get(best));
        }
        return results;
    }
}