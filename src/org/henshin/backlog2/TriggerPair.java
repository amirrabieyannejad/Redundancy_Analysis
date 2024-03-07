package org.henshin.backlog2;

public class TriggerPair extends ConflictingItems{
	String persona;
	String entity;

	public TriggerPair(String persona, String entity) {
		this.persona = persona;
		this.entity = entity;
	}

	public String getPersona() {
		return persona;
	}

	public void setPersona(String persona) {
		this.persona = persona;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

}
