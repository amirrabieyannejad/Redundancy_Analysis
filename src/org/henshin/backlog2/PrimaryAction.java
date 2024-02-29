package org.henshin.backlog2;

public class PrimaryAction extends ConflictingItems{
	private String name;
	final private String classType = "Primary Action";

	public PrimaryAction(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return classType;

	}

}
