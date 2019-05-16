package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import Gui.server;

/**
 * This code is a simple multiple thread server for chat. This code is based on
 * example code from anna's moudle.
 * 
 * @author Alex vaisman, shay naor
 *
 */

public class serverCode implements Runnable {

	private int serverPort = 36000;
	private ServerSocket serverSocket = null;
	private Thread runningThread = null;
	private boolean isStopped = false;
	private ArrayList<ServerSideClient> usersList;
	private server serv;

	
	public serverCode(int port,server serv) {
		this.serverPort = port;
		this.usersList = new ArrayList<ServerSideClient>();
		this.serv = serv;
	}

	public void run() {
		synchronized (this) {
			this.runningThread = Thread.currentThread();
		}
		// opening server socket
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			System.err.println("Cannot use this port");
			e.printStackTrace();
			System.exit(1);
		}

		while (!isStopped()) {
			// socket to accept client
			Socket clientSocket = null;

			// accepting client
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				if (isStopped()) {
					System.err.println("Server stopped");
					return;
				}
				throw new RuntimeException("Error accepting client");

			}
			// client info
			InetAddress addr = clientSocket.getInetAddress();
			System.out.println("Server: Received a new connection from (" + addr.getHostAddress() + "): "
					+ addr.getHostName() + " on port: " + clientSocket.getPort());
			String clientInfo = "";
			clientInfo = "Client on port " + clientSocket.getPort();

			new Thread(new clientThread(this, clientSocket, clientInfo)).start();
		}
		System.out.println("Server stopped");

	}

	/* getters/ setters */
	public int getServerPort() {
		return serverPort;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public Thread getRunningThread() {
		return runningThread;
	}

	public synchronized boolean isStopped() {
		return isStopped;
	}
	public server getServer() {
		return this.serv;
	}
    /**
     * Stoping the server, closing socket
     */
	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}

	
	public synchronized ArrayList<ServerSideClient> getUsersList() {
		return usersList;
	}
}

/**
 * This class is a thread that communicates with one client ,
 * it will preform  the tasks the client asks.
 * @author Alex vaisman , shay naor
 *
 */
class clientThread implements Runnable {
	private Socket clientSocket;
	private String serverText;
	private serverCode serverCode;

	public clientThread(serverCode sc, Socket clientSocket, String serverText) {
		this.clientSocket = clientSocket;
		this.serverText = serverText;
		this.serverCode = sc;
	}
    /**
     * this function reades the messages from the client and preforms the task that was asked.
     */
	public void run() {
		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String msg;
			
			
			//reading the message  from the client and acting depending on what was asked.
			while ((msg = in.readLine()) != null) {
         
				if (msg.contains("~!@#$%^&*")) { // new user joined server
					String name = "";
					int i = 0;
					while (msg.charAt(i) != '~' && msg.charAt(i + 1) != '!' && msg.charAt(i + 2) != '@') {
						name += msg.charAt(i);
						i++;
					}
					ServerSideClient serv = new ServerSideClient(name, this.getClientSocket());
					/* If this Server client doesn't exist in the container add him. */
					if (!this.serverCode.getUsersList().contains(serv)) {
						this.serverCode.getUsersList().add(serv);
						PrintWriter toDstOut = null;
						// sending message to all that a new user joined
						for (i = 0; i < this.serverCode.getUsersList().size(); i++) {
							toDstOut = new PrintWriter(
									this.serverCode.getUsersList().get(i).getServerSocket().getOutputStream(),
									true);
							toDstOut.println("Server" + "&%^@##$" + name+" Has joined the server!");
						}
						this.serverCode.getServer().printToScreen(name+" Connected to the server");
					}
				} else if (msg.contains("<Disconnect>")) {// user disconnected
			
					String name = "";
					int i = 13;
					while (i < msg.length() - 1) {
						name += msg.charAt(i);
						i++;
					}
					
					// sending message to all user that a user left the server
					PrintWriter toDstOut = null;
					for (i = 0; i < this.serverCode.getUsersList().size(); i++) {
						toDstOut = new PrintWriter(
								this.serverCode.getUsersList().get(i).getServerSocket().getOutputStream(),
								true);
						toDstOut.println("Server" + "&%^@##$" + name+" Has left the server!");
					}
					
					for (i = 0; i < this.serverCode.getUsersList().size(); i++) {
						if (this.serverCode.getUsersList().get(i).getName().equals(name)) {
							this.serverCode.getUsersList().remove(i);
						}
					}
					this.serverCode.getServer().printToScreen(name+" Diconnected from the server");

					break;
				} else if (msg.equals("Show_on_line: ")) { // show all online users
					String ans = msg +"[";
					int i = 0;
					for ( i = 0; i < this.serverCode.getUsersList().size()-1; i++) {
						ans += this.serverCode.getUsersList().get(i).getName() + ", ";

					}
					ans+=this.serverCode.getUsersList().get(i).getName()+"]";
					
					out.println(ans);
				}

				else if (msg.contains("&%^@##$")) {// message to user
					int i = 0;
					String to = "";
					while (msg.charAt(i) != '>') {
						to += msg.charAt(i);
						i++;
					}
					i++;
					String data = "";
					while (msg.charAt(i) != '&' && msg.charAt(i + 1) != '%' && msg.charAt(i + 2) != '^') {
						data += msg.charAt(i);
						i++;
					}
					i = i + 7;
					String from = "";
					while (i < msg.length()) {
						from += msg.charAt(i);
						i++;
					}

					PrintWriter toDstOut = null;

					if (!to.equals("all")) {    // sending message to one user
						for (i = 0; i < this.serverCode.getUsersList().size(); i++) {
							if (this.serverCode.getUsersList().get(i).getName().equals(to)) {
								toDstOut = new PrintWriter(
										this.serverCode.getUsersList().get(i).getServerSocket().getOutputStream(),
										true);
								toDstOut.println(from + "&%^@##$" + data);

							}
						}
					}
					else if(to.equals("all")) {  // sending message to all users
						for (i = 0; i < this.serverCode.getUsersList().size(); i++) {
							toDstOut = new PrintWriter(
									this.serverCode.getUsersList().get(i).getServerSocket().getOutputStream(),
									true);
							toDstOut.println(from + "&%^@##$" + data);
						}
					}

					System.out.println("send messeg back " + "to: " + to + "  data:  " + data);
					String ans = "<" + to + ">" + data;
					out.println(ans);

				}

				else {
					out.println(msg);
				}
			}

			System.out.println(serverText + " leaved.");
			out.close();
			in.close();
			clientSocket.close();

		} catch (IOException e) {
			System.err.println("Error " + e.getMessage());
		}
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public String getServerText() {
		return serverText;
	}

}
