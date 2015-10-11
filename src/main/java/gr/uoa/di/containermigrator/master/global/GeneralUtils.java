package gr.uoa.di.containermigrator.master.global;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class GeneralUtils {

	public static int fetchAvailablePort() {
		int port = -1;
		try (ServerSocket ss = new ServerSocket(0)) {
			port = ss.getLocalPort();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return port;
	}

	public static String generateKey(String host, String container) {
		return host.replaceAll("/", "") + "_" + container.replaceAll("/", "");
	}

}
