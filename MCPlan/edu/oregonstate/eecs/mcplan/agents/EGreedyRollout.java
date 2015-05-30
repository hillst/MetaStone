package edu.oregonstate.eecs.mcplan.agents;

import java.util.List;
import java.util.Random;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;

/**
 * Use the base policy basePi_ when sampling, 
 * the depth and height of which are controlled by parameters.
 **/
public class EGreedyRollout extends Agent {
	/* The base policy. */
	private Agent basePi_;
	
	private int n_;
	private double e_;
	
	/*if height is -1, simulate until the end of the game. */
	private int height_;
	
	private ActionFilter filter_ = null;
	private Heuristic heuristic_ = null;
	private Random random_;

	public EGreedyRollout(Agent basePolicy, int n, double e, int height){
		name_ = "E-greedy Rollout of " + basePolicy.getName();
		basePi_ = basePolicy;
		n_ = n;
		e_ = e;
		height_ = height;
		random_ = new Random();
	}
	
	public EGreedyRollout(Agent basePolicy, int n, double e, int height, ActionFilter filter, Heuristic heuristic) {
		this(basePolicy, n, e, height);
		this.filter_ = filter;
		this.heuristic_ = heuristic;
	}
	
	@Override
	public <S extends State, A> A selectAction(S state,
			Simulator<S, A> iSimulator) {
		iSimulator.setState(state);
		List<A> actions = iSimulator.getLegalActions();
		if (filter_ != null) {
			actions = filter_.filter(actions, state);
		}
		
		if(actions.size() == 1)
			return actions.get(0);
		
		double[] qValues = this.qEstimates(state, iSimulator);		
		int best = 0;
		for (int i = 1; i < qValues.length; i++){
			if(qValues[i] > qValues[best])
        		best = i;
        }	
        return actions.get(best);		
	}
	
	/**
	 * Compute the Q values of all legal actions. 
	 **/
	public <S extends State, A> double[] qEstimates(S state, Simulator<S, A> iSimulator) {
		iSimulator.setState(state);
		List<A> actions = iSimulator.getLegalActions();
		if (filter_ != null) {
			actions = filter_.filter(actions, state);
		}
		
		double[] qValues = new double[actions.size()];
		int[] nRuns = new int[actions.size()];
		
		for (int i = 0; i < actions.size(); i++) {
			qValues[i] = 0;
			nRuns[i] = 0;
		}
		
		for (int i = 0; i < n_; i++) {
			//Find best q - there are smarter ways to do this
			int best = 0;
			for (int j = 0; j < actions.size(); j++) {
				if (nRuns[i] > 0 && qValues[i]/nRuns[i] > qValues[best]/nRuns[best]) {
					best = i;
				}
			}
			//Select random w/ e probability, best otherwise
			int action_num = best;
			if (random_.nextDouble() < e_) {
				action_num = random_.nextInt(actions.size());
			}
			
			Simulator<S, A> simulator = iSimulator.copy();
			int policyID = simulator.getState().getAgentTurn();
			//System.out.println("sampling " + j);
			simulator.takeAction(actions.get(action_num));
			int reward = simulator.getReward(policyID);
			
			int k = 0; // number of actions taken by basePi; should be smaller than height_
			while (!simulator.isTerminalState()) {
				if (height_ == -1) // make sure that k < height_ if height == -1
					k = -2;
				if (k >= height_)
					break;			// stop sampling if height is reached
				A action = basePi_.selectAction(simulator.getState(), simulator.copy());
				simulator.takeAction(action);
				reward += simulator.getReward(policyID);
				k++;					
			}
			double h = 0;
			if (heuristic_ != null) {
				h = heuristic_.heuristic(simulator.getState());
			}
			
			qValues[action_num] += reward + h;
			nRuns[action_num] += 1;
		}
		
		for (int i = 0; i < actions.size(); i++) {
			qValues[i] /= nRuns[i];
		}
		
		return qValues;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
	    output.append(super.toString());
	    output.append("\n  N:     " + n_);
	    output.append("\n  Height:     " + height_);	        
	    return output.toString();	 
	 }

}
