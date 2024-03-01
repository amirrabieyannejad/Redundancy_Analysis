package org.henshin.backlog2;

public class Contains extends ConflictingItems {
	private String name;
	private String classType;

	public Contains(String name, String type) {
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
