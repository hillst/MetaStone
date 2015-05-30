package edu.oregonstate.eecs.mcplan.domains.galcon.graphics;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.oregonstate.eecs.mcplan.domains.galcon.GalconAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconLaunchAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconNothingAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconSimulator;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconState;
import edu.oregonstate.eecs.mcplan.domains.galcon.Planet;
import edu.oregonstate.eecs.mcplan.domains.galcon.Player;
import edu.oregonstate.eecs.mcplan.domains.galcon.Spaceship;

public class Monitor extends Canvas implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -5637565189353429031L;

	//private static final Logger log = Logger.getLogger(Monitor.class.getName());

	/** The stragey that allows us to use accelerate page flipping */
	private BufferStrategy strategy = null;
	private JFrame container = null;
	private int mapLimX = 0;
	private int mapLimY = 0;
	private final double mapScale = 0.85;
	private final double lengthScale = 2.0;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private static Monitor monitor = null;	
	private int maxPopulation = 0;	
	private int wait_ = 0;	
	private ArrayList<String> agents;	
	
	private boolean playback;
	private double time = 0;
	private Rectangle bar;
	private int bar_minX, bar_maxX;
	private boolean drag;
	private Point startPoint;

	public static void createMonitor(final int mapWidth, final int mapHeight)
			throws Exception {
		if (monitor == null) {
			monitor = new Monitor(mapWidth, mapHeight);
		}
		// else {
		// throw new Exception("Monitor already exists!");
		// }
	}
	
	public static void createMonitor(final int mapWidth, final int mapHeight, ArrayList<String> agents)
			throws Exception {
		if (monitor == null) {
			monitor = new Monitor(mapWidth, mapHeight, agents);
		}
		// else {
		// throw new Exception("Monitor already exists!");
		// }
	}

	public static Monitor getInstance() {
		return monitor;
	}
	
	private Monitor(final int width, final int height, ArrayList<String> agents) {
		this(width, height);
		this.agents = agents;
		this.playback = true;
		initBar();
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	private Monitor(final int width, final int height) {
		this.playback = false;
		this.mapLimX = width;
		this.mapLimY = height;
		//log.info("mapLimX: " + mapLimX);
		//log.info("mapLimY: " + mapLimY);
		this.screenWidth = width * 4;
		this.screenHeight = height * 4;
		// log.info("screenWidth: " + this.screenWidth);
		// log.info("screenHeight:" + this.screenHeight);
		// create a frame to contain our game
		container = new JFrame("GalconAI Monitor v0.1");

		// get hold the content of the frame and set up the resolution of the
		// game
		final JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(screenWidth, screenHeight));
		panel.setLayout(null);

		// setup our canvas size and put it into the content of the frame
		setBounds(0, 0, screenWidth, screenHeight);
		panel.add(this);

		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);

		// finally make the window visible
		container.pack();
		container.setResizable(false);
		showWindow(false);

		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit the game
		container.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});

		// add a key input system (defined below) to our canvas
		// so we can respond to key pressed
		// TODO
		// addKeyListener(new KeyInputHandler());

		// request the focus so key events come to us
		requestFocus();

		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		final Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		// initialise the entities in our game so there's something
		// to see at startup
		// TODO
		// initEntities();
	}
	
	public void setWait( final int wait )
	{
		assert( wait >= 0 );
		wait_ = wait;
	}
	
	public void drawAction(final GalconAction action, final GalconState state) {		
		if (action instanceof GalconNothingAction) {
			drawAction((GalconNothingAction)action, state);
		} else if (action instanceof GalconLaunchAction) {
			drawAction((GalconLaunchAction)action, state);
		}
	}
	
	private void drawAction(final GalconNothingAction action, final GalconState state) {
		//Don't draw anything for nothing action
	}
	
	private void drawAction(final GalconLaunchAction action, final GalconState state) {
		int sourceID = action.getLaunchSiteID();
		int destID = action.getDestID();
		Planet source = state.getPlanetMap().get(sourceID);
		Planet dest = state.getPlanetMap().get(destID);
		
		int sourceX = Monitor.getInstance().screenX(source.getPosX());
		int sourceY = Monitor.getInstance().screenY(source.getPosY());
		int sourceRadius = Monitor.getInstance().scale(source.getRadius());
		int destX = Monitor.getInstance().screenX(dest.getPosX());
		int destY = Monitor.getInstance().screenY(dest.getPosY());
		
		final Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		int currentPlayer = state.getAgentTurn();
		int lastPlayer = 1 - currentPlayer;
		if (lastPlayer == 0) {
			g.setColor(Color.GREEN);
		} else if (lastPlayer == 1) {
			g.setColor(Color.RED);
		} else if (lastPlayer == 2) {
			g.setColor(Color.BLUE);
		} else if (lastPlayer == 3) {
			g.setColor(Color.MAGENTA);
		} else {
			g.setColor(Color.YELLOW);
		}
		
		//Emphasize source planet
		g.setStroke(new BasicStroke(2.f));
		int bigRad = sourceRadius+5;
		g.drawOval(sourceX-bigRad, sourceY-bigRad,
				bigRad*2, bigRad*2);
		
		//Draw line
		
		final float total = 16.f;
		float filled = 0.f, empty;
		switch(action.getSize()) {
		case SMALL:
			filled = 1.f;
			break;
		case HALF:
			filled = 8.f;
			break;
		case LARGE:
			filled = 16.f;
			break;
		}
		empty = total-filled;
		final float dash[] = {filled, empty};
		Stroke s = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.f, dash, 5.f);
		g.setStroke(s);
		g.drawLine(sourceX, sourceY, destX, destY);
		g.dispose();
		strategy.show();
	}

	public void updateMonitor(final GalconState worldModel) {
		if( maxPopulation == 0 ) {
			for( final Planet p : worldModel.getPlanets() ) {
				maxPopulation += p.getCapacity();
			}
		}
		
		final Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		drawBackground((Graphics2D) g.create());
		drawBorder((Graphics2D) g.create());
		drawState( (Graphics2D) g.create(), worldModel );
		// Paint planets
		if (worldModel.getPlanets() != null) {
			for (final Planet planet : worldModel.getPlanets()) {
				PlanetPainter.plaint((Graphics2D) g.create(), planet, playback);
			}
		}
		// Paint spaceships
		if (worldModel.getPlayers() != null) {
			for (final Player player : worldModel.getPlayers()) {
				for (final Spaceship spaceship : player.getFleet()) {
					SpaceShipPainter.plaint(g, spaceship, player);
				}
			}
		}
		
		if (playback) {
			//Draw timeline
			drawTimeline(g);
			
			//Draw agents
			Color colors[] = {Color.GREEN, Color.RED};
			int y = screenHeight-5;
			for (int i = 0; i < 2; i++) {
				g.setColor(colors[i]);
				FontMetrics fm = g.getFontMetrics(g.getFont());
				String agent = agents.get(i);
				g.drawString(agent, 5, y);
				y -= 15;
			}
			
			//Draw time
			g.setColor(Color.WHITE);
			String s = Long.toString(worldModel.getCycle());
			g.drawString(s, screenWidth/2-screenWidth/10, screenHeight-5);
		}
		
		g.dispose();
		strategy.show();
	}
	
	public double getPlaybackTime() {
		return time;
	}

	private void drawBackground(final Graphics2D g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, screenWidth, screenHeight);
	}

	private void drawBorder(final Graphics2D g) {
		g.setColor(Color.WHITE);
		// Draw center cross
		g.drawLine(screenX(-4), screenY(0), screenX(4), screenY(0));
		g.drawLine(screenX(0), screenY(-4), screenX(0), screenY(4));
		// Draw top right corner
		g.drawLine(screenX(mapLimX), screenY(mapLimY), screenX(mapLimX),
				screenY(mapLimY - 4));
		g.drawLine(screenX(mapLimX), screenY(mapLimY), screenX(mapLimX - 4),
				screenY(mapLimY));
		// Draw top left corner
		g.drawLine(screenX(-mapLimX), screenY(mapLimY), screenX(-mapLimX),
				screenY(mapLimY - 4));
		g.drawLine(screenX(-mapLimX), screenY(mapLimY), screenX(-mapLimX + 4),
				screenY(mapLimY));
		// Draw bottom right corner
		g.drawLine(screenX(mapLimX), screenY(-mapLimY), screenX(mapLimX),
				screenY(-mapLimY + 4));
		g.drawLine(screenX(mapLimX), screenY(-mapLimY), screenX(mapLimX - 4),
				screenY(-mapLimY));
		// Draw bottom left corner
		g.drawLine(screenX(-mapLimX), screenY(-mapLimY), screenX(-mapLimX + 4),
				screenY(-mapLimY));
		g.drawLine(screenX(-mapLimX), screenY(-mapLimY), screenX(-mapLimX),
				screenY(-mapLimY + 4));
	}
	
	private void initBar() {
		int height = 15;
		int width = screenWidth / 2 - 10;
		int x = screenWidth / 2;
		int y = screenHeight - height - 10;
		
		int bar_width = 5;
		bar_minX = x;
		bar_maxX = bar_minX+width-bar_width;
		
		bar = new Rectangle(bar_minX, y, bar_width, height);
	}
	
	private void drawTimeline(final Graphics2D g) {
		int height = 15;
		int width = screenWidth / 2 - 10;
		int x = screenWidth / 2;
		int y = screenHeight - height - 10;
		
		g.setColor(Color.WHITE);
		g.drawRect(x, y, width, height);
		
		bar.x = bar_minX + (int)(time * (bar_maxX-bar_minX));
		g.fill(bar);
	}
	
	private double totalGrowthRate( final GalconState state, final int player )
	{
		double result = 0.0;
		for( final Planet p : state.getPlayerPlanets( player ) ) {
			// TODO: magic number is Simulator.POPULATION_GROWTH_RATE
			result += GalconSimulator.getPopulationGrowthRate(p);
		}
		return result;
	}
	
	private void drawState( final Graphics2D g, final GalconState state )
	{
		final int h_offset = 4;
		int v_offset = 4;
		final int eff_screen_width = screenWidth - (2 * h_offset);
		final int height = 10;
		final double total_pop = state.getOverallPopulation();
		final double p0_share = state.getPlayerPopulation( 0 ) / total_pop;
		final double p1_share = state.getPlayerPopulation( 1 ) / total_pop;
		final double neutral_share = 1.0 - p0_share - p1_share;
		final int end_0 = (int) (p0_share * eff_screen_width);
		final int end_neutral = end_0 + (int) (neutral_share * eff_screen_width);
		g.setColor( Color.green );
		g.fillRect( h_offset, v_offset, end_0, height );
		g.setColor( Color.yellow );
		g.fillRect( h_offset + end_0, v_offset, end_neutral - end_0, height );
		g.setColor( Color.red );
		g.fillRect( h_offset + end_neutral, v_offset, eff_screen_width - end_neutral, height );
		
		v_offset = v_offset + height + v_offset;
		final double p0_growth = totalGrowthRate( state, 0 );
		final double p1_growth = totalGrowthRate( state, 1 );
		final double total_growth = p0_growth + p1_growth;
		final double p0_growth_share = p0_growth / total_growth;
		final double p1_growth_share = 1.0 - p0_growth_share;
		final int growth_mid = (int) (p0_growth_share * eff_screen_width);
		g.setColor( Color.green );
		g.fillRect( h_offset, v_offset, growth_mid, height );
		g.setColor( Color.red );
		g.fillRect( h_offset + growth_mid, v_offset, eff_screen_width - growth_mid, height );
		
		// Vertical midpoint marker
		g.setColor( Color.white );
		g.drawLine( screenWidth / 2, 0, screenWidth / 2, 32 );
	}

	public void showWindow(final boolean arg) {
		container.setVisible(arg);
	}

	public int screenX(final int x) {
		return screenX((double) x);
	}

	public int screenY(final int y) {
		return screenY((double) y);
	}

	public int screenX(final double x) {
		final int screenX = (int) (0.5 * screenWidth * (x * mapScale
				/ mapLimX + 1.0));
		// log.info("x: " + x + " screenX:" + screenX);
		return screenX;
	}

	public int screenY(final double y) {
		final int screenY = (int) (0.5 * screenHeight * (-y * mapScale
				/ mapLimY + 1.0));
		// log.info("y: " + y + " screenY:" + screenY);
		return screenY;
	}

	public int scale(final int length) {
		return (int) (length * lengthScale);
	}

	public void setMapWidth(final int mapWidth) {
		this.mapLimX = mapWidth;
	}

	public void setMapHeight(final int mapHeight) {
		this.mapLimY = mapHeight;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		Point down = arg0.getPoint();
		if (bar.contains(down)) {
			startPoint = down;
			drag = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		drag = false;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (drag) {
			int x = arg0.getX();
			double t = (double)(x - bar_minX)/(double)(bar_maxX - bar_minX);
			t = Math.min(Math.max(0, t), 1);
			time = t;
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/*
	public static void main(String[] args) throws Exception {
		String path = "/nfs/rack/u4/z/zheng/workspace/galconai/maps/2p/train.1/random.2p.1.200.200.3.map";
		GalconState state = GalconState.restoreFromFile(path, 200, 200);
		Monitor.createMonitor(200, 200);
		Monitor.getInstance().showWindow(true);
		Monitor.getInstance().updateMonitor(state);
	}
	*/
}
