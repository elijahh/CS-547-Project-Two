package atlantis.networking;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

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
	
	public BlockingQueue<SimulationResult> resultsQueue;
	
	public AtlantisClient(int portNumber) {
		PORT_NUMBER = portNumber;
		resultsQueue = new LinkedBlockingQueue<SimulationResult>();
	}
	
	public void connect() {
		try{
			socket = new Socket("localhost", PORT_NUMBER);
			createListener();
		}catch ( IOException e ) {
			System.out.println( "No socket! " + e.toString());
		} 
	}
	
	public void createListener() {
		serverListener sl = new serverListener(socket);
		sl.start();
	}
	
	public void tellServer(GameContainer container) {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			
//			String fromClient;
//			fromClient = stdIn.readLine();
//			if(fromClient != null) {
//				System.out.println("Client: "+ fromClient);
//				out.println(fromClient);
//			}
			
			Input input = container.getInput();
			if(input.isKeyDown(Input.KEY_SPACE)) {
				out.println("press space");
			}
			
		} catch (IOException e){
			
		}
	}
	
	public class serverListener extends Thread{
		
		serverListener(Socket socket) {

		}
		
		public void run() {
			try {

//				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				String inputLine;
//				while ((inputLine = in.readLine()) != null) {
//					result = inputLine;
//				}
				
				InputStream in = socket.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(in);
				SimulationResult result = (SimulationResult) ois.readObject();
				resultsQueue.add(result);
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
