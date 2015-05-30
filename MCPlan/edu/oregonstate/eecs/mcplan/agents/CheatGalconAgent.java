package edu.oregonstate.eecs.mcplan.agents;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import edu.oregonstate.eecs.mcplan.domains.galcon.*;

public class CheatGalconAgent extends Agent {

	
	public CheatGalconAgent() {
		name_ = "Cheating Galcon Agent";
	}
	
	@Override
	public <S extends State, A> A selectAction(S state,
			Simulator<S, A> iSimulator) {
			return (A)cheatStrategy((GalconState)state);
	}
	
	private GalconAction cheatStrategy(GalconState state) {
		List<Planet> enemyPlanets = AgentHelper.getEnemyPlanets(state);
		Planet source = AgentHelper.getLargestPlanet(enemyPlanets);
		if (source == null) {
			return AgentHelper.getMatchingNothingAction(state);
		}
		Planet dest = AgentHelper.getFarthestUntargetedPlanet(state, source);
		if (dest == null) {
			return AgentHelper.getMatchingNothingAction(state);
		}
		return new GalconLaunchAction(AgentHelper.getMyAgentId(state), 
				source.getPlanetID(), dest.getPlanetID(), 
				GalconLaunchAction.LaunchSize.LARGE);
	}
}
