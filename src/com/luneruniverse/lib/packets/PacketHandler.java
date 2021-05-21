package com.luneruniverse.lib.packets;

@FunctionalInterface
public interface PacketHandler {
	/**
	 * Handle an incoming packet
	 * 
	 * @param packet - incoming packet
	 * @param output - where to output packets
	 * @return if the packet was handled
	 */
	public boolean handlePacket(Packet packet, Connection output, WaitHandler wait);
}
