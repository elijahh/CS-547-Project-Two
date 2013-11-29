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
	
	public GameStatus(PlayingState playing_state) {
		this.playing_state = playing_state;
	}
	
	// TEMPORARY FOR DEVELOPMENT
	public Worker worker_on_server_1 = 
			new Worker(350, 300, new Vector(0, 0));
	//public Vector worker_on_server_1_dest = new Vector(100,100);
	public Worker worker_on_server_2 = 
			new Worker(450, 300, new Vector(0, 0));
	//private Vector worker_on_server_2_dest = new Vector(700,500);
	// TEMPORARY FOR DEVELOPMENT
	
	public void update(GameContainer container, int delta) {
		int currentFrame = playing_state.getCurrentFrame();
		AtlantisServer server = playing_state.getServer();
		
		if (null != server) {

			// TEMPORARY FOR WORKING OUT MOVEMENT OF WORKER AND CODE FOR
			// SYNCHRONIZATION/MOVEMENT

			//System.out.println("delta: "+delta);
			
			/*while(false == worker_on_server_1.isHandlingCollision() && 
					false == worker_on_server_1.moveTo(worker_on_server_1_dest)) {
				worker_on_server_1_dest = new Vector(
						random_generator.nextInt(AtlantisGame.DISPLAY_SIZE_X),
						random_generator.nextInt(AtlantisGame.DISPLAY_SIZE_Y));
				System.out.println("Worker 1 moving to " + worker_on_server_1_dest);
			}
				
			while(false == worker_on_server_2.isHandlingCollision() &&
					false == worker_on_server_2.moveTo(worker_on_server_2_dest)) {
				worker_on_server_2_dest = new Vector(
						random_generator.nextInt(AtlantisGame.DISPLAY_SIZE_X),
						random_generator.nextInt(AtlantisGame.DISPLAY_SIZE_Y));
				System.out.println("Worker 2 moving to " + worker_on_server_2_dest);
			}*/
			
			synchronized (workers) {
				List<AtlantisEntity.Updater> updaters = 
						new ArrayList<AtlantisEntity.Updater>();
				
				if (workers.isEmpty()) {
					worker_on_server_1.setTeam(AtlantisEntity.Team.RED);
					worker_on_server_2.setTeam(AtlantisEntity.Team.BLUE);
					worker_on_server_1.update(delta);
					worker_on_server_2.update(delta);
					updaters.add(worker_on_server_1.getUpdater());
					updaters.add(worker_on_server_2.getUpdater());
				} else {
					for (Worker worker : workers.values()) {
						if (worker.getDestination() != null)
							worker.moveTo(worker.getDestination());
						worker.update(delta);
						updaters.add(worker.getUpdater());
					}
				}
				
				server.sendUpdates(updaters, playing_state.getCurrentFrame());
			}
			
			/* Process the commands sent by the clients */
			
//			while(!server.incomingLockSteps.isEmpty()) {
//				CommandLockStep step = server.incomingLockSteps.poll();
//				System.out.println(currentFrame + " "+ step.frameNum);
//			}
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
			}
			break;
		}
		
		/* Send commands to the server */		
		
		client.sendCommands(commands_to_server, playing_state.getCurrentFrame());
		commands_to_server.clear();
	}

	/* -------------------------------------------------------------------- */
	
	private Map<Long, Worker> workers = new HashMap<Long, Worker>();
	private List<Command> commands_to_server = new ArrayList<Command>();
	
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
	
	private void processCommand(Command command) {
		
	}
}
