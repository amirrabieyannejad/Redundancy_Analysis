package org.henshin.backlog.code.report;
// Stores effective value of \textit{Persona} as a 
// persona and \textit{Primary Action} as an action.
public class Triggers extends RedundancyItems {
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
