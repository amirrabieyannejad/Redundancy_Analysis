package org.henshin.backlog.code.report;
//Stores effective value of \textit{Persona}
//as a persona and \textit{Primary Action} as an action.
public class TriggersPair extends RedundancyItems{
	private String persona;
	private String action;

	public TriggersPair(String persona, String entity) {
		this.persona = persona;
		this.action = entity;
	}

	public String getPersona() {
		return persona;
	}

	public void setPersona(String persona) {
		this.persona = persona;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
