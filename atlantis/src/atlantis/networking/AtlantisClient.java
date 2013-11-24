package atlantis.networking;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import atlantis.AtlantisEntity;
import atlantis.Worker;
import atlantis.StartMenuState;
import atlantis.networking.AtlantisServer.ClientListener;

/*
 * Implement LockStepProtocol. Send commands to server. Receive simulation results from server.
 * 
 */

public class AtlantisClient {

	public DataOutputStream socket_os;
	public DataInputStream socket_is;
	public Socket socket;
	public String result;
	public static int PORT_NUMBER;
	
	public BlockingQueue<ResultLockStep> incomingLockSteps;
	
	public AtlantisClient(int portNumber) {
		PORT_NUMBER = portNumber;
		incomingLockSteps = new LinkedBlockingQueue<ResultLockStep>();
	}
	
	public void connect(String address) {
		try{
			socket = new Socket(address, PORT_NUMBER);
			createListener();
		}catch ( IOException e ) {
			System.out.println( "No socket! " + e.toString());
		} 
	}
	
	public void createListener() {
		serverListener sl = new serverListener(socket);
		sl.start();
	}
	
	public void sendCommand(GameContainer container, int frameNum, CommandLockStep step) {
		try {
		
			OutputStream out = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(step);
			
		} catch (IOException e){
			
		}
	}
	
	public class serverListener extends Thread{
		
		serverListener(Socket socket) {
		}
		
		public void run() {
			try {
				
				InputStream in = socket.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(in);
				ResultLockStep result = (ResultLockStep) ois.readObject();
				incomingLockSteps.add(result);
				ois.close();
				in.close();
				
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
						+ AtlantisServer.PORT_NUMBER + " or listening for a connection");
				System.out.println(e.getMessage());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("SimulationResult class not found");
				e.printStackTrace();
			}
		}
	}
	
	/* -------------------------------------------------------------------- */
	
	public void processUpdateEntity(AtlantisEntity update_entity) {
		if (update_entity instanceof Worker) {

			Worker updated_entity = workers.get(update_entity.getIdentity());

			if (null == updated_entity)
				updated_entity = new Worker(update_entity.getX(),
						update_entity.getY());

			updated_entity.update(update_entity);

			workers.put(new Long(updated_entity.getIdentity()),
					(Worker) updated_entity);
		}

		// TODO: update of other entity types
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
