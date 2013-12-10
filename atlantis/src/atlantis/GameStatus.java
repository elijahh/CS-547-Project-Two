package atlantis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jig.Collision;
import jig.Vector;

import org.newdawn.slick.GameContainer;

import atlantis.AtlantisEntity;
import atlantis.AtlantisEntity.Team;
import atlantis.networking.AtlantisClient;
import atlantis.networking.AtlantisServer;
import atlantis.networking.Command;
import atlantis.networking.CommandLockStep;
import atlantis.networking.ResultLockStep;
import atlantis.networking.SimulationResult;

public class GameStatus {
	
	private PlayingState playing_state;
	
	static Random random_generator;
	
	static {
		random_generator = new Random();
		random_generator.setSeed(System.currentTimeMillis());
	}
	
	public MotherShip mothership_on_server_1;
	public MotherShip mothership_on_server_2;
	
	// TEMPORARY FOR DEVELOPMENT
	public Soldier soldier_on_server_1;
	public Soldier soldier_on_server_2;
	public TacticalSub tactical_on_server_1;
	public TacticalSub tactical_on_server_2;
	public TacticalSub tactical_on_server_3;
	// TEMPORARY FOR DEVELOPMENT
	
	public ArrayList<AtlantisEntity> atlantisEntities_team_red;
	public ArrayList<AtlantisEntity> atlantisEntities_team_blue;
	
	public GameStatus(PlayingState playing_state) {
		this.playing_state = playing_state;
		
		mothership_on_server_1 = new MotherShip(1848, 1848, new Vector(0, 0));
		motherships_server_model.put(mothership_on_server_1.getIdentity(),
				mothership_on_server_1);

		mothership_on_server_2 = new MotherShip(200, 200, new Vector(0, 0));
		mothership_on_server_2.setTeam(Team.BLUE);
		motherships_server_model.put(mothership_on_server_2.getIdentity(),
				mothership_on_server_2);
		
		// TEMPORARY FOR DEVELOPMENT
		soldier_on_server_1 = 
				new Soldier(1698, 1948, new Vector(0, 0));
		soldiers_server_model.put(soldier_on_server_1.getIdentity(),
				soldier_on_server_1);
		soldier_on_server_2 = 
				new Soldier(350, 200, new Vector(0, 0));
		soldier_on_server_2.setTeam(Team.BLUE);
		soldiers_server_model.put(soldier_on_server_2.getIdentity(),
				soldier_on_server_2);
		
		tactical_on_server_1 = new TacticalSub(1548, 1848, new Vector(0,0));
		tacticals_server_model.put(tactical_on_server_1.getIdentity(), tactical_on_server_1);	
		tactical_on_server_2 = new TacticalSub(500, 200, new Vector(0,0));
		tactical_on_server_2.setTeam(Team.BLUE);
		tacticals_server_model.put(tactical_on_server_2.getIdentity(), tactical_on_server_2);
		tactical_on_server_3 = new TacticalSub(700, 200, new Vector(0,0));
		tacticals_server_model.put(tactical_on_server_3.getIdentity(), tactical_on_server_3);
		// TEMPORARY FOR DEVELOPMENT
		
	}
	
	private boolean game_over = false;
	public boolean isGameOver() { return game_over; }
	
	private boolean red_wins = false;
	public boolean isRedWinner() { return red_wins; }

	private List<Command> commands_to_server = new ArrayList<Command>();

	public void sendCommand(Command command) {
		synchronized (commands_to_server) {
			commands_to_server.add(command);
		}
	}
	
