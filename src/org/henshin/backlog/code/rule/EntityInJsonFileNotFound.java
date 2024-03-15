package org.henshin.backlog.code.rule;

public class EntityInJsonFileNotFound extends Exception{

	public EntityInJsonFileNotFound() {
		super("Entity in JOSNObject not found!");
	}
	public EntityInJsonFileNotFound(String message) {
		super(message);
	}

}
