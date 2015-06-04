package edu.oregonstate.ai.hearthstone;

import java.io.*;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.stream.Collectors;

/**
 * Created by Eiii on 6/4/2015.
 */
public class ClusterManager {
    public static void main(String args[]) {
        int count = Integer.parseInt(args[0]);
        String as = Arrays.stream(args, 1, args.length).collect(Collectors.joining(" "));
        ClusterManager manager = new ClusterManager(count, as);
        manager.scatter();
        manager.gather();
    }

    public static String makeFilename(int id) {
        return String.format("results/%d.result", id);
    }

    private final boolean useCluster = true;
    private String args;
    private int count;

    public ClusterManager(int count, String args) {
        this.args = args;
        this.count = count;
    }

    public void scatter() {
        for (int i = 0; i < count; i++) {
            String fname = makeFilename(i);
            if (new File(fname).isFile()) continue;

            String al = String.valueOf(i) + " " + args;
            if (useCluster) {
                System.out.println(String.format("Starting worker %d (\"%s\")", i, al));
                try {
                    String cmd[] = {"./make_worker.sh", String.valueOf(i), al};
                    System.out.println(String.format("\t%s %s %s",cmd[0],cmd[1],cmd[2]));
                    Process p1 = Runtime.getRuntime().exec(cmd);
                    p1.waitFor();
                    Process p2 = Runtime.getRuntime().exec(String.format("qsub ./workers/run_worker_%d.sh", i));
                    p2.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            if (results.size() < count) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Result finalResult = results.get(0);
        for (int i = 1; i < count; i++) {
            finalResult.addResult(results.get(i));
        }
        finalResult.printResult();
        try {
            FileOutputStream fileOut = new FileOutputStream("final.result");
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(finalResult);
            objOut.close();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();;
        }


    }
}
