package atlantis.networking;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Implement LockStepProtocol. Receive commands from clients. Send results to clients.
 */

public class AtlantisServer {
	
	public static int PORT_NUMBER;
	public DataOutputStream socket_os;
	public DataInputStream socket_is;
	public Socket clientSocket;
	public String command;
	
	public AtlantisServer(int portNumber) {
		PORT_NUMBER = portNumber;
	}
	
	public void connect() {
		try{
			ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
			try{				
				System.out.println("waiting for client");
				clientSocket = serverSocket.accept();
				socket_os = (DataOutputStream) clientSocket.getOutputStream();
				socket_is = (DataInputStream) clientSocket.getInputStream();
			}catch (IOException e  ) {
				System.out.println( "client error " + e.toString() );
			}
		}catch ( IOException e ) {
			System.out.println( "No socket!" + e.toString());
		} 
	}
	
	public void createListener() {
		ClientListener cl = new ClientListener(clientSocket);
		cl.start();
	}
	
	public void tellClient() {
		try {
			PrintWriter out = new PrintWriter(socket_os, true);
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			
			String fromServer;
			fromServer = stdIn.readLine();
			if(fromServer != null) {
				System.out.println("Server: "+ fromServer);
				out.println(fromServer);
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
