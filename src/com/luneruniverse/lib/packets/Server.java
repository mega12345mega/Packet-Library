package com.luneruniverse.lib.packets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
	public static long timeout = -1;
	
	
	private ServerSocket serverSocket;
	private List<ServerConnection> connections;
	private Thread thread;
	
	public Server(ConnectHandler connectHandler, PacketHandler packetHandler, int port) throws IOException {
		serverSocket = new ServerSocket(port);
		connections = new ArrayList<>();
		
		thread = new Thread(() -> {
			while (!thread.isInterrupted()) {
				try {
					Socket clientSocket = serverSocket.accept();
					try {
						ServerConnection connection = new ServerConnection(this, packetHandler, clientSocket);
						connections.add(connection);
						connectHandler.handleConnection(connection);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					break;
				}
			}
		});
		thread.start();
	}
	
	public List<ServerConnection> getConnections() {
		return connections;
	}
	
	public void stop() throws IOException {
		thread.interrupt();
		for (ServerConnection connection : connections)
			connection.stop();
		serverSocket.close();
	}
	
	public boolean isClosed() {
		return serverSocket.isClosed();
	}
	
}