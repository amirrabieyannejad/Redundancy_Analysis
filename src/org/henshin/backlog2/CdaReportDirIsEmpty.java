package org.henshin.backlog2;

public class CdaReportDirIsEmpty extends Exception{

	public CdaReportDirIsEmpty() {
		super("CDA Report Directory found but is empty!");
	}
	public CdaReportDirIsEmpty(String message) {
		super(message);
	}

}
