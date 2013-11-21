package atlantis.networking;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

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
	
	public BlockingQueue<Command> commands;
	
	public AtlantisServer(int portNumber) {
		PORT_NUMBER = portNumber;
		socketList = new ArrayList<Socket>();
	}
	
	public void run() {
		try{
			ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
			try{
				while(PlayingState.currentNumberOfPlayers < PlayingState.NUMBER_OF_PLAYERS) {
					System.out.println("waiting for client");
					Socket clientSocket = serverSocket.accept();
					socketList.add(clientSocket);
					System.out.println("connected!");
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
		System.out.println("create a listener!");
	}
	
	public void tellClient(GameContainer container) {
//		try {
//			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
//			
//		
//			Input input = container.getInput();
//			
//			if(input.isKeyDown(Input.KEY_ENTER)) {
//				out.println("press enter");
//			}
//			String fromServer;
//			fromServer = stdIn.readLine();
//			if(fromServer != null) {
//				System.out.println("Server: "+ fromServer);
//				out.println(fromServer);
//			}
//		} catch (IOException e){
//			
//		}
	}
	
	public void sendMap(String mapName) {
		SimulationResult result = new SimulationResult();
		result.setMap(mapName);
		try {
			for(Socket clientSocket: socketList) {
				OutputStream out = clientSocket.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out);
				oos.writeObject(result);
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

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					command = inputLine;
				}
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
						+ PORT_NUMBER + " or listening for a connection");
				System.out.println(e.getMessage());
			}
		}
	}
}