	public void update(GameContainer container, int delta) {
		int currentFrame = playing_state.getCurrentFrame();
		AtlantisServer server = playing_state.getServer();
		
		if (null != server) {
			
			/* ---------------------------------------------------------- */
			/* Is entity visible to opponent */
			atlantisEntities_team_red = new ArrayList<AtlantisEntity>();
			atlantisEntities_team_blue = new ArrayList<AtlantisEntity>();
			
			for(AtlantisEntity entity: motherships_server_model.values()) {
				if (entity.getTeam() == Team.RED) {
					entity.visibleToOpponent = false;
					atlantisEntities_team_red.add(entity);
				}
			}
			for(AtlantisEntity entity: soldiers_server_model.values()) {
				if (entity.getTeam() == Team.RED) {
					entity.visibleToOpponent = false;
					atlantisEntities_team_red.add(entity);
				}
			}
			for(AtlantisEntity entity: tacticals_server_model.values()) {
				if (entity.getTeam() == Team.RED) {
					entity.visibleToOpponent = false;
					atlantisEntities_team_red.add(entity);
				}
			}
			
			for(AtlantisEntity entity: motherships_server_model.values()) {
				if (entity.getTeam() == Team.BLUE) {
					entity.visibleToOpponent = false;
					atlantisEntities_team_blue.add(entity);
				}
			}
			for(AtlantisEntity entity: soldiers_server_model.values()) {
				if (entity.getTeam() == Team.BLUE) {
					entity.visibleToOpponent = false;
					atlantisEntities_team_blue.add(entity);
				}
			}
			for(AtlantisEntity entity: tacticals_server_model.values()) {
				if (entity.getTeam() == Team.BLUE) {
					entity.visibleToOpponent = false;
					atlantisEntities_team_blue.add(entity);
				}
			}
			
			for (AtlantisEntity entity1: atlantisEntities_team_red) {
				for (AtlantisEntity entity2: atlantisEntities_team_blue) {
					if (entity1.getPosition().distance(entity2.getPosition()) <= entity2.eyesight){
						entity1.visibleToOpponent = true;
						break;
					}
				}
			}
			for (AtlantisEntity entity1: atlantisEntities_team_blue) {
				for (AtlantisEntity entity2: atlantisEntities_team_red) {
					if (entity1.getPosition().distance(entity2.getPosition()) <= entity2.eyesight){
						entity1.visibleToOpponent = true;
						break;
					}
				}
			}

			/* ---------------------------------------- */


			
			if(0 >= mothership_on_server_1.getHealth()) {
				game_over = true;
				red_wins = false;
				server.sendGameOver(currentFrame, false);
			}
			
			if(0 >= mothership_on_server_2.getHealth()) {
				game_over = true;
				red_wins = true;
				server.sendGameOver(currentFrame, true);
			}
						
			List<AtlantisEntity.Updater> updaters = 
					new ArrayList<AtlantisEntity.Updater>();
			
			synchronized(motherships_server_model) {
				for(MotherShip mothership : motherships_server_model.values()) {
					Vector position = mothership.getDestination();
					if(position != null && false == mothership.isHandlingCollision())
						mothership.moveTo(position);
					mothership.update(delta);
					updaters.add(mothership.getUpdater());
				}
			}

			synchronized(soldiers_server_model) {
				List<Soldier> remove_soldiers = new LinkedList<Soldier>();
	
				for(Soldier soldier : soldiers_server_model.values()) {
					Vector position = soldier.getDestination();
					if(position != null /*&& false == soldier.isHandlingCollision()*/)
						soldier.moveTo(position);
					soldier.update(delta);
					updaters.add(soldier.getUpdater());
					
					if(soldier.getHealth() <= 0) 
						remove_soldiers.add(soldier);
					if (!soldier.visible) {
						for (TacticalSub tactical : tacticals_server_model.values()) {
							if (tactical.soldiers.contains(soldier)) {
								remove_soldiers.add(soldier);
								break;
							}
						}
					}
				}
					
				for(Soldier remove_soldier : remove_soldiers) 
					soldiers_server_model.remove(remove_soldier);
			}
	
			synchronized(tacticals_server_model) {
				List<TacticalSub> remove_subs = new LinkedList<TacticalSub>();
				
				for(TacticalSub tactical : tacticals_server_model.values()) {
					Vector position = tactical.getDestination();
					if(position != null && false == tactical.isHandlingCollision())
						tactical.moveTo(position);
					tactical.update(delta);
					updaters.add(tactical.getUpdater());
					
					if(tactical.getHealth() <= 0)
						remove_subs.add(tactical);
				}
				
				for(TacticalSub remove_sub : remove_subs)
					tacticals_server_model.remove(remove_sub);
			}
		
			server.sendUpdaters(updaters, currentFrame);
			
			/* Process the commands sent by the clients */
			
			while(!server.incomingLockSteps.isEmpty()) {
				CommandLockStep step = server.incomingLockSteps.poll();
				if (step.frameNum < currentFrame) {
					for(Command c : step.frameCommands)
						processCommand(c);
				}
			}
		}
		
		/* Process the entity updates sent by the server above. */
		
		AtlantisClient client = playing_state.getClient();
						
 		while (client.incomingLockSteps.isEmpty()) {} 		
		while (!client.incomingLockSteps.isEmpty()) {
			ResultLockStep step = client.incomingLockSteps.poll();
			if(step.frameNum <= currentFrame) {
				for (SimulationResult result : step.frameResults) {
					if (result.type == SimulationResult.GAME_OVER) {
						game_over = true;
						red_wins = result.red_wins;
					} else {
						AtlantisEntity.Updater updater = result.entity_updater;
						if (null != updater)
							processUpdaters(updater);
					}
				}			
				if(game_over == false) break;
			}
		}
		
		/* Send commands to the server */		
		
		synchronized (commands_to_server) {
			client.sendCommands(commands_to_server, currentFrame);
			commands_to_server.clear();
		}
	}

