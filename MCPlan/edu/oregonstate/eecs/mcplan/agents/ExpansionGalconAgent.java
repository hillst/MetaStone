package edu.oregonstate.eecs.mcplan.agents;

import java.util.ArrayList;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import edu.oregonstate.eecs.mcplan.domains.galcon.*;

public class ExpansionGalconAgent extends Agent {

	
	public ExpansionGalconAgent() {
		name_ = "Expansion Galcon Agent";
	}
	
	@Override
	public <S extends State, A> A selectAction(S state_, Simulator<S, A> iSimulator) {
		A action;
		GalconState state = (GalconState)state_;
		//Find most populated own planet
		Planet bigplanet = AgentHelper.getMyMostPopulatedPlanet(state);
		if (bigplanet == null) {
			action = (A)AgentHelper.getMatchingNothingAction(state);
			return action;
		}
		//Calculate max fleet size
		int maxfleetsize = AgentHelper.getLargestLegalShipCapacity(bigplanet);
		//Find biggest (in size!) enemy/neutral planet w/ pop < fleet size
		Planet target = findBiggestConquerableEnemyPlanet(state, maxfleetsize);
		//Send that shit
		if (target == null) {
			action = (A)AgentHelper.getMatchingNothingAction(state);
		} else {
			int b_id = bigplanet.getPlanetID();
			int t_id = target.getPlanetID();
			action = (A)AgentHelper.getMatchingLegalLaunchAction(state, b_id, 
					t_id, GalconLaunchAction.LaunchSize.LARGE);
		}
		return action;
	}
	
	private Planet findBiggestConquerableEnemyPlanet(GalconState state, int max_size) {
		int me = state.getAgentTurn();
		ArrayList<Planet> planets = state.getNonFriendlyPlanets(me);
		int max_cap = 0;
		Planet target = null;
		for (Planet p : planets) {
			if (p.getCapacity() > max_cap) {
				ArrayList<Spaceship> fleets = AgentHelper.getMyShipsHeadedToPlanet(state, p);
				if (p.getPopulation() < max_size && fleets.size() == 0) {
					max_cap = p.getCapacity();
					target = p;
				}
			}
		}
		return target;
	}
}
