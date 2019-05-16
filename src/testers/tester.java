package testers;

import static org.junit.jupiter.api.Assertions.*;

import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Client.clientCode;
import Gui.Client;
import Gui.server;
import Server.serverCode;

class tester {


	private server s = new server();
	private serverCode server = new serverCode(36000,s);
	private Client c = new Client();
	private clientCode client = new clientCode("test","127.0.0.1",c);
	

	
	@Test
	void testSocket() {
	    if(this.server.getServerPort()!=36000) {
	    	fail("Not yet implemented");
	    }
	}
	
	@Test
	void testUserlist() {
	    if(this.server.getUsersList().size()!=0) {
	    	fail("Not yet implemented");
	    }
	}
	@Test
	void testClientName() {
	    if(this.client.getName()!="test") {
	    	fail("Not yet implemented");
	    }
	}
	@Test
	void testIp() {
	    if(this.client.getIp()!="127.0.0.1") {
	    	fail("Not yet implemented");
	    }
	}
	


}
