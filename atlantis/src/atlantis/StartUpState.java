package atlantis;


import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;

public class StartUpState extends BasicGameState{


	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		gameState = SPLASH;
		splashTimer = 4000;
		
		
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

		if (gameState == SPLASH) {
			g.drawImage(ResourceManager.getImage(AtlantisGame.SPLASH_SCREEN_GRAPHIC), 50, 50);
			return;
		}

		/* Draw game background images */

		if (gameState == START_UP) {
			g.drawImage(ResourceManager.getImage(AtlantisGame.START_GAME_PROMPT_GRAPHIC),
					225, 320);
		}

		if (gameState == GAME_OVER) {
			g.drawImage(ResourceManager.getImage(AtlantisGame.GAME_OVER_PROMPT_GRAPHIC),
					225, 320);
		}
		
		if(gameState == MENU) {
			g.drawImage(ResourceManager.getImage(AtlantisGame.CREATE_GAME),
					280, 250);
			g.drawImage(ResourceManager.getImage(AtlantisGame.JOIN_GAME),
					280, 300);
		}

		/* Draw game */

		// TODO	
	}
	
	private final void doHousekeeping() {
        /* Housekeeping - clean up completed animations, etc. */
		
		// TODO
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		doHousekeeping();
		
		if (gameState == SPLASH) {
			splashTimer -= delta;
			if (splashTimer <= 0)
				startUp(container);
			
			return;
		}
		
		
		
		if (gameState == GAME_OVER) {
			gameOverTimer -= delta;
			if (gameOverTimer <= 0)
				startUp(container);

			return;
		}
		
		Input input = container.getInput();
		
		if (gameState == START_UP) {
			if (input.isKeyDown(Input.KEY_SPACE)) {
				menu(container);
				//Enter PlayingState
			}
			else return;
		}
		
		if(gameState == MENU) {
			//capture mouse click
			if(input.isMousePressed(input.MOUSE_LEFT_BUTTON)) {
				int posX = input.getMouseX();
				int poxY = input.getMouseY();
				//TODO if click "create game", enter PlayingState with server mode, else enter PlayingState with client mode
			}
		}

	}

	@Override
	public int getID() {
		return AtlantisGame.START_UP;
	}
	
/* -------------------------------------------------------------------- */
	
	/* Game state methods/fields */
	
	private static final int START_UP = 1;
	private static final int PLAYING = 2;
	private static final int GAME_OVER = 3;
	private static final int SPLASH = 4;
	private static final int MENU = 5;
	
	private int gameState;
	private int gameOverTimer;
	private int splashTimer;
	
	public void splash(GameContainer container) {
		gameState = SPLASH;
		container.setSoundOn(false);		
		splashTimer = 4000;
	}
	
	public void startUp(GameContainer container) {
		gameState = START_UP;
		container.setSoundOn(false);
		
		/* Reset score, starting positions, etc. */
		
		// TODO
	}
	
	public void menu(GameContainer container) {
		gameState = MENU; //enter playing state		
		container.setSoundOn(true);
		
	}
	
	public void gameOver(GameContainer container) {
		gameState = GAME_OVER;
		container.setSoundOn(false);
		
		gameOverTimer = 4000;
	}

}
