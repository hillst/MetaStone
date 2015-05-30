package edu.oregonstate.eecs.mcplan.domains.galcon;

import java.io.Serializable;

public class Spaceship implements Serializable {

	private double posX = 0;
	private double posY = 0;
	private double destPlanetRadius = 0;
	private int destPlanetID = -1;	
	private double destX = 0;
	private double destY = 0;
	private int sourcePlanetID = -1;
	private double sourceX = 0;
	private double sourceY = 0;
	private int population = 0;

	public static final double speed = 3.0;

	public Spaceship(double posX, double posY, double destX, double destY,
			double sourceX, double sourceY, double destPlanetRadius, 
			int destPlanetID, int sourcePlanetID, int population) {
		this.posX = posX;
		this.posY = posY;
		this.destX = destX;
		this.destY = destY;
		this.destPlanetRadius = destPlanetRadius;
		this.destPlanetID = destPlanetID;
		this.sourcePlanetID = sourcePlanetID;
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.population = population;
	}

	public int calculateETA() {
		double dist = GeoUtility.distance(posX, posY, destX, destY);
		return (int) Math.round(dist / speed);
	}

	public void updateLocation() {
		double dist = GeoUtility.distance(posX, posY, destX, destY);
		if (dist != 0.0) {
			posX = (destX - posX) * speed / dist + posX;
			posY = (destY - posY) * speed / dist + posY;
		}
	}

	@Override
	public String toString() {
		return "From(" + Math.round(posX) + ", " + Math.round(posY) + ") To (" + Math.round(destX) + ", " + Math.round(destY)
				+ ")  population: " + population + "  ETA: " + calculateETA();
	}

	public Spaceship copy() {
		Spaceship newSpaceship = new Spaceship(posX, posY, destX, destY,
				sourceX, sourceY, destPlanetRadius, destPlanetID, 
				sourcePlanetID, population);
		return newSpaceship;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	public int getPopulation() {
		return population;
	}

	public int getDestPlanetID() {
		return destPlanetID;
	}
	
	public int getSourcePlanetID() {
		return sourcePlanetID;
	}
	
	public double getDistFromSource() {
		return GeoUtility.distance(posX, posY, destX, destY);
	}

	public double getDestX() {
		return destX;
	}

	public double getDestY() {
		return destY;
	}
	
	public double getSourceX() {
		return sourceX;
	}

	public double getSourceY() {
		return sourceY;
	}

	public double getDestPlanetRadius() {
		return destPlanetRadius;
	}
}

