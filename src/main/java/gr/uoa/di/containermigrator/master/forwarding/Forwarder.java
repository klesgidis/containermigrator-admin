package gr.uoa.di.containermigrator.master.forwarding;

import gr.uoa.di.containermigrator.master.global.Global;
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
public class Forwarder implements Runnable, Closeable, Preferences {
	private String name;
	private String monitorKey;
	private InputStream in;
	private OutputStream out;

	public Forwarder(String name, InputStream in, OutputStream out, String monitorKey) {
		this.name = name;
		this.in = in;
		this.out = out;
		this.monitorKey = monitorKey;
	}

	public void run() {
		byte[] buf = new byte[BUF_SIZE];
		int count;

		try {
			while ((count = in.read(buf)) != -1) {
				Global.getMonitors().get(this.monitorKey).checkState();
				out.write(buf, 0, count);
			}
		} catch (SocketException e) {

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
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

	}

	public void close() throws IOException {

	}
}
