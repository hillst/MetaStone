package edu.oregonstate.eecs.mcplan.agents;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.mcplan.domains.galcon.GalconAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconLaunchAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconNothingAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconSimulator;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconState;
import edu.oregonstate.eecs.mcplan.domains.galcon.GeoUtility;
import edu.oregonstate.eecs.mcplan.domains.galcon.Planet;
import edu.oregonstate.eecs.mcplan.domains.galcon.Spaceship;

public class AgentHelper {
	

	
	/*
	 *  agent id utility methods
	 */
	public static int getMyAgentId(GalconState state){
		return state.getAgentTurn();
	}
	
	public static int getEnemyAgentId(GalconState state){
		int myId = state.getAgentTurn();
		int theirId = 1 - myId;
		return theirId;
	}
	
	/*
	 *  Planet utility methods
	 */
	public static Planet getMyMostPopulatedPlanet(GalconState state){
		List<Planet> myPlanets = getMyPlanets(state);
		if (null == myPlanets){
			return null;
		}
		if (myPlanets.size() == 0){
			return null;
		}
		return getMostPopulated(myPlanets);
	}
	
	public static Planet getMyLeastPopulatedPlanet(GalconState state){
		List<Planet> myPlanets = getMyPlanets(state);
		if (null == myPlanets){
			return null;
		}
		if (myPlanets.size() == 0){
			return null;
		}
		return getLeastPopulated(myPlanets);
	}
	
	public static Planet getOtherMostPopulatedPlanet(GalconState state){
		List<Planet> otherPlanets = state.getNonFriendlyPlanets(getMyAgentId(state));
		if (null == otherPlanets){
			return null;
		}
		if (otherPlanets.size() == 0){
			return null;
		}
		return getMostPopulated(otherPlanets);
	}
	
	public static Planet getOtherLeastPopulatedPlanet(GalconState state){
		List<Planet> otherPlanets = state.getNonFriendlyPlanets(getMyAgentId(state));
		if (null == otherPlanets){
			return null;
		}
		if (otherPlanets.size() == 0){
			return null;
		}
		return getLeastPopulated(otherPlanets);
	}
	
	private static Planet getMostPopulated(List<Planet> planets) {
		Planet mostPopulated = planets.get(0);
		for (Planet p : planets){
			if (p.getPopulation() > mostPopulated.getPopulation()){
				mostPopulated = p;
			}
		}
		return mostPopulated;
	}
	
	private static Planet getLeastPopulated(List<Planet> planets) {
		Planet leastPopulated = planets.get(0);
		for (Planet p : planets){
			if (p.getPopulation() < leastPopulated.getPopulation()){
				leastPopulated = p;
			}
		}
		return leastPopulated;
	}
	
	public static Planet getFarthestUntargetedPlanet(GalconState s, Planet source) {
		List<Planet> planets = getNotMyPlanets(s);
		if (null == planets){
			return null;
		}
		if (planets.size() == 0){
			return null;
		}
		Planet farPlanet = null;
		double maxDist = 0.;
		for (Planet p : planets) {
			double dist = GeoUtility.distance(source, p);
			if (dist > maxDist && getMyShipsHeadedToPlanet(s, p).size() == 0) {
				maxDist = dist;
				farPlanet = p;
			}
		}
		return farPlanet;
	}

	public static Planet getClosestUntargetedPlanet(GalconState s, Planet source) {
		ArrayList<Planet> planets = s.getNonFriendlyPlanets(s.getAgentTurn());
		Planet closePlanet = null;
		if (null == planets){
			return null;
		}
		if (planets.size() == 0){
			return null;
		}
		double minDist = 0.;
		for (Planet p : planets) {
			double dist = GeoUtility.distance(source, p);
			if (dist < minDist && getMyShipsHeadedToPlanet(s, p).size() == 0) {
				minDist = dist;
				closePlanet = p;
			}
		}
		return closePlanet;
	}

	public static List<Planet> getUnpopulatedPlanets(GalconState s){
		List<Planet> planets = s.getPlanets();
		if (null == planets){
			return null;
		}
		ArrayList<Planet> unpopPlanets = new ArrayList<Planet>();
		for (Planet p : planets){
			if (p.getPopulation() == 0){
				unpopPlanets.add(p);
			}
		}
		return unpopPlanets;
	}
	
