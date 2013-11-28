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
	private Worker worker_on_server_1 = 
			new Worker(350, 300, new Vector(1, 0));
	private Vector worker_on_server_1_dest = new Vector(100,100);
	private Worker worker_on_server_2 = 
			new Worker(450, 300, new Vector(1, 0));
	private Vector worker_on_server_2_dest = new Vector(700,500);
	
	// TEMPORARY FOR DEVELOPMENT
	
	public void update(GameContainer container, int delta) {
		int currentFrame = playing_state.getCurrentFrame();
		AtlantisServer server = playing_state.getServer();
		
		if (null != server) {

			// TEMPORARY FOR WORKING OUT MOVEMENT OF WORKER AND CODE FOR
			// SYNCHRONIZATION/MOVEMENT
			
			worker_on_server_1.setTeam(AtlantisEntity.Team.RED);
			worker_on_server_2.setTeam(AtlantisEntity.Team.BLUE);

			// System.out.println("delta: "+delta);
			
			while(false == worker_on_server_1.isHandlingCollision() && 
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
			}
			
			worker_on_server_1.update(delta);
			worker_on_server_2.update(delta);

			List<AtlantisEntity.Updater> updaters = 
					new ArrayList<AtlantisEntity.Updater>();
			
			updaters.add(worker_on_server_1.getUpdater());
			updaters.add(worker_on_server_2.getUpdater());
						
			server.sendUpdates(updaters, playing_state.getCurrentFrame());
			
			// END TEMPORARY SECTION
		}
		
		/* Process the updates sent by the server above. */
		
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
	}
	
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
	
	Map<Long, Worker> workers = new HashMap<Long, Worker>();
	
	public List<Worker> getWorkers() {
		List<Worker> worker_list = new ArrayList<Worker>();
		
		synchronized(workers) { 
			 worker_list.addAll(workers.values());
		}
		
		return Collections.unmodifiableList(worker_list);
	}
}
