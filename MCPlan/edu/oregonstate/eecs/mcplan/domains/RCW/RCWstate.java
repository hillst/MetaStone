package edu.oregonstate.eecs.mcplan.domains.RCW;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import edu.oregonstate.eecs.mcplan.Main;
import edu.oregonstate.eecs.mcplan.State;

/**
 * Represent a Cascades state, including information of all *reserved* territories and budget.
 */
public final class RCWstate implements State {
	private static double predatorDiscount = 0.25;	//colonization prob. is discounted if predators exist
	/* List of territories IDs in each parcel*/
	private static List<List<Integer>> mapping_ = new ArrayList<List<Integer>> ();
	/* colonization matrix; territoryID-i: territoryID-j || probability */
	private static List<List<Double>> transition_ = new ArrayList<List<Double>> ();
	
	/* Indicate the time. */
	private int year_;	
	/* Information of all territories. */
	private List<RCWTerritory> territories_ = new ArrayList<RCWTerritory> ();
		
	public RCWstate(int year, String territoriesFile, String parcelsFile, String colonizationFile) {
		getInput(territoriesFile, parcelsFile, colonizationFile);		
		year_ = year;
	}
	
	public RCWstate(List<RCWTerritory> territories, int year) {
		territories_.clear();
		if (territories != null)		
			for (RCWTerritory territory: territories)
				territories_.add(territory.copy());				
		year_ = year;
	}
	
