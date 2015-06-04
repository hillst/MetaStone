package edu.oregonstate.ai.hearthstone;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Dictionary;
import java.io.File;
import java.util.Hashtable;

/**
 * Created by Eiii on 6/4/2015.
 */
public class ClusterManager {
    public static void main(String args[]) {
        ClusterManager manager = new ClusterManager();
        manager.scatter();
        manager.gather();
    }

    public static String makeFilename(int id) {
        return String.format("results/%d.result", id);
    }

    private final boolean useCluster = false;
    private final String args = "ZOO UCT.100.1 ZOO RANDOM 5";
    private final int count = 2;

    public void scatter() {
        for (int i = 0; i < count; i++) {
            String al = String.valueOf(i) + " " + args;
            if (useCluster) {
                throw new RuntimeException();
            } else {
                AgentTest.main(al.split(" "));
            }
        }
    }

    public void gather() {
        System.out.println("Gathering:");
        Dictionary<Integer, Result> results = new Hashtable<>();
        while (results.size() < count) {
            for (int i = 0; i < count; i++) {
                if (results.get(i) != null) continue;
                String fname = makeFilename(i);
                if (new File(fname).isFile()) {
                    try {
                        FileInputStream fileIn = new FileInputStream(fname);
                        ObjectInputStream objIn = new ObjectInputStream(fileIn);
                        Result result = (Result)objIn.readObject();
                        objIn.close();
                        fileIn.close();
                        System.out.println(String.format("Got result #%d", i));
                        results.put(i, result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        for (int i = 0; i < count; i++) {
            System.out.println(i + ":");
            results.get(i).printResult();
        }
    }
}
