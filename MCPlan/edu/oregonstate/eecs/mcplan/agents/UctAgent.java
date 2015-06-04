package edu.oregonstate.eecs.mcplan.agents;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import edu.oregonstate.ai.hearthstone.HearthstoneState;
import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import net.demilich.metastone.game.actions.GameAction;

/**
 * Enhanced UCT agent that can be run as normal UCT or a modified version of UCT
 * based on input parameters.
 */
public final class UctAgent extends Agent {
    /** Small value constant. */
    private static final double EPSILON = 0.000000005;

    /** Number of simulations to run. */
    private int nSimulations_;

    /** UCT Constant. */
    private double uctConstant_;

    /**
     * Maximum number of state samples to inspect from each action.
     * -1 indicates infinite sample size.
     */
    private int sparseSampleSize_;

    /** Number of distinct UCT trees to build and evaluate. */
    private int nEnsembles_;
    
    /**	Base policy, which is RandomAgent by default. **/
    private Agent basePolicy_;

    /** Ensemble method. */
    private EnsembleMethod ensembleMethod_;
    
    private ActionFilter filter_ = null;

/*    *//** Simulation method. *//*
    private SimulationMethod simulationMethod_;*/
    
    /** Method for combining multiple trees. */
    public enum EnsembleMethod {
        ROOT_PARALLELIZATION,
        PLURALITY_VOTE
    }
    
/*    *//** Method for quickly simulating entire game. *//*
    public enum SimulationMethod {
        RANDOM
    }*/

    /**
     * Defines a general node in a UCT tree. The rewards_ field is lazily
     * initialized because its size isn't known until the first rewards vector
     * is passed to update.
     */
    private abstract class Node {
        /** Rewards for each agent. */
        protected int[] rewards_ = null;

        /** Total number of visits to this node. */
        protected int visits_ = 0;

        /**
         * Adds rewards and increments visits.
         * @param rewards for each agent.
         */
        public void update(int[] rewards) {
            if (rewards_ == null)
                rewards_ = new int[rewards.length];
            for (int i = 0; i < rewards.length; i++)
                rewards_[i] += rewards[i];
            visits_++;
        }

        public int getVisits() {
            return visits_;
        }

        /**
         * Get the reward for the specified agent.
         * 
         * @param agentId
         *            agent identifier.
         * @return accumulated reward for specified agent.
         * @throws NullPointerException
         *             if visits_ < 1.
         */
        public int getReward(int agentId) {
            return rewards_[agentId];
        }
    }

    /**
     * Holds a state and a list of pointers to action nodes. The action nodes
     * represent all legal moves from the contained state.
     */
    private class StateNode<S extends State, A> extends Node {
        private S state_;

        private List<ActionNode<S, A>> children_;

        public StateNode(S state, List<A> legalActions) {
            state_ = state;
            children_ = new ArrayList<ActionNode<S, A>>(legalActions.size());
            for (A action : legalActions)
                children_.add(new ActionNode<S, A>(action));
        }

        /**
         * Select child node with best UCT value. Always play a random
         * unexplored action first.
         * 
         * @return an action child node.
         */
        public ActionNode<S, A> uctSelect() {
            assert children_.size() > 0;
            if (visits_ <= children_.size()) {
                List<ActionNode<S, A>> unvisited = new ArrayList<ActionNode<S, A>>();
                for (ActionNode<S, A> child : children_)
                    if (child.getVisits() == 0)
                        unvisited.add(child);
                return unvisited.get((int) (Math.random() * unvisited.size()));
            } else {
                ActionNode<S, A> result = null;
                double bestUct = 0;
                double uctValue;
                for (ActionNode<S, A> child : children_) {
                    uctValue = ((double) child.getReward(state_.getAgentTurn())) / child.getVisits() + 
                            uctConstant_ * Math.sqrt(Math.log(getVisits()) / child.getVisits()) +
                            (Math.random() * EPSILON - EPSILON / 2);
                    if (result == null || uctValue > bestUct) {
                        bestUct = uctValue;
                        result = child;
                    }
                }
                return result;
            }
        }

        public S getState() {
            return state_;
        }

