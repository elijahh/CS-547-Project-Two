package atlantis;

import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import atlantis.networking.Command;
import jig.ResourceManager;
import jig.Vector;

public class Overlay {
	int clickTimer = 0;
	
	Image overlay;
	Image actionMove;
	Image actionAttack;
	Image actionPurchase;
	Image actionPurchaseSoldier;
	Image actionPurchaseTactical;
	Image actionBack;
	Image targetMove;
	Image targetAttack;
	Image pixel;
	Image upArrow;
	Image rightArrow;
	Image downArrow;
	Image leftArrow;
	
	boolean isDefaultCursorSet = true;
	boolean isArrowCursorSet = false;
	
	boolean selectWorkerUnit = false;
	boolean selectMotherShipUnit = false;
	boolean targetWorkerUnit = false;
	boolean targetMotherShipUnit = false;
	
	boolean purchaseMenuOpen = false;
	
	PlayingState playingState;
	public long selectedUnitID = -1;
	public short action = 0; // 1 = move, 2 = attack
	
	public Overlay(PlayingState ps) {
		playingState = ps;
		
		// TODO: model of unit on left, minimap on right
		overlay = ResourceManager.getImage(AtlantisGame.OVERLAY);
		actionMove = ResourceManager.getImage(AtlantisGame.ACTION_MOVE);
		actionAttack = ResourceManager.getImage(AtlantisGame.ACTION_ATTACK);
		actionPurchase = ResourceManager.getImage(AtlantisGame.ACTION_PURCHASE);
		actionPurchaseSoldier = ResourceManager.getImage(AtlantisGame.ACTION_PURCHASE_SOLDIER);
		actionPurchaseTactical = ResourceManager.getImage(AtlantisGame.ACTION_PURCHASE_TACTICAL);
		actionBack = ResourceManager.getImage(AtlantisGame.ACTION_BACK);
		targetMove = ResourceManager.getImage(AtlantisGame.TARGET_MOVE);
		targetAttack = ResourceManager.getImage(AtlantisGame.TARGET_ATTACK);
		pixel = ResourceManager.getImage(AtlantisGame.PIXEL);
		
		upArrow = ResourceManager.getImage(AtlantisGame.ARROW_UP);
		rightArrow = ResourceManager.getImage(AtlantisGame.ARROW_RIGHT);
		downArrow = ResourceManager.getImage(AtlantisGame.ARROW_DOWN);
		leftArrow = ResourceManager.getImage(AtlantisGame.ARROW_LEFT);
	}
	
	
	// TODO: should also bind actions to hotkeys in another class
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		Input input = container.getInput();
		int x = input.getMouseX(); //Local coordinates of cursor
		int y = input.getMouseY();
		
		if (selectedUnitID != -1) { // highlight selected unit
			g.setColor(Color.yellow);
			AtlantisEntity selectedUnit = null;
			
			if(selectWorkerUnit){
				selectedUnit = playingState.getStatus()
						.getIdSoldiersMapOnClient().get(selectedUnitID);
			} else if(selectMotherShipUnit){
				selectedUnit = playingState.getStatus()
						.getIdMotherShipsMapOnClient().get(selectedUnitID);
			}
			g.drawRect(selectedUnit.getCoarseGrainedMinX(),
					selectedUnit.getCoarseGrainedMinY(),
					selectedUnit.getCoarseGrainedWidth(),
					selectedUnit.getCoarseGrainedHeight());
		}
		
