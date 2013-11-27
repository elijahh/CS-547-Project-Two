package atlantis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jig.Vector;

import org.newdawn.slick.GameContainer;

import atlantis.AtlantisEntity;

import atlantis.networking.AtlantisClient;
import atlantis.networking.AtlantisServer;
import atlantis.networking.ResultLockStep;
import atlantis.networking.SimulationResult;

public class GameStatus {
	
	private PlayingState playing_state;
	
	public GameStatus(PlayingState playing_state) {
		this.playing_state = playing_state;
	}
	
	// TEMPORARY FOR DEVELOPMENT

	private Worker worker_on_server_1 = 
			new Worker(350, 300, new Vector(1, 0));
	private Worker worker_on_server_2 = 
			new Worker(450, 300, new Vector(1, 0));
	private int worker_clock;
	// TEMPORARY FOR DEVELOPMENT
	
	public void update(GameContainer container, int delta) {
		int currentFrame = playing_state.getCurrentFrame();
		AtlantisServer server = playing_state.getServer();
		
		if (null != server) {

			// TEMPORARY FOR WORKING OUT MOVEMENT OF WORKER AND CODE FOR
			// SYNCHRONIZATION
			
			worker_clock += delta;

			if (worker_clock > 250) {
				worker_clock = 0;

//				Vector move_dir = worker_on_server.getMovementDirection();
//				move_dir = new Vector(move_dir.negate());
//				worker_on_server.beginMovement(move_dir);
				
				Vector[] directions = {new Vector(0, 1),
						new Vector(0, -1),
						new Vector(1, 0),
						new Vector(-1, 0)
				};

				worker_on_server_1.beginMovement(directions[(int) (Math.random() * 4 % 4)]);
				worker_on_server_2.beginMovement(directions[(int) (Math.random() * 4 % 4)]);
			}

			System.out.println("delta: "+delta);

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