        public List<ActionNode<S, A>> getChildren() {
            return children_;
        }

        public List<A> getLegalActions() {
            List<A> legalActions = new ArrayList<A>();
            for (ActionNode<S, A> child : children_)
                legalActions.add(child.getAction());
            return legalActions;
        }
    }

    private class ActionNode<S extends State, A> extends Node {
        private A action_;

        private List<StateNode<S, A>> frequencyTable_;

        private Hashtable<Integer, StateNode<S, A>> children_;

        public ActionNode(A action) {
            action_ = action;
            frequencyTable_ = null;
            if (sparseSampleSize_ != -1)
                children_ = new Hashtable<Integer, StateNode<S, A>>(
                        sparseSampleSize_);
            else
                children_ = new Hashtable<Integer, StateNode<S, A>>();
        }

        /**
         * Will take an action from the current simulator's state, create a new
         * state node at the next state and return that state node. If sparse
         * sampling limit has been reach then a random node is returned from the
         * current list of children (this is faster).
         * 
         * @param simulator
         *            used to simulate actions.
         * @return selected child state node.
         */
        public StateNode<S, A> selectChild(Simulator<S, A> simulator) {
            if (sparseSampleSize_ == -1 || visits_ < sparseSampleSize_) {
                Simulator<S, A> clone = simulator.copy();
                clone.takeAction(action_);
                S state = clone.getState();
                StateNode<S, A> stateNode = children_.get(state.hashCode());
                if (stateNode == null) {
                    stateNode = new StateNode<S, A>(state, clone
                            .getLegalActions());
                    children_.put(state.hashCode(), stateNode);
                }
                return stateNode;
            } else {
                if (frequencyTable_ == null) {
                    frequencyTable_ = new ArrayList<StateNode<S, A>>();
                    for (StateNode<S, A> stateNode : children_.values())
                        for (int i = 0; i < stateNode.visits_; i++)
                            frequencyTable_.add(stateNode);
                    children_ = null; // Release hash table from memory
                }
                return frequencyTable_
                        .get((int) (Math.random() * frequencyTable_.size()));
            }
        }

        public A getAction() {
            return action_;
        }
    }

    /**
     * Create a traditional UCT agent.
     * 
     * @param nSimulations
     *            the number of complete games to simulate.
     * @param uctConstant
     *            controls balance between exploration and exploitation.
     */
    public UctAgent(int nSimulations, double uctConstant) {
        if (nSimulations < 1)
            throw new IllegalArgumentException("Number of Simulations < 1");
        if (uctConstant < 0)
            throw new IllegalArgumentException("UCT Constant > 0");
        nSimulations_ = nSimulations;
        uctConstant_ = uctConstant;
        sparseSampleSize_ = -1;
        nEnsembles_ = 1;
        ensembleMethod_ = EnsembleMethod.ROOT_PARALLELIZATION;
        //simulationMethod_ = SimulationMethod.RANDOM;
        basePolicy_ = new RandomAgent();
        name_ = "UCT of " + basePolicy_.getName();
    }
    
    public UctAgent(int nSimulations, double uctConstant, Agent basePi) {
    	this(nSimulations, uctConstant);
    	basePolicy_ = basePi;
    	name_ = "UCT of " + basePolicy_.getName();
    }
    

    /**
     * UCT algorithm with sparse sampling of large stochastic state spaces.
     * 
     * @param nSimulations
     *            the number of complete games played.
     * @param uctConstant
     *            controls balance between exploration and exploitation.
     * @param sparseSampleSize
     *            max number of sample states from any action node or infinite
     *            if equal to -1.
     */
    public UctAgent(int nSimulations, double uctConstant, int sparseSampleSize) {
        this(nSimulations, uctConstant);
        if (sparseSampleSize < 1 && sparseSampleSize != -1)
            throw new IllegalArgumentException("Sparse Sample Size > 0 or = -1");
        sparseSampleSize_ = sparseSampleSize;
    }
    public UctAgent(int nSimulations, double uctConstant, int sparseSampleSize, Agent basePi) {
        this(nSimulations, uctConstant, sparseSampleSize);
        basePolicy_ = basePi;
        name_ = "UCT of " + basePolicy_.getName();
    }

