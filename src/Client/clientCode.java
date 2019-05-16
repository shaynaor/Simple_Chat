package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import Gui.Client;

/**
 * This code is a simple client for chat. This code is based on example code
 * from anna's moudle.
 * 
 * @author Alex vaisman, shay naor
 *
 */
public class clientCode {
	/* Private data members */
	private String name;
	private String ip;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private Client client;


	public clientCode(String name, String ip, Client client) {
		this.name = name;
		this.ip = ip;
		this.client = client;
		creatConnection();
	}

	/**
	 * This method create connection between client and server.
	 */
	private void creatConnection() {
		try {
			/* establish the socket connection between the client and the server */
			this.socket = new Socket(ip, 36000);
			/* open a PrintWriter on the socket */
			out = new PrintWriter(socket.getOutputStream(), true);
			/* open a BufferedReader on the socket */
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Don't know about this host\n" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {

			System.out.println("Couldn't get I/O for " + "the connection to this host\n" + e.getMessage());
			e.printStackTrace();
		}
		new Thread(new serverThread(this)).start();

	}

	/**
	 * This method closing any streams connected to a socket before closing this
	 * socket
	 */
	public void close() {
		out.close();
		try {
			in.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Close client!");
			e.printStackTrace();
		}

	}

	/* Getters */
	public String getName() {
		return name;
	}

	public String getIp() {
		return ip;
	}

	public Socket getSocket() {
		return socket;
	}

	public PrintWriter getOut() {
		return out;
	}

	public BufferedReader getIn() {
		return in;
	}

	public Client getClient() {
		return client;
	}

}
/**
 * this class reads the messages from the server and displays them to the user
 * @author Alex vaisman, shay naor
 *
 */
class serverThread implements Runnable {

	private clientCode clientcode;

	public serverThread(clientCode clientcode) {

		this.clientcode = clientcode;
	}

	/**
	 * this function will read the message and display it to the user.
	 */
	public void run() {
		try {

			String msg;

			while ((msg = this.clientcode.getIn().readLine()) != null) {

				if (msg.contains("&%^@##$")) // Getting message from other user
				{
					int i = 0;
					String from = "";
					while (msg.charAt(i) != '&') {
						from += msg.charAt(i);
						i++;
					}
					i = i + 7;
					String data = "";
					while (i < msg.length()) {
						data += msg.charAt(i);
						i++;
					}
					this.clientcode.getClient().printOnScreen(from + ": " + data);
				}
				else if(msg.contains("Show_on_line: ")){ // Show on line users
					this.clientcode.getClient().printOnScreen(msg);

				}
			}

		} catch (IOException e) {
			System.err.println("Error " + e.getMessage());

		}

	}

}
