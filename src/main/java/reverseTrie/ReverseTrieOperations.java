package reverseTrie;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Murathan on 04-Mar-17.
 */
public class ReverseTrieOperations {

    public static void serializeToFile(ReverseTrie st, String word, String dir) throws IOException {
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

    public static ArrayList<ReverseTrie> deSerialize(String dir) throws IOException, ClassNotFoundException {

        ArrayList<ReverseTrie> tries = new ArrayList<>();

        File[] files = new File(dir + File.separator).listFiles();

        for (File f : files) {

            FileInputStream fis = new FileInputStream(f);
            ObjectInput in = null;
            Object o = null;
            in = new ObjectInputStream(fis);
            o = in.readObject();
            fis.close();
            in.close();

            ReverseTrie trie = (ReverseTrie) o;
            tries.add(trie);
        }

        return tries;
    }

}
