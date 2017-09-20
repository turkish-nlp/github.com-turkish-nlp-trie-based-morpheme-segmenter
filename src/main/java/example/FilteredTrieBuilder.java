/* BUILDING FILTERED FORWARD TRIES  */
package example;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import trie.TrieOperations;
import trie.TrieST;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Murathan on 04-Mar-17.
 * Editted by Diyala Erekat on 10-May-17
 */
public class FilteredTrieBuilder {

	/* Compare the similarity between two words and filter out the dissimilar ones
	*  This is done through two steps:
	*  1. Checking if the first [1-3] letters are equal:
	*  	True; go to step 2
	*  	False; filter out
	*  2. Check if their minimum edit distance value is less or equals to the threshold value
	* 	True; add it to the list
	*   False; filter out
	* */
	public Collection<String> filterOutNonSimilar(String queryWord, Collection<String> nearestWords, int thresh){
        Collection<String> filteredOut = new ArrayList<>();
        int minEditDistance;
		int firstThree = 3;
        int firstXletters =  (firstThree > queryWord.length()) ? queryWord.length() : firstThree;
        for(String word : nearestWords){
			firstXletters = firstXletters > word.length() ?  word.length() : firstXletters;
        	if(queryWord.substring(0, firstXletters).compareToIgnoreCase(word.substring(0, firstXletters))==0 ){
        		minEditDistance = minDistance(queryWord, word);
	            if(minEditDistance <= thresh){
	                filteredOut.add(word);
	            }      		
        	}

        }
        return filteredOut;
    }
    
	/*
		Implements Levenshtein distance algorithm
		which calculates the minimum edit distance value
		https://en.wikipedia.org/wiki/Levenshtein_distance
	*/
	public int minDistance(String word1, String word2) {
		int distance = 0;
		int cost, insertion, deletion, substitution;
		int len1 = word1.length();
		int len2 = word2.length();
		
		if(word1.equalsIgnoreCase(word2)) return distance; //SAME WORD
		
		int[][] distanceMatrix = new int[len1+1][len2+1];
		for (int i = 0; i <= len1; i++) distanceMatrix [i][0] = i;
		for (int j = 0; j <= len2; j++) distanceMatrix [0][j] = j;
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				cost = word1.substring(i, i+1).equalsIgnoreCase(word2.substring(j, j+1)) ? 0 : 1;
				insertion = distanceMatrix[i][j+1]+1;
				deletion = distanceMatrix[i+1][j]+1;
				substitution = distanceMatrix[i][j]+cost;
				distanceMatrix[i+1][j+1] = getMinimum(insertion, deletion, substitution);
				distance = distanceMatrix[i+1][j+1] ;
			}
		}

		return distance;
	}

	public int getMinimum(int insertion, int deletion, int substitutions){
        return (insertion > deletion ) ? 
               ((deletion > substitutions) ? substitutions : deletion) : 
               ((insertion > substitutions) ? substitutions : insertion);
    }
    
    public TrieST buildTrie(Collection<String> wordList) {
    	TrieST st = new TrieST();

        for (String str : wordList) {
            st.put(str+ "$");
        }
        return st;
    }

    /*
    * Building forward tries using filtered Word2Vec words list
	* Arguments: Vector_path, Trie_dir, Input_words, nearest_neighbors_num, threshold_value_minimum edit_distance
	*/
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String VECTOR_FILE = args[0]; //Vector file for word2vec
        String OUTPUT_DIR =  args[1]; //Directory where tries will be put in serialized form
		String OUTPUT_DIR_BACK =  args[2]; //Directory where BACKWARD tries will be put in serialized form

		String INPUT_WORD_FILENAME = args[3]; //Word which the trie will be created accordingly
        int numOfNearestWords =  Integer.parseInt(args[4]); //number of nearest neighbor words from word2vec
        int thresh =  Integer.parseInt(args[5]); //theshold for minimum edit distance
        
        FilteredTrieBuilder tb = new FilteredTrieBuilder();

        //Load the vector file
        WordVectors vectors = WordVectorSerializer.loadTxtVectors(new File(VECTOR_FILE));
        System.out.println("=========Vector file is loaded=========");
        
        String line;
		int c = 0;
        BufferedReader reader = new BufferedReader(new FileReader(INPUT_WORD_FILENAME));
       while((line = reader.readLine()) != null) {
           //Get the closest X word to INPUT_WORD
           String INPUT_WORD = line.split(" ")[1];
           if (vectors.hasWord(INPUT_WORD)) {
               System.out.println(++c);
               Collection<String> word2vecNeighbours = vectors.wordsNearest(INPUT_WORD, numOfNearestWords);
               //Filter out the noisy words
               Collection<String> filteredWord2vecNeighbours = tb.filterOutNonSimilar(INPUT_WORD, word2vecNeighbours, thresh);

               Collection<String> reverseWord2vecNeighbours = new ArrayList<>();

               for (String str : filteredWord2vecNeighbours) {
                   reverseWord2vecNeighbours.add(new StringBuilder(str).reverse().toString());
               }
               // Build the tries with the filtered words
               TrieST trie = tb.buildTrie(filteredWord2vecNeighbours);
               TrieOperations.serializeToFile(trie, INPUT_WORD, OUTPUT_DIR);

               TrieST backwardTrie = tb.buildTrie(reverseWord2vecNeighbours);
               TrieOperations.serializeToFile(backwardTrie, INPUT_WORD, OUTPUT_DIR_BACK);
           }
       }
        //Use tries by deserializing them (below it prints the words which start with the INPUT_WORD)
        System.out.println("========= Trie is built =========");
/*	 ArrayList<TrieST> tries = TrieOperations.deSerialize(OUTPUT_DIR);
       for (TrieST t : tries) {
		   Iterator ite = t.keysWithPrefix("").iterator();
            while (ite.hasNext()) {
                String next = (String) ite.next();
                System.out.println(next);
            }
        }

		ArrayList<TrieST> backWardTries = TrieOperations.deSerialize(OUTPUT_DIR_BACK);
		for (TrieST t : backWardTries) {
			Iterator ite = t.keysWithPrefix("").iterator();
			while (ite.hasNext()) {
				String next = (String) ite.next();
				System.out.println(next);
			}
		}*/
    }
}
