package crybaby.summarize;

import java.util.List;
import java.util.ArrayList;

// Not, like, an actual for serious test; just something to see if it actually
// works at all.
public class SummarizerTest {
    public static void main(String[] args) {
        List<String> comments = new ArrayList<String>();
        comments.add("The headphone cord is too short");
        comments.add("I thought the cord was too short");
        comments.add("The cord wasn't long enough to reach my ears");
        comments.add("I would've preferred a longer cord");

        comments.add("The sound quality is great");
        comments.add("I found the sound to be good overall");
        comments.add("The sound was heavy on bass but good otherwise");
        comments.add("No complaints about the sound quality");

        comments.add("One earphone broke one month after I bought it");
        comments.add("The left earphone didn't last too long");
        comments.add("The reliability is bad, it broke after only one month");
        comments.add("It broke just one month after I bought it");

        CommentSummarizer summarizer = new CommentSummarizer(comments);
        for (int i = 1; i < 5; ++i) {
            for (String s: summarizer.summarize(i)) {
                System.out.println(s);
            }
            System.out.println();
        }   
    }
}