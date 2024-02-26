package org.henshin.backlog2;

public class ActionInJsonFileNotFound extends Exception{

	public ActionInJsonFileNotFound() {
		super("Action in JOSNObject not found!");
	}
	public ActionInJsonFileNotFound(String message) {
		super(message);
	}

}
