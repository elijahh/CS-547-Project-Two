package atlantis;

import java.util.Iterator;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class AtlantisGame extends BasicGame {
	private static final String SPLASH_SCREEN_GRAPHIC = "atlantis/resource/splash.png";
	private static final String START_GAME_PROMPT_GRAPHIC = "atlantis/resource/PressSpace.png";
	private static final String GAME_OVER_PROMPT_GRAPHIC = "atlantis/resource/GameOver.png";
	
	private static final int DISPLAY_SIZE_X = 800;
	private static final int DISPLAY_SIZE_Y = 600;

	public AtlantisGame(String title) {
		super(title);
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);

		/* Game data structure setup.  Do not load images here */
		
		// TODO
	}
	
	@Override
	public void init(GameContainer container) throws SlickException {
		ResourceManager.loadImage(SPLASH_SCREEN_GRAPHIC);
		ResourceManager.loadImage(START_GAME_PROMPT_GRAPHIC);
		ResourceManager.loadImage(GAME_OVER_PROMPT_GRAPHIC);
		
		/* Load other images relevant to the main game object such as
		 * backgrounds and playfield components. */

		// TODO
		
		splash(container);
	}

	/* -------------------------------------------------------------------- */
	
	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {

		if (gameState == SPLASH) {
			g.drawImage(ResourceManager.getImage(SPLASH_SCREEN_GRAPHIC), 50, 50);
			return;
		}

		/* Draw game background images */
		
		if (gameState == START_UP) {
			g.drawImage(ResourceManager.getImage(START_GAME_PROMPT_GRAPHIC),
					225, 320);
		}
		
		if (gameState == GAME_OVER) {
			g.drawImage(ResourceManager.getImage(GAME_OVER_PROMPT_GRAPHIC),
					225, 320);
		}

		/* Draw game */
		
		// TODO
	}

	
	/* -------------------------------------------------------------------- */

	private final void doHousekeeping() {
        /* Housekeeping - clean up completed animations, etc. */
		
		// TODO
	}
	
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {

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
				newGame(container);
			}
			else return;
		}

		/* LOOK */
		/* LOOK */
		
		/* REMOVE/COMMENT OUT ONCE DEVELOPMENT STARTS */
		gameOver(container);
		
		// TODO
		
		/* LOOK */
		/* LOOK */
		
		/* Process player input, network i/o, entity updates. */
		
		// TODO
	}

	/* -------------------------------------------------------------------- */
	
	public static void main(String[] args) {
		AppGameContainer app;
		
		try {
			app = new AppGameContainer(new AtlantisGame("Atlantis"));
			app.setDisplayMode(DISPLAY_SIZE_X, DISPLAY_SIZE_Y, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	/* -------------------------------------------------------------------- */
	
	/* Game state methods/fields */
		
	private static final int START_UP = 1;
	private static final int PLAYING = 2;
	private static final int GAME_OVER = 3;
	private static final int SPLASH = 4;
	
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
	}
	
	public void newGame(GameContainer container) {
		gameState = PLAYING;		
		container.setSoundOn(true);
		
		/* Reset score, starting positions, etc. */
		
		// TODO
	}
	
	public void gameOver(GameContainer container) {
		gameState = GAME_OVER;
		container.setSoundOn(false);
		
		gameOverTimer = 4000;
	}
}
