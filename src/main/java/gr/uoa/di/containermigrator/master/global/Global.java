package gr.uoa.di.containermigrator.master.global;

import gr.uoa.di.containermigrator.master.communication.channel.Endpoint;

import java.util.HashMap;
import java.util.Map;

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

	public static Map<String, Endpoint> endpointCollection = new HashMap<>();
}
