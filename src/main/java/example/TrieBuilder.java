package example;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
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

/**
 * Created by Murathan on 04-Mar-17.
 */
public class TrieBuilder {

    private TrieST buildTrie(Collection<String> wordList) {
        TrieST st = new TrieST();

        for (String str : wordList) {
            st.put(str + "$");
        }
        return st;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        TrieBuilder tb = new TrieBuilder();

        //Directory where tries will be put in serialized form
        String OUTPUT_DIR = "output";

        //Vector file for word2vec
        String VECTOR_FILE = "C:\\Users\\Murathan\\github\\vectors.txt";

        //Load the vector file
        WordVectors vectors = WordVectorSerializer.loadTxtVectors(new File(VECTOR_FILE));
        System.out.println("========= Vector file is loaded =========");

        //Word which the trie will be created accordingly
        String INPUT_WORD = "lise";

        //Get the closest 20 word to INPUT_WORD
        Collection<String> word2vecNeighbours = vectors.wordsNearest(INPUT_WORD, 20);
        TrieST trie = tb.buildTrie(word2vecNeighbours);
        System.out.println("========= Trie is built =========");

        TrieOperations.serializeToFile(trie, INPUT_WORD, OUTPUT_DIR);

        //Use tries by deserializing them (below it prints the words which start with the INPUT_WORD)
        ArrayList<TrieST> tries = TrieOperations.deSerialize(OUTPUT_DIR);
        for (TrieST t : tries) {
            Iterator ite = t.keysWithPrefix(INPUT_WORD).iterator();

            while (ite.hasNext()) {
                String next = (String) ite.next();
                System.out.println(next);
            }
        }
    }
}