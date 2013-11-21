package atlantis;

import java.util.LinkedList;
import java.util.List;

public class GameStatus {

	public GameStatus() {
		
		// TODO Auto-generated constructor stub
	
	}
	
	public boolean connected() {
		
		// TODO
		
		return true;
	}
 	
	public boolean connect(final String server) {
		System.out.println("Connecting to " + server + " ...");
		
		// TODO
		
		return true;
	}
	
	public void update(final int delta) {
		
		// TODO
		
		/*
		 * TEMPORARY FOR ISSUE 7 - Create one worker for the client to render.
		 */
		
		if(0 == workers.size()) {
			workers.add(new Worker(400, 300));
		}
	}
	
	private List<Worker> workers = new LinkedList<Worker>();
	
	public List<Worker> getWorkers() { return workers; }
}
