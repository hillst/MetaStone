package edu.oregonstate.eecs.mcplan.domains.galcon.graphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import edu.oregonstate.eecs.mcplan.domains.galcon.Planet;

public class PlanetPainter {

	public static void plaint(Graphics2D g, Planet planet, boolean showID) {
		Monitor monitor = Monitor.getInstance();
		// Paint planet
		int planetX = monitor.screenX(planet.getPosX());
		int planetY = monitor.screenY(planet.getPosY());
		int radius = monitor.scale(planet.getRadius());

		int x = planetX - radius;
		int y = planetY - radius;
		int length = radius * 2;
		if (planet.getOwnerID() == 0) {
			g.setColor(Color.GREEN);
		} else if (planet.getOwnerID() == 1) {
			g.setColor(Color.RED);
		} else if (planet.getOwnerID() == 2) {
			g.setColor(Color.BLUE);
		} else if (planet.getOwnerID() == 3) {
			g.setColor(Color.MAGENTA);
		} else {
			g.setColor(Color.YELLOW);
		}
		g.fillOval(x, y, length, length);

		// Paint population
		g.setColor(Color.WHITE);
		FontMetrics fm = g.getFontMetrics(g.getFont());
		String pop = String.valueOf(planet.getPopulation());
		int width = fm.stringWidth(pop);
		g.drawString(pop, planetX - width / 2, y);
		
		if (showID) {
			g.setColor(Color.BLACK);
			String id = String.valueOf(planet.getPlanetID());
			width = fm.stringWidth(id);
			int height = fm.getHeight();
			g.drawString(id, planetX - width/2, planetY + height/4);
		}
	}
}
