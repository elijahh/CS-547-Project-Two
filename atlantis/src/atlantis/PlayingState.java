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

	public static volatile int currentNumberOfPlayers;
	
	ArrayList<AtlantisClient> clients;
	
	private static final int PORT_NUMBER = 6000;
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
	 */
	
	AtlantisEntity.Team team = AtlantisEntity.Team.BLUE;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

		status = new GameStatus(this);
	}
	
	public AtlantisServer getServer() { return server; }
	public AtlantisClient getClient() { return client; }
	public int getCurrentFrame() { return currentFrame; }
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {

		overlay = new Overlay();
		currentNumberOfPlayers = 0;
		currentFrame = 0;
		
		/*If in server mode, create a server thread*/
		if(StartMenuState.GAME_TYPE.equals("server")){
			System.out.println("Server mode");
			server = new AtlantisServer(PORT_NUMBER);
			server.start(); 				
			team = AtlantisEntity.Team.RED;
		}
		
		System.out.println("New client join");
		client = new AtlantisClient(PORT_NUMBER);
		client.connect(StartMenuState.ADDRESS);
		
		if(StartMenuState.GAME_TYPE.equals("server")) {
			/*
			 * Stuck when currentNumberOfPlayer is less than NUMBER_OF_PLAYERS, waiting for two players both join and starts to send map.
			 * Set NUMBER_OF_PLAYERS equal to 1 for single player and purpose of easy developing.
			 */
			while(currentNumberOfPlayers < StartMenuState.NUMBER_OF_PLAYERS){} 
			//TODO: This is test case, need to change map and map name
			mapName = "atlantis/resource/densemap.tmx"; 		
			server.sendMap(mapName, currentFrame);
			
			GroundEntity.populateTerrainMap(map);
		}
		
		//client waiting for map info
		while (client.incomingLockSteps.isEmpty()) {}

		while (!client.incomingLockSteps.isEmpty()) {
			ResultLockStep step = client.incomingLockSteps.poll();
			if(step.frameNum == 0) {
				SimulationResult result = step.frameResults.get(0);
				System.out.println("Map received!");
				mapName = result.mapName;
				map = new TiledMap(mapName);
			}
			break;
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
		map.render(0, 0);
		
		//System.out.println("RENDERING WORKERS");

		Queue<Worker> workers = 
				new PriorityQueue<Worker>(status.getWorkers());
		for (Worker w : workers) {
			//System.out.println("x:"+w.getX()+" y:"+w.getY());
			w.render(g); 
		}

		overlay.render(container, game, g);

		/* Client receive results from server */

		if(StartMenuState.GAME_TYPE.equals("server"))
			g.drawString("Server "+ server.command, 25, 200);
		
		if(StartMenuState.GAME_TYPE.equals("client"))
			g.drawString("Client "+ client.result, 25, 200);
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
