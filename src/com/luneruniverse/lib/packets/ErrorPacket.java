package com.luneruniverse.lib.packets;

public class ErrorPacket extends Packet {
	
	public ErrorPacket(String errorType, String value) {
		super("error", value, null);
		setError(errorType);
	}
	
}
