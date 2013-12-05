package atlantis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	
	// TEMPORARY FOR DEVELOPMENT
	public Soldier soldier_on_server_1;
	public Soldier soldier_on_server_2;
	public MotherShip mothership_on_server_1;
	public MotherShip mothership_on_server_2;
	// TEMPORARY FOR DEVELOPMENT
	
	public GameStatus(PlayingState playing_state) {
		this.playing_state = playing_state;
		
		// TEMPORARY FOR DEVELOPMENT
		soldier_on_server_1 = 
				new Soldier(350, 300, new Vector(0, 0));
		soldiers_server_model.put(soldier_on_server_1.getIdentity(),
				soldier_on_server_1);
		soldier_on_server_2 = 
				new Soldier(450, 300, new Vector(0, 0));
		soldier_on_server_2.setTeam(Team.BLUE);
		soldiers_server_model.put(soldier_on_server_2.getIdentity(),
				soldier_on_server_2);
		
		mothership_on_server_1 = new MotherShip(200, 200, new Vector(0,0));
		motherships_server_model.put(mothership_on_server_1.getIdentity(), mothership_on_server_1);
		
		mothership_on_server_2 = new MotherShip(400, 200, new Vector(0,0));
		mothership_on_server_2.setTeam(Team.BLUE);
		motherships_server_model.put(mothership_on_server_2.getIdentity(), mothership_on_server_2);
		// TEMPORARY FOR DEVELOPMENT
	}

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
			List<AtlantisEntity.Updater> updaters = 
					new ArrayList<AtlantisEntity.Updater>();

			synchronized(soldiers_server_model) {
				for(Soldier soldier : soldiers_server_model.values()) {
					Vector position = soldier.getDestination();
					if(position != null && false == soldier.isHandlingCollision())
						soldier.moveTo(position);
					soldier.update(delta);
					updaters.add(soldier.getUpdater());
				}
			}
			
			synchronized(motherships_server_model) {
				for(MotherShip mothership : motherships_server_model.values()) {
					Vector position = mothership.getDestination();
					if(position != null && false == mothership.isHandlingCollision())
						mothership.moveTo(position);
					mothership.update(delta);
					updaters.add(mothership.getUpdater());
				}
			}
				
			server.sendUpdates(updaters, playing_state.getCurrentFrame());
			
			/* Process the commands sent by the clients */
			
			while(!server.incomingLockSteps.isEmpty()) {
				CommandLockStep step = server.incomingLockSteps.poll();
				if (step.frameNum < playing_state.getCurrentFrame()) {
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
			if(step.frameNum == currentFrame) {
				for (SimulationResult result : step.frameResults) {
					AtlantisEntity.Updater updater = result.entity_updater;
					if (null != updater)
						processUpdater(updater);
				}
				
				break;
			}
		}
		
		/* Send commands to the server */		
		
		synchronized (commands_to_server) {
			client.sendCommands(commands_to_server,
					playing_state.getCurrentFrame());
			commands_to_server.clear();
		}
	}

	/* -------------------------------------------------------------------- */
	
	private Map<Long, Soldier> soldiersOnClient = new HashMap<Long, Soldier>();
	private Map<Long, MotherShip> motherShipsOnClient = new HashMap<Long, MotherShip>();
	
	private void processUpdater(AtlantisEntity.Updater updater) {
		if(updater.getEntityClass() == Soldier.class) {			
			synchronized (soldiersOnClient) {
				Soldier updated_entity = soldiersOnClient.get(updater.getIdentity());

				if (null == updated_entity) {
					updated_entity = new Soldier();
				}

				updated_entity.update(updater);

				soldiersOnClient.put(new Long(updater.getIdentity()),
						(Soldier) updated_entity);
			}
		} else if (updater.getEntityClass() == MotherShip.class){		
			synchronized (motherShipsOnClient) {
				MotherShip updated_entity = motherShipsOnClient.get(updater.getIdentity());

				if (null == updated_entity) {
					updated_entity = new MotherShip();
				}

				updated_entity.update(updater);

				motherShipsOnClient.put(new Long(updater.getIdentity()),
						(MotherShip) updated_entity);
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
	
	/* -------------------------------------------------------------------- */
	

	private Map<Long, Soldier> soldiers_server_model = new HashMap<Long, Soldier>();
	private Map<Long, MotherShip> motherships_server_model = new HashMap<Long, MotherShip>();
	private Map<Long, Torpedo> torpedoes_server_model = new HashMap<Long, Torpedo>();
	
	private void processCommand(Command command) {
		switch (command.type) {
		case Command.ATTACK:
			synchronized (soldiers_server_model) {
				Soldier soldier = soldiers_server_model.get(command.entityId);
				Soldier targetSoldier = soldiers_server_model.get(command.targetEntityId);
				soldier.setTarget(targetSoldier);
			}
		case Command.MOVEMENT:
			synchronized (soldiers_server_model) {
				Soldier soldier = soldiers_server_model.get(command.entityId);
				if(soldier != null) {
					soldier.setDestination(command.target);
				}
			}
			synchronized (motherships_server_model) {
				MotherShip mothership = motherships_server_model.get(command.entityId);
				if(mothership != null) {
					mothership.setDestination(command.target);
				}
			}
			break;
		}
	}
}
