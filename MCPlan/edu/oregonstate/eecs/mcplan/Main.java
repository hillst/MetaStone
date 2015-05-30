package edu.oregonstate.eecs.mcplan;

import edu.oregonstate.eecs.mcplan.agents.*;
import edu.oregonstate.eecs.mcplan.domains.RCW.RCWsimulator;
import edu.oregonstate.eecs.mcplan.domains.backgammon.BackgammonSimulator;
import edu.oregonstate.eecs.mcplan.domains.biniax.BiniaxSimulator;
import edu.oregonstate.eecs.mcplan.domains.connect4.Connect4Simulator;
import edu.oregonstate.eecs.mcplan.domains.ewn.EwnSimulator;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconSimulator;
import edu.oregonstate.eecs.mcplan.domains.havannah.HavannahSimulator;
import edu.oregonstate.eecs.mcplan.domains.yahtzee.YahtzeeSimulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to run tests on between agents and simulators.
 */
public class Main {
    /** Indicates the version date of project. */
    private static final String version_ = "2.7.2012";
    
    /**
     * If no arguments are provided then the program runs in interactive mode.
     * Otherwise if a file path argument is provided that file will be read.
     * A second argument provides the name of the output file for results.
     * If no second argument is given then the output file has the same
     * name as the input file with "_results" appended to the end.
     * 
     * @param args
     *            should be of length 0, 1 or 2
     */
    public Main(String[] args) {
        if (args.length == 0) { // Interactive Mode
            Simulator<? extends State, ?> world = selectSimulator();
            List<Agent> agents = selectAgents(world.getNumberOfAgents());
            Arbiter arbiter = new Arbiter(world, agents);
            arbiter.runSimulations(world.copy(), 1);
            //System.out.println(arbiter.getHistory());
            System.out.println(arbiter);
        } else if (args[0].equals("-v") || args[0].equals("-version")) {
            System.out.println(version_);
        } else if (args.length <= 2) { // Read flat text file
            List<String> lines = inputTestFile(args[0]);
            int nTrials = Integer.parseInt(lines.get(0));
            Simulator<? extends State, ?> world = selectSimulator(lines.get(1).split(" "));
            Simulator<? extends State, ?> simulatedWorld = selectSimulator(lines.get(2).split(" "));

            List<List<List<String>>> lists = new ArrayList<List<List<String>>>();
            for (int i = 0; i < world.getNumberOfAgents(); i++)
                lists.add(expandIntervals(lines.get(3 + i).split(" ")));

            List<int[]> values = new ArrayList<int[]>();
            List<String[]> agentArgs = new ArrayList<String[]>();
            int numIterations = 1;
            for (int i = 0; i < lists.size(); i++) {
                for (int j = 0; j < lists.get(i).size(); j++)
                    numIterations *= lists.get(i).get(j).size();
                values.add(new int[lists.get(i).size()]);
                agentArgs.add(new String[lists.get(i).size()]);
            }

            for (int i = 0; i < numIterations; i++) {
                List<Agent> agents = new ArrayList<Agent>();
                for (int j = 0; j < world.getNumberOfAgents(); j++) {
                    for (int k = 0; k < agentArgs.get(j).length; k++)
                        agentArgs.get(j)[k] = lists.get(j).get(k).get(
                                values.get(j)[k]);
                    agents.add(selectAgent(agentArgs.get(j)));
                }
                System.out.println(agents);
                Arbiter arbiter = new Arbiter(world, agents);

                arbiter.runSimulations(simulatedWorld, nTrials);

                // Output format
                // nTrials, numWins, avgRewards, stdRewards, avgMoveTime, stdMoveTime,
                // agent, param1, param2, ... paramN
                DecimalFormat df = new DecimalFormat("#.###");
                StringBuilder output = new StringBuilder();
                output.append("#");
                output.append(nTrials);
                output.append(" - ");
                output.append(world.getClass().getSimpleName());
                output.append(" - ");
                output.append(simulatedWorld.getClass().getSimpleName());
                output.append("\n");
                String temp;
                int buffer = 10;
                for (int j = 0; j < agents.size(); j++) {
                    double[] rewardsData = arbiter.getRewardsData(j);
                    double[] avgMoveTimeData = arbiter.getAvgMoveTimeData(j);
                    int numWins = arbiter.getNumWins(j);
                    temp = df.format(numWins);
                    for (int k = temp.length(); k < buffer; k++)
                        output.append(" ");
                    output.append(temp + ",");
                    temp = df.format(Utility.computeMean(rewardsData));
                    for (int k = temp.length(); k < buffer; k++)
                        output.append(" ");
                    output.append(temp + ",");
                    temp = df.format(Utility
                            .computeStandardDeviation(rewardsData));
                    for (int k = temp.length(); k < buffer; k++)
                        output.append(" ");
                    output.append(temp + ",");
                    temp = df.format(Utility.computeMean(avgMoveTimeData));
                    for (int k = temp.length(); k < buffer; k++)
                        output.append(" ");
                    output.append(temp + ",");
                    temp = df.format(Utility
                            .computeStandardDeviation(avgMoveTimeData));
                    for (int k = temp.length(); k < buffer; k++)
                        output.append(" ");
                    output.append(temp + ",");
                    for (int k = 0; k < agentArgs.get(j).length; k++) {
                        for (int l = agentArgs.get(j)[k].length(); l < buffer; l++)
                            output.append(" ");
                        output.append(agentArgs.get(j)[k]);
                        if (k < agentArgs.get(j).length - 1)
                            output.append(",");
                    }
                    output.append("\n");
                }

                if (args.length == 2)
                    recordResults(args[1], output.toString());
                else
                    recordResults(args[0] + "_results", output.toString());

                // Update values
                for (int j = 0; j < values.size(); j++) {
                    for (int k = 0; k < values.get(j).length; k++) {
                        if (values.get(j)[k] == lists.get(j).get(k).size() - 1) {
                            values.get(j)[k] = 0;
                        } else {
                            values.get(j)[k] += 1;
                            break;
                        }
                    }
                }
                
                System.out.println(arbiter);
            }
        } else
            throw new IllegalArgumentException(
                    "Illegal arguments\nusage: UctProject [test_filepath [results_filepath]]");
    }

