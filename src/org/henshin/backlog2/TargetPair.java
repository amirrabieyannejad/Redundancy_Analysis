package org.henshin.backlog2;

public class TargetPair extends ConflictingItems{
	private String action;
	private String entity;

	public TargetPair(String action, String entity) {
		this.action = action;
		this.entity = entity;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

}
