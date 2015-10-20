package gr.uoa.di.containermigrator.master.forwarding;

import gr.uoa.di.containermigrator.master.global.Preferences;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class StateMonitor implements Preferences{
	private boolean isMigrating;

	private byte[] buffer = new byte[BUF_SIZE];

	public StateMonitor() {
		this.isMigrating = false;
	}

	public synchronized void checkState() throws InterruptedException {
		while (this.isMigrating)
			wait();
	}

	public synchronized void stopTraffic() {
		this.migrationState(true);
	}

	public synchronized void resumeTraffic() {
		this.migrationState(false);
	}

	private synchronized void migrationState(boolean value) {
		this.isMigrating = value;
		notifyAll();
	}

	public byte[] getBuffer() {
		return buffer;
	}
}
