package gr.uoa.di.containermigrator.master.global;

import gr.uoa.di.containermigrator.master.forwarding.StateMonitor;

import java.util.ArrayList;
import java.util.UUID;
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

	public static ConcurrentMap<String, MigrationInfo> migrationInfos = null;
	public static synchronized ConcurrentMap<String, MigrationInfo> getMigrationInfos() {
		if (migrationInfos == null) migrationInfos = new ConcurrentHashMap<>();
		return migrationInfos;
	}

}
