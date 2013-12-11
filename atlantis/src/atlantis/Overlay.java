package atlantis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import atlantis.AtlantisEntity.Team;
import atlantis.networking.Command;
import jig.ResourceManager;
import jig.Vector;

public class Overlay {
	
	private static final String SOLDIER_SOUND_1 = "atlantis/resource/soldier_sound_1.wav";
	private static final String SOLDIER_SOUND_2 = "atlantis/resource/soldier_sound_2.wav";
	private static final String SOLDIER_SOUND_3 = "atlantis/resource/soldier_sound_3.wav";
	private static final String SOLDIER_SOUND_4 = "atlantis/resource/soldier_sound_4.wav";
	private static final String SOLDIER_SOUND_5 = "atlantis/resource/soldier_sound_5.wav";
	private static final String MOTHERSHIP_SELECTED = "atlantis/resource/mothership_select.wav";
	private static final String TACTICAL_SELECTED = "atlantis/resource/tactical_select.wav";
	
	static {		
		ResourceManager.loadSound(SOLDIER_SOUND_1);
		ResourceManager.loadSound(SOLDIER_SOUND_2);
		ResourceManager.loadSound(SOLDIER_SOUND_3);
		ResourceManager.loadSound(SOLDIER_SOUND_4);
		ResourceManager.loadSound(SOLDIER_SOUND_5);	
		ResourceManager.loadSound(MOTHERSHIP_SELECTED);
		ResourceManager.loadSound(TACTICAL_SELECTED);
	}
		
	int clickTimer = 0;
	
	Image overlay;
	Image actionMove;
	Image actionAttack;
	Image actionMount;
	Image actionUnmount;
	Image actionPurchase;
	Image actionPurchaseSoldier;
	Image actionPurchaseTactical;
	Image actionBack;
	Image targetMove;
	Image targetAttack;
	Image targetMount;
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
	public short action = 0; // 1 = move, 2 = attack, 3 = mount
	
