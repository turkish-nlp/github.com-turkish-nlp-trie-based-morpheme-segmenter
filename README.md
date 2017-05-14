# Trie-Based-Morpheme-Segmenter

Java project for the morpheme segmentation which is one of the most common NLP tasks, especially for agglutinative languages.

Implemented model combines information obtained from three sources, namely forward-tries (prefix trees), backward-tries (suffix trees) and "word2vec".

In order to build forward-tries, code block in the /src/main/java/example/TrieBuilder can be used as follows:

        ForwardTrieBuilder tb = new ForwardTrieBuilder();

        //Directory where tries will be put in serialized form
        String OUTPUT_DIR = "forward-tries";

        //Vector file for word2vec
        String VECTOR_FILE = "vector.txt";

        //Load the vector file
        WordVectors vectors = WordVectorSerializer.loadTxtVectors(new File(VECTOR_FILE));
        System.out.println("========= Vector file is loaded =========");

        //Word which the trie will be created accordingly
        String INPUT_WORD_FILENAME = "corpus/test.txt";

        String INPUT_WORD;
        BufferedReader reader = new BufferedReader(new FileReader(INPUT_WORD_FILENAME));
        while((INPUT_WORD = reader.readLine()) != null){
            //Get the closest X word to INPUT_WORD
            Collection<String> word2vecNeighbours = vectors.wordsNearest(INPUT_WORD, 20);
            TrieST trie = tb.buildTrie(word2vecNeighbours);
            TrieOperations.serializeToFile(trie, INPUT_WORD, OUTPUT_DIR);
        }

        System.out.println("========= Tries is built =========");

        //Use tries by deserializing them (below it prints the words which start with the INPUT_WORD)
        ArrayList<TrieST> tries = TrieOperations.deSerialize(OUTPUT_DIR);
        for (TrieST t : tries) {
            Iterator ite = t.keysWithPrefix("").iterator();

            while (ite.hasNext()) {
                String next = (String) ite.next();
                System.out.println(next);
            }
        }
    }

In order to build backward-tries, code block in the /src/main/java/example/BackwardTrieBuilder can be used as follows:

        TrieBuilder tb = new TrieBuilder();

        //Directory where tries will be put in serialized form
        String OUTPUT_DIR = "backward-tries";

        //Vector file for word2vec
        String VECTOR_FILE = "vectors.txt";

        BackwardTrieBuilder tb = new BackwardTrieBuilder();
        //Load the vector file
        WordVectors vectors = WordVectorSerializer.loadTxtVectors(new File(VECTOR_FILE));
        System.out.println("========= Vector file is loaded =========");

        //Word which the trie will be created accordingly
        String INPUT_WORD_FILENAME = "corpus/test.txt";

        String INPUT_WORD;
        BufferedReader reader = new BufferedReader(new FileReader(INPUT_WORD_FILENAME));
        while((INPUT_WORD = reader.readLine()) != null){
            //Get the closest X word to INPUT_WORD
            Collection<String> word2vecNeighbours = vectors.wordsNearest(INPUT_WORD, 20);
            ReverseTrie trie = tb.buildTrie(word2vecNeighbours);
            ReverseTrieOperations.serializeToFile(trie, INPUT_WORD, OUTPUT_DIR);
        }

        System.out.println("========= Tries is built =========");

        ReverseTrieOperations.serializeToFile(trie, INPUT_WORD, OUTPUT_DIR);

        //Use tries by deserializing them (below it prints the words which start with the INPUT_WORD)
        ArrayList<ReverseTrie> tries = ReverseTrieOperations.deSerialize(OUTPUT_DIR);
        for (ReverseTrie t : tries) {
            Iterator ite = t.keysWithPostfix(INPUT_WORD).iterator();

            while (ite.hasNext()) {
                String next = (String) ite.next();
                System.out.println(next);
            }
        }

In order to run learning process with created tries, code block in the /src/main/java/example/Main can be used as follows:

        //Directory where forward-tries will be put in serialized form
        String FORWARD_TRIE_DIR = "forward-tries";

        //Directory where backward-tries will be put in serialized form
        String BACKWARD_TRIE_DIR = "backward-tries";

        //Vector file for word2vec
        String VECTOR_FILE = "vector.txt";

        //Word which the trie will be created accordingly
        int NUMBER_OF_ITERATION = 40;

        Main m = new Main();
        m.learn(FORWARD_TRIE_DIR, BACKWARD_TRIE_DIR, VECTOR_FILE, NUMBER_OF_ITERATION);


