package edu.oregonstate.eecs.mcplan.agents;

import java.util.ArrayList;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import edu.oregonstate.eecs.mcplan.domains.galcon.*;

public class DefensiveGalconAgent extends Agent {

	
	public DefensiveGalconAgent() {
		name_ = "Defensive Galcon Agent";
	}
	
	@Override
	public <S extends State, A> A selectAction(S state_, Simulator<S, A> iSimulator) {
		A action;
		GalconState state = (GalconState)state_;
		int me = state.getAgentTurn();
		//Get attacking enemy fleets
		ArrayList<Spaceship> enemy = AgentHelper.getAttackingEnemyShips(state);
		if (enemy.size() == 0) {
			//We're not being attacked-- expand!
			action = (A)simpleExpand(state);
			return action;
		} //From here on- at least one fleet in enemy
		//Find biggest friendly planet under attack that's going to be conquered
		Planet defend = getBiggestDefendingPlanet(state, enemy);
		if (defend == null) {
			action = (A)simpleExpand(state);
			return action;
		}
		int attackers = AgentHelper.getTotalPopulationOfShips(AgentHelper.getEnemyShipsHeadedToPlanet(state, defend));
		//Find closest planet with a larger max fleet size
		Planet reinforce = getReinforcingPlanet(state, defend, attackers);
		if (reinforce == null) {
			action = (A)simpleExpand(state);
			return action;
		}
		//Calculate how many ships we should send
		GalconLaunchAction.LaunchSize fleet_size = calcFleetSize(defend, reinforce, attackers);
		
		action = (A)AgentHelper.getMatchingLegalLaunchAction(state, reinforce.getPlanetID(), defend.getPlanetID(), fleet_size);
		return action;
	}
	
	private GalconAction simpleExpand(GalconState state) {
		int me = state.getAgentTurn();
		
		Planet p = AgentHelper.getMyMostPopulatedPlanet(state);
		if (p == null) return AgentHelper.getMatchingNothingAction(state);
		
		int maxfleetsize = GalconSimulator.getLaunchPop(p, GalconLaunchAction.LaunchSize.LARGE);
		Planet d = findBiggestEnemyPlanet(state, maxfleetsize);
		if (d == null) return AgentHelper.getMatchingNothingAction(state);
		
		return AgentHelper.getMatchingLegalLaunchAction(state, p.getPlanetID(), d.getPlanetID(), GalconLaunchAction.LaunchSize.LARGE);
	}
	
	private Planet findBiggestEnemyPlanet(GalconState state, int max_size) {
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
	
	private Planet getBiggestDefendingPlanet(GalconState state, ArrayList<Spaceship> enemy) {
		Planet defend = null;
		int max_cap = 0;
		for (Spaceship f : enemy) {
			Planet dest = state.getPlanetMap().get(f.getDestPlanetID());
			ArrayList<Spaceship> friendly = AgentHelper.getMyShipsHeadedToPlanet(state, dest);
			
			if (dest.getPopulation() < f.getPopulation() && 
					dest.getCapacity() > max_cap && 
					friendly.size() == 0) {
				defend = dest;
			}
		}
		return defend;
	}
	
	private Planet getReinforcingPlanet(GalconState state, Planet defend, int attackers) {				
		int me = state.getAgentTurn();
		Planet closest = null;
		double min_dist = Double.MAX_VALUE;
		for (Planet p : state.getPlayerPlanets(me)) {
			int defenders = GalconSimulator.getLaunchPop(p, GalconLaunchAction.LaunchSize.LARGE);
			if (defenders > attackers) {
				double dist = GeoUtility.distance(p, defend);
				if (dist < min_dist) {
					min_dist = dist;
					closest = p;
				}
			}
		}
		
		return closest;
	}
	
	private GalconLaunchAction.LaunchSize calcFleetSize(Planet defend, Planet reinforce, int attackers) {
		int diff = defend.getPopulation() - attackers;
		
		GalconLaunchAction.LaunchSize min_lsize = GalconLaunchAction.LaunchSize.LARGE;
		for (GalconLaunchAction.LaunchSize lsize : GalconLaunchAction.LaunchSize.values()) {
			int size = GalconSimulator.getLaunchPop(reinforce, lsize);
			int min_size = GalconSimulator.getLaunchPop(reinforce, min_lsize);
			if (size > diff && size < min_size) min_lsize = lsize;
		}
		return min_lsize;
	}
}
