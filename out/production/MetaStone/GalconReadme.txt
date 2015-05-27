 Description of the Game:
	Our Galcon game starts in space, with planets of random sizes placed
	symmetrically across the play area. One pair of the planets are chosen as 
	the player's home planets, and each player gains control of one of them at
	the start of the game.
	Every step of the game each player must take an action. Sending a certain
	amount of population from one planet they own to any another planet is a 
	valid action, as is doing nothing.
	When a player chooses to send a ship from one of their source planets to
	another planet, a spaceship will be created at the source planet with the
	specified population. That population will be subtracted from the source
	planet's population. The ship will then fly through space until it reaches
	its destination. When it arrives, if the planet is controlled by an enemy
	or neutral player the population on the ship and the population on the 
	planet fight one another. The side with the larger population usually wins,
	but not always. The larger the population advantage, the better that side's
	chance of winning. After the battle, the planet's population is set to the 
	amount of population on the winning side and the winning player gains
	control of the planet.
	Every game step the population on each planet grows based on the planet's
	size. Bigger planets have larger growth rates and larger population caps, so
	they tend to be more valuable!

Using MCPlan:
	If the project is run with no parameters, it defaults to interactive mode. 
	It will ask which domain you wish to run-- select the 'Galcon Preset'
	domain. It will then ask you to select two agents to play the game against
	each other. Select one from the list, but know that some agents will ask
	you to provide parameters or even select another 'base' agent.
	Once the agents are selected a visualization window will open to display
	the game as it plays out between the two agents. The library will output
	information about the current state of the game and the actions the agents
	are taking at each step, and when the game is done it will print out the 
	ending rewards, indicating which agent has won.
	
More on Agents:
	The two simplest agents you can use are the 'Random' and 'DumbGalconAgent'
	agents. Both require no configuration after selecting them. The 'Random'
	agent simply selects a random legal action each turn and plays it, while the
	'DumbGalconAgent' agent is more aware of the state of the Galcon game it's
	playing-- however, it doesn't make very good decisions. It still should be a
	good starting point for another, more intelligent simple agent though!
	
	The 'PolicyRollout' agent is more complicated. (I suppose I probably 
	shouldn't explain what it is since it's a topic in the course). It has a few
	parameters you need to specify.
	Width: The number of times it will run each legal action. The end reward of
	 the action will be calculated by averaging the rewards gained from each
	 simulation. Only needs to be more than 1 if the simulation is random!
	Height: The number of steps each simulation will be run for before checking
	 the rewards. -1 runs it all the way until the horizon, which is probably
	 what you want in this case.
	Base Agent: You'll be asked to provide another agent as the base policy for
	 PolicyRollout. You should probably choose a simpler policy, like Random or
	 something like the DumbGalconAgent.
	In non-interactive mode, the format for a PolicyRollout agent is:
	 PolicyRollout (Width) (Height) (BaseAgent) (BaseArgs)
	 ...without parens, of course.

Classes:
GalconAction
	Parent class for all Galcon actions. You shouldn't instantiate these 
	 directly.
	 
	Methods-
		int getPlayerID();
		  Returns the ID of the player executing the action.
	
GalconLaunchAction
	Represents the action of launching a certain number of ships from one 
	 planet that the player owns to any other planet on the field.
	 
	Methods-
		GalconLaunchAction(int playerID, int launchSiteID, int destID, 
		 int population);
		   Constructor for the class. PlayerID is the player executing the
		   action, launchSiteID is the ID of the source planet, destID is
		   the ID of the destination planet, and population is the number
		   of ships to send.
		   
		int getPopulation();
		int getLaunchSiteID();
		int getDestID();
		   Getters for the respective variables.


GalconNothingAction
	Represents the action of doing nothing-- basically, passing a turn.
	
	Methods-
		GalconNothingAction(int playerID);
		   Constructor for the class. PlayerID is the player executing the
		   action.


