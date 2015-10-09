package gr.uoa.di.containermigrator.master.forwarding;

import gr.uoa.di.containermigrator.master.global.Preferences;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class Listener implements Runnable, Preferences {
	private final InetSocketAddress address;
	private final String monitorKey;

	public Listener(InetSocketAddress address, String monitorKey) {
		this.address = address;
		this.monitorKey = monitorKey;
	}

	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(0);

			System.out.println(serverSocket.getLocalPort());

			while (true) {
				Socket src = serverSocket.accept();

				new Thread(new Processor(
						src,
						new Socket(this.address.getAddress().toString().replace("/", ""), this.address.getPort()),
						this.monitorKey)
				).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
