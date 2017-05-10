/* BUILDING UNFILTERED BACKWARD TRIES  */
package example3;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import reverseTrie.ReverseTrieOperations;
import reverseTrie.ReverseTrie;
import trie.TrieOperations;
import trie.TrieST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Murathan on 04-Mar-17.
 * Editted by Diyala Erekat on 9-May-17
 */
public class TrieBuilder {

    private ReverseTrie buildTrie(Collection<String> wordList) {
    	ReverseTrie st = new ReverseTrie();

        for (String str : wordList) {
            st.put("$"+str);
        }
        return st;
    }

    /* Build backward tries with words from word2vec */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String VECTOR_FILE = args[0]; //Vector file for word2vec
        String OUTPUT_DIR =  args[1]; //Directory where tries will be put in serialized form
        String INPUT_WORD_FILENAME = args[2]; //Word which the trie will be created accordingly
        int numOfNearestWords =  Integer.parseInt(args[3]);
        
        TrieBuilder tb = new TrieBuilder();
        //Load the vector file
        WordVectors vectors = WordVectorSerializer.loadTxtVectors(new File(VECTOR_FILE));
        System.out.println("========= Vector file is loaded =========");
        String INPUT_WORD;
        BufferedReader reader = new BufferedReader(new FileReader(INPUT_WORD_FILENAME));
        while((INPUT_WORD = reader.readLine()) != null){
	        //Get the closest X word to INPUT_WORD
	        Collection<String> word2vecNeighbours = vectors.wordsNearest(INPUT_WORD, numOfNearestWords);
			ReverseTrie trie = tb.buildTrie(word2vecNeighbours);
	        ReverseTrieOperations.serializeToFile(trie, INPUT_WORD, OUTPUT_DIR);
        }
		
        //Use tries by deserializing them (below it prints the words which start with the INPUT_WORD)
        System.out.println("========= Trie is built =========");
        ArrayList<ReverseTrie> tries = ReverseTrieOperations.deSerialize(OUTPUT_DIR);
        for (ReverseTrie t : tries) {
           Iterator ite = t.keysWithPostfix("").iterator();
            while (ite.hasNext()) {
                String next = (String) ite.next();
                System.out.println(next);
            }
        }
    }
}
