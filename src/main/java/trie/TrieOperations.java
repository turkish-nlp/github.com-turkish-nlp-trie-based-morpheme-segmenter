package trie;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Murathan on 04-Mar-17.
 */
public class TrieOperations {

    public static void serializeToFile(TrieST st, String word, String dir) throws IOException {
        // toByteArray
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes = null;
        out = new ObjectOutputStream(bos);
        out.writeObject(st);
        yourBytes = bos.toByteArray();

        bos.close();
        out.close();

        FileUtils.writeByteArrayToFile(new File(dir + File.separator + word), yourBytes);
    }

    public static ArrayList<TrieST> deSerialize(String dir) throws IOException, ClassNotFoundException {

        ArrayList<TrieST> tries = new ArrayList<>();

        File[] files = new File(dir + File.separator).listFiles();

        for (File f : files) {
            if(f.getName().contains("DS_"))
                continue;

            FileInputStream fis = new FileInputStream(f);
            ObjectInput in = null;
            Object o = null;
            in = new ObjectInputStream(fis);
            o = in.readObject();
            fis.close();
            in.close();

            TrieST trie = (TrieST) o;
            tries.add(trie);
        }

        return tries;
    }

}
