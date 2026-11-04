package com.koios.client;

/**
 * The current state of the client build
 * 
 * @author Timothy Howard <Arithium>
 *
 */
public enum ClientStatus {

	/**
	 * The client is set up for local connections
	 */
	LOCAL("127.0.0.1", 43594),

	/**
	 * The client is set up for live use
	 */
	LIVE("gs.koiosps.com", 43594),

	/**
	 * The client is set up for development on the testing server
	 */
	BETA("srv.zarpor.org", 43594);

	/**
	 * The IP address as a long
	 */
	private String address;

	/**
	 * The port of the address
	 */
	private int port;
	
	/**
	 * Cached values array.
	 */
	private static final ClientStatus[] values = values();

	/**
	 * Constructs a new {@link ClientStatus} to know where to connect the client too
	 * 
	 * @param address The ip address as a long
	 * @param port The port to connect too
	 */
	ClientStatus(String address, int port) {
		this.address = address;
		this.port = port;
	}

	/**
	 * Gets the address to connect too
	 * 
	 * @return Gets the address the client will connect too
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Gets the port the client will connect too
	 * 
	 * @return The port the client will connect too
	 */
	public int getPort() {
		return port;
	}
	
	public ClientStatus getNext() {
		int next = ordinal() + 1;
		if(next >= values.length) return values[0];
		return values[next];
	}

}
