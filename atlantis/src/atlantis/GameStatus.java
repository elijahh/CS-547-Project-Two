package atlantis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStatus {
	
	public void processUpdateEntity(AtlantisEntity.Updater updater) {
		if(updater.getEntityClass() == Worker.class) {			
			synchronized (workers) {
				Worker updated_entity = workers.get(updater.getIdentity());

				if (null == updated_entity)
					updated_entity = new Worker();

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
