package org.henshin.backlog2;

public class CdaReportDirNotFound extends Exception{

	public CdaReportDirNotFound() {
		super("CDA Report Directory was not found!");
	}
	public CdaReportDirNotFound(String message) {
		super(message);
	}

}
