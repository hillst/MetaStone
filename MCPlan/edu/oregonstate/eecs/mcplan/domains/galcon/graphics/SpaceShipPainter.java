package edu.oregonstate.eecs.mcplan.domains.galcon.graphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import edu.oregonstate.eecs.mcplan.domains.galcon.GeoUtility;
import edu.oregonstate.eecs.mcplan.domains.galcon.Player;
import edu.oregonstate.eecs.mcplan.domains.galcon.Spaceship;

public class SpaceShipPainter {

	public static void plaint(Graphics2D g, Spaceship spaceship, Player player) {		
		Monitor monitor = Monitor.getInstance();
		int shipX = monitor.screenX(spaceship.getPosX());
		int shipY = monitor.screenY(spaceship.getPosY());
		int radius = monitor.scale(2);
		int x = shipX - radius;
		int y = shipY - radius;
		int width = radius;
		int length = (int)(radius*1.5);
		Color col;
		if (player.getPlayerID() == 0) {
			col = Color.GREEN;
		} else if (player.getPlayerID() == 1) {
			col = Color.RED;
		} else if (player.getPlayerID() == 2) {
			col = Color.BLUE;
		} else if (player.getPlayerID() == 3) {
			col = Color.MAGENTA;
		} else {
			col = Color.YELLOW;
		}
		
		float dist = (float)GeoUtility.distance(spaceship.getPosX(), spaceship.getPosY(),
				spaceship.getSourceX(), spaceship.getSourceY());
		float brightness;
		
		float min_dist = 50, max_dist = 200, min_b = 0.4f;
		if (dist < min_dist) {
			brightness = 1;
		} else if (dist > max_dist) {
			brightness = min_b;
		} else {
			brightness = 1.f-((dist-min_dist)/(max_dist-min_dist))*min_b;;
		}
		
		col = new Color((int)(col.getRed()*brightness), (int)(col.getGreen()*brightness),
				(int)(col.getBlue()*brightness));
		
		g.setColor(col);
		//this is a mess
		//draws rotated triangles
		double dx = spaceship.getDestX() - spaceship.getSourceX();
		double dy = spaceship.getDestY() - spaceship.getSourceY();
		double theta = Math.atan2(dx, dy);
		
		int midX = shipX;
		int midY = shipY;
		int[] xOff = {midX-width, midX, midX+width};
		int[] yOff = {midY+length, midY-length, midY+length};
		double[] xPts = new double[3];
		double[] yPts = new double[3];
		int minY = monitor.getHeight();
		
		for (int i = 0; i < 3; i++) {
			double xp = midX + (xOff[i] - midX) * Math.cos(theta) - (yOff[i] - midY) * Math.sin(theta);
			double yp = midY + (xOff[i] - midX) * Math.sin(theta) + (yOff[i] - midY) * Math.cos(theta);
			xPts[i] = (int)xp;
			yPts[i] = (int)yp;
			if (yp < minY) minY = (int)yp;
		}
		
		Path2D.Double path = new Path2D.Double();
		path.moveTo(xPts[0], yPts[0]);
		path.lineTo(xPts[1], yPts[1]);
		path.lineTo(xPts[2], yPts[2]);
		path.lineTo(xPts[0], yPts[0]);
		
		g.fill(path);
		
		// Paint population
		g.setColor(Color.WHITE);
		FontMetrics fm = g.getFontMetrics(g.getFont());
		String pop = String.valueOf(spaceship.getPopulation());
		int twidth = fm.stringWidth(pop);
		g.drawString(pop, shipX - twidth / 2, minY);
	}
}
