package org.henshin.backlog.code.report;

public class ContainsPair extends ConflictingItems{
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
