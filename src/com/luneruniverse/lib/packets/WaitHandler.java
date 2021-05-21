package com.luneruniverse.lib.packets;

public class WaitHandler {
	
	private boolean waiting;
	
	public WaitHandler() {
		this.waiting = true;
	}
	
	public void dontWait() {
		this.waiting = false;
	}
	
	public boolean isWaiting() {
		return waiting;
	}
	
}
