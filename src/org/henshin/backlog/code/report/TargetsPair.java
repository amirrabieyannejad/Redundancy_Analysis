package org.henshin.backlog.code.report;

//Stores effective value of \textit{Primary/Secondary Action}
//and \textit{Primary/Secondary Entity} in action and entity 
//fields accordingly.
public class TargetsPair extends RedundancyItems{
	private String action;
	private String entity;

	public TargetsPair(String action, String entity) {
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
