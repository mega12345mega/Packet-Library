package com.luneruniverse.lib.packets;

class Supplier<T> {
	
	private T value;
	
	public Supplier(T value) {
		this.value = value;
	}
	public Supplier() {
		this(null);
	}
	
	public void set(T value) {
		this.value = value;
	}
	public T get() {
		return value;
	}
	
}
