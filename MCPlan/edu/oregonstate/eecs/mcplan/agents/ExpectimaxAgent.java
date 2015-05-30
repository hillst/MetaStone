package edu.oregonstate.eecs.mcplan.agents;

import java.util.List;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;

/**
 * The Brute Force Agent explore the entire tree up to a certain depth.
 */
public class ExpectimaxAgent extends Agent {
    /** Max depth of search tree. */
    private int maxDepth_;

    /** Sparse sample size of states. */
    private int sampleSize_;

    /** Number of Monte-Carlo simulations run at leaves of tree. */
    private int numSimulations_;
    
    /** Base policy, which is RandomAgent by default. **/
    private Agent basePolicy_;

    public ExpectimaxAgent(int maxDepth, int sampleSize, int numSimulations, Agent basePi) {
        if (maxDepth < 1 || sampleSize < 1 || numSimulations < 1)
            throw new IllegalArgumentException(
                    "Max depth > 0 : Sample Size > 0 : Number of Simulations > 0");
        name_ = "Expectimax";
        maxDepth_ = maxDepth;
        sampleSize_ = sampleSize;
        numSimulations_ = numSimulations;
        basePolicy_ = basePi;
    }
    
    public ExpectimaxAgent(int maxDepth, int sampleSize, int numSimulations) {
    	this(maxDepth, sampleSize, numSimulations, new RandomAgent());
    }

    @Override
    public <S extends State, A> A selectAction(S state,
            Simulator<S, A> iSimulator) {
    	iSimulator.setState(state);
        List<A> actions = iSimulator.getLegalActions();
        double[][] qValues = new double[actions.size()][iSimulator
                .getNumberOfAgents()];
        for (int i = 0; i < actions.size(); i++) {
            for (int j = 0; j < sampleSize_; j++) {
                Simulator<S, A> clone = iSimulator.copy();
                clone.takeAction(actions.get(i));
                double[] values = sparseSampleTree(clone, maxDepth_ - 1);
                int[] rewards = clone.getRewards();
                for (int k = 0; k < qValues[i].length; k++)
                    qValues[i][k] = rewards[k] + values[k];
            }
            for (int j = 0; j < qValues[i].length; j++)
                qValues[i][j] /= sampleSize_;
        }
        // Find max qValue
        int best = 0;
        for (int i = 1; i < actions.size(); i++)
            if (qValues[i][iSimulator.getState().getAgentTurn()] > qValues[best][iSimulator
                    .getState().getAgentTurn()])
                best = i;
        return actions.get(best);
    }

    private <S extends State, A> double[] sparseSampleTree(
            Simulator<S, A> iSimulator, int horizon) {
        List<A> actions = iSimulator.getLegalActions();
        if (actions.size() == 0) { // if terminal state return reward
            int[] rewards = iSimulator.getRewards();
            double[] values = new double[iSimulator.getNumberOfAgents()];
            for (int i = 0; i < values.length; i++)
                values[i] = rewards[i];
            return values;
        }
        double[][] qValues = new double[actions.size()][iSimulator
                .getNumberOfAgents()];
        for (int i = 0; i < actions.size(); i++) { // i = action index
            if (horizon > 0) {
                for (int j = 0; j < sampleSize_; j++) { // j = number of samples
                                                        // of taking action i
                    Simulator<S, A> clone = iSimulator.copy();
                    clone.takeAction(actions.get(i));
                    double[] values = sparseSampleTree(clone, horizon - 1);
                    int[] rewards = clone.getRewards();
                    for (int k = 0; k < qValues[i].length; k++)
                        qValues[i][k] = rewards[k] + values[k];
                }
                for (int j = 0; j < qValues[i].length; j++)
                    qValues[i][j] /= sampleSize_;
            } else {
                double[] totalRewards = new double[iSimulator
                        .getNumberOfAgents()];
                for (int k = 0; k < numSimulations_; k++) {
                    double[] rewards = simulateGame(iSimulator.copy());
                    for (int l = 0; l < rewards.length; l++)
                        totalRewards[l] += rewards[l];
                }
                for (int k = 0; k < totalRewards.length; k++)
                    totalRewards[k] /= numSimulations_;
                qValues[i] = totalRewards;
            }
        }
        // Find max qValue
        int best = 0;
        for (int i = 1; i < actions.size(); i++)
            if (qValues[i][iSimulator.getState().getAgentTurn()] > qValues[best][iSimulator
                    .getState().getAgentTurn()])
                best = i;
        return qValues[best];
    }

    private <S extends State, A> double[] simulateGame(
            Simulator<S, A> iSimulator) {
        List<A> actions = iSimulator.getLegalActions();
        int[] rewards = iSimulator.getRewards();
        double[] totalRewards = new double[rewards.length];
        for (int i = 0; i < rewards.length; i++)
            totalRewards[i] += rewards[i];
        while (actions.size() > 0) {
        	A action = basePolicy_.selectAction(iSimulator.getState(), iSimulator);
        	iSimulator.takeAction(action);
            for (int i = 0; i < totalRewards.length; i++)
                totalRewards[i] += iSimulator.getRewards()[i];
            actions = iSimulator.getLegalActions();
        }
        return totalRewards;
    }
    
    @Override
    public String toString(){
    	StringBuilder output = new StringBuilder();
        output.append(super.toString());
        output.append("\n  Depth of Simulations:     " + maxDepth_);
        output.append("\n  Sample Size:              " + sampleSize_);
        output.append("\n  Number of Simulations:       " + numSimulations_);
        output.append("\n  Base Policy:           " + basePolicy_);        
        return output.toString();
    }
}
