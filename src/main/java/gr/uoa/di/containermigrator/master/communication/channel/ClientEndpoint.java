package gr.uoa.di.containermigrator.master.communication.channel;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class ClientEndpoint implements AutoCloseable {
	private String address;
	private int port;

	private Socket socket = null;

	public ClientEndpoint(String address, int port) {
		this.address = address;
		this.port = port;
	}


	public Socket getSocket() throws IOException {
		if (socket == null)
			socket = new Socket(address, port);
		return socket;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void close() throws Exception {
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}
}
