package atlantis;

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
import atlantis.networking.ResultLockStep;
import atlantis.networking.SimulationResult;

public class GamePrepareState extends BasicGameState {

	private static final int PORT_NUMBER = 6000;
	public static volatile int currentNumberOfPlayers;
	static AtlantisServer server;
	static AtlantisClient client;
	int frameNum = 0;
	boolean hasSentMap;
	boolean isClientConnected;
	static AtlantisMap map;
	
	//static String mapName = "atlantis/resource/densemap.tmx"; 
	static String mapName = "atlantis/resource/bigmap.tmx"; 
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		
		hasSentMap = false;
		isClientConnected = false;
		currentNumberOfPlayers = 0;
		
		if(StartMenuState.GAME_TYPE.equals("server")){
			System.out.println("Server mode");
			server = new AtlantisServer(PORT_NUMBER);
			server.start(); 				
			//team = AtlantisEntity.Team.RED;
		}
		
		System.out.println("You have joined game");
		client = new AtlantisClient(PORT_NUMBER);
		isClientConnected = client.connect(StartMenuState.ADDRESS);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		
		int i = 1;
		for(i=1; i<= currentNumberOfPlayers; i++) {
			g.drawString("Player "+i+" has joined the game",170, 195+25*(i-1));
		}
		
		if(hasSentMap) {
			g.drawString("Map has been sent", 170, 195+25*i);
		}
		
		if(StartMenuState.GAME_TYPE == "client") {//Need to acknowledge receive map
			g.drawString("Waiting for server to start...", 170, 195+25*(i+1));
		}
		
		if(currentNumberOfPlayers == StartMenuState.NUMBER_OF_PLAYERS && hasSentMap) {
			g.drawImage(ResourceManager.getImage(AtlantisGame.START_GAME),
					200, 400);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		
		Input input = container.getInput();
		
		if(StartMenuState.GAME_TYPE == "server") {
			if(currentNumberOfPlayers == StartMenuState.NUMBER_OF_PLAYERS && hasSentMap == false) {
				server.sendMap(mapName, frameNum);
				hasSentMap = true;
			}
		
			if(currentNumberOfPlayers == StartMenuState.NUMBER_OF_PLAYERS && input.isKeyPressed(Input.KEY_SPACE) && hasSentMap) {
				server.startGame(frameNum);
			}
		}
		
		if(StartMenuState.GAME_TYPE == "client") {
			if(isClientConnected) {
				currentNumberOfPlayers = 2; //This only works for two players. If there are more than two, server need to send everybody's information to everybody 
			}
		}
		

		if (!client.incomingLockSteps.isEmpty()) {
			ResultLockStep step = client.incomingLockSteps.poll();
			if(step.frameNum == 0) {
				SimulationResult result = step.frameResults.get(0);
				if(result.type == result.MAP) {
					System.out.println("Map received!");
					mapName = result.mapName;
					map = new AtlantisMap(mapName);
				}
				else if(result.type == result.MESSAGE) {
					if(result.message.equals("start")) game.enterState(AtlantisGame.PLAYING);
				}
			}
		}
	}

	@Override
	public int getID() {
		return AtlantisGame.PREPARE;
	}

	public static AtlantisMap getMap() {
		return map;
	}
}
