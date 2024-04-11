package org.henshin.backlog.code.report;

public class Items extends RedundancyItems {

	protected String name;
	protected String classType;

	public Items(String name, String classType) {
		this.name = name;
		this.classType = classType;
	}
	
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }
}
