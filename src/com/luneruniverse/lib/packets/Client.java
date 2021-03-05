package com.luneruniverse.lib.packets;

import java.io.IOException;
import java.net.Socket;

public class Client extends Connection {
	
	public static long timeout = -1;
	@Override
	protected long getTimeout() {
		return timeout;
	}
	
	
	
	public Client(PacketHandler handler, String ip, int port) throws IOException {
		super(handler, new Socket(ip, port));
	}
	public Client(PacketHandler handler, int port) throws IOException {
		this(handler, "127.0.0.1", port);
	}
	
}