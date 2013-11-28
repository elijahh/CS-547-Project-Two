// H/T: http://stackoverflow.com/questions/134492/
// 				how-to-serialize-an-object-into-a-string

package atlantis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import atlantis.networking.AtlantisClient;
import atlantis.networking.AtlantisServer;
import atlantis.networking.CommandLockStep;
import atlantis.networking.ResultLockStep;
import atlantis.networking.SimulationResult;

public class PlayingState extends BasicGameState{

	Overlay overlay;	
	AtlantisServer server;
	AtlantisClient client;
	public String command;
	public String result;
	
	GameStatus status;
	
	TiledMap map;
	String mapName;
	volatile int currentFrame;
	
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
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		overlay = new Overlay();
		currentFrame = 0;
		map = GamePrepareState.getMap();
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
		map.render(0, 0);
		
		//System.out.println("RENDERING WORKERS");

		Queue<Worker> workers = 
				new PriorityQueue<Worker>(status.getWorkers());
		for (Worker w : workers) {
			w.render(g); 
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
	}

	@Override
	public int getID() {
		return AtlantisGame.PLAYING;
	}
	
	/* -------------------------------------------------------------------- */
	
	/* Game state methods/fields */	
	
}
