package TentaPlugg.tenta160317;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Task3 {
    List<String> filePaths = new ArrayList<String>();
    ConcurrentHashMap<String, Integer> wordCounters = new ConcurrentHashMap<String, Integer>();
    void initiateFilePaths() {
    // Initiate the list filePaths with some file paths.
    }

    List<String> getWordsFromOneFile(String filePath) {
    // Returns a list of words from the specified file.

}

    void updateCounter(String word) {
        int counter = 0;
        if (!wordCounters.containsKey(word)) {
            wordCounters.put(word, counter);
        }
        counter = wordCounters.get(word);
        counter++;
        wordCounters.put(word, counter);
    }

    void processWordsFromOneFile(List<String> words) {
        words.stream().parallel().forEach((String word) -> updateCounter(word));
    }

    void processWordsFromFiles() {
        filePaths.stream().parallel().map((String filePath) -> getWordsFromOneFile(filePath))
                .forEach((List<String> words) -> processWordsFromOneFile(words));
    }

    void presentCounters() {
        System.out.println("Counters:");
        wordCounters.forEach((String key, Integer value) -> System.out.println(key + " = " + value));
    }

    public static void main(String[] args) {
        Task3 program = new Task3();
        program.initiateFilePaths();
        program.processWordsFromFiles();
        program.presentCounters();
    }
}