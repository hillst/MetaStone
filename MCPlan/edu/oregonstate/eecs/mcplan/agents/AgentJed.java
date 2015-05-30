package edu.oregonstate.eecs.mcplan.agents;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import edu.oregonstate.eecs.mcplan.domains.galcon.*;

public class AgentJed extends Agent {

	private Simulator simulator = null;
	public AgentJed() {
		name_ = "AgentJed";
	}
	
	@Override
	public <S extends State, A> A selectAction(S state,
			Simulator<S, A> iSimulator) {
		    simulator = iSimulator;
			return (A)jedStrategy((GalconState)state);
	}
	
	private GalconAction jedStrategy(GalconState state){
		// if large unoccupied planet close to my biggest planet, go there
		int id = AgentHelper.getMyAgentId(state);
		GalconAction action = targetCloseUnoccupiedLargePlanet(state);
		if (null != action){
			System.out.println("used it!");
			return action;
		}
		// else if a planet I own is close to a planet that is occupied by the other player, but that has less than half my population, send half population there
		
		return randomStrategy(state);
	}
	private <T> boolean isListWithContents(List<T> list){
		if (null == list) return false;
		if (list.size() == 0) return false;
		return true;
	}
	
	//private Planet getClosestPlanet
	private GalconAction targetCloseUnoccupiedLargePlanet(GalconState state){
		//determine what large means
		int thresh = getLargenessThreshold(state);
		//get list of unoccupied planets, larger than threshold
		List<Planet> emptyPlanets = AgentHelper.getNeutralPlanets(state);
		if (!isListWithContents(emptyPlanets)){
			return null;
		}
		List<Planet> largeEmptyPlanets = getPlanetsLargerThanThreshold(emptyPlanets, thresh);
		if (!isListWithContents(largeEmptyPlanets)){
			return null;
		}
		Planet myBigPopPlanet = AgentHelper.getMyMostPopulatedPlanet(state);
		if (null == myBigPopPlanet){
			return null;
		}
		Planet closestEmpty = AgentHelper.getClosestPlanet(largeEmptyPlanets, myBigPopPlanet);
		if (null == closestEmpty){
			return null;
		}
		//int crewSize = AgentHelper.getLargestLegalShipCapacity(myBigPopPlanet);
		GalconLaunchAction.LaunchSize launchSize = GalconLaunchAction.LaunchSize.LARGE;
		GalconLaunchAction launchAction = AgentHelper.getMatchingLegalLaunchAction(state,  myBigPopPlanet.getPlanetID(), closestEmpty.getPlanetID(), launchSize);
		return launchAction;
	}
	private List<Planet> getPlanetsLargerThanThreshold(List<Planet> planets, int thresh){
		ArrayList<Planet> largePlanets = new ArrayList<Planet>();
		for (Planet p : planets){
			if (p.getCapacity() >= thresh){
				largePlanets.add(p);
			}
		}
		return largePlanets;
	}
	
	
	private int getLargenessThreshold(GalconState state){
		List<Planet> planets = state.getPlanets();
		double maxCapacity = 0;
		double minCapacity = 100000000;
		for (Planet p : planets){
			int c = p.getCapacity();
			if (c > maxCapacity){
				maxCapacity = c;
			}
			if (c < minCapacity){
				minCapacity = c;
			}
		}
		double delta = maxCapacity - minCapacity;
		double onefourth = delta / 4;
		double thresh = maxCapacity - onefourth;
		return (int)thresh;
	}
	
	private GalconAction dumbStrategy(GalconState state) {
		int playerID = state.getAgentTurn();
		Planet source = AgentHelper.getMyMostPopulatedPlanet(state);
		if (source == null) {
			return new GalconNothingAction(playerID);
		}
		//int amt = AgentHelper.getLargestLegalShipCapacity(source);
		GalconLaunchAction.LaunchSize launchSize = GalconLaunchAction.LaunchSize.LARGE;
		Planet dest = AgentHelper.getFarthestUntargetedPlanet(state, source);
		if (dest == null) {
			return new GalconNothingAction(playerID);
		}
		GalconLaunchAction launchAction = AgentHelper.getMatchingLegalLaunchAction(state,  source.getPlanetID(), dest.getPlanetID(), launchSize);
		return launchAction;
	}
	
	private GalconAction randomStrategy(GalconState state) {
		simulator.setState(state);
        List<GalconAction> actions = simulator.getLegalActions();
        return actions.get((int) (Math.random() * actions.size()));
	}
	
}
