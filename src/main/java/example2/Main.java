/* MORPHOLOGICAL SEGMENTATION USING FILTERED FORWARD TRIES  */
package example2;
import core.Inference;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import trie.TrieToFiles;
import java.io.File;
import java.io.IOException;

/**
 * 
 * Editted by Diyala on 09-May-17.
 * Created by Murathan on 04-Mar-17.
 */
public class Main {

    // Method expects the directory where tries stored and vector file for word2vec
    private void learn(String trieDir, String corpusFile, String vectorFile, int noOfIteration) throws IOException, ClassNotFoundException {
    	TrieToFiles preOperator = new TrieToFiles(vectorFile);
		preOperator.run(trieDir, "maps");
        
		System.out.println("========= Pre-Operations are finished. Learning is started =========");

        Inference inference = new Inference("maps",corpusFile, 4, noOfIteration, 0.000001, 0.037 ,true ,true, true, false ,3 ,1, 0.1 );
        inference.run();
        System.out.println("========= Learning is finished. RESULTS: =========");

    }

	/*
	* Arguments: Vector_file_path, Trie_dir, corpus_file, number_of_iteration
	*/
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //Vector file for word2vec
        String VECTOR_FILE = args[0];
        
        //Directory where tries will be put in serialized form
        String TRIE_DIR =  args[1];

        String CORPUS_FILE = args[2];
        int NUMBER_OF_ITERATION = Integer.parseInt(args[3]);

        Main m = new Main();
        m.learn(TRIE_DIR,CORPUS_FILE,VECTOR_FILE,NUMBER_OF_ITERATION);
    }
}
