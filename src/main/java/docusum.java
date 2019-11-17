
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import util.Parser;
import util.SentenceRanking;

public class docusum  {


    public static void main(String[] args) {
        runner runobj = new runner();
        runobj.run();
    }


}

class runner extends Base{
    public void run(){
        List<String> stanfordParser = new ArrayList<>();

        stanfordParser.addAll(articleSentences);

        HashMap<String, Double> sentenceValue = Parser.getHashMap(stanfordParser, stopWords);
        PriorityQueue<SentenceRanking> sentenceRanked = rankSentences(sentenceValue);
        printLimitedSummary(sentenceRanked);
    }
}