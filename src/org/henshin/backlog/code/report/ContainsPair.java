package org.henshin.backlog.code.report;
//ConflictPair: Stores the identifier of the two created 
//user stories as a conflict pair. It also saves the total
//number of redundancy clauses within the US-pair.
public class ContainsPair extends RedundancyItems{
	String parentEntity;
	String childEntity;

	public ContainsPair(String action, String entity) {
		this.parentEntity = action;
		this.childEntity = entity;
	}

	public String getParentEntity() {
		return parentEntity;
	}

	public void setParentEntity(String parentEntity) {
		this.parentEntity = parentEntity;
	}

	public String getChildEntity() {
		return childEntity;
	}

	public void setChildEntity(String childEntity) {
		this.childEntity = childEntity;
	}



}
