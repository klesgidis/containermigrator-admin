package gr.uoa.di.containermigrator.master;

import gr.uoa.di.containermigrator.master.communication.channel.ChannelUtils;
import gr.uoa.di.containermigrator.master.communication.channel.ClientEndpoint;
import gr.uoa.di.containermigrator.master.communication.channel.EndpointCollection;
import gr.uoa.di.containermigrator.master.communication.protocol.Protocol;
import gr.uoa.di.containermigrator.master.forwarding.Listener;
import gr.uoa.di.containermigrator.master.global.Global;

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
	private final static String list = "list";
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
						if (args.length < 3) { usage(); break; }
						String host = args[1];
						String container = args[2];

						handleStart(host, container);
						break;
					}
					case migrate: {
						if (args.length < 4) { usage(); break; }
						String srcHost = args[1];
						String trgHost = args[2];
						String container = args[3];
						// TODO Migrate container from src to trg node

						handleMigrate(srcHost, trgHost, container);
						break;
					}
					case nodes: {
						// TODO Have a thread that pings to see if it is active
						Map<String, EndpointCollection> peers = Global.getProperties().getWorkers();
						for (Map.Entry<String, EndpointCollection> peer : peers.entrySet()) {
							System.out.println(peer.getKey());
						}
						break;
					}
					case list: {
						if (args.length < 2) { usage(); break; }
						// TODO Show containers of specific node and its state

						break;
					}
					case help: {
						StringBuilder sb = new StringBuilder("");
						sb.append("-- start <container-name>")
								.append(":\t\tStarts an already existing container ")
								.append("and  returns the binding INetAddress. \n");
						sb.append("-- help:\t\t\t\t\t\t")
								.append("Returns all the available commands.");
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

//		String s;
//		try {
//			while ((s = in.readLine()) != null && s.length() != 0) {
//				if (s.equals("0")) {
//					StateMonitor.getInstance().migrationState(false);
//				} else if (s.equals("1")) {
//					StateMonitor.getInstance().migrationState(true);
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private void handleMigrate(String srcHost, String trgHost, String container) throws Exception {
		// TODO Stop sending traffic
		Protocol.AdminMessage message = Protocol.AdminMessage.newBuilder()
				.setType(Protocol.AdminMessage.Type.MIGRATE)
				.setMigrate(Protocol.AdminMessage.Migrate.newBuilder()
						.setContainer(container)
						.setSource(srcHost)
						.setTarget(trgHost))
				.build();

		try (ClientEndpoint cEnd = Global.getProperties().getWorkers().get(srcHost).getAdminChannel().getClientEndpoint();
			 DataOutputStream dOut = new DataOutputStream(cEnd.getSocket().getOutputStream())) {
			ChannelUtils.sendAdminMessage(message, dOut);
		}
		// TODO Receive OK
		// TODO Start sending traffic
	}

	private void handleStart(String host, String containerName) throws Exception {
		Protocol.AdminMessage message = Protocol.AdminMessage.newBuilder()
				.setType(Protocol.AdminMessage.Type.START)
				.setStart(Protocol.AdminMessage.Start.newBuilder()
						.setContainer(containerName))
				.build();

		Protocol.AdminResponse response = null;
		try (ClientEndpoint cEnd = Global.getProperties().getWorkers().get(host).getAdminChannel().getClientEndpoint();
			 DataOutputStream dOut = new DataOutputStream(cEnd.getSocket().getOutputStream());
			 DataInputStream dIn = new DataInputStream(cEnd.getSocket().getInputStream())) {
			ChannelUtils.sendAdminMessage(message, dOut);

			response = ChannelUtils.recvAdminResponse(dIn);
		}

		if (response.getType() == Protocol.AdminResponse.Type.OK) {
			new Thread(new Listener(new InetSocketAddress(
					Global.getProperties().getWorkers().get(host).getDataChannel().getClientEndpoint().getAddress(),
					Global.getProperties().getWorkers().get(host).getDataChannel().getClientEndpoint().getPort()
			))).start();
		} else
			throw new Exception("Error starting container. Message: " + response.getPayload());
	}
}