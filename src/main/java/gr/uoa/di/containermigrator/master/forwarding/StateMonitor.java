package gr.uoa.di.containermigrator.master.forwarding;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class StateMonitor {
	private boolean isMigrating;

	public StateMonitor() {
		this.isMigrating = false;
	}

	public synchronized void checkState() throws InterruptedException {
		while (this.isMigrating)
			wait();
	}

	public synchronized void migrationState(boolean value) {
		this.isMigrating = value;
		notifyAll();
	}
}
