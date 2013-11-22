package atlantis;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

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

	public static final int NUMBER_OF_PLAYERS = 1;
	
	public static volatile int currentNumberOfPlayers;
	
	ArrayList<AtlantisClient> clients;
	
	private static final int PORT_NUMBER = 6000;
	AtlantisServer server;
	AtlantisClient client;
	public String command;
	public String result;
	
	TiledMap map;
	String mapName;
	int currentFrame;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

	}
	
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
		}
		
		
		System.out.println("New client join");
		client = new AtlantisClient(PORT_NUMBER);
		client.connect(StartMenuState.ADDRESS);
		
		if(StartMenuState.GAME_TYPE.equals("server")) {
			/*
			 * Stuck when currentNumberOfPlayer is less than NUMBER_OF_PLAYERS, waiting for two players both join and starts to send map.
			 * Set NUMBER_OF_PLAYERS equal to 1 for single player and purpose of easy developing.
			 */
			while(currentNumberOfPlayers < NUMBER_OF_PLAYERS){} 
			//TODO: This is test case, need to change map and map name
			mapName = "atlantis/resource/densemap.tmx"; 		
			server.sendMap(mapName, currentFrame);
		}
		
		//client waiting for map info
		while (client.incomingLockSteps.isEmpty()) {}

		while (!client.incomingLockSteps.isEmpty()) {
			ResultLockStep step = client.incomingLockSteps.poll();
			if(step.frameNum == 0) {
				SimulationResult result = step.frameResults.get(0);
				System.out.println("Map reveived!");
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

		overlay.render(container, game, g);

		
		/* Client render frame result from server */

	}
	
	private final void doHousekeeping() {
        /* Housekeeping - clean up completed animations, etc. */
		
		// TODO
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		// If in server mode, wait for current frame commands, send simulation of current frame result 
		//CommandLockStep incomingCommands = server.incomingLockSteps.poll();
		
		/* do computation */
		
		//Store simulation results in outgoingResults
		//ResultLockStep outgoingResults = new ResultLockStep(currentFrame);
		
		/* -------------------------------------------------------------------- */
		
		//CommandLockStep outgoingCommands = new CommandLockStep(currentFrame);
		/* Add commands to outgoingCommands and send current frame commands to server */
		
		// Wait and get incoming simulation results of current frame from server 
		//ResultLockStep incomingResults = client.incomingLockSteps.poll(); 
	}

	@Override
	public int getID() {
		return AtlantisGame.PLAYING;
	}
	
	/* -------------------------------------------------------------------- */
	
	/* Game state methods/fields */	
	
}
