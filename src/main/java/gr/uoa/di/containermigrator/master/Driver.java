package gr.uoa.di.containermigrator.master;

import gr.uoa.di.containermigrator.master.forwarding.Listener;
import gr.uoa.di.containermigrator.master.global.Global;

import java.net.InetSocketAddress;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class Driver {
	private static void usage() {
		System.out.println("Usage: containermigrator <property-file>");
		System.exit(1);
	}

	private static void init(String[] args) {
		if (args.length == 1)
			Global.loadProperties(args[0]);
		else
			usage();
	}

	public static void main(String[] args) {
		init(args);

		new Thread(new CliDaemon()).start();
	}
}
