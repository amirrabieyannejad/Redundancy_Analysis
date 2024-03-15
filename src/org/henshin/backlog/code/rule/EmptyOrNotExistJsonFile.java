package org.henshin.backlog.code.rule;

public class EmptyOrNotExistJsonFile extends Exception{

	public EmptyOrNotExistJsonFile() {
		super("The JOSNObject is empty or not exist!");
	}

}
