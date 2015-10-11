package gr.uoa.di.containermigrator.master.forwarding;

import gr.uoa.di.containermigrator.master.global.Global;
import gr.uoa.di.containermigrator.master.global.Preferences;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class Listener extends Thread implements Preferences {
	private final InetSocketAddress address;
	private final int listenPort;
	private final StateMonitor monitor;

	private ServerSocket serverSocket;

	public Listener(InetSocketAddress address, int listenPort, StateMonitor monitor) {
		this.address = address;
		this.listenPort = listenPort;
		this.monitor = monitor;
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(this.listenPort);

			System.out.println(serverSocket.getLocalPort());

			while (true) {
				Socket src = this.serverSocket.accept();

				new Processor(
					src,
					new Socket(this.address.getAddress().toString().replace("/", ""), this.address.getPort()),
					this.monitor).start();
			}
		}
		catch (SocketException e) {
			System.out.println("SocketException");
		}
		catch (InterruptedIOException e) {
			System.out.println("InterruptedIOException");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public int getListenPort() {
		return listenPort;
	}
}
