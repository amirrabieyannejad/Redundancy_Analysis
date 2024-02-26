package org.henshin.backlog2;

public class EntityInJsonFileNotFound extends Exception{

	public EntityInJsonFileNotFound() {
		super("Entity in JOSNObject not found!");
	}
	public EntityInJsonFileNotFound(String message) {
		super(message);
	}

}
