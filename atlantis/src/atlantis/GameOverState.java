package atlantis;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class GameOverState extends BasicGameState{
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
	}
	
	private final void doHousekeeping() {
        /* Housekeeping - clean up completed animations, etc. */
		
		// TODO
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
	}

	@Override
	public int getID() {
		return AtlantisGame.GAME_OVER;
	}
	
	/* -------------------------------------------------------------------- */
	
	/* Game state methods/fields */
	private int gameOverTimer;
	
	
	public void gameOver(GameContainer container) {
		container.setSoundOn(false);

	}

}
