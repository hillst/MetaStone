package edu.oregonstate.eecs.mcplan.agents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import edu.oregonstate.eecs.mcplan.domains.RCW.RCWnoopAction;
import edu.oregonstate.eecs.mcplan.domains.RCW.RCWpurchaseAction;
import edu.oregonstate.eecs.mcplan.domains.RCW.RCWsimulator;
import edu.oregonstate.eecs.mcplan.domains.RCW.RCWstate;

public class DesignedSolution extends Agent {
	private List<String> solution_ = new ArrayList<String> ();
	
	public DesignedSolution(String filePath){			
		getSolution(filePath);		
		System.out.println("The solution is \n" + solution_);
		name_ = "DesignedSolution";
	}		
		
	@Override
	public <S extends State, A> A selectAction(S state,
			Simulator<S, A> iSimulator) {
		iSimulator.setState(state);
		List<A> actions = iSimulator.getLegalActions();
		RCWstate State = (RCWstate) state;
		
		if (actions.size() == 1)
			return actions.get(0);
		if (State.getYear() >= solution_.size())
			return (A) new RCWnoopAction();		
		
		for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i).toString().toLowerCase().equals(
                    solution_.get(State.getYear()).toLowerCase())) {            	
                return actions.get(i);
            }
        }
		System.out.println("Not a valid action in the solution. ");
		return (A) new RCWnoopAction();
	}
	
	public void getSolution(String filePath){
		try{
			BufferedReader input = new BufferedReader(new FileReader(filePath));			
			String line = "";
			while ((line = input.readLine()) != null){
				String[] parts = line.split(" ");
				if (parts == null || parts.length == 0)
					continue;
				for (String str : parts){
					if (str == null || str.isEmpty())
						continue;
					solution_.add(str);
				}
			}
		input.close();
		} catch(IOException exception) {
			throw new IllegalArgumentException("Filename " + filePath
                + " invalid.");
		}	
	}
}
