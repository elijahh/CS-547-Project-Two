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
	boolean selectTacticalUnit = false;
	boolean targetUnit = false;
	
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
	
	
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		Input input = container.getInput();
		int x = input.getMouseX(); //Local coordinates of cursor
		int y = input.getMouseY();
		
		checkHotKey(input);
		
		if (selectedUnitID != -1) { // highlight selected unit
			g.setColor(Color.yellow);
			AtlantisEntity selectedUnit = null;
			
			if(selectWorkerUnit){
				selectedUnit = playingState.getStatus()
						.getIdSoldiersMapOnClient().get(selectedUnitID);
			} else if(selectMotherShipUnit){
				selectedUnit = playingState.getStatus()
						.getIdMotherShipsMapOnClient().get(selectedUnitID);
			} else if(selectTacticalUnit){
				selectedUnit = playingState.getStatus()
						.getIdTacticalsMapOnClient().get(selectedUnitID);
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
			if(PlayingState.viewportOffsetX < 0) PlayingState.viewportOffsetX += 10;			
		}	else if (isCursorAtTopEdge(x,y)) {// cursor becomes arrow at edge
			if (!isArrowCursorSet) {
				container.setMouseCursor(upArrow, 0, 0);
				isArrowCursorSet = true;
				isDefaultCursorSet = false;
			}
			if(PlayingState.viewportOffsetY < 0) PlayingState.viewportOffsetY += 10;			
		} else if (isCursorAtRightEdge(x,y)) {// cursor becomes arrow at edge
			if (!isArrowCursorSet) {
				container.setMouseCursor(rightArrow, 0, 0);
				isArrowCursorSet = true;
				isDefaultCursorSet = false;
			}
			if(PlayingState.viewportOffsetX > -PlayingState.MAP_WIDTH + 800) PlayingState.viewportOffsetX -= 10;			
		} else if (isCursorAtBottomEdge(x,y)) {// cursor becomes arrow at edge
			if (!isArrowCursorSet) {
				container.setMouseCursor(downArrow, 0, 0);
				isArrowCursorSet = true;
				isDefaultCursorSet = false;
			}
			if(PlayingState.viewportOffsetY > -PlayingState.MAP_HEIGHT + 470) PlayingState.viewportOffsetY -= 10;			
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
			
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
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
				if(selectWorkerUnit == false && selectMotherShipUnit == false) {
					Map<Long, TacticalSub> tacticals = playingState.getStatus()
							.getIdTacticalsMapOnClient();
					for (Long id : tacticals.keySet()) {
						TacticalSub tactical = tacticals.get(id);
						if (tactical.getTeam() != playingState.team) continue;
						if (y > tactical.getCoarseGrainedMinY() &&
								y < tactical.getCoarseGrainedMaxY() &&
								x > tactical.getCoarseGrainedMinX() &&
								x < tactical.getCoarseGrainedMaxX()) {
							selectedUnitID = id.longValue();
							selectTacticalUnit = true;
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

			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON) ||
					input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
				
				Command move_command = new Command(Command.MOVEMENT,
						playingState.getCurrentFrame(), new Vector(x-PlayingState.viewportOffsetX, y-PlayingState.viewportOffsetY),
						selectedUnitID, 0);
				GameStatus status = playingState.getStatus();
				status.sendCommand(move_command);
				
				action = 0;
			}
		} else if (action == 2) { // attack
			if (isDefaultCursorSet || isArrowCursorSet) {
				container.setMouseCursor(pixel, 0, 0);
				isDefaultCursorSet = false;
				isArrowCursorSet = false;
			}
			g.drawImage(targetAttack, x, y);

			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON) ||
					input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
				GameStatus status = playingState.getStatus();
			
				targetUnit = false;
				long targetUnitID = -1;
				y += targetAttack.getHeight() / 2f;
				x += targetAttack.getWidth() / 2f;
				
				Map<Long, Soldier> soldiers = status
						.getIdSoldiersMapOnClient();
				for (Long id : soldiers.keySet()) {
					Soldier soldier = soldiers.get(id);
					if (soldier.getTeam() == playingState.team) continue;
					if (y > soldier.getCoarseGrainedMinY() &&
							y < soldier.getCoarseGrainedMaxY() &&
							x > soldier.getCoarseGrainedMinX() &&
							x < soldier.getCoarseGrainedMaxX()) {
						targetUnitID = id.longValue();
						targetUnit = true;
						break;
					}
				}
				
				if(!targetUnit) {
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
							targetUnit = true;
							break;
						}
					}
				}
				
				if (!targetUnit) {
					Map<Long, TacticalSub> tacticalSubs = playingState.getStatus()
							.getIdTacticalsMapOnClient();
					for (Long id : tacticalSubs.keySet()) {
						TacticalSub tactical = tacticalSubs.get(id);
						if (tactical.getTeam() == playingState.team) continue;
						if (y > tactical.getCoarseGrainedMinY() &&
								y < tactical.getCoarseGrainedMaxY() &&
								x > tactical.getCoarseGrainedMinX() &&
								x < tactical.getCoarseGrainedMaxX()) {
							targetUnitID = id.longValue();
							targetUnit = true;
							break;
						}
					}
				}
				
				if (targetUnit) {
					Command attack_command = new Command(Command.ATTACK,
							playingState.getCurrentFrame(), new Vector(x-PlayingState.viewportOffsetX, y-PlayingState.viewportOffsetY),
							selectedUnitID, targetUnitID);
					status.sendCommand(attack_command);
				}

				action = 0;
			}
		}
		
		g.drawImage(overlay, 0, 470);
		g.setColor(Color.black);
		g.fillRect(675, 475, 120, 120);
		g.setColor(Color.white);
		// Total minimap size scale:  120/2048 ~= 0.0586
		// Region width:              800/2048 * 120 ~= 46.875
		// Region height:             470/2048 * 120 ~= 27.539
		float miniMapX = 675 - PlayingState.viewportOffsetX * 0.0586f;
		float miniMapY = 475 - PlayingState.viewportOffsetY * 0.0586f;
		g.drawRect(miniMapX, miniMapY, 46.875f, 27.539f);
		
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
						g.fillRect(x, y, 60, 20);
						g.setColor(Color.black);
						g.drawString("Move(M)", x, y);
						
						if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) action = 1;
					} else if (x > 350 && x < 400 && !selectMotherShipUnit) { // attack button
						// tooltip
						x += 20;
						g.setColor(Color.yellow);
						g.fillRect(x, y, 80, 20);
						g.setColor(Color.black);
						g.drawString("Attack(A)", x, y);
						
						if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) action = 2;
					}
				}
				
				if (x > 230 && x < 280) { // purchase button
					x += 20;
					g.setColor(Color.yellow);
					g.fillRect(x, y, 100, 20);
					g.setColor(Color.black);
					g.drawString("Purchase(P)", x, y);
					
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
					g.fillRect(x, y, 200, 20);
					g.setColor(Color.black);
					g.drawString("Purchase Soldier (500)", x, y);
					
					if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) &&
							clickTimer <= 0 && playingState.gold >= 500) {
						playingState.gold -= 500;
						clickTimer = 1000;
						playingState.getStatus().sendCommand(new Command(
								Command.PURCHASE, playingState.getCurrentFrame(),
								new Vector(400 - PlayingState.viewportOffsetX,
										200 - PlayingState.viewportOffsetY),
								0, playingState.team.ordinal()));
					}
				} else if (x > 350 && x < 400) {
					x += 20;
					g.setColor(Color.yellow);
					g.fillRect(x, y, 310, 20);
					g.setColor(Color.black);
					g.drawString("Purchase Tactical Submarine (2000)", x, y);
					
					if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) &&
							clickTimer <= 0 && playingState.gold >= 2000) {
						playingState.gold -= 2000;
						clickTimer = 1000;
						playingState.getStatus().sendCommand(new Command(
								Command.PURCHASE, playingState.getCurrentFrame(),
								new Vector(400 - PlayingState.viewportOffsetX,
										200 - PlayingState.viewportOffsetY),
								1, playingState.team.ordinal()));
					}
				}
			}
		}
		
		g.setColor(Color.yellow);
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
	
	private void checkHotKey(Input input) {
		if (input.isKeyPressed(Input.KEY_M)) {
			action = 1;
		} else if(input.isKeyPressed(Input.KEY_A)) {
			action = 2;
		} else if(input.isKeyPressed(Input.KEY_P)) {
			purchaseMenuOpen = true;
		}
	}
}
