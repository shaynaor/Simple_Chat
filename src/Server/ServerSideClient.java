package Server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class represent storage of clients.
 * 
 * @author shaynaor and alex vaisman.
 *
 */
public class ServerSideClient {
	/* Private data members */
	private String name;
	private Socket socket;

	/**
	 * Constructor.
	 * 
	 * @param name - client name.
	 * @param sok  - soket.
	 */
	public ServerSideClient(String name, Socket sok) {
		this.name = name;
		this.socket = sok;
	}
	/* Getters */
	public String getName() {
		return name;
	}

	public Socket getServerSocket() {
		return socket;
	}
}