GalconSimulator
	The driving simulator for the game. No need to instantiate one when
	writing an agent, but you may make use of some of its methods.
	
	Methods-
		static ArrayList<GalconAction> getLegalAction(GalconState state,
		 int playerID);
		   Returns an ArrayList of currently legal actions for the specified
		   player in the given state.
		   
		static int getReward(GalconState state, int playerID);
		   Returns the reward in the given state for the player provided. The 
		   reward is calculated as the change in the given player's population 
		   since the previous timestep minus the change in the player's 
		   opponent's population since the previous timestep.
		
		static double getPopulationGrowthRate(Planet planet);
			Returns the amount of population the given planet will produce in
			the next timestep.
		
		static ArrayList<Double> getLegalLaunchRatios();
			(NOTE - this method will likely be moved to GalconState or at least
			somewhere less weird and awkward)
			Returns an array list of the legal ratios of fleet population to
			planet population that can be launched from a planet.
		   
		GalconState simulate(ArrayList<GalconAction> actions, 
		 GalconState state);
		   (NOTE - this method's signature is likely going to change)
		   Returns a new state that represents the current game state after the
		   action in the ArrayList parameter have been executed. The 
		   simulation's time will not move forward until each player has a 
		   submitted action. In the current implementation, the 'actions' 
		   ArrayList should have exactly one Action in it that represents the
		   action to be taken.
		   
		void setState(GalconState state);
		   Sets the simulator's internal state to the given state.
		void setState(GalconState state, List<GalconAction> legalActions);
		   (NOTE - students probably won't need to use this)
		   Same as above, but overrides the calculated legal actions with
		   those is the provided list.
		   
		void takeAction(GalconAction action);
			Updates the internal gamestate to reflect that the provided action
			has been taken.
			
		int getNumberOfAgents();
			Returns the number of agents. Will always be 2.
			
		boolean isTerminalState();
			Returns true if the current state reflects a completed game (i.e 
			the game is over).


GalconState
	An object representing the current game state-- e.g. the current position
	of player's fleets, population of planets, location of planets, etc.
	
	Methods-
		int getPlayerPopulation(int playerID);
		   Returns the given player's total population on the board, planets
		   and fleets included.
		
		int getOverallPopulation();
		   Returns the total population on the board of both players and all
		   neutral planets.
			
		ArrayList<Planet> getPlayerPlanets(int playerID);
		   Returns an ArrayList of planets the given player currently owns.
		   The 'neutral' player has a PlayerID of -1. If you want to get a list
		   of all the neutral planets, call this with -1 as the parameter.
			
		ArrayList<Planet> getNonFriendlyPlanets(int playerID);
		   Returns an ArrayList of planets the given player currently doesn't 
		   own.
			
		Player getPlayer(int playerID);
		   Returns a the Player object represented by the given playerID.
		   Usually used when you need to examine a player's fleets.
			
		HashMap<Integer, Planet> getPlanetMap();
		   Returns a map that associates the planet's IDs to the Planet
		   objects they represent.
			
		ArrayList<Planet> getPlanets();
		   Returns an ArrayList of all planets.
		
		ArrayList<Player> getPlayers();
		   Same for players.
			
		GalconState copy();
		   Returns a copy of the state.
		  
		boolean isGameOver();
		   Returns true if the state reflects a completed game.
		
		int getMapWidth();
		int getMapHeight();
		   Returns the play field's width and height.
		  
		long getCycle();
		   Returns the 'tick' of the game that this state represents.
			
		int getAgentTurn();
		   Return which agent's turn it is. If you need to figure out which
		   player you are in your agent's select action function, this is
		   probably the way you should do it!
		   Note that since there are always two players in the game and the
		   PlayerID is just the index of the player, you can always get your
		   opponent's ID by calculating 1-getAgentTurn().
		   
GeoUtility
	Basically a container class for a distance function that's easy to use
	with different Galcon objects. Has a single static distance function that
	can calculate the distance between two arbitrary points or two given Planet
	objects.

Planet
	A class that represents a single Planet on the play field, including its
	size, owner, position, and current population.
	
	Methods-
		boolean isOccupied();
		   Returns true if the planet is currently owned by one of the two
		   players, i.e. it isn't neutral.
		int getPosX();
		int getPosY();
		int getRadius();
		int getPopulation();
		int getOwnerID();
		int getCapacity();
		int getPlanetID();
		   Getters for various attributes.

Player
	A class that represents one of the players in the game. When referenced in
	an agent, it's most likely to get information about the current state of
	the player's fleets.
	
	Methods-
		int getPlayerID();
		   Returns the player's ID.
		ArrayList<Spaceship> getFleet();
		   Returns an ArrayList of spaceship objects that make up the player's
		   active spaceships, i.e. the ones flying around.
		void setFleet(ArrayList<Spaceship> fleet);
		   Sets the state of the player's fleet. May be useful for some weird
		   simulation stuff.
		boolean isGameOver();
		   Returns true if the player has lost, but really you should look at
		   the current state's rewards instead.

Spaceship
	A class that represents one single fleet flying from one planet to another.
	
	Methods-
		int calculateETA();
			Returns the number of timesteps before the fleet reaches its
			target.
		double getPosX();
		double getPosY();
			Getters for the ship's current position.
		int getPopulation();
			Returns the population (i.e. size) of the ship.
		int getDestPlanetID();
			Returns the ID of the planet the ship is headed for.
		double getDestX();
		double getDestY();
			Getters for the specific location of the ship's destination.
		double getDestPlanetRadius();
			Returns the radius of the destination planet.