    /**
     * Inputs a test file and removes all comments and empty lines.
     * 
     * @param filepath
     * @return
     */
    private List<String> inputTestFile(String filepath) {
        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader input = new BufferedReader(new FileReader(filepath));
            String line = input.readLine();
            while (line != null) {
                line = line.trim();
                if (line.length() > 0 && line.charAt(0) != '#')
                    lines.add(line);
                line = input.readLine();
            }
            input.close();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Filename " + filepath
                    + " invalid.");
        }
        return lines;
    }

    private void recordResults(String filepath, String results) {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(filepath,
                    true));
            output.write(results);
            output.close();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not write to " + filepath);
        }
    }

    private List<List<String>> expandIntervals(String[] args) {
        List<List<String>> list = new ArrayList<List<String>>();
        for (int i = 0; i < args.length; i++) {
            ArrayList<String> temp = new ArrayList<String>();
            if (args[i].charAt(0) == '['
                    && args[i].charAt(args[i].length() - 1) == ']') {
                String[] elements = args[i].substring(1, args[i].length() - 1)
                        .split(",");
                for (int j = 0; j < elements.length; j++) {
                    String[] parts = elements[j].split(":");
                    if (parts.length == 3) {
                        int min = Integer.parseInt(parts[0]);
                        int steps = Integer.parseInt(parts[1]);
                        int max = Integer.parseInt(parts[2]);
                        for (int k = 0; k <= steps; k++)
                            temp.add(String.valueOf(min + k
                                    * ((max - min) / steps)));
                    } else
                        temp.add(elements[j]);
                }
            } else
                temp.add(args[i]);
            list.add(temp);
        }
        return list;
    }

    private Simulator<? extends State, ?> selectSimulator(String[] args) {
        if (args[0].equalsIgnoreCase("Backgammon"))
            return new BackgammonSimulator();
        else if (args[0].equalsIgnoreCase("Biniax"))
            return new BiniaxSimulator();
        else if (args[0].equalsIgnoreCase("Connect4"))
            return new Connect4Simulator(args[1]);
        else if (args[0].equalsIgnoreCase("EWN"))
            return new EwnSimulator();
        else if (args[0].equalsIgnoreCase("Havannah"))
            return new HavannahSimulator();
        else if (args[0].equalsIgnoreCase("Yahtzee"))
            return new YahtzeeSimulator();
        else if (args[0].equalsIgnoreCase("RCW"))
        	return new RCWsimulator(Integer.parseInt(args[1]), args[2], args[3], args[4]);
        else if (args[0].equalsIgnoreCase("Galcon")) {
        	boolean useMonitor = Integer.parseInt(args[3]) == 1;
        	boolean stochastic = Integer.parseInt(args[4]) == 1;
        	return new GalconSimulator(Integer.parseInt(args[1]), Integer.parseInt(args[2]), 
        			useMonitor, stochastic, Long.parseLong(args[5]));
        } else
            throw new IllegalArgumentException("Invalid simulator name: "
                    + args[0]);
    }

    /**
     * Choose a simulator for testing.
     */
    public static Simulator<? extends State, ?> selectSimulator() {
        while (true) {
            System.out.println("-Select Simulator-");
            System.out.println("1 Backgammon");
            System.out.println("2 Biniax");
            System.out.println("3 Connect 4");
            System.out.println("4 Einstein Wurfelt Nicht");
            System.out.println("5 Havannah");
            System.out.println("6 Yahtzee");
            System.out.println("7 RCW");
            System.out.println("8 Galcon");
            System.out.println("9 Galcon Preset");
            switch (getIntegerInput()) {
            case 1:
                return new BackgammonSimulator();
            case 2:
                return new BiniaxSimulator();
            case 3:
            	System.out.println("Select opponent: 1-Random, 2-Heuristic");
            	switch (getIntegerInput()){
            	case 1:
            		return new Connect4Simulator("Random");
            	case 2:
            		return new Connect4Simulator("Heuristic");
            	}                
            case 4:
                return new EwnSimulator();
            case 5:
                return new HavannahSimulator();
            case 6:
                return new YahtzeeSimulator();
            case 7:
            	System.out.print("Total simulation time horizon: ");
                int time = getIntegerInput();    
                System.out.print("Territories file: ");
                String territoriesFile = getInput();
                System.out.print("Parcels file: ");
            	String parcelsFile = getInput();
            	System.out.print("Colonization file: ");
            	String colonizationFile = getInput();
            	
            	return new RCWsimulator(time, "territories-gridmap.csv", 
            			"parcels-gridmap-1.csv", "colonization-gridmap.csv");
                //return new RCWsimulator(time, territoriesFile, parcelsFile, colonizationFile);
            case 8:
            	System.out.print("Max cycles (-1 for no limit): ");
            	int max_cycles = getIntegerInput();
            	System.out.print("Decision epoch: ");
            	int epoch = getIntegerInput();
            	System.out.print("Montor on? (1 for yes, no otherwise): ");
            	boolean monitor = getIntegerInput() == 1 ? true : false;
            	System.out.print("Use stochastic simulation? (1 for yes, no otherwise): ");
            	boolean stochastic = getIntegerInput() == 1 ? true : false;
            	System.out.print("Map seed (0 for random): ");
            	long seed = getIntegerInput();
            	return new GalconSimulator(max_cycles, epoch, monitor, stochastic, seed);
            case 9:
            	return new GalconSimulator(400, 10, true, false, 0);
            }
        }
    }

    public static <S extends State, A> Agent selectAgent(String[] args) {    	
        if (args[0].equals("Random"))
            return new RandomAgent();
        else if (args[0].equals("UCT")) {
        	String[] baseArgs = new String[args.length - 6];
        	for (int i = 0; i < baseArgs.length; i++)
        		baseArgs[i] = args[i + 6];
        	Agent pi = selectAgent(baseArgs);
            return new UctAgent(Integer.parseInt(args[1]), Double
                    .parseDouble(args[2]), Integer.parseInt(args[3]), Integer
                    .parseInt(args[4]), args[5], pi);
        }
        else if (args[0].equals("Expectimax")){
        	String[] baseArgs = new String[args.length - 4];
        	for (int i = 0; i < baseArgs.length; i++)
        		baseArgs[i] = args[i + 4];
        	Agent pi = selectAgent(baseArgs);
            return new ExpectimaxAgent(Integer.parseInt(args[1]), Integer
                    .parseInt(args[2]), Integer.parseInt(args[3]), pi);
        }
    	else if (args[0].equals("PolicyRollout")){
    		String[] baseArgs = new String[args.length - 3];
    		for (int i = 0; i < baseArgs.length; i++)
    			baseArgs[i] = args[i + 3];
    		Agent pi = selectAgent(baseArgs);
    		return new PolicyRollout(pi, Integer.parseInt(args[1]), 
    				Integer.parseInt(args[2]));
    	}
    	else if (args[0].equals("UniformRollout")){
    		String[] baseArgs = new String[args.length - 3];
    		for (int i = 0; i < baseArgs.length; i++)
    			baseArgs[i] = args[i + 3];
    		Agent pi = selectAgent(baseArgs);
    		return new UniformRollout(pi, Integer.parseInt(args[1]), 
    				Integer.parseInt(args[2]));
    	}
    	else if (args[0].equals("eGreedyRollout")){
    		String[] baseArgs = new String[args.length - 3];
    		for (int i = 0; i < baseArgs.length; i++)
    			baseArgs[i] = args[i + 3];
    		Agent pi = selectAgent(baseArgs);
    		return new EGreedyRollout(pi, Integer.parseInt(args[1]), 
    				Double.parseDouble(args[2]), Integer.parseInt(args[3]));
    	}
    	else if (args[0].equals("DesignedSolution")){        	
        	return new DesignedSolution(args[1]);
        }
    	else if (args[0].equals("DumbGalconAgent")) {
    		return new DefensiveGalconAgent();
    	}
    	else if (args[0].equals("DefensiveGalconAgent")) {
    		return new DefensiveGalconAgent();
    	}
    	else if (args[0].equals("ExpansionGalconAgent")) {
    		return new ExpansionGalconAgent();
    	}
    	else if (args[0].equals("AgentJed")) {
    		return new AgentJed();
    	}
    	else if (args[0].equals("NothingGalconAgent")) {
    		return new NothingGalconAgent();
    	}
        throw new IllegalArgumentException("Invalid Agent Selection");
    }

    public static <S extends State, A> List<Agent> selectAgents(int numAgents) {
        List<Agent> agents = new ArrayList<Agent>();
        for (int i = 0; i < numAgents; i++) {
            System.out.println("-Select Agent-");
            System.out.println("1 Human Agent");
            System.out.println("2 Random Agent");
            System.out.println("3 UCT Agent");
            System.out.println("4 Expectimax Agent");
            System.out.println("5 Policy Rollout");
            System.out.println("6 Uniform Rollout");
            System.out.println("7 e-Greedy Rollout");
            System.out.println("8 DesignedSolutions");
            System.out.println("9 Nothing Galcon Agent");
            System.out.println("10 Dumb Galcon Agent");
            System.out.println("11 Expansion Galcon Agent");
            System.out.println("12 Defensive Galcon Agent");
            System.out.println("13 Agent Jed");
            System.out.println("14 Cheating Galcon Agent");
            System.out.println("15 Galcon Filter Test Agent");

            boolean valid = false;
            while (!valid) {
                switch (getIntegerInput()) {
                case 1:
                    agents.add(new HumanAgent());
                    valid = true;
                    break;
                case 2:
                    agents.add(new RandomAgent());
                    valid = true;
                    break;
                case 3:
                    System.out.print("Num Simulations: ");
                    int numSimulations = getIntegerInput();
                    System.out.print("UCTK: ");
                    double uctK = getDoubleInput();
                    System.out.print("Sample Size: ");
                    int sampleSize = getIntegerInput();
                    System.out.print("Ensemble Size: ");
                    int ensembleTrials = getIntegerInput();
                    System.out.println("Select the base policy: \n");
                    List<Agent> pi = selectAgents(1);
                    agents.add(new UctAgent(numSimulations, uctK, sampleSize,
                            ensembleTrials, "ROOT_PARALLELIZATION", pi.get(0)));
                    valid = true;
                    break;
                case 4:
                    System.out.print("Max Depth: ");
                    int maxDepth = getIntegerInput();
                    System.out.print("Sample Size: ");
                    sampleSize = getIntegerInput();
                    System.out.print("Num Simulations: ");
                    numSimulations = getIntegerInput();
                    pi = selectAgents(1);
                    agents.add(new ExpectimaxAgent(maxDepth, sampleSize,
                            numSimulations, pi.get(0)));
                    valid = true;
                    break;
                case 5:
                	System.out.print("Width: ");
                    int width = getIntegerInput();
                    System.out.print("Height (enter -1 if height is infinity): ");
                    int height = getIntegerInput();
                	pi = selectAgents(1);
                	agents.add(new PolicyRollout(pi.get(0), width, height));
                	valid = true;
                	break;
                case 6:
                	System.out.print("N: ");
                    int n = getIntegerInput();
                    System.out.print("Height (enter -1 if height is infinity): ");
                    int height2 = getIntegerInput();
                	pi = selectAgents(1);
                	agents.add(new UniformRollout(pi.get(0), n, height2));
                	valid = true;
                	break;
                case 7:
                	System.out.print("N: ");
                    int n2 = getIntegerInput();
                    System.out.print("e: ");
                    double e = getDoubleInput();
                    System.out.print("Height (enter -1 if height is infinity): ");
                    int height3 = getIntegerInput();
                	pi = selectAgents(1);
                	agents.add(new EGreedyRollout(pi.get(0), n2, e, height3));
                	valid = true;
                	break;
                case 8:
                	agents.add(new DesignedSolution(""));
                	valid = true;
                	break;
                case 9:
                	agents.add(new NothingGalconAgent());
                	valid = true;
                	break;
                case 10:
                	agents.add(new DumbGalconAgent());
                	valid = true;
                	break;
                case 11:
                	agents.add(new ExpansionGalconAgent());
                	valid = true;
                	break;
                case 12:
                	agents.add(new DefensiveGalconAgent());
                	valid = true;
                	break;
                case 13:
                	agents.add(new AgentJed());
                	valid = true;
                	break;
                case 14:
                	agents.add(new CheatGalconAgent());
                	valid = true;
                case 15:
                	agents.add(new GalconFilterAgent());
                	valid = true;
                default:
                    System.out.println("Invalid Selection");
                    break;
                }
            }
        }
        return agents;
    }

    public static String getInput() {
        String input = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            input = in.readLine();
        } catch (IOException e) {
        }
        return input;
    }

    public static int getIntegerInput() {
        while (true) {
            String input = getInput();
            try {
                int selection = Integer.parseInt(input);
                return selection;
            } catch (NumberFormatException exception) {
            }
        }
    }

    public static double getDoubleInput() {
        while (true) {
            String input = getInput();
            try {
                double selection = Double.parseDouble(input);
                return selection;
            } catch (NumberFormatException e) {
            }
        }
    }

    public static void main(String[] args) {
    	String[] arg = new String[2];
    	arg[0] = "input";
    	arg[1] = "result";
        //new Main(arg);
    	new Main(args);
    }
}
