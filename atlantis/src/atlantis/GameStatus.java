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
	public Worker worker_on_server_1;
	public Worker worker_on_server_2;
	// TEMPORARY FOR DEVELOPMENT
	
	public GameStatus(PlayingState playing_state) {
		this.playing_state = playing_state;
		
		// TEMPORARY FOR DEVELOPMENT
		worker_on_server_1 = 
				new Worker(350, 300, new Vector(0, 0));
		workers_server_model.put(worker_on_server_1.getIdentity(),
				worker_on_server_1);
		worker_on_server_2 = 
				new Worker(450, 300, new Vector(0, 0));
		workers_server_model.put(worker_on_server_2.getIdentity(),
				worker_on_server_2);
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
			
			synchronized(workers_server_model) {
				for(Worker worker : workers_server_model.values()) {
					if(worker.getDestination() != null)
						worker.moveTo(worker.getDestination());
					worker.update(delta);
					updaters.add(worker.getUpdater());
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
	
	private Map<Long, Worker> workers = new HashMap<Long, Worker>();
	
	private void processUpdater(AtlantisEntity.Updater updater) {
		if(updater.getEntityClass() == Worker.class) {			
			synchronized (workers) {
				Worker updated_entity = workers.get(updater.getIdentity());

				if (null == updated_entity) {
					updated_entity = new Worker();
				}

				updated_entity.update(updater);

				workers.put(new Long(updater.getIdentity()),
						(Worker) updated_entity);
			}
		} else {		
			// TODO: update of other entity types
		}
	}
	
	public List<Worker> getWorkers() {
		List<Worker> worker_list = new ArrayList<Worker>();
		
		synchronized(workers) { 
			 worker_list.addAll(workers.values());
		}
		
		return Collections.unmodifiableList(worker_list);
	}
	
	public Map<Long, Worker> getIdWorkersMap() {
		Map<Long, Worker> id_worker_map;
		
		synchronized(workers) {
			id_worker_map = Collections.unmodifiableMap(workers);
		}
		
		return id_worker_map;
	}
	
	/* -------------------------------------------------------------------- */
	
	private Map<Long, Worker> workers_server_model = new HashMap<Long, Worker>();
	
	private void processCommand(Command command) {
		switch (command.type) {
		case Command.MOVEMENT:
			synchronized (workers_server_model) {
				Worker worker = workers_server_model.get(command.entityId);
				System.out.println(worker + " MOVE TO " + command.target);
				worker.setDestination(command.target);
			}
			break;
		}
	}
}
