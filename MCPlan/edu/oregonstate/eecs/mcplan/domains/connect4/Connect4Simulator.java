package edu.oregonstate.eecs.mcplan.domains.connect4;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.mcplan.Simulator;

public class Connect4Simulator extends Simulator<Connect4State, Connect4Action> {
    private static final int NUMBER_OF_AGENTS = 1;

    private static final long ALL_LOCATIONS = (1L << ((Connect4State
            .getHeight() + 1) * Connect4State.getWidth())) - 1;

    private static final long FIRST_COLUMN = (1L << Connect4State.getHeight() + 1) - 1;

    private static final long BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN;

    private static final long ABOVE_TOP_ROW = BOTTOM_ROW << Connect4State
            .getHeight();

    private int[] height_ = null;
    
    public enum OppoAgent {
        Random,
        Heuristic
    }
    
    public OppoAgent oppoAgent_;
    

    public Connect4Simulator(String oppoAgent) {
    	oppoAgent_ = OppoAgent.valueOf(oppoAgent);
        setInitialState();
    }

    private Connect4Simulator(Connect4State state,
            List<Connect4Action> legalActions, int[] rewards, int[] height, String oppoAgent) {
        state_ = state;
        legalActions_ = new ArrayList<Connect4Action>();
        for (Connect4Action action : legalActions)
            legalActions_.add(action);
        rewards_ = new int[NUMBER_OF_AGENTS];
        for (int i = 0; i < NUMBER_OF_AGENTS; i++)
            rewards_[i] = rewards[i];
        height_ = new int[height.length];
        for (int i = 0; i < height.length; i++)
            height_[i] = height[i];
        oppoAgent_ = OppoAgent.valueOf(oppoAgent);
    }

    @Override
    public Simulator<Connect4State, Connect4Action> copy() {
        return new Connect4Simulator(state_, legalActions_, rewards_, height_, oppoAgent_.toString());
    }

    public void setInitialState() {
        state_ = new Connect4State(new long[2], 0);
        computeRewards();
        computeLegalActions();
    }

    @Override
    public void setState(Connect4State state) {
        state_ = state;
        computeRewards();
        computeLegalActions();
    }

    @Override
    public void setState(Connect4State state, List<Connect4Action> legalActions) {
        state_ = state;
        legalActions_ = legalActions;
        if (legalActions_.size() == 0)
            computeRewards();
        else
            rewards_ = new int[NUMBER_OF_AGENTS];
        computeHeight();
    }

    private void computeHeight() {
        height_ = new int[Connect4State.getWidth()];
        long[] bitBoards = state_.getBitBoards();
        long bitBoard = bitBoards[0] | bitBoards[1];
        for (int i = 0; i < Connect4State.getWidth(); i++) {
            height_[i] = (Connect4State.getHeight() + 1) * i;
            while ((bitBoard & (1L << height_[i])) != 0)
                height_[i]++;
        }
    }

    @Override
    public void takeAction(Connect4Action playerAction) {
        this.takeOneAction(playerAction);        
        Connect4Action oppoAction = this.getOppoAction();
        if (oppoAction != null)
        	this.takeOneAction(oppoAction);
    }
    
    private void takeOneAction(Connect4Action action){
    	if (!legalActions_.contains(action))
            throw new IllegalArgumentException("Action " + action
                    + " not possible from current state.");
        long[] bitBoards = state_.getBitBoards();
        bitBoards[state_.getAgentTurn()] ^= (1L << (height_[action
                .getLocation()]++));
        state_ = new Connect4State(bitBoards, (state_.getAgentTurn() + 1) % 2);
        computeRewards();
        computeLegalActions();
    }
    
    private Connect4Action getOppoAction(){
    	if (legalActions_.size() == 0)
    		return null;
    	switch	(oppoAgent_){
    	case Random:
    		return legalActions_.get((int) (Math.random() * legalActions_.size()));    		
    	case Heuristic:
    		break;
    	}
    	return null;
    }
    
    private void computeLegalActions() {
        legalActions_ = new ArrayList<Connect4Action>();
        computeHeight();
        if (rewards_[0] == 0) {
            long bitBoard = state_.getBitBoards()[state_.getAgentTurn()];
            for (int i = 0; i < Connect4State.getWidth(); i++)
                if (((bitBoard | (1L << height_[i])) & ABOVE_TOP_ROW) == 0)
                    legalActions_.add(Connect4Action.valueOf(i));
        }
    }

    public void computeRewards() {
        long[] bitBoards = state_.getBitBoards();
        int height = Connect4State.getHeight();

        for (int i = 0; i < bitBoards.length; i++) {
            long bitBoard = bitBoards[i];
            long diagonal1 = bitBoard & (bitBoard >> height);
            long horizontal = bitBoard & (bitBoard >> (height + 1));
            long diagonal2 = bitBoard & (bitBoard >> (height + 2));
            long vertical = bitBoard & (bitBoard >> 1);
            if (((diagonal1 & (diagonal1 >> 2 * height))
                    | (horizontal & (horizontal >> 2 * (height + 1)))
                    | (diagonal2 & (diagonal2 >> 2 * (height + 2))) | (vertical & (vertical >> 2))) != 0) {
                if (i == 0) {
                    rewards_ = new int[] { 1, -1 };
                    return;
                } else {
                    rewards_ = new int[] { -1, 1 };
                    return;
                }
            }
        }
        rewards_ = new int[NUMBER_OF_AGENTS];
    }

    @Override
    public int getNumberOfAgents() {
        return NUMBER_OF_AGENTS;
    }

    @Override
    public double[] getFeatureVector(Connect4Action action) {
        throw new IllegalStateException("Unimplemented");
    }
}
