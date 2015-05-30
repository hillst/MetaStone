package edu.oregonstate.eecs.mcplan;

import java.util.ArrayList;
import java.util.List;

/**
 * A simulator controls the state transitions of a given domain. A simulator is
 * associated with a domain specific state and action type. Each simulator
 * contains a reference to a current state and all legal actions and agent
 * reward values associated with that state.
 */
public abstract class Simulator<S extends State, A> {
    /** Current state. */
    protected S state_;

    /** List of legal actions from current state. */
    protected List<A> legalActions_;

    /** List of rewards in current state. */
    protected int[] rewards_;

    /**
     * Create an identical copy of the simulator with all cached legal actions
     * and rewards.
     * 
     * @return copy of the simulator.
     */
    public abstract Simulator<S, A> copy();

    /**
     * Sets the simulator to an initial state.
     */
    public abstract void setInitialState();
    
    /**
     * Sets simulator to an arbitrary state and computes legal actions and
     * rewards.
     * 
     * @param state
     *            any legal state.
     */
    public abstract void setState(S state);

    /**
     * Set simulator to arbitrary state and legal set of actions and computes
     * rewards.
     * 
     * @param state
     *            any legal state.
     * @param legalActions
     *            legal actions from state.
     */
    public abstract void setState(S state, List<A> legalActions);

    /**
     * A simulator can take an action to transition from the current state to a
     * new state.
     * 
     * @param action
     *            the action to be taken.
     * @exception IllegalArgumentException
     *            if action is not legal from the current state.
     */
    public abstract void takeAction(A action);

    /**
     * This method returns a list of legal actions. This method assumes that all
     * actions are immutable objects. Otherwise, it must be overridden.
     * 
     * @return a List of legal actions from current state.
     */
    public List<A> getLegalActions() {
        List<A> legalActions = new ArrayList<A>();
        for (A action : legalActions_)
            legalActions.add(action);
        return legalActions;
    }

    /**
     * Rewards for each agent may be indexed by that agent's id.
     * 
     * @return array of rewards for each agent.
     */
    public int[] getRewards() {
        int[] rewards = new int[getNumberOfAgents()];
        for (int i = 0; i < getNumberOfAgents(); i++)
            rewards[i] = rewards_[i];
        return rewards;
    }

    /**
     * Get reward for the specified agent.
     * 
     * @param agentId
     *            the agent to get reward value.
     * @return the reward in current state of single agent.
     */
    public int getReward(int agentId) {
        return rewards_[agentId];
    }

    /**
     * A state is terminal if there are no legal actions from that state.
     * 
     * @return true if state is terminal.
     */
    public boolean isTerminalState() {
        return legalActions_.size() == 0;
    }

    /**
     * This method assumes that agents take alternating turns.
     * 
     * @param agentTurn
     *            agent turn.
     * @return the agent id for next turn.
     */
    public int getNextAgentTurn(int agentTurn) {
        return (agentTurn + 1) % getNumberOfAgents();
    }

    /**
     * Current state of the simulator.
     * 
     * @return current state.
     */
    public S getState() {
        return state_;
    }

    /**
     * Gets the number of agents taking actions in the given domain.
     * 
     * @return number of agents.
     */
    public abstract int getNumberOfAgents();

    /**
     * Used to generate a vector of features given the current state and an
     * action taken from that state.
     * 
     * @param action
     *            action taken from current state.
     * @return feature vector given current state and action taken.
     */
    public abstract double[] getFeatureVector(A action);

    /**
     * @return string representation of state, legal actions and rewards.
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(state_.toString());
        output.append("\nActions [ ");
        for (A action : legalActions_)
            output.append(action + " ");
        output.append("]\nRewards [ ");
        for (int reward : rewards_)
            output.append(reward + " ");
        output.append("]");
        return output.toString();
    }
}
