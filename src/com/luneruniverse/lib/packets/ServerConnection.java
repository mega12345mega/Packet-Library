package com.luneruniverse.lib.packets;

import java.io.IOException;
import java.net.Socket;

public class ServerConnection extends Connection {
	
	@Override
	protected long getTimeout() {
		return Server.timeout;
	}
	
	
	
	private Server server;
	
	ServerConnection(Server server, PacketHandler handler, Socket socket) throws IOException {
		super(handler, socket);
		this.server = server;
	}
	
	public Server getServer() {
		return server;
	}
	
}
