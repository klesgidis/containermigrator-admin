package gr.uoa.di.containermigrator.master;

import com.sun.istack.internal.Nullable;
import gr.uoa.di.containermigrator.master.communication.channel.ChannelUtils;
import gr.uoa.di.containermigrator.master.communication.channel.ClientEndpoint;
import gr.uoa.di.containermigrator.master.communication.channel.Endpoint;
import gr.uoa.di.containermigrator.master.communication.protocol.Protocol;
import gr.uoa.di.containermigrator.master.forwarding.StateMonitor;
import gr.uoa.di.containermigrator.master.global.GeneralUtils;
import gr.uoa.di.containermigrator.master.global.Global;
import gr.uoa.di.containermigrator.master.global.MigrationInfo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class CliDaemon implements Runnable {
	private final static String start = "start";
	private final static String migrate = "migrate";
	private final static String nodes = "nodes";
	private final static String help = "help";

	private void usage() {
		System.out.println("Command in not specified correctly. Type \"help\" for more information.");
	}

	public void run() {
		System.out.println("Type help for more information");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		String line;
		try {
			while ((line = in.readLine()) != null && line.length() != 0) {
				String [] args = line.split(" ");
				String cmd = args[0];
				switch (cmd) {
					case start: {
						if (args.length != 3) { usage(); break; }
						String host = this.generateHost(args[1]);
						String container = args[2];

						if (host == null) {
							System.out.println("Worker " + args[1] + " doesn't exist.");
							break;
						}

						handleStart(host, container);
						break;
					}
					case migrate: {
						if (args.length != 4) { usage(); break; }
						String srcHost = this.generateHost(args[1]);
						String trgHost = this.generateHost(args[2]);
						String container = args[3];

						if (srcHost == null) {
							System.out.println("Worker " + args[1] + " doesn't exist.");
							break;
						}
						if (trgHost == null) {
							System.out.println("Worker " + args[2] + " doesn't exist.");
							break;
						}

						handleMigrate(srcHost, trgHost, container);
						break;
					}
					case nodes: {
						handleNodes();
						break;
					}
					case help: {
						StringBuilder sb = new StringBuilder("");
						sb.append("-- start <worker-name> <container-name> :\n")
								.append("\t\tStarts an already existing container ")
								.append("and returns the listening port. \n");
						sb.append("-- migrate <src-worker> <trg-worker> <container-name> :\n")
								.append("\t\tMigrate <container-name> container from ")
								.append("<src-worker> to <trg-worker>. \n");
						sb.append("-- nodes :\n")
								.append("\t\tShows the available nodes")
								.append("for migration. \n");
						sb.append("-- help :\n")
								.append("\t\tDisplays the available commands.");
						System.out.println(sb.toString());
						break;
					}
					default:
						usage();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//region Handlers

	private void handleNodes() throws Exception {
		Iterable<String> response = ChannelUtils.pingNodes();
		for (String str : response) System.out.println(str);
	}

	private void handleMigrate(String srcHost, String trgHost, String container) throws Exception {
		String key = GeneralUtils.generateKey(srcHost, container);

		// Stop traffic
		Global.getMigrationInfos().get(key).getMonitor().stopTraffic();

		Protocol.AdminMessage message = Protocol.AdminMessage.newBuilder()
				.setType(Protocol.AdminMessage.Type.MIGRATE)
				.setMigrate(Protocol.AdminMessage.Migrate.newBuilder()
						.setContainer(container)
						.setSource(srcHost)
						.setTarget(trgHost))
				.build();

		Protocol.AdminResponse response = null;
		try (ClientEndpoint cEnd = Global.getProperties().getWorkers().get(srcHost).getClientEndpoint();
			 DataOutputStream dOut = new DataOutputStream(cEnd.getSocket().getOutputStream());
			 DataInputStream dIn = new DataInputStream(cEnd.getSocket().getInputStream())) {
			ChannelUtils.sendAdminMessage(message, dOut);

			response = ChannelUtils.recvAdminResponse(dIn);
		}
		if (response == null)
			throw new Exception("Didn't receive response");
		else if (response.getType() == Protocol.AdminResponse.Type.OK) {
			// TODO This has to change with specific responses
			String[] tokens = response.getPayload().split("#");

			String newContainerName = tokens[0];
			int trgListenPort = Integer.parseInt(tokens[1]);

			// Add listener to forward traffic to new host
			Global.getMigrationInfos().get(key).updateMigrationInfo(trgHost, newContainerName, trgListenPort);

			// Update key name
			String newKey = GeneralUtils.generateKey(trgHost, newContainerName);
			MigrationInfo mi = Global.getMigrationInfos().remove(key);
			Global.getMigrationInfos().put(newKey, mi);

			// Resume traffic
			Global.getMigrationInfos().get(newKey).getMonitor().resumeTraffic();
			System.out.println("OK");
		} else if (response.getType() == Protocol.AdminResponse.Type.ERROR)
			throw new Exception("Error migrating container. Message: " + response.getPayload());
	}

	private void handleStart(String host, String container) throws Exception {
		Protocol.AdminMessage message = Protocol.AdminMessage.newBuilder()
				.setType(Protocol.AdminMessage.Type.START)
				.setStart(Protocol.AdminMessage.Start.newBuilder()
						.setContainer(container))
				.build();

		Protocol.AdminResponse response = null;
		try (ClientEndpoint cEnd = Global.getProperties().getWorkers().get(host).getClientEndpoint();
			 DataOutputStream dOut = new DataOutputStream(cEnd.getSocket().getOutputStream());
			 DataInputStream dIn = new DataInputStream(cEnd.getSocket().getInputStream())) {
			ChannelUtils.sendAdminMessage(message, dOut);

			response = ChannelUtils.recvAdminResponse(dIn);
		}

		if (response == null)
			throw new Exception("Didn't receive response");
		else if (response.getType() == Protocol.AdminResponse.Type.OK) {
			// We expect the port that listens for the specific container
			String address = Global.getProperties().getWorkers().get(host).getClientEndpoint().getAddress();
			int port = Integer.parseInt(response.getPayload());
			int listenPort = GeneralUtils.fetchAvailablePort();

			String key = GeneralUtils.generateKey(host, container);
			Global.getMigrationInfos().put(key, new MigrationInfo(
					host,
					container,
					new StateMonitor()
			));

			// Start listener for forwarding data
			Global.getMigrationInfos().get(key)
					.setListener(new InetSocketAddress(address, port), listenPort);
			Global.getMigrationInfos().get(key)
					.startListener();

			System.out.println("OK");
		} else if (response.getType() == Protocol.AdminResponse.Type.ERROR)
			throw new Exception("Error starting container. Message: " + response.getPayload());
	}

	//endregion

	//region Utilities

	/**
	 * Returns host name if user gave IP. This is used to generate always the same
	 * result depending on user input.
	 * @param host IP or worker name
	 * @return
	 */
	@Nullable
	private String generateHost(String host) {
		// if user gave IP return worker name
		if (Global.getProperties().getAddressToWorkerMapping().containsKey(host))
			return Global.getProperties().getAddressToWorkerMapping().get(host);

		// if user gave worker name return worker name
		if (Global.getProperties().getWorkers().containsKey(host))
			return host;

		// worker name doesn't exist
		return null;
	}

	//endregion
}
