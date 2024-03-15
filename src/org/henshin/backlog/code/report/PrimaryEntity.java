package org.henshin.backlog.code.report;

public class PrimaryEntity extends ConflictingItems{
	private String name;
	final private String classType = "Primary Entity";

	public PrimaryEntity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return classType;

	}
}
