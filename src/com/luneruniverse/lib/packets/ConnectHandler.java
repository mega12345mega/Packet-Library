package com.luneruniverse.lib.packets;

@FunctionalInterface
public interface ConnectHandler {
	/**
	 * Handle a new connection
	 * 
	 * @param connection - new connection
	 */
	public void handleConnection(ServerConnection connection);
}
