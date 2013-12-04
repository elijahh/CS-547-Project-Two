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
	
	/* -------------------------------------------------------------------- */
	
	private Map<Long, Soldier> soldiers_server_model = new HashMap<Long, Soldier>();
	
	private void processCommand(Command command) {
		switch (command.type) {
		case Command.MOVEMENT:
			synchronized (soldiers_server_model) {
				Soldier soldier = soldiers_server_model.get(command.entityId);
				soldier.setDestination(command.target);
			}
			break;
		}
	}
}
