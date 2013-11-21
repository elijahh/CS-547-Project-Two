package atlantis;

import java.util.PriorityQueue;
import java.util.Queue;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class PlayingState extends BasicGameState{
	Overlay overlay;
	TiledMap map;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		map = new TiledMap("atlantis/resource/densemap.tmx");
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		overlay = new Overlay();
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
		GameStatus status = AtlantisGame.getGameStatus();
		Queue<Worker> workers = new PriorityQueue(status.getWorkers());
		
		for(Worker w : workers) w.render(g); 
		
		overlay.render(container, game, g);
	}
	
	private final void doHousekeeping() {
        /* Housekeeping - clean up completed animations, etc. */
		
		// TODO
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		AtlantisGame.getGameStatus().update(delta);
	}

	@Override
	public int getID() {
		return AtlantisGame.PLAYING;
	}
	
	/* -------------------------------------------------------------------- */
	
	/* Game state methods/fields */	
	
}
