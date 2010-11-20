package crybaby.summarize;

import java.util.ArrayList;
import java.util.Map;

public class BagOfWords {
    ArrayList<Double> wordCounts;

    public BagOfWords(ArrayList<Double> counts) {
        wordCounts = counts;
    }

    public BagOfWords(Map<String, Integer> wordNumbers,
                      int wordCount,
                      String sentence) {
        wordCounts = new ArrayList<Double>(wordCount);
        for (int i = 0; i < wordCount; ++i) {
            wordCounts.add(0.0);
        }
        for (String word: sentence.split("\\s+")) {
            Integer index = wordNumbers.get(word.toLowerCase());
            //TODO: handle cases where the word isn't found
            wordCounts.set(index, wordCounts.get(index) + 1);
        }
    }

    //TODO: maybe factor all this stuff out into a "Vector" class?
    public int size() {
        return wordCounts.size();
    }

    public double get(int index) {
        //TODO: handle cases where index is out of bounds
        return wordCounts.get(index);
    }

    public double squared_distance(BagOfWords other) {
        //TODO: handle cases where other vector is a different size
        double sum = 0;
        for (int i = 0; i < size(); ++i) {
            sum += Math.pow(get(i) - other.get(i), 2);
        }
        return sum;
    }

    public double distance(BagOfWords other) {
        return Math.sqrt(squared_distance(other));
    }

    public BagOfWords plus(BagOfWords other) {
        ArrayList<Double> sum = new ArrayList<Double>(size());
        for (int i = 0; i < size(); ++i) {
            sum.add(get(i) + other.get(i));
        }
        return new BagOfWords(sum);
    }

    public double squared_magnitude() {
        double sum = 0;
        for (int i = 0; i < size(); ++i) {
            sum += Math.pow(get(i), 2);
        }
        return sum;
    }

    public double magnitude() {
        return Math.sqrt(squared_magnitude());
    }

    public BagOfWords scale(double scale_factor) {
        ArrayList<Double> scaled = new ArrayList<Double>(size());
        for (int i = 0; i < size(); ++i) {
            scaled.add(get(i) * scale_factor);
        }
        return new BagOfWords(scaled);
    }
}