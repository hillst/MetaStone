package edu.oregonstate.eecs.mcplan.domains.galcon;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.oregonstate.eecs.mcplan.domains.galcon.graphics.Monitor;

import edu.oregonstate.eecs.mcplan.Simulator;

public class GalconSimulator extends Simulator<GalconState, GalconAction>{
	public static final int NUMBER_OF_AGENTS = 2;
	public static final int MAX_PLANET_RADIUS = 8;
	public static final int MIN_PLANET_RADIUS = 4;
	private static final double POPULATION_GROWTH_RATE = 0.02;
	
	private int epoch;
	private long max_cycles, seed;
	private boolean monitor_on, stochastic;
	
	public GalconSimulator(long max_cycles, int epoch, boolean monitor_on, boolean stochastic, long seed) {
		this.max_cycles = max_cycles;
		this.epoch = epoch;
		this.monitor_on = monitor_on;
		this.stochastic = stochastic;
		this.seed = seed;
		
		//Handle incorrect epoch?
		if (epoch < 1) {
			//TODO: idk some exception thing
		}
		
		setInitialState();
	}
	
	private GalconSimulator(long max_cycles, int epoch, boolean monitor_on, boolean stochastic, 
			GalconState state, List<GalconAction> legalActions, int[] rewards) {
		this.max_cycles = max_cycles;
		this.epoch = epoch;	
		this.monitor_on = monitor_on;
		this.stochastic = stochastic;
		
		state_ = state;
		legalActions_ = new ArrayList<GalconAction>();
		for (GalconAction a : legalActions) {
			legalActions_.add(a);
		}
		rewards_ = rewards;
	}
	
	public static ArrayList<GalconAction> getLegalAction(GalconState state, int playerID) {
		ArrayList<GalconAction> actions = new ArrayList<GalconAction>();
		
		actions.add(new GalconNothingAction(playerID)); //You can always do nothing!
		
		ArrayList<Planet> friendlyPlanets = state.getPlayerPlanets(playerID);
		if (friendlyPlanets == null || friendlyPlanets.isEmpty()) {
			// log.info("Player[" + playerID + "] doesn't own any planet!");
			// log.info("player:" + state.getPlayer(playerID));
			return actions;
		}
		
		ArrayList<Planet> planets = state.getPlanets();
		if (planets == null || planets.isEmpty()) {
			// log.info("Player[" + playerID + "] owns all planets!");
			return actions;
		}
		for (Planet launchSite : friendlyPlanets) {
			for (Planet destination : planets) {
				for (GalconLaunchAction.LaunchSize size : GalconLaunchAction.LaunchSize.values()) {
					GalconAction launchAction = new GalconLaunchAction(playerID,
							launchSite.getPlanetID(),
							destination.getPlanetID(), size);
					actions.add(launchAction);
				}
			}
		}
		return actions;
	}
	
	private GalconState simulate(GalconAction action, GalconState state)
			throws Exception {
		//okay so this isn't really an 'immutable state' but it's a start right
		GalconState newState = state.copy();

		//Execute actions
		newState = execAction(action, newState);
		
		//Update gamestate
		if (newState.getAgentTurn() == this.getNumberOfAgents()-1) {
			for (int i = 0; i < this.epoch; i++) {
				newState = runSimulationStep(newState); //Run epoch sim steps
			}
		}
		
		for (int i = 0; i < GalconSimulator.NUMBER_OF_AGENTS; i++) {
			int pop = state.getPlayerPopulation(i);
			newState.setLastPop(i, pop);
		}
		
		newState.advanceAgentTurn(); //Next agent's turn
		return newState;
	}
	
	private GalconState runSimulationStep(GalconState state) throws Exception {
		GalconState newState = state.copy(); //this really accomplishes nothing but but 'immutable state'
		for (Planet planet : newState.getPlanets()) {
			updatePopulation(planet);
		}
		
		HashMap<Integer, Planet> planetMap = newState.getPlanetMap();
		for (Player player : newState.getPlayers()) {
			updatePlayerFleet(player, planetMap);
		}
		// Check game over
		for (Player player : newState.getPlayers()) {
			ArrayList<Planet> playerPlanets = newState.getPlayerPlanets(player
					.getPlayerID());
			// log.info("player:" + player);
			if (player.getFleet().isEmpty() && playerPlanets.isEmpty()) {
				// log.info("Player " + player.getPlayerID() + " is defeated.");
				player.setGameOver(true);
			}
		}
		
		newState.setCycle(newState.getCycle() + 1);
		
		if (monitor_on) {
			Monitor.getInstance().updateMonitor(newState);
		}
		
		return newState;
	}
	
