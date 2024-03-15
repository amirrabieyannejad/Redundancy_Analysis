package org.henshin.backlog.code.report;

public class TriggerPair extends ConflictingItems{
	private String persona;
	private String action;

	public TriggerPair(String persona, String entity) {
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
