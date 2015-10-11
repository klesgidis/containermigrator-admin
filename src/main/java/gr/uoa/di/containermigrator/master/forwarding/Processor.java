package gr.uoa.di.containermigrator.master.forwarding;

import gr.uoa.di.containermigrator.master.global.Global;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class Processor extends Thread {
	private final Socket src;
	private final Socket trg;

	private final StateMonitor monitor;

	public Processor(Socket src, Socket trg, StateMonitor monitor) {
		this.src = src;
		this.trg = trg;
		this.monitor = monitor;
	}

	public void run() {
		try {
			new Forwarder(src.getInputStream(), trg.getOutputStream(), this.monitor).start();
			new Forwarder(trg.getInputStream(), src.getOutputStream(), this.monitor).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
