package example;

import core.Inference;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import trie.TrieToFiles;

import java.io.File;
import java.io.IOException;

/**
 * Created by Murathan on 04-Mar-17.
 */
public class Main {

    // Method expects the directory where tries stored and vector file for word2vec
    private void learn(String trieDir, String vectorFile, int noOfIteration) throws IOException, ClassNotFoundException {


        TrieToFiles preOperator = new TrieToFiles(vectorFile);
        preOperator.run(trieDir, "maps");
        System.out.println("========= Pre-Operations are finished. Learning is started =========");


        Inference inference = new Inference("maps","corpus/wordlist-2010.tur.txt", 4, noOfIteration, 0.000001, 0.037 ,true ,true, true, false ,3 ,1, 0.1 );
        inference.run();
        System.out.println("========= Learning is finished. RESULTS: =========");

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //Directory where tries will be put in serialized form
        String TRIE_DIR = "output";

        //Vector file for word2vec
        String VECTOR_FILE = "C:\\Users\\Murathan\\github\\vectors.txt";

        //Word which the trie will be created accordingly
        int NUMBER_OF_ITERATION = 40;

        Main m = new Main();
        m.learn(TRIE_DIR,VECTOR_FILE,NUMBER_OF_ITERATION);
    }
}
