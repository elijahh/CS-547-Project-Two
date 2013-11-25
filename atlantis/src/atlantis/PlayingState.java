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

		Queue<Worker> workers = 
				new PriorityQueue<Worker>(client.getWorkers());
		for (Worker w : workers)
			w.render(g); 

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

	// TEMPORARY FOR ISSUE 11
	private Worker worker_on_server = 
			new Worker(400, 300, new Vector(1, 0));
	private int worker_clock;
	// TEMPORARY FOR ISSUE 11
	
	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
//		if(StartMenuState.GAME_TYPE.equals("server"))
//			server.tellClient(container);
//		
//		if(StartMenuState.GAME_TYPE.equals("client"))
//			client.tellServer(container);

		/* ---------------------------------------------------------------- */
		
		// TEMPORARY FOR WORKING OUT MOVEMENT OF WORKER AND CODE FOR
		// SERIALIZATION/DESERIALIZATION
		
		// H/T: http://stackoverflow.com/questions/134492/
		//		how-to-serialize-an-object-into-a-string
		
		// Make the worker move, serialize, deserialize, and update
		// into the client's entity list.
		
		worker_clock += delta;
		
		if(worker_clock > 1000) {
			worker_clock = 0;
			
			Vector move_dir =  worker_on_server.getMovementDirection();
			move_dir = new Vector(move_dir.negate());
			worker_on_server.beginMovement(move_dir);
		}
		
		worker_on_server.update(delta);
				
		AtlantisEntity.Updater updater = worker_on_server.getUpdater();
		AtlantisEntity.Updater deserialized_updater = null;
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(updater);
			os.close();

			byte[] serialized_updater = bos.toByteArray();

			ByteArrayInputStream bis = new ByteArrayInputStream(
					serialized_updater);
			ObjectInputStream ois = new ObjectInputStream(bis);
			deserialized_updater = (AtlantisEntity.Updater) ois.readObject();
			ois.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(null != deserialized_updater)
			client.processUpdateEntity(deserialized_updater);
		
		/* ---------------------------------------------------------------- */
				
	}

	@Override
	public int getID() {
		return AtlantisGame.PLAYING;
	}
	
	/* -------------------------------------------------------------------- */
	
	/* Game state methods/fields */	
	
}
