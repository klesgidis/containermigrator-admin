package gr.uoa.di.containermigrator.master.global;

import gr.uoa.di.containermigrator.master.communication.channel.Endpoint;
import gr.uoa.di.containermigrator.master.forwarding.StateMonitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class Global implements Preferences {
	private static WorkersProperties properties = null;
	public static void loadProperties(String propertyFile) {
		properties = new WorkersProperties(propertyFile);
	}
	public static WorkersProperties getProperties() {
		if (properties == null) throw new NullPointerException("Node properties are not initialized.");
		return properties;
	}

	public static ConcurrentMap<String, StateMonitor> monitors = null;
	public static synchronized ConcurrentMap<String, StateMonitor> getMonitors() {
		if (monitors == null) monitors = new ConcurrentHashMap<>();
		return monitors;
	}
}
