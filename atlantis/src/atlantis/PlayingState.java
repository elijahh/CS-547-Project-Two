// H/T: http://stackoverflow.com/questions/134492/
// 				how-to-serialize-an-object-into-a-string

package atlantis;

import java.util.PriorityQueue;
import java.util.Queue;

import jig.ResourceManager;

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
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		overlay = new Overlay(this);
		currentFrame = 0;
		
		viewportOffsetX = 0;
		viewportOffsetY = 0;
		
		map = GamePrepareState.getMap();
		GroundEntity.populateTerrainMap(map);
		
		if (StartMenuState.GAME_TYPE.equals("server"))
			team = AtlantisEntity.Team.RED;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
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

		overlay.render(container, game, g);

		/* Client receive results from server */

		if(StartMenuState.GAME_TYPE.equals("server"))
			g.drawString("Server ", 25, 200);
		
		if(StartMenuState.GAME_TYPE.equals("client"))
			g.drawString("Client ", 25, 200);
	}
	
	private final void doHousekeeping() {
        /* Housekeeping - clean up completed animations, etc. */
		
		// TODO
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		currentFrame += 1;
		status.update(container, delta);
		overlay.update(delta);
	}

	@Override
	public int getID() {
		return AtlantisGame.PLAYING;
	}
	
	public TiledMap getMap() { return map; }
}
