package edu.oregonstate.eecs.mcplan.domains.galcon;

public class GeoUtility {

	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	public static double distance(int x1, int y1, int x2, int y2) {
		return distance((double) x1, (double) y1, (double) x2, (double) y2);
	}

	public static double distance(Planet a, Planet b) {
		return distance(a.getPosX(), a.getPosY(), b.getPosX(), b.getPosY());
	}
}
