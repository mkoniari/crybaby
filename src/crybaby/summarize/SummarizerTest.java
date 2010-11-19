package crybaby.summarize;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

// Not, like, an actual for serious test; just something to see if it actually
// works at all.
public class SummarizerTest {
    public static void main(String[] args) {
        List<String> comments = new ArrayList<String>();
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            while ((line = br.readLine()) != null) {
                comments.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        CommentSummarizer summarizer = new CommentSummarizer(comments);
        List<String> results = summarizer.summarize();
        for (String s: results) {
            System.out.println(s+"\n");
        }
    }
}