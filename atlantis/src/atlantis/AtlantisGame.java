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
import org.newdawn.slick.state.StateBasedGame;

public class AtlantisGame extends StateBasedGame {
	
	public static final int START_UP = 1;
	public static final int PLAYING = 2;
	public static final int GAME_OVER = 3;
	public static final int SPLASH = 4;
	
	public static final String SPLASH_SCREEN_GRAPHIC = "atlantis/resource/splash.png";
	public static final String START_GAME_PROMPT_GRAPHIC = "atlantis/resource/PressSpace.png";
	public static final String GAME_OVER_PROMPT_GRAPHIC = "atlantis/resource/GameOver.png";
	public static final String CREATE_GAME = "atlantis/resource/create_game.png";
	public static final String JOIN_GAME = "atlantis/resource/join_game.png";
	
	private static final int DISPLAY_SIZE_X = 800;
	private static final int DISPLAY_SIZE_Y = 600;

	public AtlantisGame(String title) {
		super(title);		
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);

		/* Game data structure setup.  Do not load images here */
		
		// TODO
	}
	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new StartUpState());
		addState(new PlayingState());
		addState(new GameOverState());
		
		ResourceManager.loadImage(SPLASH_SCREEN_GRAPHIC);
		ResourceManager.loadImage(START_GAME_PROMPT_GRAPHIC);
		ResourceManager.loadImage(GAME_OVER_PROMPT_GRAPHIC);
		ResourceManager.loadImage(CREATE_GAME);
		ResourceManager.loadImage(JOIN_GAME);
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
}
