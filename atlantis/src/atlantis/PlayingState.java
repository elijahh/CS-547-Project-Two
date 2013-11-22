package atlantis;


import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;


import atlantis.networking.AtlantisClient;
import atlantis.networking.AtlantisServer;
import atlantis.networking.SimulationResult;


public class PlayingState extends BasicGameState{

	Overlay overlay;

	public static final int NUMBER_OF_PLAYERS = 2;
	
	public static volatile int currentNumberOfPlayers;
	
	ArrayList<AtlantisClient> clients;
	
	private static final int PORT_NUMBER = 6000;
	AtlantisServer server;
	AtlantisClient client;
	public String command;
	public String result;
	
	TiledMap map;
	String mapName;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

		map = new TiledMap("atlantis/resource/densemap.tmx", "atlantis/resource");

	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {

		overlay = new Overlay();
		currentNumberOfPlayers = 0;
		map  = new TiledMap("atlantis/resource/densemap.tmx");
		
		if(StartMenuState.GAME_TYPE.equals("server")){
			System.out.println("server mode");
			server = new AtlantisServer(PORT_NUMBER);
			server.start(); 

			//If in host mode, create another thread of client
			client = new AtlantisClient(PORT_NUMBER);
			client.connect();
			
			while(currentNumberOfPlayers < 2){}
			
			server.sendMap("new map");
					
		}else if(StartMenuState.GAME_TYPE.equals("client")){
			System.out.println("client mode");
			client = new AtlantisClient(PORT_NUMBER);
			client.connect();
			//client waiting for map info
			while (client.resultsQueue.isEmpty()) {}
			while (!client.resultsQueue.isEmpty()) {
				SimulationResult result = client.resultsQueue.poll();
				if(result.type == SimulationResult.MAP) {
					mapName = result.stringContent;
					System.out.println(mapName);
				}
				break;
			}
		}
		
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
		map.render(0, 0);

		GameStatus status = AtlantisGame.getGameStatus();
		Queue<Worker> workers = new PriorityQueue(status.getWorkers());
		
		for(Worker w : workers) w.render(g); 
		
		overlay.render(container, game, g);

		/* Client receive results from server */

		if(StartMenuState.GAME_TYPE.equals("server"))
			g.drawString("Command: "+ server.command, 25, 200);
		
		if(StartMenuState.GAME_TYPE.equals("client"))
			g.drawString("Result: "+ client.result, 25, 200);

	}
	
	private final void doHousekeeping() {
        /* Housekeeping - clean up completed animations, etc. */
		
		// TODO
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

//		if(StartMenuState.GAME_TYPE.equals("server"))
//			server.tellClient(container);
//		
//		if(StartMenuState.GAME_TYPE.equals("client"))
//			client.tellServer(container);

		AtlantisGame.getGameStatus().update(delta);
	}

	@Override
	public int getID() {
		return AtlantisGame.PLAYING;
	}
	
	/* -------------------------------------------------------------------- */
	
	/* Game state methods/fields */	
	
}
