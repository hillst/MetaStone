package edu.oregonstate.eecs.mcplan.agents;

import java.util.ArrayList;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import edu.oregonstate.eecs.mcplan.domains.galcon.*;

public class DumbGalconAgent extends Agent {

	
	public DumbGalconAgent() {
		name_ = "Dumb Expansionist Galcon Agent";
	}
	
	@Override
	public <S extends State, A> A selectAction(S state,
			Simulator<S, A> iSimulator) {
			return (A)dumbStrategy((GalconState)state);
	}
	
	private GalconAction dumbStrategy(GalconState state) {
		Planet source = AgentHelper.getMyMostPopulatedPlanet(state);
		if (source == null) {
			return AgentHelper.getMatchingNothingAction(state);
		}
		Planet dest = AgentHelper.getFarthestUntargetedPlanet(state, source);
		if (dest == null) {
			return AgentHelper.getMatchingNothingAction(state);
		}
		return AgentHelper.getMatchingLegalLaunchAction(state, source.getPlanetID(),
				dest.getPlanetID(), GalconLaunchAction.LaunchSize.LARGE);
	}
}
