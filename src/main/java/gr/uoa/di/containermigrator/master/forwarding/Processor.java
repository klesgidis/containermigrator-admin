package gr.uoa.di.containermigrator.master.forwarding;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class Processor implements Runnable {
	private final Socket src;
	private final Socket trg;

	private final String monitorKey;

	public Processor(Socket src, Socket trg, String monitorKey) {
		this.src = src;
		this.trg = trg;
		this.monitorKey = monitorKey;
	}

	public void run() {
		try {
			new Thread(new Forwarder("Request", src.getInputStream(), trg.getOutputStream(), this.monitorKey)).start();
			new Thread(new Forwarder("Response", trg.getInputStream(), src.getOutputStream(), this.monitorKey)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
