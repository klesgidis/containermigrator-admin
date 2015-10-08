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

	public Listener(InetSocketAddress address) {
		this.address = address;
	}

	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(0);

			System.out.println(serverSocket.getLocalPort());

			while (true) {
				Socket src = serverSocket.accept();
				System.out.println("Accepted Connection from " + src.getInetAddress().toString());

				new Thread(new Processor(src, new Socket(this.address.getAddress().toString().replace("/", ""), this.address.getPort())))
						.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
