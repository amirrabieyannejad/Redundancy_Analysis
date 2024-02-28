package org.henshin.backlog2;

public class Triggers extends ConflictingItems {
	private String name;
	private String classType;

	public Triggers(String name, String type) {
		this.name = name;
		this.classType = type;

	}

	public String getName() {
		return name;
	}

	public String getType() {
		return classType;

	}

}
