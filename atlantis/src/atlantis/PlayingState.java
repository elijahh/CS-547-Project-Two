// H/T: http://stackoverflow.com/questions/134492/
// 				how-to-serialize-an-object-into-a-string

package atlantis;

import java.util.PriorityQueue;
import java.util.Queue;

import jig.ResourceManager;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import atlantis.networking.AtlantisClient;
import atlantis.networking.AtlantisServer;

public class PlayingState extends BasicGameState{

	Overlay overlay;	
	AtlantisServer server;
	AtlantisClient client;
	public String command;
	public String result;
	
	GameStatus status;
	
	AtlantisMap map;
	String mapName;
	volatile int currentFrame;
	
	public static int viewportOffsetX;
	public static int viewportOffsetY;
	
	public static int MAP_WIDTH;
	public static int MAP_HEIGHT;
	
	/* Team is assumed to be BLUE (remote player). Code which starts server 
	 * below reassigns team as RED (local player */
	
	AtlantisEntity.Team team = AtlantisEntity.Team.BLUE;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

		status = new GameStatus(this);
	}
	
	public AtlantisServer getServer() { return GamePrepareState.server; }
	public AtlantisClient getClient() { return GamePrepareState.client; }
	public int getCurrentFrame() { return currentFrame; }
	public GameStatus getStatus() { return status; }
	
	Animation shimmer;
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		overlay = new Overlay(this);
		currentFrame = 0;
		
		viewportOffsetX = 0;
		viewportOffsetY = 0;
		
		map = GamePrepareState.getMap();
		
		MAP_WIDTH = map.getWidth() * map.getTileWidth();
		MAP_HEIGHT = map.getHeight() * map.getTileHeight();
		
		GroundEntity.populateTerrainMap(map);
		shimmer = new Animation(
				ResourceManager.getSpriteSheet(AtlantisGame.SHIMMER,
						800, 600), 0, 0, 2, 0, true, 500, true);
		shimmer.setPingPong(true);
		
		if (StartMenuState.GAME_TYPE.equals("server"))
			team = AtlantisEntity.Team.RED;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		System.out.println(viewportOffsetX + ", " + viewportOffsetY);
		map.render(viewportOffsetX, viewportOffsetY);
		//System.out.println("RENDERING SOLDIERS");

		Queue<Soldier> soldiers = 
				new PriorityQueue<Soldier>(status.getSoldiers());
		for (Soldier w : soldiers) {
			w.render(g);
		}
		
		// Render mothership
		Queue<MotherShip> motherShips = 
				new PriorityQueue<MotherShip>(status.getMotherShips());
		for (MotherShip mothership : motherShips) {
			mothership.render(g); 
		}
		
		// Render tacticalsub
		Queue<TacticalSub> tacticals = 
				new PriorityQueue<TacticalSub>(status.getTacticals());
		for (TacticalSub tactical : tacticals) {
			tactical.render(g); 
		}
		
		shimmer.draw();

		overlay.render(container, game, g);

		/* Client receive results from server */

		g.setColor(Color.yellow);
		
		if(StartMenuState.GAME_TYPE.equals("server"))
			g.drawString("Server ", 25, 200);
		
		if(StartMenuState.GAME_TYPE.equals("client"))
			g.drawString("Client ", 25, 200);
		
		g.drawString("Gold: " + (int) gold, 10, 30);
	}
	
	private final void doHousekeeping() {
        /* Housekeeping - clean up completed animations, etc. */
		
		// TODO
	}
	
	float gold = 0;
	
	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		currentFrame += 1;
		gold += delta / 180f;
		status.update(container, delta);
		overlay.update(delta);
		
		// collect battle winnings
		Queue<Soldier> soldiers = 
				new PriorityQueue<Soldier>(status.getSoldiers());
		for (Soldier w : soldiers) {
			if (w.getTeam() == this.team) gold += w.reward;
		}
	}

	@Override
	public int getID() {
		return AtlantisGame.PLAYING;
	}
	
	public TiledMap getMap() { return map; }
}
