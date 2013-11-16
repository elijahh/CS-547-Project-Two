package atlantis;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class StartMenuState extends BasicGameState {

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.drawImage(ResourceManager.getImage(AtlantisGame.CREATE_GAME),
				280, 250);
		g.drawImage(ResourceManager.getImage(AtlantisGame.JOIN_GAME),
				280, 300);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		//capture mouse click
		Input input = container.getInput();
		if(input.isMousePressed(input.MOUSE_LEFT_BUTTON)) {
			int posX = input.getMouseX();
			int poxY = input.getMouseY();
			//TODO if click "create game", enter PlayingState with server mode, else enter PlayingState with client mode
		}
	}

	@Override
	public int getID() {
		return AtlantisGame.MENU;
	}

}