		if (isCursorAtLeftEdge(x,y)) {// cursor becomes arrow at edge
			if (!isArrowCursorSet) {
				container.setMouseCursor(leftArrow, 0, 0);
				isArrowCursorSet = true;
				isDefaultCursorSet = false;
			}
			PlayingState.viewportOffsetX += 10;			
		}	else if (isCursorAtTopEdge(x,y)) {// cursor becomes arrow at edge
			if (!isArrowCursorSet) {
				container.setMouseCursor(upArrow, 0, 0);
				isArrowCursorSet = true;
				isDefaultCursorSet = false;
			}
			PlayingState.viewportOffsetY += 10;			
		} else if (isCursorAtRightEdge(x,y)) {// cursor becomes arrow at edge
			if (!isArrowCursorSet) {
				container.setMouseCursor(rightArrow, 0, 0);
				isArrowCursorSet = true;
				isDefaultCursorSet = false;
			}
			PlayingState.viewportOffsetX -= 10;			
		} else if (isCursorAtBottomEdge(x,y)) {// cursor becomes arrow at edge
			if (!isArrowCursorSet) {
				container.setMouseCursor(downArrow, 0, 0);
				isArrowCursorSet = true;
				isDefaultCursorSet = false;
			}
			PlayingState.viewportOffsetY -= 10;			
		}  else if (y > 470) { // select action
			if (!isDefaultCursorSet) {
				container.setMouseCursor(AtlantisGame.cursor, 0, 0);
				isDefaultCursorSet = true;
				isArrowCursorSet = false;
			}
		} else if (action == 0) { // select unit
			if (!isDefaultCursorSet) {
				container.setMouseCursor(AtlantisGame.cursor, 0, 0);
				isDefaultCursorSet = true;
				isArrowCursorSet = false;
			}
			
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
				selectWorkerUnit = false;
				selectMotherShipUnit = false;
				selectedUnitID = -1;
				
				Map<Long, Soldier> soldiers = playingState.getStatus()
						.getIdSoldiersMapOnClient();
				for (Long id : soldiers.keySet()) {
					Soldier soldier = soldiers.get(id);
					if (soldier.getTeam() != playingState.team) continue;
					if (y > soldier.getCoarseGrainedMinY() &&
							y < soldier.getCoarseGrainedMaxY() &&
							x > soldier.getCoarseGrainedMinX() &&
							x < soldier.getCoarseGrainedMaxX()) {
						selectedUnitID = id.longValue();
						selectWorkerUnit = true;
						break;
					}
				}
				if(selectWorkerUnit == false) {
					Map<Long, MotherShip> motherships = playingState.getStatus()
							.getIdMotherShipsMapOnClient();
					for (Long id : motherships.keySet()) {
						MotherShip mothership = motherships.get(id);
						if (mothership.getTeam() != playingState.team) continue;
						if (y > mothership.getCoarseGrainedMinY() &&
								y < mothership.getCoarseGrainedMaxY() &&
								x > mothership.getCoarseGrainedMinX() &&
								x < mothership.getCoarseGrainedMaxX()) {
							selectedUnitID = id.longValue();
							selectMotherShipUnit = true;
							break;
						}
					}
				}
			}
		} else if (action == 1) { // move
			if (isDefaultCursorSet || isArrowCursorSet) {
				container.setMouseCursor(pixel, 0, 0);
				isDefaultCursorSet = false;
				isArrowCursorSet = false;
			}
			g.drawImage(targetMove, x, y);
			
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) ||
					input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
				
				Command move_command = new Command(Command.MOVEMENT,
						playingState.getCurrentFrame(), new Vector(x-PlayingState.viewportOffsetX, y-PlayingState.viewportOffsetY),
						selectedUnitID, 0);
				GameStatus status = playingState.getStatus();
				status.sendCommand(move_command);
				

				action = 0;
				selectedUnitID = -1;
			}
		} else if (action == 2) { // attack
			if (isDefaultCursorSet || isArrowCursorSet) {
				container.setMouseCursor(pixel, 0, 0);
				isDefaultCursorSet = false;
				isArrowCursorSet = false;
			}
			g.drawImage(targetAttack, x, y);
			
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) ||
					input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
				GameStatus status = playingState.getStatus();
				Map<Long, Soldier> soldiers = status
						.getIdSoldiersMapOnClient();
				targetWorkerUnit = false;
				targetMotherShipUnit = false;
				long targetUnitID = -1;
				for (Long id : soldiers.keySet()) {
					Soldier soldier = soldiers.get(id);
					if (soldier.getTeam() == playingState.team) continue;
					y += targetAttack.getHeight() / 2f;
					x += targetAttack.getWidth() / 2f;
					if (y > soldier.getCoarseGrainedMinY() &&
							y < soldier.getCoarseGrainedMaxY() &&
							x > soldier.getCoarseGrainedMinX() &&
							x < soldier.getCoarseGrainedMaxX()) {
						targetUnitID = id.longValue();
						targetWorkerUnit = true;
						break;
					}
				}
				
				if(targetWorkerUnit == false) {
					Map<Long, MotherShip> motherships = playingState.getStatus()
							.getIdMotherShipsMapOnClient();
					for (Long id : motherships.keySet()) {
						MotherShip mothership = motherships.get(id);
						if (mothership.getTeam() == playingState.team) continue;
						if (y > mothership.getCoarseGrainedMinY() &&
								y < mothership.getCoarseGrainedMaxY() &&
								x > mothership.getCoarseGrainedMinX() &&
								x < mothership.getCoarseGrainedMaxX()) {
							targetUnitID = id.longValue();
							targetMotherShipUnit = true;
							break;
						}
					}
				}
				
				if (targetWorkerUnit || targetMotherShipUnit) {
					Command attack_command = new Command(Command.ATTACK,
							playingState.getCurrentFrame(), new Vector(x-PlayingState.viewportOffsetX, y-PlayingState.viewportOffsetY),
							selectedUnitID, targetUnitID);
					status.sendCommand(attack_command);
				}

				action = 0;
				selectedUnitID = -1;
			}
		}
		
		g.drawImage(overlay, 0, 470);
		
		if (!purchaseMenuOpen) {
			g.drawImage(actionPurchase, 230, 520);
			if (selectedUnitID != -1) {
				g.drawImage(actionMove, 290, 520);
				if (!selectMotherShipUnit) g.drawImage(actionAttack, 350, 520);
			}
		} else {
			g.drawImage(actionBack, 230, 520);
			g.drawImage(actionPurchaseSoldier, 290, 520);
			g.drawImage(actionPurchaseTactical, 350, 520);
		}
		
		if (y > 520 && y < 570) {
			if (!purchaseMenuOpen) {
				if (selectedUnitID != -1) {
					if (x > 290 && x < 340) { // move button
						// tooltip
						x += 20;
						g.setColor(Color.yellow);
						g.fillRect(x, y, 40, 20);
						g.setColor(Color.black);
						g.drawString("Move", x, y);
						
						if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) action = 1;
					} else if (x > 350 && x < 400 && !selectMotherShipUnit) { // attack button
						// tooltip
						x += 20;
						g.setColor(Color.yellow);
						g.fillRect(x, y, 60, 20);
						g.setColor(Color.black);
						g.drawString("Attack", x, y);
						
						if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) action = 2;
					}
				}
				
				if (x > 230 && x < 280) { // purchase button
					x += 20;
					g.setColor(Color.yellow);
					g.fillRect(x, y, 80, 20);
					g.setColor(Color.black);
					g.drawString("Purchase", x, y);
					
					if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) &&
							clickTimer <= 0) {
						clickTimer = 1000;
						purchaseMenuOpen = true;
					}
				}
			} else { // purchasing
				if (x > 230 && x < 280) { // back
					x += 20;
					g.setColor(Color.yellow);
					g.fillRect(x, y, 40, 20);
					g.setColor(Color.black);
					g.drawString("Back", x, y);
					
					if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) &&
							clickTimer <= 0) {
						clickTimer = 1000;
						purchaseMenuOpen = false;
					}
				} else if (x > 290 && x < 340) { // purchase soldier
					x += 20;
					g.setColor(Color.yellow);
					g.fillRect(x, y, 150, 20);
					g.setColor(Color.black);
					g.drawString("Purchase Soldier", x, y);
				} else if (x > 350 && x < 400) {
					x += 20;
					g.setColor(Color.yellow);
					g.fillRect(x, y, 250, 20);
					g.setColor(Color.black);
					g.drawString("Purchase Tactical Submarine", x, y);
				}
			}
		}
	}
	
	public void update(int delta) {
		if (clickTimer > 0) clickTimer -= delta;
	}
	
	private boolean isCursorAtLeftEdge(int x, int y) {
		return (x > 0 && x < 30 && y > 30 && y < 570); 
	}
	
	private boolean isCursorAtTopEdge(int x, int y) {
		return ( y > 0 && y < 30 && x > 30 && x < 770); 
	}
	
	private boolean isCursorAtRightEdge(int x, int y) {
		return ( x > 770 && x < 800 && y > 30 && y < 570); 
	}
	
	private boolean isCursorAtBottomEdge(int x, int y) {
		return ( y > 570 && y < 600 && x > 30 && x < 770); 
	}
}
