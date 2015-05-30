package edu.oregonstate.eecs.mcplan.agents;

import edu.oregonstate.eecs.mcplan.State;

public interface Heuristic {
	public double heuristic(State state_);
}
