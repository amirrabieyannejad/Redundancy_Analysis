package org.henshin.backlog2;

public class SecondaryAction {
	private String name;
	final private String classType = "Secondary Action";

	public SecondaryAction(String name) {
		this.name = name;

	}

	public String getName() {
		return name;
	}

	public String getType() {
		return classType;
	}
}
