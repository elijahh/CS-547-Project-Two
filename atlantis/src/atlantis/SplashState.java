package atlantis;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class SplashState extends BasicGameState {

	private int splashTimer;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		//splashTimer = 4000;
		splashTimer = 0;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.drawImage(ResourceManager.getImage(AtlantisGame.SPLASH_SCREEN_GRAPHIC),
				50, 50);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		splashTimer -= delta;
		if (splashTimer <= 0)
			game.enterState(AtlantisGame.MENU);
	}

	@Override
	public int getID() {
		return AtlantisGame.SPLASH;
	}

}
