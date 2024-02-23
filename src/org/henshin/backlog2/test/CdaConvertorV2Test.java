package org.henshin.backlog2.test;

import org.henshin.backlog2.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class CdaConvertorV2Test {

	/*
	 * Test Case: Successful Extraction Scenario: Provide a directory with valid
	 * files and a valid JSON file. Expected Outcome: The method should successfully
	 * extract reports without any errors.
	 * 
	 */

	@Test
	public void testExtractReports_completeMajorElements() {
		// Arrange: Prepare the test data
		String directoryName = "Tests\\ExactMatch";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";

		// Act: Call the method you want to test
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getPath() + "\\Textual_Report_Test.txt");

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
	public void testExtractReports_notCompleteMajorElements() {
		// Arrange: Prepare the test data
		String directoryName = "Tests\\MajorElementNotFound";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";

		// Act: Call the method you want to test
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getPath() + "\\Textual_Report_Test.txt");

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
	public void testInvalidDirectoryName() {
		// Arrange: Prepare the test data

		String directoryName = "Tests\\Dummy";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		try {
			cdaConvertor.extractReports(fileWrite);
			assertTrue(cdaReport.length() == 0);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}