	/* -------------------------------------------------------------------- */
	
	private Map<Long, Soldier> soldiersOnClient = new HashMap<Long, Soldier>();
	private Map<Long, MotherShip> motherShipsOnClient = new HashMap<Long, MotherShip>();
	private Map<Long, TacticalSub> tacticalsOnClient = new HashMap<Long, TacticalSub>();
	
	private void processUpdaters(AtlantisEntity.Updater updater) {
		long identity = updater.getIdentity();
		
		if(updater.getEntityClass() == Soldier.class) {			
			synchronized (soldiersOnClient) {
				Soldier updated_entity = soldiersOnClient.get(identity);

				if (null == updated_entity) {
					updated_entity = new Soldier();
				}

				updated_entity.update(updater);
				
				if (updated_entity.visibleToOpponent || updated_entity.getTeam() == playing_state.team){
					if(updated_entity.getHealth() > 0) {					
						soldiersOnClient.put(identity, updated_entity);
					} else {
						soldiersOnClient.remove(identity);
					}
				} else {
					soldiersOnClient.remove(identity);
				}
			}
		} else if (updater.getEntityClass() == MotherShip.class){		
			synchronized (motherShipsOnClient) {
				MotherShip updated_entity = motherShipsOnClient.get(identity);

				if (null == updated_entity) {
					updated_entity = new MotherShip();
				}

				updated_entity.update(updater);
				
				if (updated_entity.visibleToOpponent || updated_entity.getTeam() == playing_state.team){
					motherShipsOnClient.put(identity, updated_entity);
				} else {
					motherShipsOnClient.remove(identity);
				}
			}
		} else if (updater.getEntityClass() == TacticalSub.class){
			synchronized (tacticalsOnClient) {
				TacticalSub updated_entity = tacticalsOnClient.get(identity);

				if (null == updated_entity) {
					updated_entity = new TacticalSub();
				}

				updated_entity.update(updater);
				
				if (updated_entity.visibleToOpponent || updated_entity.getTeam() == playing_state.team){
					if(updated_entity.getHealth() > 0)
						tacticalsOnClient.put(identity, updated_entity);
					else if(updated_entity.didExplodeOnClient == true) {
						tacticalsOnClient.remove(identity);
					} 
				} else {
					tacticalsOnClient.remove(identity);
				}
			}
		} else {
			// TODO: update of other entity types
		}
	}
	
	public List<Soldier> getSoldiers() {
		List<Soldier> soldier_list = new ArrayList<Soldier>();
		
		synchronized(soldiersOnClient) { 
			 soldier_list.addAll(soldiersOnClient.values());
		}
		
		return Collections.unmodifiableList(soldier_list);
	}
	
	public Map<Long, Soldier> getIdSoldiersMapOnClient() {
		Map<Long, Soldier> id_soldier_map;
		
		synchronized(soldiersOnClient) {
			id_soldier_map = Collections.unmodifiableMap(soldiersOnClient);
		}
		
		return id_soldier_map;
	}
	
	public List<MotherShip> getMotherShips() {
		List<MotherShip> motherShip_list = new ArrayList<MotherShip>();
		
		synchronized(motherShipsOnClient) { 
			 motherShip_list.addAll(motherShipsOnClient.values());
		}
		
		return Collections.unmodifiableList(motherShip_list);
	}
	
	public Map<Long, MotherShip> getIdMotherShipsMapOnClient() {
		Map<Long, MotherShip> id_motherShip_map;
		
		synchronized(motherShipsOnClient) {
			id_motherShip_map = Collections.unmodifiableMap(motherShipsOnClient);
		}
		
		return id_motherShip_map;
	}
	
	public List<TacticalSub> getTacticals() {
		List<TacticalSub> tactical_list = new ArrayList<TacticalSub>();
		
		synchronized(tacticalsOnClient) { 
			 tactical_list.addAll(tacticalsOnClient.values());
		}
		
		return Collections.unmodifiableList(tactical_list);
	}
	