	/* Get data files and set up the map information. */
	public void getInput(String territoriesFile, String parcelsFile, String colonizationFile){
		mapping_.clear();
		transition_.clear();
		
		/* Set up transition matrix. */
		File file = new File(colonizationFile);
		BufferedReader bufRdr;	
		try{
			bufRdr = new BufferedReader(new FileReader(file));	
			String line;			
			while((line = bufRdr.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line,",");				
				List<Double> temp = new ArrayList<Double> ();
				while(st.hasMoreTokens())
					temp.add(Double.parseDouble(st.nextToken()));				
				transition_.add(temp);				
			}
			bufRdr.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		/* Set up cost information of parcels, used to set reservation of territories. */	
		file = new File(parcelsFile);
		List<Integer> parcels = new ArrayList<Integer> ();	
		
		try{
			bufRdr = new BufferedReader(new FileReader(file));	
			String line = bufRdr.readLine();			
			while((line = bufRdr.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line,",");
				int id = Integer.parseInt(st.nextToken());
				parcels.add(Integer.parseInt(st.nextToken()));
			}
			bufRdr.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
		/* Set up initial state (reserved territories). */
		/* Set up mapping from parcel to territories. */
		file = new File(territoriesFile);
		for (int i = 0; i < parcels.size(); i++)
			mapping_.add(new ArrayList<Integer> ());
		try{
			bufRdr = new BufferedReader(new FileReader(file));	
			String line = bufRdr.readLine();			
			while((line = bufRdr.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line,",");
				int ter_id = Integer.parseInt(st.nextToken());
				int par_id = Integer.parseInt(st.nextToken());
				int temp = Integer.parseInt(st.nextToken());
				boolean occupied = false;
				if (temp == 1)
					occupied = true;
				temp = Integer.parseInt(st.nextToken());
				boolean predator = false;
				if (temp == 1)
					predator = true;
				boolean reservation = false;
				if (parcels.get(par_id) == 1)
					reservation = true;
				territories_.add(new RCWTerritory(ter_id, reservation, occupied, 0, predator));
				
				mapping_.get(par_id).add(ter_id);	
			}
			bufRdr.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* A parcel is legal for purchase iff it is not yet reserved. */
	public List<Integer> getAffordableParcels(){
		List<Integer> affordableParcels = new ArrayList<Integer> ();
        for (int i = 0; i < mapping_.size(); i++){
        	if (mapping_.get(i).size() != 0)
        		if (!territories_.get(mapping_.get(i).get(0)).isReserved())
        			affordableParcels.add(i);
        }
        return affordableParcels;
	}
	
	/* A parcel is invaded if at least one territory in it is invaded. */
	public List<Integer> getInvadedParcels(){
		List<Integer> invadedParcels = new ArrayList<Integer> ();
        for (int i = 0; i < mapping_.size(); i++){
        	if (mapping_.get(i).size() != 0){
        		boolean invaded = false;
        		for (int ter : mapping_.get(i))
        			if (territories_.get(ter).isInvaded() && territories_.get(ter).isReserved()){
        				invaded = true;
        				break;
        			}
        		if (invaded)
        			invadedParcels.add(i);
        	}
        }
        return invadedParcels;
	}
	
	/* A parcel is legal for treatment iff it is already reserved.	*/
	public List<Integer> getTreatableParcels(){
		List<Integer> treatableParcels = new ArrayList<Integer> ();
        for (int i = 0; i < mapping_.size(); i++){
        	if (mapping_.get(i).size() !=  0)
        		if (territories_.get(mapping_.get(i).get(0)).isReserved())
        			treatableParcels.add(i);
        }
        return treatableParcels;
	}
	
	public void purchaseParcel(int parcel){		
        for (int i = 0; i < mapping_.get(parcel).size(); i++){
        	int ter_id = mapping_.get(parcel).get(i); 
        	territories_.get(ter_id).setReservation(true);
        }        	
	}
	
	public void treatParcel(int parcel){		
        for (int i = 0; i < mapping_.get(parcel).size(); i++){
        	int ter_id = mapping_.get(parcel).get(i); 
        	territories_.get(ter_id).beTreated();
        }        	
	}
	
	public void killPedators(int parcel){		
		for (int i = 0; i < mapping_.get(parcel).size(); i++){
        	int ter_id = mapping_.get(parcel).get(i); 
        	territories_.get(ter_id).killPredator();
        } 
	}
	
	/* Simulate the spread of both RCW and predators. */
	public void spread(){
		/* get a copy of territories_ and change occupancy and invasion info in territories_ to false. */
		List<RCWTerritory> copy = new ArrayList<RCWTerritory> ();
    	for (RCWTerritory territory : territories_){    
    		RCWTerritory temp = territory.copy();
    		copy.add(temp);   
    		territory.setOccupancy(false);    
    		territory.killPredator();///////////
    	}    	

    	for (int i = 0; i < territories_.size(); i++) {     
    		if (!copy.get(i).isReserved())
    			continue;
    		// bird spread
    		if (!copy.get(i).isInvaded() ||
    				(copy.get(i).isInvaded() && Math.random() > 0.8))
    				//if predator exists, with p, no birds will go there    			
	    		for (int j = 0; j < copy.size(); j++){//every possible source, j -> i
	    			if (territories_.get(i).isOccupied())
	    				break;
	    			if (!copy.get(j).isOccupied())
	    				continue;  
	    			double birdProb = this.adjustProb(transition_.get(j).get(i), 
	    						copy.get(i).getTreatNum(), copy.get(i).isInvaded());    			
	    			if (Math.random() < birdProb )
	    				//System.out.println("ter " + i + " is occupied by " + j);
	    				territories_.get(i).setOccupancy(true);        	    		
	    		}    		
	    		
	    	// predator spread
	    	for (int j = 0; j < copy.size(); j++){
	    		if (territories_.get(i).isInvaded())
	    			break;
	    		if (!copy.get(j).isInvaded())
	    			continue;
	    		if (territories_.get(i).isOccupied()
	    				&& Math.random() < 1 * transition_.get(j).get(i))
	    			territories_.get(i).beInvaded();  
	    	}		
    	}
    	year_++;
	}
	
	public double adjustProb(double prob, int treatNum, boolean invasion){	
		if (Math.random() < 0.5  - treatNum / 2.0)
			return 0;
		/*double x = prob / (1 - prob);		
		if (invasion)
			return x / (x + Math.exp(-treatNum)) * predatorDiscount;
		return x / (x + Math.exp(-treatNum)); // actual prob using logistic	
*/	
		double x = prob + treatNum / 5.0;
		if (x > 1)
			x = 1;
		if (invasion)
			return x * predatorDiscount;
		return x;
		}
	public void setYear (int year){
		year_ = year;
	}
	public List<RCWTerritory> getTerritories(){
		return territories_;
	}	
	public RCWTerritory getTerritory(int id){		
		return territories_.get(id);
	}	
	public int getYear(){
		return year_;
	}

	public List<List<Integer>> getMapping(){
		return mapping_;
	}	
	public List<List<Double>> getTransition(){
		return transition_;
	}
	
	@Override
	public int getAgentTurn() {
		return 0;
	}

    @Override
    public int hashCode() {
    	int reward = 0;
    	for (RCWTerritory ter : territories_)
    		if (ter.isOccupied())
    			reward++;
        return reward;
    }
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RCWstate))
            return false;
        RCWstate state = (RCWstate) object;
        for(int i = 0; i < state.territories_.size(); i++)
        	if (!state.getTerritories().get(i).equals(this.territories_.get(i)))
        		return false;        
        return true;
    }
    
    @Override
    // Only return IDs of all occupied territories
    public String toString() {
    	StringBuilder birds = new StringBuilder();
    	StringBuilder predators = new StringBuilder();
    	for (RCWTerritory ter : territories_){
			if (ter.isOccupied())
				birds.append(ter.getID() + " ");
			if (ter.isInvaded())
				predators.append(ter.getID() + " ");
    	}
    	return birds.toString() + "\n" + predators.toString();
    }
    
}