	public static int getReward(GalconState state, int playerID) {
		//these used to be doubles but there's no reason for that afaik
		int otherPlayerID = 1-playerID;
		int playerPopulation = state.getPlayerPopulation(playerID);
		int enemyPopulation = state.getPlayerPopulation(otherPlayerID);
		
		//Deltas don't work without a little extra care-- just raw pop for now
		int lastPlayerPop = state.getLastPop(playerID);
		int lastEnemyPop = state.getLastPop(otherPlayerID);
		
		return (playerPopulation - lastPlayerPop) - (enemyPopulation - lastEnemyPop);
	}
	
	public static double getPopulationGrowthRate(Planet planet) {
		int population = planet.getPopulation();
		double delta = POPULATION_GROWTH_RATE
				* (double) population
				* (double) (1.0 - (double) population
						/ (double) planet.getCapacity());
		return delta;
	}
	
	private void updatePopulation(Planet planet) {
		if (planet.getOwnerID() != -1) {
			int population = planet.getPopulation();
			double delta = Math.ceil(getPopulationGrowthRate(planet));
			if (delta > 0) {
				population += delta;
				planet.setPopulation(population);
			}
			// log.info("Update population of planet[" + planet.getPlanetID()
			// + "] from " + (population - delta) + " to " + population);
		}
	}
	
	private GalconState execAction(GalconAction action, GalconState state) throws Exception {
		GalconState newState = null;
		
		//Check legality of action		
		if (!this.legalActions_.contains(action)) {
			throw new IllegalArgumentException("Illegal action " + action);
		}
		
		if (action instanceof GalconLaunchAction) {
			newState = launchSpaceship((GalconLaunchAction) action, state);
			return newState;
		} else if (action instanceof GalconNothingAction) {
			//do nothing ofc
			return state;
		}
		else {
			throw new IllegalArgumentException("Unknown action " + action);
		}
	}
	
	private GalconState launchSpaceship(GalconLaunchAction launchAction, GalconState state)
			throws Exception {
		// log.info("Launch space ship: " + launchAction);
		
		// State is apparently supposed to be immutable so...
		// Well, it's a start at least.
		GalconState newState = state.copy();
		
		if (getLaunchPop(state, launchAction) <= 0) {
			return newState;
		}

		HashMap<Integer, Planet> planetMap = newState.getPlanetMap();
		Planet launchSite = planetMap.get(launchAction.getLaunchSiteID());
		if (launchSite == null) {
			throw new Exception("Invalid launch site ID: "
					+ launchAction.getLaunchSiteID());
		}
		int launchPop = getLaunchPop(state, launchAction);
		int newPop = launchSite.getPopulation() - launchPop;
		if (newPop <= 0) {
			// log.info("Not enough population! current population ("
			// + launchSite.getPopulation() + ") < population to launch"
			// + launchPop);
			// log.info("launchAction: " + launchAction);
			launchPop = (int) ((double) launchSite.getPopulation() * 0.9);
			newPop = launchSite.getPopulation() - launchPop;
		}
		launchSite.setPopulation(newPop);

		Planet destination = planetMap.get(launchAction.getDestID());
		if (destination == null) {
			throw new Exception("Invalid destination ID: "
					+ launchAction.getDestID());
		}
		double d = GeoUtility.distance(launchSite, destination);
		double r1 = (double) launchSite.getRadius();
		double x1 = (double) launchSite.getPosX();
		double x2 = (double) destination.getPosX();

		double y1 = (double) launchSite.getPosY();
		double y2 = (double) destination.getPosY();

		double xl = (x2 - x1) * r1 / d + x1;
		double yl = (y2 - y1) * r1 / d + y1;
		
		Spaceship spaceship = new Spaceship(xl, yl, x2, y2,
				x1, y1, destination.getRadius(), destination.getPlanetID(), 
				launchSite.getPlanetID(), launchPop);
		Player player = newState.getPlayer(launchAction.getPlayerID());
		// log.info("player:" + player);
		player.getFleet().add(spaceship);
		
		return newState;
	}
	
	private void updatePlayerFleet(Player player,
			HashMap<Integer, Planet> planetMap) throws Exception {
		ArrayList<Spaceship> fleet = player.getFleet();
		ArrayList<Spaceship> newFleet = new ArrayList<Spaceship>();
		for (Spaceship spaceship : fleet) {
			double dist = GeoUtility.distance(spaceship.getPosX(),
					spaceship.getPosY(), spaceship.getDestX(),
					spaceship.getDestY());
			// log.info("dist:" + dist + "  radius:"
			// + (double) spaceship.getDestination().getRadius());
			if (dist < spaceship.getDestPlanetRadius()
					|| spaceship.calculateETA() == 0) {
				Planet destPlanet = planetMap.get(spaceship.getDestPlanetID());
				if (destPlanet == null) {
					throw new Exception("Invalid destination planetID: "
							+ spaceship.getDestPlanetID());
				}
				attack(spaceship, player, destPlanet);

			} else {
				spaceship.updateLocation();
				newFleet.add(spaceship);
			}
		}
		player.setFleet(newFleet);
	}
	
