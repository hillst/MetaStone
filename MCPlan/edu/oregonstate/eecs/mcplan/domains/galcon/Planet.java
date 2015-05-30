package edu.oregonstate.eecs.mcplan.domains.galcon;

import java.io.Serializable;

public class Planet implements Serializable, Comparable<Planet> {

	private int planetID = -1;

	private int radius = 0;

	private int capacity = 0;

	private int population = 0;

	private int posX = 0;

	private int posY = 0;

	private int ownerID = 0; // Player ID of owner

	private static int calculatePlanetCapacity(int radius) {
		return 20 * radius * radius;
	}

	public Planet(int id, int radius, int population, int posX, int posY,
			int ownerID) {
		this.planetID = id;
		this.radius = radius;
		this.population = population;
		this.posX = posX;
		this.posY = posY;
		this.capacity = calculatePlanetCapacity(this.radius);
		this.ownerID = ownerID;
	}

	public Planet copy() {
		Planet newPlanet = new Planet(planetID, radius, population, posX, posY,
				ownerID);
		// newPlanet.setPopulation(population);
		return newPlanet;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getRadius() {
		return radius;
	}

	public boolean isOccupied() {
		return ownerID >= 0;
	}

	public Planet setOwnerID(int ownerID) {
		this.ownerID = ownerID;
		return this;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	@Override
	public String toString() {
		return "Planet[" + planetID + "](" + posX + "," + posY + ")  radius:"
				+ radius + "  population:" + population + "  capacity:"
				+ capacity + "  ownerID:" + ownerID;
	}

	public int getPopulation() {
		return population;
	}

	public int getOwnerID() {
		return ownerID;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getPlanetID() {
		return planetID;
	}

	@Override
	public int compareTo(Planet o) {
		return population - o.getPopulation();
	}
}