	public Map<Long, TacticalSub> getIdTacticalsMapOnClient() {
		Map<Long, TacticalSub> id_tactical_map;
		
		synchronized(tacticalsOnClient) {
			id_tactical_map = Collections.unmodifiableMap(tacticalsOnClient);
		}
		
		return id_tactical_map;
	}
	
	
	/* -------------------------------------------------------------------- */
	

	private Map<Long, Soldier> soldiers_server_model = new HashMap<Long, Soldier>();
	private Map<Long, MotherShip> motherships_server_model = new HashMap<Long, MotherShip>();
	private Map<Long, TacticalSub> tacticals_server_model = new HashMap<Long, TacticalSub>();
	
	private void processCommand(Command command) {
		Soldier soldier = soldiers_server_model.get(command.entityId);
		TacticalSub tactical = tacticals_server_model.get(command.entityId);
		MotherShip mothership = motherships_server_model.get(command.entityId);
		
		switch (command.type) {
		case Command.ATTACK:
			synchronized (soldiers_server_model) {
				Soldier targetSoldier = soldiers_server_model.get(command.attackTargetId);
				if (soldier != null && targetSoldier != null) {
					soldier.setTarget(targetSoldier);
					soldier.stopMoving();
				} else if (tactical != null && targetSoldier != null && tactical.health > 0) {
					tactical.setTarget(targetSoldier);
					tactical.stopMoving();
				}
			}
			synchronized (motherships_server_model) {
				MotherShip targetShip = motherships_server_model.get(command.attackTargetId);
				if (soldier != null && targetShip != null) {
					soldier.setTarget(targetShip);
					soldier.stopMoving();
				} else if (tactical != null && targetShip != null && tactical.health > 0) {
					tactical.setTarget(targetShip);
					tactical.stopMoving();
				}
			}
			synchronized (tacticals_server_model) {
				TacticalSub targetSub = tacticals_server_model.get(command.attackTargetId);
				if (soldier != null && targetSub != null) {
					soldier.setTarget(targetSub);
					soldier.stopMoving();
				} else if (tactical != null && targetSub != null && tactical.health > 0) {
					tactical.setTarget(targetSub);
					tactical.stopMoving();
				}
			}
			break;
		case Command.MOUNT:
			synchronized (soldiers_server_model) {
				if (soldier != null) {
					synchronized (tacticals_server_model) {
						soldier.mount(tacticals_server_model.get(command.attackTargetId));
					}
				}
			}
			break;
		case Command.UNMOUNT:
			synchronized (tacticals_server_model) {
				if (tactical != null) {
					tactical.unload();
				}
			}
			break;
		case Command.MOVEMENT:
			synchronized (soldiers_server_model) {
				boolean is_target_inside_obstacle = 
					this.playing_state.getMap()
						.isPositionVectorInsideTerrainTile(command.target);
				
				if(soldier != null && (false == is_target_inside_obstacle)) {
					soldier.setDestination(command.target);
					soldier.isAttacking = false;
				} else {
					System.out.println("Can't move there.");
					// TODO Notify the user
				}
			}
			synchronized (motherships_server_model) {
				if(mothership != null) {
					mothership.setDestination(command.target);
				}
			}
			synchronized (tacticals_server_model) {
				if(tactical != null && tactical.health > 0) {
					tactical.setDestination(command.target);
					tactical.isAttacking = false;
				}
			}
			break;
		case Command.PURCHASE:
			synchronized (soldiers_server_model) {
				if (command.entityId == 0) { // purchase soldier
					Soldier newSoldier = new Soldier(command.target.getX(),
							command.target.getY());
					newSoldier.setTeam(Team.values()[(int) command.attackTargetId]);
					soldiers_server_model.put(newSoldier.getIdentity(), newSoldier);
					for (Soldier other : soldiers_server_model.values()) { // nudge
						Collision collision = newSoldier.collides(other);
						if (null != collision) {
							if(other.getMovementDirection().equals(AtlantisEntity.STOPPED_VECTOR)) {
								Vector their_position = other.getPosition();
								double angle_to_other_soldier = newSoldier.getPosition().angleTo(their_position);
								Vector direction_to_other_soldier =
										AtlantisEntity.getVectorForAngle(angle_to_other_soldier);
								other.nudgeNudge(direction_to_other_soldier);
							}
						}
					}
				}
			}
			synchronized (tacticals_server_model) {
				if (command.entityId == 1) { // purchase tactical sub
					TacticalSub newTactical = new TacticalSub(command.target.getX(),
							command.target.getY());
					newTactical.setTeam(Team.values()[(int) command.attackTargetId]);
					tacticals_server_model.put(newTactical.getIdentity(), newTactical);
				}
			}
		}
	}
}