	private void attack(Spaceship spaceship, Player player, Planet planet) {
		// log.info("Attack!\n  spaceship:" + spaceship + "\n  playerID:"
		// + player.getPlayerID() + "\n  planet:" + planet);
		if (player.getPlayerID() == planet.getOwnerID()) {
			int newPop = spaceship.getPopulation() + planet.getPopulation();
			// log.info("New population: " + newPop);
			planet.setPopulation(newPop);
		} else {
			int newPop;
			if (this.stochastic) {
				newPop = battle(spaceship.getPopulation(), planet.getPopulation());
			} else {
				newPop = spaceship.getPopulation() - planet.getPopulation();
			}
			//log.info(New population: " + newPop);
			if (newPop > 0) {
				planet.setPopulation(newPop);
				planet.setOwnerID(player.getPlayerID());
			} else {
				planet.setPopulation(-newPop);
			}
		}
	}
	
	private static int battle(int fleet_pop, int planet_pop) {
		Random r = new Random();
		
		while (planet_pop > 0 && fleet_pop > 0) {
			if (r.nextDouble() > 0.5) {
				planet_pop--;
			} else {
				fleet_pop--;
			}
		}
		
		return fleet_pop - planet_pop;
	}
	
	public static GalconState random2PlayerUniverse(int planetBaseNum,
			int initalPopulation, long seed) throws Exception {
		Random random = new Random();
		if (seed != 0) random.setSeed(seed);

		int planetMargin = 2;
		ArrayList<Integer> planetRadiusList = new ArrayList<Integer>();
		for (int r = MIN_PLANET_RADIUS; r <= MAX_PLANET_RADIUS; ++r) {
			planetRadiusList.add(r);
		}
		int maxNumPlanetHalf = 10;

		ArrayList<Point> grid = new ArrayList<Point>();
		for (int x = 1; x <= maxNumPlanetHalf; ++x) {
			for (int y = 1; y <= maxNumPlanetHalf; ++y) {
				Point pos1 = new Point(x, y);
				Point pos2 = new Point(x, -y);
				grid.add(pos1);
				grid.add(pos2);
			}
		}

		ArrayList<Point> planetPos = new ArrayList<Point>();
		for (int i = 0; i < planetBaseNum; ++i) {
			int index = random.nextInt(grid.size());
			planetPos.add(grid.get(index));
			grid.remove(index);
		}

		int playerBasePlanetIndex = random.nextInt(planetBaseNum);
		ArrayList<Planet> planets = new ArrayList<Planet>();
		for (int i = 0; i < planetPos.size(); ++i) {
			Point point = planetPos.get(i);
			int radius = planetRadiusList.get(random.nextInt(planetRadiusList
					.size()));
			int x = point.x * (MAX_PLANET_RADIUS + planetMargin) * 2;
			int y = point.y * (MAX_PLANET_RADIUS + planetMargin) * 2;
			ArrayList<Planet> ps = new ArrayList<Planet>();
			int population = random.nextInt(100);
			ps.add(new Planet(planets.size(), radius, population, x, y, -1));
			ps.add(new Planet(planets.size() + 1, radius, population, -x, -y,
					-1));
			if (playerBasePlanetIndex == i) {
				for (int p = 0; p < ps.size(); ++p) {
					ps.get(p).setOwnerID(p).setPopulation(initalPopulation);
				}
			}
			planets.addAll(ps);
		}

		int mapSize = (MAX_PLANET_RADIUS + planetMargin) * 2 * maxNumPlanetHalf;
		GalconState initState = new GalconState(planets, null, mapSize, mapSize, 0, 0);
		return initState;
	}
    
