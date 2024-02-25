package org.henshin.backlog2;

public class EmptyOrNotExistJsonFile extends Exception{

	public EmptyOrNotExistJsonFile() {
		super("The JOSNObject is empty or not exist!");
	}

}
