package gr.uoa.di.containermigrator.master.global;

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

	public static ConcurrentMap<String, MigrationInfo> migrationInfos = null;
	public static synchronized ConcurrentMap<String, MigrationInfo> getMigrationInfos() {
		if (migrationInfos == null) migrationInfos = new ConcurrentHashMap<>();
		return migrationInfos;
	}

	public static void printMigrationInfoKeys() {
		System.out.println("Migration Infos (" + getMigrationInfos().size() + ")");
		for (Map.Entry<String, MigrationInfo> entry : getMigrationInfos().entrySet())
			System.out.println(entry.getKey());
	}
}
