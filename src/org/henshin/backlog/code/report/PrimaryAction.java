package org.henshin.backlog.code.report;

public class PrimaryAction extends RedundancyItems{
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
