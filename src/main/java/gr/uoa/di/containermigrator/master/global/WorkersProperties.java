package gr.uoa.di.containermigrator.master.global;

import gr.uoa.di.containermigrator.master.communication.channel.EndpointCollection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class WorkersProperties implements Preferences {

	public Map<String, EndpointCollection> workers = new HashMap<>();

	public WorkersProperties(String propertyFile) {
		ClassLoader classLoader = getClass().getClassLoader();
		String resourcePath = classLoader.getResource(propertyFile).getFile();

		try (InputStream input = new FileInputStream(resourcePath)) {
			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			String [] workers = prop.getProperty("nodes").split(",");
			for (String worker : workers) {
				String addr = prop.getProperty("node." + worker + ".address");

				int dataPort = Integer.parseInt(prop.getProperty("node." + worker + ".data.port"));
				int dataListenPort = Integer.parseInt(prop.getProperty("node." + worker + ".data.listenPort"));

				int adminPort = Integer.parseInt(prop.getProperty("node." + worker + ".admin.port"));
				int adminListenPort = Integer.parseInt(prop.getProperty("node." + worker + ".admin.listenPort"));

				this.workers.put(worker, new EndpointCollection(addr, dataPort, dataListenPort,
						adminPort, adminListenPort));
			}

		} catch (IOException|NullPointerException e) {
			e.printStackTrace();
		}
	}

	public Map<String, EndpointCollection> getWorkers() {
		return workers;
	}
}
