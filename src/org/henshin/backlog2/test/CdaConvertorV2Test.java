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
	public void testExtractReports_completeMajorElements_edge() throws EmptyOrNotExistJsonFile, NullPointerException,
			CdaReportDirNotFound, JsonFileNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ExactMatch";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";

		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json");
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);

		try {
			cdaConvertor.extractReports(fileWrite, jsonWriter);

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
					&& result.toString().contains("Targets: Link from \"review\" to \"compliance\" is found."));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testExtractReports_completeMajorElements_upperEdge() throws EmptyOrNotExistJsonFile,
			NullPointerException, CdaReportDirNotFound, JsonFileNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ExactMatch_upperEdge";

		String jsonFileName = "Tests\\g03_baseline_pos_num.json";

		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json");
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);

		try {
			cdaConvertor.extractReports(fileWrite, jsonWriter);

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
					&& result.toString().contains("Targets: Link from \"ensure\" to \"compliance\" is found.")
					&& result.toString().contains("Triggers: Link from \"staff member\" to \"manage\" is found"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testExtractReports_notCompleteMajorElements() throws EmptyOrNotExistJsonFile, NullPointerException,
			CdaReportDirNotFound, JsonFileNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\MajorElementNotFound";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";

		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json");
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);
		try {
			cdaConvertor.extractReports(fileWrite, jsonWriter);

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

	@Test(expected = CdaReportDirNotFound.class)
	public void testInvalidDirectoryName() throws EmptyOrNotExistJsonFile, CdaReportDirNotFound, JsonFileNotFound,
			CdaReportDirIsNotADirectory, NullPointerException, IOException, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\Dummy";
		String jsonFileName = "Tests\\g03_baseline_pos_num.json";
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json");
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);
		cdaConvertor.extractReports(fileWrite, jsonWriter);

	}

	// Check if JSON file is already exist
	@Test(expected = JsonFileNotFound.class)
	public void testInvalidJsonFile() throws JsonFileNotFound, NullPointerException, IOException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, EmptyOrNotExistJsonFile, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ExactMatch_testInvalidJsonFile";
		String jsonFileName = "Tests\\dummyJOSN.json";
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		System.out.println("[testEmptyJSONFile] json path is: " + cdaConvertor.getAbsoluteFinalReportDir());
		System.out.println("[testEmptyJSONFile] dir path is: " + cdaConvertor.getAbsoluteDirPath());
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json");
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);
		cdaConvertor.extractReports(fileWrite, jsonWriter);

	}

	// Check if CDA Report directory is empty
	@Test(expected = CdaReportDirIsEmpty.class)
	public void testEmptyDirectroy() throws EmptyOrNotExistJsonFile, CdaReportDirNotFound, NullPointerException,
			IOException, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\EmptyDirectory";
		String jsonFileName = "dummy";
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryName, jsonFileName);
		new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
	}

	@Test(expected = EmptyOrNotExistJsonFile.class)
	public void testEmptyJSONFile() throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException,
			CdaReportDirNotFound, CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ExactMatch_testEmptyJSONFile";
		String jsonFileNameds = "Tests\\empty_json_file.json";
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2(directoryNamed, jsonFileNameds);
		// System.out.println("[testEmptyJSONFile] json path is: " +
		// cdaConvertor.getAbsoluteFinalReportDir());
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// cdaConvertor.getAbsoluteDirPath());

		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json");
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);

		// This line should throw EmptyJsonFile exception
		cdaConvertor.extractReports(fileWrite, jsonWriter);
	}

}