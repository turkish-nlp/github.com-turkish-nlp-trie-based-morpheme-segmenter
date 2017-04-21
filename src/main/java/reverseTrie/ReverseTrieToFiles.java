package reverseTrie;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.eclipse.jetty.util.ConcurrentHashSet;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Mete Han Kahraman
 */
public class ReverseTrieToFiles {

    private List<ReverseTrie> trieList = new ArrayList<>();
    private List<String> searchedWordList = new ArrayList<>();
    private HashSet<String> similarityKeys = new HashSet<>();
    private static WordVectors vectors;

    public ConcurrentHashMap<String, Double> concurrentSimilarityScores = new ConcurrentHashMap<>();


    public HashMap<String, Double> similarityScoresToSerialize = new HashMap<>();
    public HashMap<String, HashMap<String, Integer>> branchFactors = new HashMap<>();
    public HashMap<String, TreeSet<String>> trieWords = new HashMap<>();

    public ReverseTrieToFiles(String vectorDir) throws IOException, ClassNotFoundException {
        this.vectors = WordVectorSerializer.loadTxtVectors(new File(vectorDir));
    }

    public void run(String dir, String outputDir) throws IOException, ClassNotFoundException {
        generateTrieList(dir);
        serialize(outputDir, similarityScoresToSerialize, "similarityScoresToSerialize");
        serialize(outputDir, trieWords, "trieWords");
        serialize(outputDir, branchFactors, "branchFactors");
    }

/*    public static void main(String[] args) throws IOException, ClassNotFoundException {
        TrieToFiles ttf = new TrieToFiles(args[0], args[1], args[2]);
    }*/

    public void generateTrieList(String dir) throws IOException, ClassNotFoundException {

        File[] files = new File(dir + File.separator).listFiles();

        for (File f : files) {
            FileInputStream fis = new FileInputStream(f);
            ObjectInput in = null;
            Object o = null;
            in = new ObjectInputStream(fis);
            o = in.readObject();
            fis.close();
            in.close();

            ReverseTrie trie = (ReverseTrie) o;
            trieList.add(trie);
            searchedWordList.add(f.getName());
        }

        fillBranchFactorMap();
        fillSimilarityMap();
        System.out.println();
    }

    private void fillSimilarityMap() {

        ArrayList<Set<String>> setsForParallel = new ArrayList<>();
        int count = 0;
        HashSet<String> tmp = new HashSet<>();
        for (String w : similarityKeys) {
            if (count < 2000) {
                tmp.add(w);
                count++;
            }
            if (count == 2000) {
                count = 0;
                setsForParallel.add(tmp);
                tmp = new HashSet<>();
            }
        }
        setsForParallel.add(tmp);

        ConcurrentHashSet<String> tokens = new ConcurrentHashSet<>();

        for (Set<String> similarityKeys : setsForParallel) {
            similarityKeys.parallelStream().forEach((word) -> {
                for (int i = word.length(); i > 1; i--) {
                    tokens.add(word.substring(0, i));
                }
            });
        }

        setsForParallel = new ArrayList<>();
        count = 0;
        tmp = new HashSet<>();
        for (String w : tokens) {
            if (count < 2000) {
                tmp.add(w);
                count++;
            }
            if (count == 2000) {
                count = 0;
                setsForParallel.add(tmp);
                tmp = new HashSet<>();
            }
        }
        setsForParallel.add(tmp);

        for (Set<String> similarityKeys : setsForParallel) {
            similarityKeys.parallelStream().forEach((word) -> {
                for (int i = 1; i < word.length(); i++) {
                    String key = word.substring(0, i);
                    String keyCopy = word.substring(0, i);
                    keyCopy = keyCopy + "+" + word.substring(i);
                    double cosine = vectors.similarity(key, word);
                    if (!vectors.hasWord(key) || !vectors.hasWord(word) || cosine < 0 || cosine > 1) {
                        cosine = -1;
                    }
                    concurrentSimilarityScores.put(keyCopy, cosine);
                }
            });
        }

        for (String str : concurrentSimilarityScores.keySet()) {
            similarityScoresToSerialize.put(str, concurrentSimilarityScores.get(str));
        }


    }

    private void fillBranchFactorMap() {

        for (ReverseTrie trie : trieList) {
            for (String word : trie.getWordList().keySet()) {
                if (!word.endsWith("$")) {
                    if (branchFactors.containsKey(searchedWordList.get(trieList.indexOf(trie)))) {
                        branchFactors.get(searchedWordList.get(trieList.indexOf(trie))).put(word, trie.getWordList().get(word));
                    } else {
                        HashMap<String, Integer> branches = new HashMap<>();
                        branches.put(word, trie.getWordList().get(word));
                        branchFactors.put(searchedWordList.get(trieList.indexOf(trie)), branches);
                    }
                } else {

                    similarityKeys.add(word.substring(0, word.length() - 1));

                    if (trieWords.containsKey(searchedWordList.get(trieList.indexOf(trie)))) {
                        trieWords.get(searchedWordList.get(trieList.indexOf(trie))).add(word);
                    } else {
                        TreeSet<String> words = new TreeSet<>();
                        words.add(word);
                        trieWords.put(searchedWordList.get(trieList.indexOf(trie)), words);
                    }
                }
            }
        }
    }

    private void serialize(String dir, Map toBeSerialized, String fileName) throws IOException {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        // toByteArray
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes = null;
        out = new ObjectOutputStream(bos);
        out.writeObject(toBeSerialized);
        yourBytes = bos.toByteArray();

        bos.close();
        out.close();

        FileUtils.writeByteArrayToFile(new File(dir + "/" + fileName), yourBytes);
    }

}
