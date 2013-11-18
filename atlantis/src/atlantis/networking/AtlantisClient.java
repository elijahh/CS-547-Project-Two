package atlantis.networking;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import atlantis.networking.AtlantisServer.ClientListener;

/*
 * Implement LockStepProtocol. Send commands to server. Receive simulation results from server.
 * 
 */

public class AtlantisClient {

	public DataOutputStream socket_os;
	public DataInputStream socket_is;
	public Socket socket;
	public String command;
	
	public AtlantisClient() {
		
	}
	
	public void connect() {
		try{
			Socket socket = new Socket("localhost", AtlantisServer.PORT_NUMBER);
			try{				
				socket_os = (DataOutputStream) socket.getOutputStream();
				socket_is = (DataInputStream) socket.getInputStream();
			}catch (IOException e  ) {
				System.out.println( "client error " + e.toString() );
			}
		}catch ( IOException e ) {
			System.out.println( "No socket!" + e.toString());
		} 
	}
	
	public void createListener() {
		serverListener sl = new serverListener(socket);
		sl.start();
	}
	
	public void tellServer() {
		try {
			PrintWriter out = new PrintWriter(socket_os, true);
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			
			String fromClient;
			fromClient = stdIn.readLine();
			if(fromClient != null) {
				System.out.println("Client: "+ fromClient);
				out.println(fromClient);
			}
		} catch (IOException e){
			
		}
	}
	
	public class serverListener extends Thread{
		
		serverListener(Socket socket) {

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
						+ AtlantisServer.PORT_NUMBER + " or listening for a connection");
				System.out.println(e.getMessage());
			}
		}
	}
}