	public static GalconState random4PlayerUniverse(int planetBaseNum, int numPlayer,
			int initalPopulation, long seed) throws Exception {
		if (numPlayer > 4) {
			throw new Exception(
					"There are more than 4 players in the 4 player-map!");
		}

		Random random = new Random();
		if (seed != 0) random.setSeed(seed);

		int planetMargin = 2;
		ArrayList<Integer> planetRadiusList = new ArrayList<Integer>();
		for (int r = MIN_PLANET_RADIUS; r <= MAX_PLANET_RADIUS; ++r) {
			planetRadiusList.add(r);
		}
		int maxNumPlanetHalf = 10;

		ArrayList<Point> grid = new ArrayList<Point>();
		for (int x = 1; x <= maxNumPlanetHalf; ++x) {
			for (int y = 1; y <= maxNumPlanetHalf; ++y) {
				Point pos = new Point(x, y);
				grid.add(pos);
			}
		}

		ArrayList<Point> planetPos = new ArrayList<Point>();
		for (int i = 0; i < planetBaseNum; ++i) {
			int index = random.nextInt(grid.size());
			planetPos.add(grid.get(index));
			grid.remove(index);
		}

		int playerBasePlanetIndex = random.nextInt(planetBaseNum);
		ArrayList<Planet> planets = new ArrayList<Planet>();
		for (int i = 0; i < planetPos.size(); ++i) {
			Point point = planetPos.get(i);
			int radius = planetRadiusList.get(random.nextInt(planetRadiusList
					.size()));
			int x = point.x * (MAX_PLANET_RADIUS + planetMargin) * 2;
			int y = point.y * (MAX_PLANET_RADIUS + planetMargin) * 2;
			ArrayList<Planet> ps = new ArrayList<Planet>();
			int population = random.nextInt(100);
			ps.add(new Planet(planets.size(), radius, population, x, y, -1));
			ps.add(new Planet(planets.size() + 1, radius, population, -x, -y,
					-1));
			ps.add(new Planet(planets.size() + 2, radius, population, -y, x, -1));
			ps.add(new Planet(planets.size() + 3, radius, population, y, -x, -1));
			if (playerBasePlanetIndex == i) {
				for (int p = 0; p < ps.size(); ++p) {
					if (numPlayer > p) {
						ps.get(p).setOwnerID(p).setPopulation(initalPopulation);
					}
				}
			}
			planets.addAll(ps);
		}

		int mapSize = (MAX_PLANET_RADIUS + planetMargin) * 2 * maxNumPlanetHalf;

		ArrayList<Player> players = new ArrayList<Player>();
		GalconState initState = new GalconState(planets, players, mapSize, mapSize, 0, 0);
		return initState;
	}
	
	private void computeRewards() {
		int[] rewards = new int[NUMBER_OF_AGENTS];
		for (int i = 0; i < NUMBER_OF_AGENTS; i++) {
			rewards[i] = GalconSimulator.getReward(state_, i);
		}
		rewards_ = rewards;
	}
	
	private void computeLegalActions() {
		legalActions_ = GalconSimulator.getLegalAction(state_, state_.getAgentTurn());
	}
	
	@Override
	public Simulator<GalconState, GalconAction> copy() {
		return new GalconSimulator(max_cycles, epoch, false, stochastic, state_, legalActions_, rewards_);
	}

	@Override
	public void setInitialState() {
		try {
			state_ = GalconSimulator.random2PlayerUniverse(10, 100, seed);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		state_.createPlayers(NUMBER_OF_AGENTS);
		
		if (monitor_on) {
			try {
				Monitor.createMonitor(state_.getMapWidth(), state_.getMapHeight());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
			Monitor.getInstance().showWindow(true);
		}
		
		computeRewards();
		computeLegalActions();
	}

	@Override
	public void setState(GalconState state) {
		state_ = state;	

		computeRewards();
		computeLegalActions();
	}

	@Override
	public void setState(GalconState state, List<GalconAction> legalActions) {
		state_ = state;
		legalActions_ = legalActions;

		computeRewards();

	}

	@Override
	public void takeAction(GalconAction action) {
		try {
			GalconState newState = simulate(action, state_);
			state_ = newState;
		} catch (Exception e) {
			//TODO I can't help but feel like we should actually handle an exception here
			e.printStackTrace();
			System.exit(1);
		}

		computeRewards();
		computeLegalActions();

	}

	@Override
	public int getNumberOfAgents() {
		return NUMBER_OF_AGENTS;
	}

	@Override
	public double[] getFeatureVector(GalconAction action) {
		throw new IllegalStateException("Unimplemented");
	}
	
	@Override
	public boolean isTerminalState() {
		return state_.isGameOver() || (state_.getCycle() > max_cycles && max_cycles != -1);
	}
	
	public static int getLaunchPop(Planet p, GalconLaunchAction.LaunchSize size) {
		int planetPop = p.getPopulation();
		double pct = 0;
		switch (size) {
		case SMALL:
			pct = 0.15;
			break;
		case HALF:
			pct = 0.5;
			break;
		case LARGE:
			pct = 0.85;
			break;
		}
		
		return (int)(planetPop * pct);
	}
	
	public static int getLaunchPop(GalconState s, GalconLaunchAction a) {
		Planet source = s.getPlanetMap().get(a.getLaunchSiteID());
		return getLaunchPop(source, a.getSize());
	}
}
