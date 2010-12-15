package crybaby.summarize;

import java.util.List;
import java.util.ArrayList;

public class KMeansClusterer {
    List<BagOfWords> data;
    List<BagOfWords> means;
    //TODO: maybe make this take in some "vector" class instead
    public KMeansClusterer(List<BagOfWords> vectors, int k) {
        data = vectors;
        means = new ArrayList<BagOfWords>(k);
        // Initialize means based on the k-means++ scheme
        // Well, kind of; I don't actually want to figure out the entire
        // distribution of distances, so I'll kind of fake it and hope it's
        // good enough.
        int index = (int)(Math.random() * vectors.size());
        means.add(data.get(index));
        for (int i = 1; i < k; ++i) {
            while(true) {
                index = (int)(Math.random() * vectors.size());
                BagOfWords candidate = data.get(index);
                double dist = candidate.squared_distance(means.get(i-1));
                // Select with probability proportional to squared distance
                // Well, kind of...
                if (dist / 100.0 < Math.random()) {
                    means.add(candidate);
                    break;
                }
            }
        }
    }

    public List<BagOfWords> means() {
        return means;
    }

    public List<BagOfWords> means(List<List<BagOfWords>> clusters) {
        List<BagOfWords> means = new ArrayList<BagOfWords>(clusters.size());
        for (List<BagOfWords> cluster: clusters) {
            //TODO: handle empty clusters in a more intelligent way
            if (cluster.size() == 0) {
                continue;
            }
            BagOfWords average = cluster.get(0);
            for (int i = 1; i < cluster.size(); ++i) {
                average = average.plus(cluster.get(i));
            }
            means.add(average.scale(1.0/cluster.size()));
        }
        return means;
    }

    public List<List<BagOfWords>> clusters() {
        return clusters(means());
    }

    public List<List<BagOfWords>> clusters(List<BagOfWords> means) {
        List<List<BagOfWords>> clusters = new ArrayList<List<BagOfWords>>();
        for (BagOfWords mean: means) {
            clusters.add(new ArrayList<BagOfWords>());
        }
        for (BagOfWords datum: data) {
            int best = 0;
            for (int i = 1; i < means.size(); ++i) {
                if (datum.squared_distance(means.get(i)) <
                    datum.squared_distance(means.get(best))) {
                    best = i;
                }
            }
            clusters.get(best).add(datum);
        }
        return clusters;
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; ++i) {
            oneStep();
        }
    }

    public void runToConvergence() {
        // Look at me, I'm gonna use recursion to simulate iteration
        List<BagOfWords> currentMeans = means;
        if (means.size() != currentMeans.size()) {
            runToConvergence();
        } else {
            for (int i = 0; i < means.size(); ++i) {
                if (means.get(i).squared_distance(currentMeans.get(i)) > 0.1) {
                    runToConvergence();
                    break;
                }
            }
        }
    }

    private void oneStep() {
        List<List<BagOfWords>> clusters = clusters();
        means = means(clusters);
    }
}