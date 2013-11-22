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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.tiled.TiledMap;

import atlantis.PlayingState;

/*
 * Implement LockStepProtocol. Receive commands from clients. Send results to clients.
 */

public class AtlantisServer extends Thread{
	
	public static int PORT_NUMBER;
	public DataOutputStream socket_os;
	public DataInputStream socket_is;

	public ArrayList<Socket> socketList;
	public String command;
	
	public BlockingQueue<CommandLockStep> incomingLockSteps;

	
	public AtlantisServer(int portNumber) {
		PORT_NUMBER = portNumber;
		socketList = new ArrayList<Socket>();
		incomingLockSteps = new LinkedBlockingQueue<CommandLockStep>();
	}
	
	public void run() {
		try{
			ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
			try{
				while(PlayingState.currentNumberOfPlayers < PlayingState.NUMBER_OF_PLAYERS) {
					System.out.println("Waiting for client...");
					Socket clientSocket = serverSocket.accept();
					socketList.add(clientSocket);
					System.out.println("Connected!");
					createListener(clientSocket);
					PlayingState.currentNumberOfPlayers++;
				}
			}catch (IOException e  ) {
				System.out.println( "client error " + e.toString() );
			}
		}catch ( IOException e ) {
			System.out.println( "No socket!" + e.toString());
		}
	}
	
	public void createListener(Socket clientSocket) {
		ClientListener cl = new ClientListener(clientSocket);
		cl.start();
		System.out.println("Create a listener!");
	}
	
	public void sendResult(GameContainer container, int frameNum, ResultLockStep step) {
		try {
			for(Socket socket: socketList) {
				OutputStream out = socket.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out);
				oos.writeObject(step);
			}
		} catch (IOException e){
			
		}
	}
	
	public void sendMap(String mapName, int frameNum) {
		SimulationResult result = new SimulationResult();
		result.setMap(mapName);
		ResultLockStep step = new ResultLockStep(frameNum);
		step.addResult(result);
		try {
			for(Socket clientSocket: socketList) {
				OutputStream out = clientSocket.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out);
				oos.writeObject(step);
				oos.close();
				out.close();
			}
		} catch (IOException e){
			
		}
	}
	
	public class ClientListener extends Thread{
		
		Socket socket;
		public ClientListener(Socket clientSocket) {
			socket = clientSocket;
		}
		
		public void run() {
			try {
				
				InputStream in = socket.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(in);
				CommandLockStep commands = (CommandLockStep) ois.readObject();
				incomingLockSteps.add(commands);
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
	
}
