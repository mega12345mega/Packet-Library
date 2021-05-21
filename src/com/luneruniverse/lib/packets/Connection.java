package com.luneruniverse.lib.packets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.luneruniverse.lib.packets.exceptions.ErrorPacketException;
import com.luneruniverse.lib.packets.exceptions.TimeoutException;

public abstract class Connection {
	
	protected long getTimeout() {
		return -1;
	}
	
	
	public static PrintStream LOG;
	public static boolean LOG_DISPLAY_CLASS;
	private String getLogClass() {
		return LOG_DISPLAY_CLASS ? "(" + this.getClass().getSimpleName() + ") " : "";
	}
	
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	private Map<Long, Packet> waitingResponse;
	private Thread thread;
	
	protected Connection(PacketHandler handler, Socket socket) throws IOException {
		this.socket = socket;
		
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		waitingResponse = new HashMap<>();
		
		thread = new Thread(() -> {
			try {
				while (socket.isConnected()) {
					try {
						
						Packet incomingPacket = Packet.receive(in);
						if (LOG != null)
							LOG.println("Received packet " + getLogClass() + "- " + incomingPacket);
						if (incomingPacket.getResponseId() != -1) {
							if (waitingResponse.containsKey(incomingPacket.getResponseId()))
								startWaitingThread(incomingPacket, waitingResponse.remove(incomingPacket.getResponseId()).getResponseHandler());
							continue;
						}
						startWaitingThread(incomingPacket, handler);
						
					} catch (BadPacketException e) {
						
						StringWriter error = new StringWriter();
						e.printStackTrace(new PrintWriter(error));
						
						sendMessage(new ErrorPacket("not a packet", error.toString()));
						
					} catch (IOException e) {
						
						break;
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}
	private void startWaitingThread(Packet incomingPacket, PacketHandler handler) {
		WaitHandler wait = new WaitHandler();
		Thread thread = new Thread(() -> {
			try {
				if (!handler.handlePacket(incomingPacket, this, wait) && !incomingPacket.hasError())
					sendMessage(new ErrorPacket("invalid packet type", incomingPacket.getRequest()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		thread.start();
		while (wait.isWaiting() && thread.isAlive()) {
			try {
				thread.join(10);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public void sendMessage(Packet packet) throws IOException {
		if (packet.getResponseHandler() != null)
			waitingResponse.put(packet.getId(), packet);
		if (LOG != null)
			LOG.println("Sending packet " + getLogClass() + "- " + packet);
		packet.send(out);
	}
	public Packet sendMessageWithReply(Packet packet) throws IOException {
		Supplier<Packet> response = new Supplier<>();
		PacketHandler responseHandler = packet.getResponseHandler();
		packet.setResponseHandler((responsePacket, output, wait) -> {
			if (responseHandler != null)
				responseHandler.handlePacket(responsePacket, output, wait);
			response.set(responsePacket);
			return true;
		});
		sendMessage(packet);
		
		long start = System.currentTimeMillis();
		while (response.get() == null) {
			if (getTimeout() >= 0) {
				if (System.currentTimeMillis() - start > getTimeout())
					throw new TimeoutException(packet);
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new IOException("reply interrupted");
			}
		}
		
		if (response.get().hasError())
			throw new ErrorPacketException(response.get());
		return response.get();
	}
	
	public void stop() throws IOException {
		thread.interrupt();
		socket.close();
		in.close();
		out.close();
		waitingResponse.clear();
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
}
