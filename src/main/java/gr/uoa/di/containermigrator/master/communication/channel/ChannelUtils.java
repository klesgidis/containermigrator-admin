package gr.uoa.di.containermigrator.master.communication.channel;

import gr.uoa.di.containermigrator.master.communication.protocol.Protocol;
import gr.uoa.di.containermigrator.master.global.Global;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class ChannelUtils {
	public static void sendAdminMessage(Protocol.AdminMessage message, DataOutputStream dOut) throws IOException {
		message.writeDelimitedTo(dOut);
	}
	public static Protocol.AdminMessage recvAdminMessage(DataInputStream dIn) throws IOException {
		return Protocol.AdminMessage.parseDelimitedFrom(dIn);
	}

	public static void multicastAdminMessage(Protocol.AdminMessage message) throws Exception {
		for (Map.Entry<String, Endpoint> pair : Global.getProperties().getWorkers().entrySet()) {
			try (ClientEndpoint cEnd = pair.getValue().getClientEndpoint();
				 Socket sock = cEnd.getSocket();
				 DataOutputStream dOut = new DataOutputStream(sock.getOutputStream())) {

				sendAdminMessage(message, dOut);
			}
		}
	}

	public static void sendAdminResponse(Protocol.AdminResponse response, DataOutputStream dOut) throws IOException {
		response.writeDelimitedTo(dOut);
	}
	public static Protocol.AdminResponse recvAdminResponse(DataInputStream dIn) throws IOException {
		return Protocol.AdminResponse.parseDelimitedFrom(dIn);
	}


}