	public static Planet getLargestPlanet(List<Planet> planets){
		if (null == planets){
			return null;
		}
		Planet largest = planets.get(0);
		for (Planet p : planets){
			if (p.getRadius() > largest.getRadius()){
				largest = p;
			}
		}
		return largest;
	}

	public static List<Planet> getNeutralPlanets(GalconState state){
		return state.getPlayerPlanets(-1);
	}
	
	public static List<Planet> getEnemyPlanets(GalconState state){
		int enemy = getEnemyAgentId(state);
		return state.getPlayerPlanets(enemy);
	}
	
	public static List<Planet> getMyPlanets(GalconState state) {
		int me = getMyAgentId(state);
		return state.getPlayerPlanets(me);
	}
	
	public static List<Planet> getNotMyPlanets(GalconState state) {
		return state.getNonFriendlyPlanets(getMyAgentId(state));
	}

	public static Planet getClosestPlanet(List<Planet> candidatePlanets, Planet referencePlanet){
		if (null == candidatePlanets){
			return null;
		}
		if (candidatePlanets.size() == 0){
			return null;
		}
		Planet closest = candidatePlanets.get(0);
		for (Planet p : candidatePlanets){
			if (GeoUtility.distance(p, referencePlanet) < GeoUtility.distance(closest, referencePlanet)){
				closest = p;
			}
		}
		return closest;
	}
	/*
	 * Ship utility methods
	 */
	
	public static ArrayList<Spaceship> getAttackingEnemyShips(GalconState state) {
		ArrayList<Spaceship> fleets = state.getPlayer(getEnemyAgentId(state)).getFleet();
		ArrayList<Spaceship> result = new ArrayList<Spaceship>();
		for (Spaceship f : fleets) {
			Planet dest = state.getPlanetMap().get(f.getDestPlanetID());
			if (dest.getOwnerID() == getMyAgentId(state)) result.add(f);
		}
		return result;
	}
	
	public static ArrayList<Spaceship> getMyShipsHeadedToPlanet(GalconState s, Planet p) {
		ArrayList<Spaceship> fleets = s.getPlayer(s.getAgentTurn()).getFleet();
		ArrayList<Spaceship> result = new ArrayList<Spaceship>();
		for (Spaceship f : fleets) {
			if (f.getDestPlanetID() == p.getPlanetID()) {
				result.add(f);
			}
		}
		return result;
	}
	
	public static ArrayList<Spaceship> getEnemyShipsHeadedToPlanet(GalconState s, Planet p) {
		ArrayList<Spaceship> fleets = s.getPlayer(getEnemyAgentId(s)).getFleet();
		ArrayList<Spaceship> result = new ArrayList<Spaceship>();
		for (Spaceship f : fleets) {
			if (f.getDestPlanetID() == p.getPlanetID()) {
				result.add(f);
			}
		}
		return result;
	}

	public static int getTotalPopulationOfShips(List<Spaceship> ships){
		int total = 0;
		for (Spaceship s : ships){
			total += s.getPopulation();
		}
		return total;
	}

	public static int getLargestLegalShipCapacity(Planet p) {
		return GalconSimulator.getLaunchPop(p, GalconLaunchAction.LaunchSize.LARGE);
	}
	/*
	 * GalconAction utility methods
	 */
	
	public static GalconLaunchAction getMatchingLegalLaunchAction(GalconState s, 
			int sourcePlanetID, int destPlanetID, GalconLaunchAction.LaunchSize launchSize){
		int myAgentId = getMyAgentId(s);
		ArrayList<GalconAction> actions = GalconSimulator.getLegalAction(s, myAgentId);
		for (GalconAction action : actions){
			if (action.getPlayerID() == myAgentId){
				if (action instanceof GalconLaunchAction){
					GalconLaunchAction launchAction = (GalconLaunchAction) action;
					if (launchAction.getLaunchSiteID() == sourcePlanetID){
						if (launchAction.getDestID() == destPlanetID){
							if (launchAction.getSize() == launchSize){
								return launchAction;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public static GalconNothingAction getMatchingNothingAction(GalconState s) {
		int myAgentId = getMyAgentId(s);
		ArrayList<GalconAction> actions = GalconSimulator.getLegalAction(s, myAgentId);
		for (GalconAction action : actions){
			if (action.getPlayerID() == myAgentId){
				if (action instanceof GalconNothingAction){
					return (GalconNothingAction)action;
				}
			}
		}
		return null;
	}
}
