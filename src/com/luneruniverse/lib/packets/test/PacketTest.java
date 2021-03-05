package com.luneruniverse.lib.packets.test;

import java.io.IOException;

import com.luneruniverse.lib.packets.Client;
import com.luneruniverse.lib.packets.Connection;
import com.luneruniverse.lib.packets.Packet;
import com.luneruniverse.lib.packets.Server;
import com.luneruniverse.lib.packets.exceptions.ErrorPacketException;

public class PacketTest {
	
	public static void main(String[] args) throws IOException {
		new PacketTest();
	}
	
	public PacketTest() throws IOException {
		Connection.LOG = System.out;
		Connection.LOG_DISPLAY_CLASS = true;
		
		Server server = new Server((connection) -> {
			try {
				System.out.println(connection.sendMessageWithReply(new Packet("msg", null)).getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, (packet, output) -> {
			if (packet.hasError()) {
				new ErrorPacketException(packet).printStackTrace();
				return true;
			}
			if (packet.getRequest().equals("getData")) {
				try {
					packet.reply(new Packet("response", ":D", null), output);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		}, 6666);
		
		Client client = new Client((packet, output) -> {
			if (packet.getRequest().equals("msg")) {
				try {
					Packet reply = output.sendMessageWithReply(new Packet("getData", null));
					packet.reply(new Packet("response", reply.getValue(), null), output);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		}, 6666);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		server.stop();
		client.stop();
	}
	
}
