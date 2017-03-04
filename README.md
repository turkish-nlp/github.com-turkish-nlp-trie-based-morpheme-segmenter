# Trie-Based-Morpheme-Segmenter

Java project for the morpheme segmentation which is one of the most common NLP tasks, especially for agglutinative languages.

Implemented model combines information obtained from two sources, namely tries (prefix trees) and "word2vec".

In order to build tries, code block in the /src/main/java/example/TrieBuilder can be used as follows:

        TrieBuilder tb = new TrieBuilder();

        //Directory where tries will be put in serialized form
        String OUTPUT_DIR = "output";

        //Vector file for word2vec
        String VECTOR_FILE = "vectors.txt";

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


In order to run learning process with created tries, code block in the /src/main/java/example/Main can be used as follows:

        //Directory where tries will be put in serialized form
        String TRIE_DIR = "output";

        //Vector file for word2vec
        String VECTOR_FILE = "vectors.txt";

        //Word which the trie will be created accordingly
        int NUMBER_OF_ITERATION = 40;

        Main m = new Main();
        m.learn(TRIE_DIR,VECTOR_FILE,NUMBER_OF_ITERATION);