	public Overlay(PlayingState ps) {
		playingState = ps;
		
		overlay = ResourceManager.getImage(AtlantisGame.OVERLAY);
		actionMove = ResourceManager.getImage(AtlantisGame.ACTION_MOVE);
		actionAttack = ResourceManager.getImage(AtlantisGame.ACTION_ATTACK);
		actionMount = ResourceManager.getImage(AtlantisGame.ACTION_MOUNT);
		actionUnmount = ResourceManager.getImage(AtlantisGame.ACTION_UNMOUNT);
		actionPurchase = ResourceManager.getImage(AtlantisGame.ACTION_PURCHASE);
		actionPurchaseSoldier = ResourceManager.getImage(AtlantisGame.ACTION_PURCHASE_SOLDIER);
		actionPurchaseTactical = ResourceManager.getImage(AtlantisGame.ACTION_PURCHASE_TACTICAL);
		actionBack = ResourceManager.getImage(AtlantisGame.ACTION_BACK);
		targetMove = ResourceManager.getImage(AtlantisGame.TARGET_MOVE);
		targetAttack = ResourceManager.getImage(AtlantisGame.TARGET_ATTACK);
		targetMount = ResourceManager.getImage(AtlantisGame.TARGET_MOUNT);
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
			if (selectedUnit != null && selectedUnit.visible) {
					g.drawRect(selectedUnit.getCoarseGrainedMinX(),
							selectedUnit.getCoarseGrainedMinY(),
							selectedUnit.getCoarseGrainedWidth(),
							selectedUnit.getCoarseGrainedHeight());
			} else { // deselect if invisible
				selectedUnitID = -1;
				selectWorkerUnit = selectMotherShipUnit = selectTacticalUnit = false; 
				selectedUnit = null;
			}
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
					if (!soldier.visible) continue;
					if (y > soldier.getCoarseGrainedMinY() &&
							y < soldier.getCoarseGrainedMaxY() &&
							x > soldier.getCoarseGrainedMinX() &&
							x < soldier.getCoarseGrainedMaxX()) {
						selectedUnitID = id.longValue();
						selectWorkerUnit = true;
						ResourceManager.getSound(getRandomSound()).play();
						break;
					}
				}
				if(selectWorkerUnit == false) {
					Map<Long, MotherShip> motherships = playingState.getStatus()
							.getIdMotherShipsMapOnClient();
					for (Long id : motherships.keySet()) {
						MotherShip mothership = motherships.get(id);
						if (mothership.getTeam() != playingState.team) continue;
						if (!mothership.visible) continue;
						if (y > mothership.getCoarseGrainedMinY() &&
								y < mothership.getCoarseGrainedMaxY() &&
								x > mothership.getCoarseGrainedMinX() &&
								x < mothership.getCoarseGrainedMaxX()) {
							selectedUnitID = id.longValue();
							selectMotherShipUnit = true;
							selectTacticalUnit = false;
							ResourceManager.getSound(MOTHERSHIP_SELECTED).play();
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
						if (!tactical.visible) continue;
						if (y > tactical.getCoarseGrainedMinY() &&
								y < tactical.getCoarseGrainedMaxY() &&
								x > tactical.getCoarseGrainedMinX() &&
								x < tactical.getCoarseGrainedMaxX()) {
							selectedUnitID = id.longValue();
							selectTacticalUnit = true;
							ResourceManager.getSound(TACTICAL_SELECTED).play();
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
				
				Map<Long, Soldier> soldiers = playingState.getStatus()
						.getIdSoldiersMapOnClient();
				for (Long id : soldiers.keySet()) {
					if(selectedUnitID == id.longValue()){
						ResourceManager.getSound(getRandomSound()).play();
						break;
					}
				}
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
					if (!soldier.visible) continue;
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
					Map<Long, MotherShip> motherships = status
							.getIdMotherShipsMapOnClient();
					for (Long id : motherships.keySet()) {
						MotherShip mothership = motherships.get(id);
						if (mothership.getTeam() == playingState.team) continue;
						if (!mothership.visible) continue;
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
					Map<Long, TacticalSub> tacticalSubs = status
							.getIdTacticalsMapOnClient();
					for (Long id : tacticalSubs.keySet()) {
						TacticalSub tactical = tacticalSubs.get(id);
						if (tactical.getTeam() == playingState.team) continue;
						if (!tactical.visible) continue;
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
					
					for (Long id : soldiers.keySet()) {
						if(selectedUnitID == id.longValue()){
							ResourceManager.getSound(getRandomSound()).play();
							break;
						}
					}
				}

				action = 0;
			}
		} else if (action == 3) { // mount
			if (isDefaultCursorSet || isArrowCursorSet) {
				container.setMouseCursor(pixel, 0, 0);
				isDefaultCursorSet = false;
				isArrowCursorSet = false;
			}
			g.drawImage(targetMount, x, y);

			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON) ||
					input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
				GameStatus status = playingState.getStatus();
			
				targetUnit = false;
				long targetUnitID = -1;
				y += targetMount.getHeight() / 2f;
				x += targetMount.getWidth() / 2f;
				
				Map<Long, TacticalSub> tacticalSubs = status
						.getIdTacticalsMapOnClient();
				for (Long id : tacticalSubs.keySet()) {
					TacticalSub tactical = tacticalSubs.get(id);
					if (tactical.getTeam() != playingState.team) continue;
					if (!tactical.visible) continue;
					if (tactical.soldiers.size() >= 4) continue;
					if (y > tactical.getCoarseGrainedMinY() &&
							y < tactical.getCoarseGrainedMaxY() &&
							x > tactical.getCoarseGrainedMinX() &&
							x < tactical.getCoarseGrainedMaxX()) {
						targetUnitID = id.longValue();
						targetUnit = true;
						break;
					}
				}
				
				if (targetUnit) {
					Command mount_command = new Command(Command.MOUNT,
							playingState.getCurrentFrame(), new Vector(x-PlayingState.viewportOffsetX, y-PlayingState.viewportOffsetY),
							selectedUnitID, targetUnitID);
					status.sendCommand(mount_command);
				}

				action = 0;
			}			
		} else if (action == 4) { // unmount
			GameStatus status = playingState.getStatus();
			Command unmount_command = new Command(Command.UNMOUNT,
					playingState.getCurrentFrame(), new Vector(0, 0),
					selectedUnitID, 0);
			status.sendCommand(unmount_command);
			action = 0;
		}
		
		g.drawImage(overlay, 0, 470);
		// unit model
		if (selectWorkerUnit) {
			Soldier s = playingState.getStatus().getIdSoldiersMapOnClient()
					.get(selectedUnitID);
			if (s != null) {
				g.setColor(Color.white);
				g.drawString("Soldier", 5, 475);
				g.drawImage(ResourceManager.getImage(s
						.getStillImageFilename(AtlantisEntity.DOWN_UNIT_VECTOR))
						.getScaledCopy(.9f), 40, 490);
				if (x < 120 && y > 470) { // info
					g.drawString("High Damage", 10, 500);
					g.drawString("Low Health", 10, 520);
					g.drawString("Short Range", 10, 540);
				}
			}
		} else if (selectMotherShipUnit) {
			MotherShip m = playingState.getStatus().getIdMotherShipsMapOnClient()
					.get(selectedUnitID);
			if (m != null) {
				g.setColor(Color.white);
				g.drawString("Mothership", 5, 475);
				g.drawImage(ResourceManager.getImage(m
						.getStillImageFilename(AtlantisEntity.RIGHT_UNIT_VECTOR))
						.getScaledCopy(.6f), 5, 500);
				if (x < 120 && y > 470) { // info
					g.drawString("Central Base", 10, 500);
					g.drawString("High Health", 10, 520);
					g.drawString("Low Speed", 10, 540);
				}
			}
		} else if (selectTacticalUnit) {
			TacticalSub t = playingState.getStatus().getIdTacticalsMapOnClient()
					.get(selectedUnitID);
			if (t != null) {
				g.setColor(Color.white);
				g.drawString("Tactical Sub", 5, 475);
				g.drawImage(ResourceManager.getImage(t
						.getStillImageFilename(AtlantisEntity.RIGHT_UNIT_VECTOR))
						.getScaledCopy(.8f), 5, 500);
				if (x < 120 && y > 470) { // info
					g.drawString("Holds 4 Soldiers", 10, 500);
					g.drawString("Long Range", 10, 520);
					g.drawString("Medium Health", 10, 540);
					g.drawString("Low Damage", 10, 560);
				}
			}
		}
		
		// minimap
		g.setColor(Color.black);
		g.fillRect(675, 475, 120, 120);
		g.setColor(Color.white);
		// Total minimap size scale:  120/2048 ~= 0.0586; inverse 2048/120 ~= 17.067
		// Region width:              800/2048 * 120 ~= 46.875
		// Region height:             470/2048 * 120 ~= 27.539
		float miniMapX = 675 - PlayingState.viewportOffsetX * 0.0586f;
		float miniMapY = 475 - PlayingState.viewportOffsetY * 0.0586f;
		g.drawRect(miniMapX, miniMapY, 46.875f, 27.539f);
		GameStatus status = playingState.getStatus();
		ArrayList<AtlantisEntity> allEntities = new ArrayList<AtlantisEntity>();
		allEntities.addAll(status.getSoldiers());
		allEntities.addAll(status.getMotherShips());
		allEntities.addAll(status.getTacticals());
		for (AtlantisEntity s : allEntities) {
			if (s.visible && s.getTeam() == playingState.team) {
				if (playingState.team == Team.RED) {
					g.setColor(Color.red);
					if (s.getClass() == MotherShip.class) g.setColor(Color.magenta); 
				} else {
					g.setColor(Color.blue);
					if (s.getClass() == MotherShip.class) g.setColor(Color.cyan);
				}
				g.drawOval(675 + (s.getX()-PlayingState.viewportOffsetX) * 0.0586f,
						475 + (s.getY()-PlayingState.viewportOffsetY) * 0.0586f, 1, 1);
			} else if (s.visible && s.visibleToOpponent && s.getTeam() != playingState.team) {
				if (playingState.team == Team.RED) {
					g.setColor(Color.blue);
					if (s.getClass() == MotherShip.class) g.setColor(Color.cyan);
				} else {
					g.setColor(Color.red);
					if (s.getClass() == MotherShip.class) g.setColor(Color.magenta);
				}
				g.drawOval(675 + (s.getX()-PlayingState.viewportOffsetX) * 0.0586f,
						475 + (s.getY()-PlayingState.viewportOffsetY) * 0.0586f, 1, 1);
			}
		}
		if (x > 675 && y > 475 && input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			PlayingState.viewportOffsetX = Math.min(Math.max(-1250, (int) (-(x-700) * 17.067f)), 0);
			PlayingState.viewportOffsetY = Math.min(Math.max(-1580, (int) (-(y-490) * 17.067f)), 0);
		}
		
		if (!purchaseMenuOpen) {
			g.drawImage(actionPurchase, 230, 520);
			if (selectedUnitID != -1) {
				g.drawImage(actionMove, 290, 520);
				if (!selectMotherShipUnit) g.drawImage(actionAttack, 350, 520);
				if (selectWorkerUnit) {
					g.drawImage(actionMount, 410, 520);
				} else if (selectTacticalUnit) {
					g.drawImage(actionUnmount, 410, 520);
				}
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
					} else if (x > 410 && x < 460 && selectWorkerUnit) { // mount button
						// tooltip
						x += 20;
						g.setColor(Color.yellow);
						g.fillRect(x, y, 110, 20);
						g.setColor(Color.black);
						g.drawString("Mount(T) Sub", x, y);
						
						if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) action = 3;
					} else if (x > 410 && x < 460 && selectTacticalUnit) { // unmount button
						// tooltip
						x += 20;
						g.setColor(Color.yellow);
						g.fillRect(x, y, 190, 20);
						g.setColor(Color.black);
						int numSoldiers = playingState.getStatus()
								.getIdTacticalsMapOnClient()
								.get(selectedUnitID)
								.numSoldiers;
						String unloadMessage = "Unload(U) " + numSoldiers + " Soldier";
						if (numSoldiers != 1) unloadMessage += "s";
						g.drawString(unloadMessage, x, y);
						
						if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) action = 4;						
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
						status = playingState.getStatus();
						MotherShip ourShip = null;
						for (MotherShip mothership : status.getMotherShips()) {
							if (mothership.getTeam() == playingState.team) {
								ourShip = mothership;
								break;
							}
						}
						if (ourShip != null) {
							boolean canSpawn = false;
							float spawnX = ourShip.getX()-PlayingState.viewportOffsetX-50;
							float spawnY = ourShip.getY()-PlayingState.viewportOffsetY-50;
							// scan radius 50 around mothership for spawn point
							for (int i = 0; i <= 2; i++) {
								for (int j = 0; j <= 2; j++) {
									spawnX += 50*i;
									spawnY += 50*j;
									if (spawnX > 2040 || spawnY > 2040) continue;
									if (!playingState.map.isPositionVectorInsideTerrainTile(
											new Vector(spawnX, spawnY))) {
										canSpawn = true;
										break;
									}
								}
							}
							if (canSpawn) {
								status.sendCommand(new Command(
										Command.PURCHASE, playingState.getCurrentFrame(),
										new Vector(spawnX, spawnY),
										0, playingState.team.ordinal()));
							} else {
								playingState.gold += 500;
								g.setColor(Color.red);
								g.drawString("Unable to spawn soldier. Please move your mothership away from terrain.", 130, 575);							
							}
						}
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
						status = playingState.getStatus();
						MotherShip ourShip = null;
						for (MotherShip mothership : status.getMotherShips()) {
							if (mothership.getTeam() == playingState.team) {
								ourShip = mothership;
								break;
							}
						}
						if (ourShip != null) {
							float spawnX = ourShip.getX()-PlayingState.viewportOffsetX-40;
							if (spawnX < 0) spawnX += 80;
							status.sendCommand(new Command(
									Command.PURCHASE, playingState.getCurrentFrame(),
									new Vector(spawnX,
									ourShip.getY()-PlayingState.viewportOffsetY),
									1, playingState.team.ordinal()));
						}
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
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			action = 0;
		} else if (input.isKeyPressed(Input.KEY_M) && (selectWorkerUnit || selectMotherShipUnit || selectTacticalUnit)) {
			action = 1;
		} else if(input.isKeyPressed(Input.KEY_A) && (selectWorkerUnit || selectTacticalUnit)) {
			action = 2;
		} else if (input.isKeyPressed(Input.KEY_T) && selectWorkerUnit) {
			action = 3;
		} else if (input.isKeyPressed(Input.KEY_U) && selectTacticalUnit) {
			action = 4;
		} else if(input.isKeyPressed(Input.KEY_P)) {
			purchaseMenuOpen = true;
		}
	}
	
	private String getRandomSound() {
		int random = (int) (Math.random() *5 % 5);
		switch (random){
		case 0:
			return SOLDIER_SOUND_1;
		case 1:
			return SOLDIER_SOUND_2;
		case 2:	
			return SOLDIER_SOUND_3;
		case 3:
			return SOLDIER_SOUND_4;
		case 4:
			return SOLDIER_SOUND_5;
		}
		return null;
	}
}
