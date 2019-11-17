
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import util.Parser;
import util.SentenceRanking;

public class docusum  {


    public static void main(String[] args) throws IOException {

        String text = "BATON ROUGE, La. — Gov. John Bel Edwards of Louisiana, the only Democratic governor in the Deep South, narrowly won re-election Saturday, overcoming the intervention of President Trump, who visited the state multiple times in an effort to help Mr. Edward’s Republican challenger and demonstrate his own clout.\n" +
                "\n" +
                "It was the second blow at the ballot box for Mr. Trump this month in a Republican-leaning state, following the Democratic victory in the Kentucky governor’s race, where the president also campaigned for the G.O.P. candidate.\n" +
                "\n" +
                "In Louisiana, Mr. Trump had wagered significant political capital to try to lift Eddie Rispone, a businessman who ran against Mr. Edwards in large part by embracing the president and his agenda. Mr. Trump campaigned for Mr. Rispone twice in the final two weeks of the race, warning Louisiana voters that a loss would reflect poorly on his presidency — the same appeal he made in Kentucky earlier this month to try to help Gov. Matt Bevin, who ultimately lost.\n" +
                "\n" +
                "Of the three governor’s races this year, all in deep red states, Republicans won only one, in Mississippi. Republicans also lost control of both chambers of the state legislature in Virginia, where many Democratic candidates were sharply critical of Mr. Trump. \n"+"The victory was a deeply personal one for Mr. Edwards, a conservative Democrat in a state and region where his party can often be a disqualifier in statewide races. He campaigned on his accomplishments in office, like balancing the budget, increasing education spending and expanding Medicaid. He also highlighted his conservative stances on abortion and guns and showcased his background as a West Point graduate and son of a sheriff, to appeal to right-leaning voters.\n" +
                "\n" +
                "In his victory speech, Mr. Edwards said, “Our shared love for Louisiana is always more important than the partisan differences that sometimes divide us. And as for the president: God bless his heart.”\n" +
                "\n" +
                "Before the election, Mr. Rispone, a construction magnate from Baton Rouge, had never before run for political office. He vaulted ahead after more prominent Republicans decided against running and became competitive against the governor after cloaking himself in Mr. Trump’s popularity.\n" +
                "\n" +
                "The results indicated that many voters here were happy with the incumbent.\n" +
                "\n" +
                "And on a night when the attention of many Louisianans was split between the election and the football game between top-ranked Louisiana State and the University of Mississippi, Mr. Edwards ventured an explanation for why voters were comfortable re-electing him.\n" +
                "\n" +
                "“It is an easier state to govern when the Saints and LSU are winning,” he said in an interview. “People are just in a better mood.”";
        runner runobj = new runner();
        runobj.sendToArticlePAth(text);
        runobj.run();
    }




}

class runner extends Base{
    void sendToArticlePAth(String text) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("/Users/RUCHITA/Desktop/docsumjava/BigDataJava/text/article.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.write(text);

        writer.close();
    }

    public void run(){
        List<String> stanfordParser = new ArrayList<>();

        stanfordParser.addAll(articleSentences);

        HashMap<String, Double> sentenceValue = Parser.getHashMap(stanfordParser, stopWords);
        PriorityQueue<SentenceRanking> sentenceRanked = rankSentences(sentenceValue);
        printLimitedSummary(sentenceRanked);
    }
}