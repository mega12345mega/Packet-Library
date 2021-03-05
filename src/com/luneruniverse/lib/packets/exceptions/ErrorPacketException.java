package com.luneruniverse.lib.packets.exceptions;

import java.io.IOException;

import com.luneruniverse.lib.packets.Packet;

@SuppressWarnings("serial")
public class ErrorPacketException extends IOException {
	
	private final Packet packet;
	
	public ErrorPacketException(Packet packet) {
		super("Error packet (" + packet.getError() + "; " + packet.getValue() + ") - " + packet);
		this.packet = packet;
	}
	
	public String getErrorType() {
		return packet.getError();
	}
	public String getErrorMessage() {
		return packet.getValue();
	}
	
	public Packet getPacket() {
		return packet;
	}
	
}
