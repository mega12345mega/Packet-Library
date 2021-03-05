package com.luneruniverse.lib.packets.exceptions;

import java.io.IOException;

import com.luneruniverse.lib.packets.Packet;

@SuppressWarnings("serial")
public class TimeoutException extends IOException {
	
	private final Packet packet;
	
	public TimeoutException(Packet packet) {
		super("Response timed out");
		this.packet = packet;
	}
	
	public Packet getSentPacket() {
		return packet;
	}
	
}
