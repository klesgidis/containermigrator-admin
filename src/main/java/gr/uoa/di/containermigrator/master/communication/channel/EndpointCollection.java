package gr.uoa.di.containermigrator.master.communication.channel;

/**
 * @author Kyriakos Lesgidis
 * @email klesgidis@di.uoa.gr
 */
public class EndpointCollection {
	private Endpoint dataChannel;
	private Endpoint adminChannel;

	public EndpointCollection(String address, int dataPort, int dataListenPort, int adminPort, int adminListenPort) {
		this.dataChannel = new Endpoint(address, dataPort, dataListenPort);
		this.adminChannel = new Endpoint(address, adminPort, adminListenPort);
	}

	public Endpoint getDataChannel() {
		return dataChannel;
	}

	public Endpoint getAdminChannel() {
		return adminChannel;
	}
}
