package org.henshin.backlog.code.report;

public class SecondaryEntity  extends RedundancyItems{
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
