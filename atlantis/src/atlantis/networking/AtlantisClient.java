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
import atlantis.PlayingState;
import atlantis.Soldier;
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
	
	public boolean connect(String address) {
		try{
			socket = new Socket(address, PORT_NUMBER);
			createListener();
		}catch ( IOException e ) {
			System.out.println( "No socket! " + e.toString());
		}		
		return true;
	}
	
	public void createListener() {
		serverListener sl = new serverListener(socket);
		sl.start();
	}
	
	public void sendCommand(CommandLockStep step) {
		try {
		
			OutputStream out = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(step);
			
		} catch (IOException e){
			
		}
	}
	
	public void sendCommands(List<Command> commands, int frameNum) {
		CommandLockStep step = new CommandLockStep(frameNum);
		
		for(Command c : commands)
			step.addCommand(c);
		
		sendCommand(step);
	}
	
	public class serverListener extends Thread{
		
		serverListener(Socket socket) {
		}
		
		public void run() {
			try {
				while (true) {
					InputStream in = socket.getInputStream();
					ObjectInputStream ois = new ObjectInputStream(in);
					ResultLockStep result = (ResultLockStep) ois.readObject();
					incomingLockSteps.add(result);
					// ois.close();
					// in.close();
				}	
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
						+ PORT_NUMBER + " or listening for a connection");
				System.out.println(e.getMessage());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("SimulationResult class not found");
				e.printStackTrace();
			}
		}
	}
}
