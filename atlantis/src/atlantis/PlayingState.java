package atlantis;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import atlantis.networking.AtlantisClient;
import atlantis.networking.AtlantisServer;

public class PlayingState extends BasicGameState{

	Overlay overlay;

	
	private static final int PORT_NUMBER = 6000;
	AtlantisServer server;
	AtlantisClient client;
	public String command;
	public String result;

	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {

		overlay = new Overlay();
		
		if(StartMenuState.GAME_TYPE.equals("server")){
			System.out.println("server mode");
			server = new AtlantisServer(PORT_NUMBER);
			server.connect();
			server.createListener();
		}else if(StartMenuState.GAME_TYPE.equals("client")){
			System.out.println("client mode");
			client = new AtlantisClient(PORT_NUMBER);
			client.connect();
			client.createListener();
		}
		
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

		overlay.render(container, game, g);

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
		if(StartMenuState.GAME_TYPE.equals("server"))
			server.tellClient(container);
		
		if(StartMenuState.GAME_TYPE.equals("client"))
			client.tellServer(container);
	}

	@Override
	public int getID() {
		return AtlantisGame.PLAYING;
	}
	
	/* -------------------------------------------------------------------- */
	
	/* Game state methods/fields */	
	
}
