package org.henshin.backlog2.test;

import org.henshin.backlog2.*;
import org.json.JSONException;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CdaConvertorV2Test {


	@Test
	public void testExtractReports_completeMajorElements_edge() throws EmptyOrNotExistJsonFile {
		
		String directoryName = "Tests\\ExactMatch";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";

		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");

		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);

		try {
			cdaConvertor.extractReports(fileWrite);

			// Read the Textual_report_Test file to verify the results
			BufferedReader reader = new BufferedReader(new FileReader(cdaReport));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
			reader.close();
			fileWrite.close();
			assertTrue(result.toString().contains("Secondary Entity: compliance")
					&& result.toString().contains("Secondary Action: review")
					&& result.toString().contains("Link from Secondary Action: targets"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	@Test
	public void testExtractReports_completeMajorElements_upperEdge() throws EmptyOrNotExistJsonFile {
		
		String directoryName = "Tests\\ExactMatch_upperEdge";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";

		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");

		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);

		try {
			cdaConvertor.extractReports(fileWrite);

			// Read the Textual_report_Test file to verify the results
			BufferedReader reader = new BufferedReader(new FileReader(cdaReport));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
			reader.close();
			fileWrite.close();
			assertTrue(result.toString().contains("Secondary Entity: compliance")
					&& result.toString().contains("Secondary Action: ensure")
					&& result.toString().contains("Primary Aciton: manage")
					&& result.toString().contains("Link from Secondary Action: targets")
					&& result.toString().contains("Link from Persona: triggers")
					);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testExtractReports_notCompleteMajorElements() throws EmptyOrNotExistJsonFile {

		String directoryName = "Tests\\MajorElementNotFound";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";

		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");

		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);

		try {
			cdaConvertor.extractReports(fileWrite);

			// Read the Textual_report_Test file to verify the results
			BufferedReader reader = new BufferedReader(new FileReader(cdaReport));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
			reader.close();
			fileWrite.close();
			assertTrue(result.toString().contains("No redundancy found between user stories!"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testInvalidDirectoryName() throws EmptyOrNotExistJsonFile {

		String directoryName = "Tests\\Dummy";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		try {
			cdaConvertor.extractReports(fileWrite);
			assertTrue(cdaReport.length() == 0);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (NullPointerException e) {

			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void testInvalidJsonFile() throws EmptyOrNotExistJsonFile, NullPointerException, IOException {
		String directoryName = "Tests\\ExactMatch_testInvalidJsonFile";
		String jsonFileName = "Tests\\dummyJOSN.json";
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");

		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		cdaConvertor.extractReports(fileWrite);
}

	@Test
	public void testEmptyDirectroy() throws EmptyOrNotExistJsonFile {
		String directoryName = "Tests\\EmptyDirectory";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);

		try {
			List<ConflictPair> conflictPairs = cdaConvertor.extractReports(fileWrite);
			assertTrue(conflictPairs.size() == 0);

		} catch (IOException e) {

		} catch (NullPointerException e) {

			e.printStackTrace();
		}
	}

	@Test(expected = EmptyOrNotExistJsonFile.class)
	public void testEmptyJSONFile() throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException {
		String directoryNamed = "Tests\\ExactMatch_testEmptyJSONFile";
		String jsonFileNameds = "Tests\\empty_json_file.json";
		CdaConvertorV2 cdaConv = new CdaConvertorV2(directoryNamed, jsonFileNameds);
		System.out.println("[testEmptyJSONFile] json path is: " + cdaConv.getAbsoluteJsonFileDir());

		File cdaReport = new File(cdaConv.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConv.createOrOverwriteReportFile(cdaReport);

		// This line should throw EmptyJsonFile exception
		cdaConv.extractReports(fileWrite);
	}
	


}