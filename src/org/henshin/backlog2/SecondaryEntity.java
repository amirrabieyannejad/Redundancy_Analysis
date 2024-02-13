package org.henshin.backlog2;

public class SecondaryEntity {
	private String name;
	final private String classType = "Secondary Entity";

	public SecondaryEntity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return classType;

	}
}
