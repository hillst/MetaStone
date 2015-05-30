package edu.oregonstate.eecs.mcplan.agents;

import java.util.List;

import edu.oregonstate.eecs.mcplan.State;

public interface ActionFilter {
	public <A> List<A> filter(List<A> actionList, State state);
}