    /**
     * UCT algorithm with sparse sampling and ensemble methods.
     * 
     * @param nSimulations
     *            the number of complete games played.
     * @param uctConstant
     *            controls balance between exploration and exploitation.
     * @param sparseSampleSize
     *            max number of sample states from any action node or infinite
     *            if equal to -1.
     * @param ensembleTrials
     *            number of trees separate trees built.
     */
    public UctAgent(int nSimulations, double uctConstant, int sparseSampleSize,
            int ensembleTrials, String ensembleMethod) {
        this(nSimulations, uctConstant, sparseSampleSize);
        if (ensembleTrials < 1)
            throw new IllegalArgumentException("Ensemble trials must be > 0");
        nEnsembles_ = ensembleTrials;
        ensembleMethod_ = EnsembleMethod.valueOf(ensembleMethod);
    }
    public UctAgent(int nSimulations, double uctConstant, int sparseSampleSize,
            int ensembleTrials, String ensembleMethod, Agent basePi) {
        this(nSimulations, uctConstant, sparseSampleSize, ensembleTrials, ensembleMethod);
        basePolicy_ = basePi;
        name_ = "UCT of " + basePolicy_.getName();
    }
    
    public UctAgent(int nSimulations, double uctConstant, int sparseSampleSize,
            int ensembleTrials, String ensembleMethod, Agent basePi, ActionFilter filter) {
    	this(nSimulations, uctConstant, sparseSampleSize, ensembleTrials, ensembleMethod, basePi);
    	this.filter_ = filter;
    }

    /**
     * Builds UCT trees and then selects the best action.
     * If the number of trajectories is less than the number of actions at the
     * root state then not all actions are explored at least one time. In this
     * situation the best action is selected from only those that have been
     * explored.
     */
    @Override
    public <S extends State, A> A selectAction(S state,
            Simulator<S, A> iSimulator) {
    	iSimulator.setState(state);
        List<A> legalActions = iSimulator.getLegalActions();

        if (filter_ != null) {
        	legalActions = filter_.filter(legalActions, state);
        }
        // If only one action possible skip action selection algorithms
        if (legalActions.size() == 1)
            return legalActions.get(0);
        boolean steveRules = false;
        if (steveRules){
            legalActions = legalActions.subList(0,1);
            ((HearthstoneState)iSimulator.getState()).setLegalActions((List<GameAction>)legalActions);
        }


        int agentTurn = iSimulator.getState().getAgentTurn();

        // Generate UCT trees and save root action values
        double[][] rootActionRewards = new double[nEnsembles_][legalActions
                .size()];
        int[][] rootActionVisits = new int[nEnsembles_][legalActions.size()];
        for (int i = 0; i < nEnsembles_; i++) {
            StateNode<S, A> root = new StateNode<S, A>(iSimulator.getState(), legalActions);
            for (int j = 0; j < nSimulations_; j++)
                playSimulation(root, iSimulator.copy());

            List<ActionNode<S, A>> children = root.getChildren();
            for (int j = 0; j < children.size(); j++) {
                if (children.get(j).getVisits() > 0) {
                    rootActionRewards[i][j] = children.get(j).getReward(
                            agentTurn);
                    rootActionVisits[i][j] = children.get(j).getVisits();
                }
            }
        }
        for (int i = 0; i < rootActionRewards[0].length; i++) {
            System.out.println("Action: " + legalActions.get(i) + " REWARD: " + rootActionRewards[0][i]);
        }
        return legalActions.get(selectActionIndex(rootActionRewards,
                rootActionVisits, ensembleMethod_));
    }
    //dicks

