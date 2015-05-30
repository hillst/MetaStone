package edu.oregonstate.eecs.mcplan.domains.galcon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import edu.oregonstate.eecs.mcplan.State;

public class GalconState implements State {

	//private static final Logger log = Logger.getLogger(State.class.getName());

	private ArrayList<Planet> planets = null;

	private ArrayList<Player> players = null;

	private long cycle = 0;

	private HashMap<Integer, Planet> planetMap = null;

	private int mapWidth = 0;

	private int mapHeight = 0;
	
	private int agentTurn;
	
	private int lastPop[] = new int[2];

	
	public GalconState(ArrayList<Planet> planets, ArrayList<Player> players,
			int mapWidth, int mapHeight, long cycle, int turn, int[] lastPop) {
		this.planets = planets;
		this.players = players;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.cycle = cycle;
		this.agentTurn = turn;
		this.lastPop = lastPop;
		updateStateSummary();
	}
	
	public GalconState(ArrayList<Planet> planets, ArrayList<Player> players,
			int mapWidth, int mapHeight, long cycle, int turn) {
		this.planets = planets;
		this.players = players;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.cycle = cycle;
		this.agentTurn = turn;
		updateStateSummary();
	}

	public void updateStateSummary() {
		// Update mean planet radius and mean population growth rate
		if (planets != null) {
			planetMap = new HashMap<Integer, Planet>();
			for (Planet planet : planets) {
				planetMap.put(planet.getPlanetID(), planet);
			}
		}
	}

	public int getPlayerPopulation(int playerID) {
		int population = 0;
		ArrayList<Planet> planets = getPlayerPlanets(playerID);
		for (Planet planet : planets) {
			population += planet.getPopulation();
		}
		for (Spaceship spaceship : getPlayer(playerID).getFleet()) {
			population += spaceship.getPopulation();
		}
		return population;
	}

	public int getOverallPopulation() {
		int population = 0;
		for (Planet planet : planets) {
			population += planet.getPopulation();
		}
		for (Player player : players) {
			for (Spaceship spaceship : player.getFleet()) {
				population += spaceship.getPopulation();
			}
		}
		return population;
	}

	public ArrayList<Planet> getPlayerPlanets(int playerID) {
		ArrayList<Planet> ps = new ArrayList<Planet>();
		for (Planet p : planets) {
			if (p.getOwnerID() == playerID) {
				ps.add(p);
			}
		}
		return ps;
	}

	public ArrayList<Planet> getNonFriendlyPlanets(int playerID) {
		ArrayList<Planet> ps = new ArrayList<Planet>();
		for (Planet p : planets) {
			if (p.getOwnerID() != playerID) {
				ps.add(p);
			}
		}
		return ps;
	}

	public Player getPlayer(int playerID) {
		for (Player p : players) {
			if (p.getPlayerID() == playerID) {
				return p;
			}
		}
		//log.info("Invalid player: " + playerID);
		return null;
	}

	public HashMap<Integer, Planet> getPlanetMap() {
		return planetMap;
	}

	public ArrayList<Planet> getPlanets() {
		return planets;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public GalconState copy() {
		ArrayList<Planet> newPlanets = null;
		if (planets != null) {
			newPlanets = new ArrayList<Planet>();
			for (Planet planet : planets) {
				newPlanets.add(planet.copy());
			}
		}

		ArrayList<Player> newPlayers = new ArrayList<Player>();
		if (players != null) {
			newPlayers = new ArrayList<Player>();
			for (Player player : players) {
				newPlayers.add(player.copy());
			}
		}

		GalconState newState = new GalconState(newPlanets, newPlayers, mapWidth, mapHeight,
				cycle, agentTurn, lastPop);
		return newState;
	}

//	public void createPlayers(ArrayList<AbstractAgent> agentList) {
	public void createPlayers(int numAgents) {
		players = new ArrayList<Player>();
		//int numAgents = agentList.size();
		for (int i = 0; i < numAgents; ++i) {
			/* Unnecessary - Agents know which player they are?
			AbstractAgent agent = agentList.get(i);
			agent.setPlayerID(i);
			String agentName = agent.getName();
			*/
			String agentName = "Agent" + Integer.toString(i);
			players.add(new Player(i, agentName,
					new ArrayList<Spaceship>(), false));
		}
		for (Planet planet : planets) {
			if (planet.getOwnerID() >= players.size()) {
				planet.setOwnerID(-1);
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Cycle #" + cycle + " - ");
		buffer.append("Player " + agentTurn + "'s turn\n");
		buffer.append("Planets:\n");
		for (Planet planet : planets) {
			buffer.append("  " + planet + "\n");
		}
		buffer.append("Players:\n");
		for (Player player : players) {
			buffer.append("  " + player + "\n");
		}
		return buffer.toString();
	}

	public boolean isGameOver() {
		int gameOverCount = 0;
		for (Player player : players) {
			if (player.isGameOver()) {
				++gameOverCount;
			}
		}

		if (gameOverCount + 1 >= players.size()) {
			return true;
		} else {
			return false;
		}
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public long getCycle() {
		return cycle;
	}

	public void setCycle(long cycle) {
		this.cycle = cycle;
	}
	
	public int getAgentTurn() {
		return agentTurn;
	}
	
	public void advanceAgentTurn() {
		agentTurn++;
		if (agentTurn == GalconSimulator.NUMBER_OF_AGENTS) { //wrap around
			agentTurn = 0;
		}
	}
	
	public int getLastPop(int player) {
		return lastPop[player];
	}
	
	public void setLastPop(int player, int amt) {
		lastPop[player] = amt;
	}
}
