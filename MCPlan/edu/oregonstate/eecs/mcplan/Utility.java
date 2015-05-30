package edu.oregonstate.eecs.mcplan;

public class Utility {
    public static final double N95 = 1.959963984540;

    public static final double N98 = 2.326347874041;

    public static final double N99 = 2.575829303549;
    
    // Suppress default constructor for noninstantiability
    private Utility() {
        throw new AssertionError();
    }

    public static double computeMean(double[] data) {
        double mean = 0;
        for (double value : data)
            mean += value;
        return mean / data.length;
    }

    public static double computeStandardDeviation(double[] data) {
        double mean = computeMean(data);
        double sum = 0;
        for (double value : data)
            sum += (value - mean) * (value - mean);
        return Math.sqrt(sum / data.length);
    }

    public static double compute99ConfidenceInterval(double standardDeviation,
            int sizeOfDataset) {
        return (standardDeviation / (Math.sqrt(sizeOfDataset))) * N99;
    }
}