    private int selectActionIndex(double[][] rootActionsRewards,
            int[][] rootActionsVisits, EnsembleMethod ensembleMethod) {
        int actionIndex = 0;
        int size = rootActionsRewards[0].length;
        switch (ensembleMethod) {
        case ROOT_PARALLELIZATION:
            double[] values = new double[size];
            int[] visits = new int[size];
            for (int i = 0; i < nEnsembles_; i++)
                for (int j = 0; j < size; j++) {
                    values[j] += rootActionsRewards[i][j];
                    visits[j] += rootActionsVisits[i][j];
                }
            for (int i = 1; i < values.length; i++)
                if (visits[i] > 0
                        && (visits[actionIndex] == 0 || values[i] / visits[i] > values[actionIndex]
                                / visits[actionIndex]))
                    actionIndex = i;
            break;
        case PLURALITY_VOTE:
            int[] votes = new int[size];
            for (int i = 0; i < nEnsembles_; i++) {
                int voteIndex = -1;
                double bestAvgReward = 0;
                for (int j = 0; j < size; j++) {
                    if (rootActionsVisits[i][j] > 0) {
                        double avgReward = rootActionsRewards[i][j]
                                / rootActionsVisits[i][j];
                        if (voteIndex == -1 || avgReward > bestAvgReward) {
                            voteIndex = j;
                            bestAvgReward = avgReward;
                        }
                    }
                }
                votes[voteIndex] += 1;
            }
            List<Integer> selectedVotes = new LinkedList<Integer>();
            selectedVotes.add(0);
            for (int i = 1; i < votes.length; i++) {
                if (votes[i] >= votes[selectedVotes.get(0)]) {
                    if (votes[i] > votes[selectedVotes.get(0)])
                        selectedVotes.clear();
                    selectedVotes.add(i);
                }
            }
            actionIndex = selectedVotes.get((int) (Math.random() * selectedVotes.size()));
            break;
        }
        return actionIndex;
    }

    /**
     * This method walks down the tree making decisions of the best nodes as it
     * goes. When it reaches an unexplored leaf node it plays a random game to
     * initialize that nodes value.
     * 
     * @param node
     *            current state node being traversed in tree.
     * @param simulator
     *            contains current state of game being played.
     * @return rewards of simulated game are passed up the tree.
     */
    private <S extends State, A> int[] playSimulation(
            StateNode<S, A> node, Simulator<S, A> iSimulator) {
        int[] rewards;
        if (iSimulator.isTerminalState() || node.getVisits() == 0)
            rewards = simulateGame(iSimulator);
        else
            rewards = playSimulation(node.uctSelect(), iSimulator);
        node.update(rewards);
        return rewards;
    }

    /**
     * This method walks down the tree making decisions of the best nodes as it
     * goes. When it reaches an unexplored leaf node it plays a random game to
     * initialize that nodes value.
     * 
     * @param node
     *            current action node being traversed in tree.
     * @param simulator
     *            contains current state of game being played.
     * @return rewards of simulated game are passed up the tree.
     */
    private <S extends State, A> int[] playSimulation(
            ActionNode<S, A> node, Simulator<S, A> iSimulator) {
        StateNode<S, A> child = node.selectChild(iSimulator);
        iSimulator.setState(child.getState(), child.getLegalActions());
        int[] rewards = playSimulation(child, iSimulator);
        node.update(rewards);
        return rewards;
    }

    /**
     * Quickly simulate a game from the current state and return accumulated
     * reward.
     * 
     * @param simulator
     *            a copy of the simulator you want to use to simulate game.
     * @return accumulated reward vector from the game.
     */
    private <S extends State, A> int[] simulateGame(Simulator<S, A> iSimulator) {
        int[] totalRewards = iSimulator.getRewards();
        while (!iSimulator.isTerminalState()) {
        	A action = basePolicy_.selectAction(iSimulator.getState(), iSimulator.copy());
            iSimulator.takeAction(action);
            for (int i = 0; i < totalRewards.length; i++)
                totalRewards[i] += iSimulator.getRewards()[i];
        }
        return totalRewards;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(super.toString());
        output.append("\n  Number of Simulations:     " + nSimulations_);
        output.append("\n  UCT Constant(s):              " + uctConstant_);
        if (sparseSampleSize_ > 0)
            output
                    .append("\n  Sparse Sample Size:        "
                            + sparseSampleSize_);
        if (nEnsembles_ > 1) {
            output.append("\n  Number of Ensembles:       " + nEnsembles_);
            output.append("\n  Ensemble Method:           " + ensembleMethod_);
        }
        output.append("\n	Base Policy:	" + basePolicy_);
        return output.toString();
    }
}
