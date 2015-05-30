package edu.oregonstate.eecs.mcplan.domains.RCW;

import java.io.*;
import java.util.*;

import edu.oregonstate.eecs.mcplan.History;
import edu.oregonstate.eecs.mcplan.Main;
import edu.oregonstate.eecs.mcplan.Simulator;

/**
 * Represent a Cascades simulator. Assume that parcels are stored according to their IDs.
 * 
 */
public final class RCWsimulator extends Simulator<RCWstate, RCWaction> {
	private int TimeHorizon_;
	public static final int NUMBER_OF_AGENTS = 1;
	private static String territoriesFile_ = "territories-gridmap.csv";
	private static String parcelsFile_ = "parcels-gridmap-1.csv";
	private static String colonizationFile_ = "colonization-gridmap.csv";
	
    /** Keeps track of states of each year. */
    private List<String> stateHistory_;
    /** Keeps track of actions taken each year. */
    private List<List<String>> actionHistory_;

	public RCWsimulator(int timeHorizon, String territoriesFile, String parcelsFile, String colonizationFile) {
		TimeHorizon_ = timeHorizon;
		territoriesFile_ = territoriesFile;
		parcelsFile_ = parcelsFile;
		colonizationFile_ = colonizationFile;
		setInitialState();		
	}
      
	public RCWsimulator(RCWstate state, List<RCWaction> legalActions, int timeHorizon){
		state_ = new RCWstate(state.getTerritories(), state.getYear());
		TimeHorizon_ = timeHorizon;
		legalActions_ = new ArrayList<RCWaction>();
        for (RCWaction action : legalActions)
            legalActions_.add(action);        
        computeRewards();
        stateHistory_ = new ArrayList<String> ();
        actionHistory_ = new ArrayList<List<String>> ();
	}
	
	@Override
	public Simulator<RCWstate, RCWaction> copy() {
		return new RCWsimulator(state_, legalActions_, TimeHorizon_);
	}

	@Override
	public void setInitialState() {	
		state_ = new RCWstate(0, territoriesFile_, parcelsFile_, colonizationFile_);
		computeLegalActions();	
		computeRewards();	
		stateHistory_ = new ArrayList<String>();
		actionHistory_ = new ArrayList<List<String>> ();	
		stateHistory_.add(0 + ": " + state_.toString());		
		List<String> action = new ArrayList<String> ();
		actionHistory_.add(action);		
	}

	@Override
	public void setState(RCWstate state) {        
		state_ = state;// new RCWstate(state.getTerritories(), state.getYear());
		computeLegalActions();
		computeRewards();		
	}

	@Override
	public void setState(RCWstate state, List<RCWaction> legalActions) {
        state_ = state;
        legalActions_ = legalActions;
        computeRewards();
	}

	@Override
	public void takeAction(RCWaction action) {
        if (!legalActions_.contains(action))
            throw new IllegalArgumentException("Action " + action
                    + " not possible from current state.");
        
        if (action instanceof RCWpurchaseAction) {
        	RCWpurchaseAction purchaseAction = (RCWpurchaseAction) action;
        	int parcel = purchaseAction.getParcel();
        	state_.purchaseParcel(parcel);  
        	int year = state_.getYear();
        	if (year < actionHistory_.size()){
        		List<String> actions = actionHistory_.get(year);
        		actions.add(action.toString());
        		actionHistory_.set(year, actions);
        	}
        } 
        if (action instanceof RCWtreatAction) {
        	RCWtreatAction treatAction = (RCWtreatAction) action;
        	state_.treatParcel(treatAction.getParcel());
        }
        if (action instanceof RCWkillAction){
        	RCWkillAction killAction = (RCWkillAction) action;
        	state_.killPedators(killAction.getParcel());
        }
        
        state_.spread();       	
        int year = state_.getYear() ;	
        if (year <= actionHistory_.size()){
        		stateHistory_.add(year + ": " + state_.toString());
        		List<String> act = new ArrayList<String> ();
        		actionHistory_.add(act);
        	}   
   		computeLegalActions();
       	computeRewards();
	}
	
	private void computeLegalActions() {		
        legalActions_ = new ArrayList<RCWaction>();   
        if (TimeHorizon_ <= state_.getYear() )
        	return;
        
        List<Integer> parcels = state_.getAffordableParcels();
        for (int parcel : parcels)
        	legalActions_.add(new RCWpurchaseAction(parcel));
        
        parcels = state_.getInvadedParcels();
        for (int parcel : parcels)
        	legalActions_.add(new RCWkillAction(parcel));
        
        parcels = state_.getTreatableParcels();
        for (int parcel : parcels)
        	legalActions_.add(new RCWtreatAction(parcel));
                
        RCWnoopAction action = new RCWnoopAction();
        legalActions_.add(action);        
	}	
	
	private void computeRewards(){
        rewards_ = new int[NUMBER_OF_AGENTS];
        if ( isTerminalState()) {
        	List<RCWTerritory> territories = state_.getTerritories();
    		for (RCWTerritory ter : territories){
    			if (ter.isOccupied() && ter.isReserved())
    				rewards_[0]++;
    		}
        }
	}
	
	@Override
	public int getNumberOfAgents() {
		return NUMBER_OF_AGENTS;
	}
	
	@Override
	public double[] getFeatureVector(RCWaction action) {
		throw new IllegalStateException("Unimplemented");
	}
    
    public int getTimeHorizon(){
    	return TimeHorizon_;
    }

	@Override
	public RCWstate getState(){
		return new RCWstate(state_.getTerritories(), state_.getYear());
	}
    public String getStateHistory(){
    	StringBuilder output = new StringBuilder();;
    	for (int i = 0; i < stateHistory_.size(); i++)
    		output.append(stateHistory_.get(i) + "\n");
    	return output.toString();
    }
    public String getActionHistory(){
    	StringBuilder output = new StringBuilder();;
    	for (int i = 0; i < actionHistory_.size(); i++)
    	{
    		List<String> actions = actionHistory_.get(i);
    		output.append(i + ": ");
    		for (int j = 0; j < actions.size(); j++)
    			output.append(actionHistory_.get(i).get(j).toString() + " ");
    		output.append("\n");
    	}    		
    	return output.toString();
    }    
    
    public String toString(){    	
    	return super.toString() + "\nRCW_" + this.TimeHorizon_ + "years" ;
    }
    
	public static void main(String[] args) {
		
	}
}
