import util.QueueComparator;
import util.SentenceRanking;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by jonathankeys on 8/29/17.
 *
 * Base class contains all the methods needed to retrieve and process data on
 * the article/corpus which the data is from.
 */
class Base {

    private final String stopWordsPath = "/Users/RUCHITA/Desktop/docsumjava/BigDataJava/text/stopWords.txt";
    private final String articlePath = "/Users/RUCHITA/Desktop/docsumjava/BigDataJava/text/article.txt";
    final String learnerPath = "/Users/RUCHITA/Desktop/docsumjava/BigDataJava/serialized/learner.ser";

    Stack<String> stopWords;
    Stack<String> articleSentences;
    Stack<String> articleWords;

    // To initialize all the articles, sentences, and words
    {
        try {
            stopWords = createStopWords();
            articleSentences = createArticleSentences();
            articleWords = createArticleWords();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create List of strings of stop words noted in file.
     *
     * @return stack of strings containing each stop word.
     */
    private Stack<String> createStopWords() throws FileNotFoundException {
        Stack<String> stopWords = new Stack<>();
        Scanner stopWordList = new Scanner(new FileReader(stopWordsPath));
        while (stopWordList.hasNextLine()) {
            stopWords.push(stopWordList.nextLine());
        }
        return stopWords;
    }

    /**
     * Create list of sentences in the article.
     *
     * @return stack of strings containing each sentence of the article.
     */
    private Stack<String> createArticleSentences() throws FileNotFoundException {
        Stack<String> articleSentences = new Stack<>();
        Pattern sentencePattern = Pattern
              .compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[" + ".!?]?['\"]?(?=\\s|$)",
                    Pattern.MULTILINE | Pattern.COMMENTS);

        Scanner article = new Scanner(new FileReader(articlePath));

        while (article.hasNextLine()) {
            Matcher matcher = sentencePattern.matcher(article.nextLine());
            while (matcher.find()) {
                articleSentences.push(matcher.group());
            }
        }
        return articleSentences;
    }

    /**
     * Create list of words in the article.
     *
     * @return stack of strings containing each individual word (without punctuation) of the article.
     */
    private Stack<String> createArticleWords() throws FileNotFoundException {
        Stack<String> articleWords = new Stack<>();

        new Scanner(new FileReader(articlePath)).forEachRemaining(word -> {
            word = word.replaceAll("[^a-zA-Z ]", "");
            if (!word.equalsIgnoreCase("")) articleWords.push(word.toLowerCase());
        });

        return articleWords;
    }

    /**
     * Create Json object of all words in the article and the amount of occurrences they have.
     *
     * @param articleWords List of strings containing each word in the article.
     * @return Json object containing all words as keys with their values being the amount of occurrences of that word.
     */
    HashMap<String, Integer> findWordOccurrences(List<String> articleWords) {
        HashMap<String, Integer> builder = new HashMap<>();
        articleWords.forEach(word -> builder.put(word, Collections.frequency(articleWords, word)));
        return builder;
    }

    /**
     * Create Json object of all words in the article and the amount of occurrences they have, ignoring all stop words
     * in each sentence and setting them to a value of 0.
     *
     * @param articleWords List of strings containing each word in the article.
     * @param stopWords List of strings containing each stop word.
     * @return Json object containing all words as keys with their values being the amount of occurrences of that word,
     * except if the key is a stop word - it will be set to 0.
     */
    HashMap<String, Double> findWordOccurrences(List<String> articleWords, List<String> stopWords, int inverse) {
        HashMap<String, Double> builder = new HashMap<>();

        articleWords.forEach(word -> {
            int frequency = Collections.frequency(articleWords, word);
            boolean isInverse = inverse == 1;

            double occurrence = isInverse ? 1/frequency : frequency;

            for (String stopWord : stopWords) {
                if (word.equalsIgnoreCase(stopWord)) {
                    occurrence = 0;
                    break;
                }
            }
            builder.put(word, occurrence);
        });
        return builder;
    }

    HashMap<String, Double> findWordOccurrences(List<String> articleWords, List<String> stopWords,
          HashMap<String, Double> allWords) {

        HashMap<String, Double> builder = new HashMap<>();

        double occurrence;

        Set<String> articleWordsSet = new HashSet<>(articleWords);
        for (String word : articleWordsSet) {
            occurrence = (1.0 / allWords.get(word));
            for (String stopWord : stopWords) {
                if (word.equalsIgnoreCase(stopWord)) {
                    occurrence = 0;
                    break;
                }
            }
            builder.put(word, occurrence);
        }
        return builder;
    }

    /**
     * Create Json object of all sentences in the article and amount of occurrences of non-stop words, the higher value
     * the sentence, the more important it will be ranked.
     *
     * @param articleSentences List of strings containing each sentence of the article.
     * @param mergedObject Json object containing all words as keys with their values being the amount of occurrences of
     * that word.
     * @return Json object containing each sentence as keys with their values being the amount of occurrences of words
     * in the sentence.
     */
    HashMap<String, Double> findWordInSentenceOccurrences(List<String> articleSentences,
          HashMap<String, Double> mergedObject) {

        HashMap<String, Double> builder = new HashMap<>();
        articleSentences.forEach(sentence -> {
            final double[] occurrences = {0.0};
            mergedObject.keySet().forEach(k -> occurrences[0] += sentence.contains(k) ? mergedObject.get(k) : 0);
            builder.put(sentence, (occurrences[0]));
        });

        return builder;
    }

    /**
     * Create an organized list of sentences sorted so that the first sentence is the most important and each sentence
     * thereafter being of less and less importance.
     *
     * @param sentenceValue Json object containing each sentence as keys with their values being the amount of
     * occurrences of words in the sentence.
     * @return list of strings sorted so that the more important the sentence, the earlier on in the list it will occur.
     */
    PriorityQueue<SentenceRanking> rankSentences(HashMap<String, Double> sentenceValue) {
        PriorityQueue<SentenceRanking> queue = new PriorityQueue<>(new QueueComparator());

        queue.addAll(sentenceValue.keySet().stream().map(key -> new SentenceRanking(key, sentenceValue.get(key)))
              .collect(Collectors.toList()));
        return queue;
    }

    static void printLimitedSummary(PriorityQueue<SentenceRanking> sentences) {

        //double percent = (new Scanner(System.in).nextDouble()) / 100;

        int numToDisplay = 5;
        String ans = "";
        for (int i = 0; i < numToDisplay; i++) {
            SentenceRanking item = sentences.peek();
            ans += item.getSentence()+"\n";
            sentences.remove();
        }
        System.out.println(ans);

    }

    /**
     * Print out all keys and respective value for Json object.
     *
     * @param sentenceValue Json object with key/value pairs to be printed.
     */
    private static void printJson(HashMap<String, Double> sentenceValue) {
        sentenceValue.forEach((k, v) ->  System.out.println("Key: " + k + "\nValue: " + v + "\n"));
    }

    /**
     * Print out all items in String list.
     *
     * @param stopWords List of strings to be printed out.
     */
    private static void printList(List<String> stopWords) {
        stopWords.forEach(System.out::println);
    }


    /**
     * Print out all objects created and used in project.
     *  @param stopWords String list of all stop words.
     * @param articleSentences String list of all sentences in article.
     * @param articleWords String list of all words in article.
     * @param wordsNoStopWordsValue Json object of all words and their occurrence amount.
     * @param sentenceValue Json object of all sentences and their ranking amount.
     */
    static void printAll(List<String> stopWords, List<String> articleSentences, List<String> articleWords,
          HashMap<String, Double> wordsNoStopWordsValue, HashMap<String, Double> sentenceValue) {

        System.out.println("--- JSON Word Object ---");
        printJson(wordsNoStopWordsValue);

        System.out.println("--- JSON Sentence Object ---");
        printJson(sentenceValue);

        System.out.println("\n\n--- summarizers.util.Article Words ---");
        printList(articleWords);

        System.out.println("\n\n--- summarizers.util.Article Sentences ---");
        printList(articleSentences);

        System.out.println("\n\n--- Stop Words ---");
        printList(stopWords);
    }

}
