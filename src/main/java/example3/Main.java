/* MORPHOLOGICAL SEGMENTATION USING UNFILTERED BACKWARD TRIES  */
package example3;

import core.Inference;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import reverseTrie.ReverseTrieToFiles;
import java.io.File;
import java.io.IOException;

/**
 * Created by Murathan on 04-Mar-17.
 * Editted by Diala on 10-May-17.
 */
public class Main {

    // Method expects the directory where tries stored and vector file for word2vec
    private void learn(String trieDir, String corpusFile, String vectorFile, int noOfIteration) throws IOException, ClassNotFoundException {
    	
    	ReverseTrieToFiles preOperator = new ReverseTrieToFiles(vectorFile);
        
		preOperator.run(trieDir, "maps");
		System.out.println("========= Pre-Operations are finished. Learning is started =========");

        Inference inference = new Inference("maps",corpusFile, 4, noOfIteration, 0.000001, 0.037 ,true ,true, true, false ,3 ,1, 0.1 );
        inference.run();
        System.out.println("========= Learning is finished. RESULTS: =========");

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //Vector file for word2vec
        String VECTOR_FILE = args[0];
        
        //Directory where tries will be put in serialized form
        String TRIE_DIR =  args[1];       
        
        String CORPUS_FILE = args[2];
        
        //Word which the trie will be created accordingly
        int NUMBER_OF_ITERATION = Integer.parseInt(args[3]);

        Main m = new Main();
        m.learn(TRIE_DIR,CORPUS_FILE,VECTOR_FILE,NUMBER_OF_ITERATION);
    }
}
