package reverseTrie;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ReverseTrie implements Serializable{

    private static final int R = 2048;        // extended ASCII
    private static final long serialVersionUID = 5667024450138064887L;

    private Node root;      // root of trie

    private int N;          // number of keys in trie
    private transient AtomicInteger atom = new AtomicInteger();
    private Map<String, Integer> wordList;

    public Map<String, Integer> getWordList() {
        return wordList;
    }

    // R-way trie node
    private static class Node implements Serializable {

        private Object val;
        private Node[] next = new Node[R];
        private ArrayList<Integer> used = new ArrayList<Integer>();
    }

    /**
     * Initializes an empty string symbol table.
     */
    public ReverseTrie() {
        wordList = new TreeMap<>();
    }
    public ReverseTrie(Map<String, Integer> wordList) {
        this.wordList = new TreeMap<>(wordList);
    }

    public ReverseTrie cloneTrie()
    {
        return new ReverseTrie(this.wordList);
    }
    /**
     * Returns the Integer associated with the given key.
     *
     * @param key the key
     * @return the Integer associated with the given key if the key is in the symbol table and <tt>null</tt> if the key is not in the symbol table
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public Integer get(String key) {
    	key = reverseString(key);
        Node x = get(root, key, 0);
        if (x == null) {
            return null;
        }
        return (Integer) x.val;
    }

    /**
     * Does this symbol table contain the given key?
     *
     * @param key the key
     * @return <tt>true</tt> if this symbol table contains <tt>key</tt> and
     * <tt>false</tt> otherwise
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public boolean contains(String key) {
        return get(key) != null;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) {
            return null;
        }
        if (d == key.length()) {
            return x;
        }
        char c = key.charAt(d);
        return get(x.next[c], key, d + 1);
    }

    /**
     * Inserts the key-Integer pair into the symbol table, overwriting the old Integer with the new Integer if the key is already in the symbol table. If the Integer is <tt>null</tt>,
     * this effectively deletes the key from the symbol table.
     *
     * @param key the key
     // * @param val the Integer
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public boolean put(String key) {
    	key = reverseString(key);
        Integer val = (Integer) atom.getAndIncrement();
        if (val == null) {
            deleteReversed(key);
        } else {
            StringBuilder sb = new StringBuilder();
            root = put(root, key, val, 0, sb);
        }
        return this.contains(key);
    }

    private Node put(Node x, String key, Integer val, int d, StringBuilder stringBuilder) {
        if (x == null) {
            x = new Node();
        }
        if (d == key.length()) {
            if (x.val == null) {
                N++;
            }
            stringBuilder.append(key.charAt(d-1));
            wordList.put(stringBuilder.toString(), x.used.size());
            x.val = val;
            return x;
        }
        char c = key.charAt(d);

        if (!(d == 0)) {
            stringBuilder.append(key.charAt(d-1));
            if (!x.used.contains((int) c))
                x.used.add((int) c);
            wordList.put(stringBuilder.toString(), x.used.size());
        }
        x.next[c] = put(x.next[c], key, val, d + 1, stringBuilder);

        return x;
    }

    /**
     * Returns the number of key-Integer pairs in this symbol table.
     *
     * @return the number of key-Integer pairs in this symbol table
     */
    public int size() {
        return N;
    }

    /**
     * Is this symbol table empty?
     *
     * @return <tt>true</tt> if this symbol table is empty and <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns all keys in the symbol table as an <tt>Iterable</tt>. To iterate over all of the keys in the symbol table named <tt>st</tt>, use the foreach notation: <tt>for
     * (Key key : st.keys())</tt>.
     *
     * @return all keys in the sybol table as an <tt>Iterable</tt>
     */
    public Iterable<String> keys() {
        return keysWithPostfix("");
    }
    private String reverseString (String s){
    	return new StringBuilder(s).reverse().toString();
    }
    /**
     * Returns all of the keys in the set that ends with <tt>postfix</tt>.
     *
     * @param prefix the prefix
     * @return all of the keys in the set that ends with <tt>postfix</tt>, as an iterable
     */
    public Iterable<String> keysWithPostfix(String postfix) {
    	postfix = reverseString(postfix);
        Queue<String> results = new LinkedList<>();
        Node x = get(root, postfix, 0);
        collect(x, new StringBuilder(postfix), results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, Queue<String> results) {
        if (x == null) {
            return;
        }
        if (x.val != null) {
            results.add(prefix.toString());
        }
        for (char c = 0; c < R; c++) {
            prefix.append(c);
            collect(x.next[c], prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * Returns all of the keys in the symbol table that match <tt>pattern</tt>, where . symbol is treated as a wildcard character.
     *
     * @param pattern the pattern
     * @return all of the keys in the symbol table that match <tt>pattern</tt>, as an iterable, where . is treated as a wildcard character.
     */
    public Iterable<String> keysThatMatch(String pattern) {
        Queue<String> results = new LinkedList<String>();
        collect(root, new StringBuilder(), pattern, results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, String pattern, Queue<String> results) {
        if (x == null) {
            return;
        }
        int d = prefix.length();
        if (d == pattern.length() && x.val != null) {
            results.add(prefix.toString());
        }
        if (d == pattern.length()) {
            return;
        }
        char c = pattern.charAt(d);
        if (c == '.') {
            for (char ch = 0; ch < R; ch++) {
                prefix.append(ch);
                collect(x.next[ch], prefix, pattern, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        } else {
            prefix.append(c);
            collect(x.next[c], prefix, pattern, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * Returns the string in the symbol table that is the longest prefix of <tt>query</tt>, or <tt>null</tt>, if no such string.
     *
     * @param query the query string
     * @return the string in the symbol table that is the longest prefix of <tt>query</tt>, or <tt>null</tt> if no such string
     * @throws NullPointerException if <tt>query</tt> is <tt>null</tt>
     */
    public String longestPostfixOf(String query) {
    	query = reverseString(query);
        int length = longestPostfixOf(root, query, 0, -1);
        if (length == -1) {
            return null;
        } else {
            return query.substring(0, length);
        }
    }

    // returns the length of the longest string key in the subtrie
    // rooted at x that is a prefix of the query string,
    // assuming the first d character match and we have already
    // found a prefix match of given length (-1 if no such match)
    private int longestPostfixOf(Node x, String query, int d, int length) {
        if (x == null) {
            return length;
        }
        if (x.val != null) {
            length = d;
        }
        if (d == query.length()) {
            return length;
        }
        char c = query.charAt(d);
        return longestPostfixOf(x.next[c], query, d + 1, length);
    }

    /**
     * Removes the key from the set if the key is present.
     *
     * @param key the key
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public void delete(String key) {
    	key = reverseString(key);
        root = delete(root, key, 0);
    }
    private void deleteReversed(String key){
    	root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d) {
        if (x == null) {
            return null;
        }
        if (d == key.length()) {
            if (x.val != null) {
                N--;
            }
            x.val = null;
        } else {
            char c = key.charAt(d);
            x.next[c] = delete(x.next[c], key, d + 1);
        }

        // remove subtrie rooted at x if it is completely empty
        if (x.val != null) {
            return x;
        }
        for (int c = 0; c < R; c++) {
            if (x.next[c] != null) {
                return x;
            }
        }
        return null;
    }
    public static void main(String[] args) {

        String line = "geldi gel$ gelirken gelir gelmeli gelince gelz";
        StringTokenizer stz = new StringTokenizer(line, " ");

        ReverseTrie st = new ReverseTrie();
        int i = 0;
        while (stz.hasMoreTokens()) {
            String key = stz.nextToken();
            st.put(key);
            i++;
        }
        // print results
        if (st.size() < 100) {
            System.out.println("keys(\"\"):");
            for (String key : st.keys()) {
                System.out.println(key + " " + st.get(key));
            }
            System.out.println();
        }

        System.out.println(st.wordList.toString());

        System.out.println(st.contains("geld"));
    }
   

    /*didnt edit this function.
    static private int noOfMostChildren(Node x)
    {
        int max = 0;
        for(int j=0; j< x.used.size() ;j++)
        {
            int tmp = 0;
            if(x.next[x.used.get(j)] != null)
                tmp = x.next[x.used.get(j)].size;
            if(tmp > max)
                max = tmp;
        }
        return max;
    }
    */
}
