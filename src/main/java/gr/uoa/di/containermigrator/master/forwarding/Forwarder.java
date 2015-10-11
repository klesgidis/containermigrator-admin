package gr.uoa.di.containermigrator.master.forwarding;

import gr.uoa.di.containermigrator.master.global.Preferences;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class Forwarder extends Thread implements Closeable, Preferences {
	private InputStream in;
	private OutputStream out;
	private StateMonitor monitor;

	public Forwarder(InputStream in, OutputStream out, StateMonitor monitor) {
		this.in = in;
		this.out = out;
		this.monitor = monitor;
	}

	public void run() {
		int count;
		try {
			while ((count = in.read(monitor.getBuffer())) != -1) {
				// TODO Maybe having the same buffer for both forwarders produces problems
				monitor.checkState();
				out.write(monitor.getBuffer(), 0, count);
			}
		} catch (SocketException e) {

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Child " + Thread.currentThread().getId() + " interrupted");
		} finally {
			try {
				in.close();
			} catch (IOException e) {

			}
			try {
				out.close();
			} catch (IOException e) {

			}
		}
		//System.out.println("Forwarder " + Thread.currentThread().getName() + " finished.");
	}

	public void close() throws IOException {

	}
}
