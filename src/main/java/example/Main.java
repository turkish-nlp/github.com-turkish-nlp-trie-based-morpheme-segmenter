package example;

import core.Inference;

import java.io.IOException;

/**
 * Created by Murathan on 04-Mar-17.
 * Edited by Ahmet on 14-May-17.
 */
public class Main {

    // Method expects the directory where tries stored and vector file for word2vec
    private void learn(String trieDir_f, String trieDir_b, String vectorFile, int noOfIteration) throws IOException, ClassNotFoundException {


        trie.TrieToFiles preOperator_f = new trie.TrieToFiles(vectorFile);
        reverseTrie.ReverseTrieToFiles preOperator_b = new reverseTrie.ReverseTrieToFiles();
        preOperator_f.run(trieDir_f, "maps");
        preOperator_b.run(trieDir_b, "maps");
        System.out.println("========= Pre-Operations are finished. Learning is started =========");


        Inference inference = new Inference("maps","corpus/wordlist-2010.tur.txt", 4, 4, noOfIteration, 0.000001, 0.037 ,true , true, true, true, false,3,1, 0.1 );
        inference.run();
        System.out.println("========= Learning is finished. =========");

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //Directory where forward-tries will be put in serialized form
        String FORWARD_TRIE_DIR = "forward-tries";

        //Directory where backward-tries will be put in serialized form
        String BACKWARD_TRIE_DIR = "backward-tries";

        //Vector file for word2vec
        String VECTOR_FILE = "/Users/ahmet/Desktop/MorphologySoftware/word2vec/turkce";

        //Word which the trie will be created accordingly
        int NUMBER_OF_ITERATION = 1;

        Main m = new Main();
        m.learn(FORWARD_TRIE_DIR, BACKWARD_TRIE_DIR, VECTOR_FILE, NUMBER_OF_ITERATION);
    }
}
