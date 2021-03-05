package com.luneruniverse.lib.packets;

import java.io.IOException;

@SuppressWarnings("serial")
class BadPacketException extends IOException {
	public BadPacketException(Exception cause) {
		super("Bad packet", cause);
	}
}
