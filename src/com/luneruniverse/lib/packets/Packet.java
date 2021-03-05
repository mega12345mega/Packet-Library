package com.luneruniverse.lib.packets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class Packet {
	
	private static final JsonParser parser = new JsonParser();
	private static long nextId = 0;
	
	private final String request;
	private final JsonObject data;
	private final long id;
	private PacketHandler responseHandler;
	private long responseId;
	private String error;
	
	private Packet(String request, JsonObject data, long id, long responseId, PacketHandler responseHandler) {
		this.responseHandler = responseHandler;
		this.request = request;
		this.data = data;
		this.id = id;
		this.responseId = responseId;
	}
	private Packet(String request, JsonObject data, long responseId, PacketHandler responseHandler) {
		this(request, data, nextId++, responseId, responseHandler);
	}
	public Packet(String request, JsonObject data, PacketHandler responseHandler) {
		this(request, data, -1, responseHandler);
	}
	public Packet(String request, String value, PacketHandler responseHandler) {
		this(request, new JsonObject(), responseHandler);
		data.addProperty("value", value);
	}
	public Packet(String request, PacketHandler responseHandler) {
		this(request, new JsonObject(), responseHandler);
	}
	
	void setError(String error) {
		this.error = error;
	}
	public String getError() {
		return error;
	}
	public boolean hasError() {
		return error != null;
	}
	
	
	public String getRequest() {
		return request;
	}
	public JsonObject getData() {
		return data;
	}
	public String getValue() {
		return data.get("value").getAsString();
	}
	long getId() {
		return id;
	}
	long getResponseId() {
		return responseId;
	}
	PacketHandler getResponseHandler() {
		return responseHandler;
	}
	
	void setResponseHandler(PacketHandler responseHandler) {
		this.responseHandler = responseHandler;
	}
	
	
	// IO
	void send(PrintWriter output) throws IOException {
		JsonObject packetMap = new JsonObject();
		packetMap.addProperty("request", request);
		packetMap.add("data", data);
		packetMap.addProperty("id", id);
		packetMap.addProperty("responseId", responseId);
		if (hasError())
			packetMap.addProperty("error", error);
		
		output.println(packetMap.toString());
	}
	public void reply(Packet packet, Connection output) throws IOException {
		packet.responseId = id;
		output.sendMessage(packet);
	}
	static Packet receive(BufferedReader reader) throws IOException {
		try {
			String line = reader.readLine();
			if (line == null)
				throw new IOException("Stream closed");
			JsonObject packet = parser.parse(line).getAsJsonObject();
			String request = packet.get("request").getAsString();
			JsonObject data = packet.get("data").getAsJsonObject();
			long id = packet.get("id").getAsLong();
			long responseId = packet.get("responseId").getAsLong();
			
			Packet output = new Packet(request, data, id, responseId, null);
			if (packet.has("error"))
				output.setError(packet.get("error").getAsString());
			
			return output;
		} catch (JsonParseException | ClassCastException e) {
			throw new BadPacketException(e);
		}
	}
	
	@Override
	public String toString() {
		return "[Packet; Id: " + id + "; Response id: " + responseId + (hasError() ? "; Error: " + error : "") + "; Request type: " + request + "; Data: " + data.toString() + "]";
	}
	
}
