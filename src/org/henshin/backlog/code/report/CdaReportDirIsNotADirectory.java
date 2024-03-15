package org.henshin.backlog.code.report;

public class CdaReportDirIsNotADirectory extends Exception{

	public CdaReportDirIsNotADirectory() {
		super("CDA Report Directory is not a directory!");
	}
	public CdaReportDirIsNotADirectory(String message) {
		super(message);
	}

}
