package gr.uoa.di.containermigrator.master.global;

import gr.uoa.di.containermigrator.master.forwarding.Listener;
import gr.uoa.di.containermigrator.master.forwarding.StateMonitor;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class MigrationInfo {

	private String host;

	private String container;

	private Listener listener;

	private StateMonitor monitor;

	public MigrationInfo(String host, String container, StateMonitor monitor) {
		this.host = host;
		this.container = container;
		this.monitor = monitor;
	}

	public void startListener() {
		this.listener.start();
	}

	public void updateMigrationInfo(String host, String container, int port) {
		System.out.println("Restart Listener");
		this.host = host;
		this.container = container;
		String address = Global.getProperties().getWorkers().get(this.host).getClientEndpoint().getAddress();

		this.listener.close();
		int listenPort = this.listener.getListenPort();

		this.listener = new Listener(new InetSocketAddress(address, port), listenPort, this.monitor);
		this.listener.start();
		System.out.println("DONE");
		// TODO update structures and keys
	}

	public void setListener(InetSocketAddress address, int listenPort) {
		this.listener = new Listener(address, listenPort, this.monitor);
	}

	public StateMonitor getMonitor() {
		return monitor;
	}
}
