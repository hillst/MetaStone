package edu.oregonstate.eecs.mcplan.domains.galcon;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {

	private int playerID = -1;

	private String playerName = null;

	private ArrayList<Spaceship> fleet = new ArrayList<Spaceship>();

	private boolean gameOver = false;

	public Player(int id, String playerName, ArrayList<Spaceship> fleet,
			boolean gameOver) {
		this.playerID = id;
		this.playerName = playerName;
		this.fleet = fleet;
		this.gameOver = gameOver;
	}

	public int getPlayerID() {
		return playerID;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Player[" + playerID + "]  playerName:" + playerName
				+ "  fleet size:" + fleet.size());
		if (!fleet.isEmpty()) {
			for (int i = 0; i < fleet.size(); ++i) {
				Spaceship spaceship = fleet.get(i);
				buffer.append("\n  spaceship[" + i + "]: " + spaceship);
			}
		}
		return buffer.toString();
	}

	public ArrayList<Spaceship> getFleet() {
		return fleet;
	}

	public void setFleet(ArrayList<Spaceship> fleet) {
		this.fleet = fleet;
	}

	public Player copy() {
		String name = new String(playerName);
		ArrayList<Spaceship> newFleet = null;
		if (fleet != null) {
			newFleet = new ArrayList<Spaceship>();
			for (Spaceship spaceship : fleet) {
				newFleet.add(spaceship.copy());
			}
		}
		Player newPlayer = new Player(playerID, name, newFleet, gameOver);
		return newPlayer;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameover) {
		this.gameOver = gameover;
	}

	public String getPlayerName() {
		return playerName;
	}
}

