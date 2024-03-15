package org.henshin.backlog.test;

import org.henshin.backlog.code.report.CdaReportDirIsEmpty;
import org.henshin.backlog.code.report.CdaReportDirIsNotADirectory;
import org.henshin.backlog.code.report.CdaReportDirNotFound;
import org.henshin.backlog.code.report.ReportExtractor;
import org.henshin.backlog.code.rule.EmptyOrNotExistJsonFile;
import org.henshin.backlog.code.rule.JsonFileNotFound;
import org.henshin.backlog.code.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReportExtractorTest {

	@Test
	public void testExtractReports_completeMajorElements_edge() throws EmptyOrNotExistJsonFile, NullPointerException,
			CdaReportDirNotFound, JsonFileNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\extractReports\\ExactMatch";
		String jsonFileName = "Tests\\ReportExtractor\\g03_baseline_pos_num.json";

		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
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
	public void testExtractReports_completeMajorElements_upperEdge()
			throws EmptyOrNotExistJsonFile, NullPointerException, CdaReportDirNotFound, JsonFileNotFound,
			CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\extractReports\\ExactMatch_upperEdge";

		String jsonFileName = "Tests\\ReportExtractor\\g03_baseline_pos_num.json";

		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
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
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\extractReports\\MajorElementNotFound";
		String jsonFileName = "Tests\\ReportExtractor\\g03_baseline_pos_num.json";

		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
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
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\Dummy";
		String jsonFileName = "Tests\\ReportExtractor\\g03_baseline_pos_num.json";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
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
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\ExactMatch_testInvalidJsonFile";
		String jsonFileName = "Tests\\ReportExtractor\\dummyJOSN.json";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
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
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\EmptyDirectory";
		String jsonFileName = "dummy";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
		new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
	}

	@Test(expected = EmptyOrNotExistJsonFile.class)
	public void testEmptyJSONFile() throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException,
			CdaReportDirNotFound, CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\ExactMatch_testEmptyJSONFile";
		String jsonFileNameds = "Tests\\ReportExtractor\\empty_json_file.json";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
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

	// check if potential redundant user stories has same Persona, if so, check
	// if persona will be highlighted
	@Test
	public void testExtractReports_highlightPersona() throws NullPointerException, EmptyOrNotExistJsonFile, IOException,
			JSONException, CdaReportDirNotFound, CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_persona_redundancy";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_persona_redundancy\\g05_baseline_pos.json";
		String persona= "Triggers: Link from \"data publishing user\" to \"import\" is found";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
		// System.out.println("[testEmptyJSONFile] json path is: " +
		// cdaConvertor.getAbsoluteFinalReportDir());
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// cdaConvertor.getAbsoluteDirPath());

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
			assertTrue(result.toString().contains(persona)
					&& result.toString().contains("#data publishing user#"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testHighlightConflicts_noBenefitInUs1()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_US1_No_Benefit";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_US1_No_Benefit\\g04_baseline_pos.json";
		String uS1 = "user_story_26: #g04# as an employee from the hr department, i want to #have# "
				+ "#access# to the full information of all employees working for this business.";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
		// System.out.println("[testEmptyJSONFile] json path is: " +
		// cdaConvertor.getAbsoluteFinalReportDir());
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// cdaConvertor.getAbsoluteDirPath());

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

			assertTrue(result.toString().contains(uS1) && !result.toString().contains(uS1 + "so that"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testHighlightConflicts_noBenefitInUs2()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_US2_No_Benefit";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_US2_No_Benefit\\g11_baseline_pos.json";
		String uS2 = " user_story_73: #g11# as a team member, i want to #understand# #how# work"
				+ " moves between ux/content > visual design > front end dev for a sprint cycle.";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
// System.out.println("[testEmptyJSONFile] json path is: " +
// cdaConvertor.getAbsoluteFinalReportDir());
// System.out.println("[testEmptyJSONFile] dir path is: " +
// cdaConvertor.getAbsoluteDirPath());

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

			assertTrue(result.toString().contains(uS2) && !result.toString().contains(uS2 + "so that"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testHighlightConflicts_noBenefitInBoth()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_both_No_Benefit";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_both_No_Benefit\\g04_baseline_pos.json";
		String uS2 = "user_story_10: #g04# as a #user#, i want to be able to #view# a #map display# "
				+ "of the special waste drop off sites around my #area#.";
		String uS1 = "user_story_09: #g04# as a #user#, i want to be able to #view# a "
				+ "#map display# of the public recycling bins around my #area#.";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
// System.out.println("[testEmptyJSONFile] json path is: " +
// cdaConvertor.getAbsoluteFinalReportDir());
// System.out.println("[testEmptyJSONFile] dir path is: " +
// cdaConvertor.getAbsoluteDirPath());

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

			assertTrue(result.toString().contains(uS2) && !result.toString().contains(uS2 + "so that")
					&& result.toString().contains(uS1) && !result.toString().contains(uS1 + "so that"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testHighlightConflicts_BenefitInBoth()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_both_has_Benefit";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_both_has_Benefit\\g14_baseline_pos.json";
		String uS2Main = "user_story_02: #g14# as a #publisher#, i want to #publish# a #dataset#";
		String uS2Benefit = ", so that i can share the dataset publicly with everyone.";
		String uS1Main = "user_story_01: #g14# as a #publisher#, i want to #publish# a #dataset#";
		String uS1Benefit = ", so that i can view just the dataset with a few people.";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
// System.out.println("[testEmptyJSONFile] json path is: " +
// cdaConvertor.getAbsoluteFinalReportDir());
// System.out.println("[testEmptyJSONFile] dir path is: " +
// cdaConvertor.getAbsoluteDirPath());

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

			assertTrue(result.toString().contains(uS1Main) && result.toString().contains(uS1Benefit)
					&& result.toString().contains(uS2Main) && result.toString().contains(uS2Benefit));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Check if number of maximal redundancy clauses are match to the value stored
	// in
	// JSON_Report_Test.json File
	@Test
	public void testExtractReports_getTotalConflictedElements()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\extractReports\\CDA_Report_gTest_both_has_Benefit";
		String jsonFileNameds = "Tests\\ReportExtractor\\extractReports\\CDA_Report_gTest_both_has_Benefit\\g14_baseline_pos.json";

		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
		String jsonReportPath = cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json";

		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(jsonReportPath);
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);

		try {
			cdaConvertor.extractReports(fileWrite, jsonWriter);

			// Read the JSON_report_Test file to verify the results
			JSONArray jsonArray = cdaConvertor.readJsonArrayFromFile(jsonReportPath);
			// System.out.println("[testEmptyJSONFile] dir path is: " +
			// jsonArray.toString());
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			JSONObject jsonStatus = jsonObject.getJSONObject("Status");
			int maximal = jsonStatus.getInt("Total Redundancy Clauses");

			fileWrite.close();
			// check if maximal number of redundancy clause barbells are match
			assertTrue(maximal == 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Check if number of redundancy clauses in Main part of user stories are match
	// to the value stored in
	// JSON_Report_Test.json File
	@Test
	public void testExtractReports_getMainPartConflictedElements()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\extractReports\\CDA_Report_gTest_both_has_Benefit";
		String jsonFileNameds = "Tests\\ReportExtractor\\extractReports\\CDA_Report_gTest_both_has_Benefit\\g14_baseline_pos.json";

		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
		String jsonReportPath = cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json";

		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(jsonReportPath);
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);

		try {
			cdaConvertor.extractReports(fileWrite, jsonWriter);

			// Read the JSON_report_Test file to verify the results
			JSONArray jsonArray = cdaConvertor.readJsonArrayFromFile(jsonReportPath);
			// System.out.println("[testEmptyJSONFile] dir path is: " +
			// jsonArray.toString());
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			JSONObject jsonStatus = jsonObject.getJSONObject("Status");
			int main = jsonStatus.getInt("Main Part Redundancy Clauses");

			fileWrite.close();
			// check if maximal number of redundancy clause barbells are match
			assertTrue(main == 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Check if number of redundancy clauses in Benefit part of user stories are
	// match to the value stored in
	// JSON_Report_Test.json File
	@Test
	public void testExtractReports_getBenefitPartConflictedElements()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\extractReports\\CDA_Report_gTest_Benefit_part_conflicted_Elements";
		String jsonFileNameds = "Tests\\ReportExtractor\\extractReports\\CDA_Report_gTest_Benefit_part_conflicted_Elements\\g05_baseline_pos.json";

		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
		String jsonReportPath = cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json";

		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(jsonReportPath);
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);

		try {
			cdaConvertor.extractReports(fileWrite, jsonWriter);

			// Read the JSON_report_Test file to verify the results
			JSONArray jsonArray = cdaConvertor.readJsonArrayFromFile(jsonReportPath);
			// System.out.println("[testEmptyJSONFile] dir path is: " +
			// jsonArray.toString());
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			JSONObject jsonStatus = jsonObject.getJSONObject("Status");
			int benefit = jsonStatus.getInt("Benefit Part Redundancy Clause");

			fileWrite.close();
			// check if maximal number of redundancy clause barbells are match
			assertTrue(benefit == 4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testHighlightConflicts_ContainInBenefitPart()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Contains_In_Benefit_Part";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Contains_In_Benefit_Part\\g08_baseline_pos.json";
		
		String contains = "Contains: Link between \"validity\" and \"trust\" is found.";
		
		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
		// System.out.println("[testEmptyJSONFile] json path is: " +
		// cdaConvertor.getAbsoluteFinalReportDir());
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// cdaConvertor.getAbsoluteDirPath());

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

			assertTrue(result.toString().contains(contains) 
					&& result.toString().contains("#validity#")
					&& result.toString().contains("#trust#"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Check if contains in included in main part of sentence. If so, then highlighting contains 
	// should be expected
	@Test
	public void testHighlightConflicts_ContainInMainPart()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Contains_In_Main_Part";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Contains_In_Main_Part\\g18_baseline_pos.json";
		
		String contains = "Contains: Link between \"experiment\" and \"log book page\" is found.";
		
		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
		// System.out.println("[testEmptyJSONFile] json path is: " +
		// cdaConvertor.getAbsoluteFinalReportDir());
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// cdaConvertor.getAbsoluteDirPath());

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

			assertTrue(result.toString().contains(contains) 
					&& result.toString().contains("#experiment#")
					&& result.toString().contains("#log book page#"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Check if targets in included in main part of sentence. If so, then highlighting contains 
	// should be expected
	@Test
	public void testHighlightConflicts_TargetsInMainPart()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Targets_In_Main_Part";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Targets_In_Main_Part\\g22_baseline_pos.json";
		
		String us1 = "#g22# as an it staff member, i want to #know# #how# the #data# is #used#, "
				+ "so that i can determine what kind of basic services and functionalities are required.";
		
		String us2 = "#g22# as a data manager, i want to #know# #how# the #data# is #used#, so that i can develop more"
				+ " detailed usage and support scenarios with researchers.";
		
		String targets1 = "Targets: Link from \"know\" to \"how\" is found.";
		String targets2 = "Targets: Link from \"used\" to \"data\" is found.";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
		// System.out.println("[testEmptyJSONFile] json path is: " +
		// cdaConvertor.getAbsoluteFinalReportDir());
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// cdaConvertor.getAbsoluteDirPath());

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

			assertTrue(result.toString().contains(targets1) 
					&& result.toString().contains(targets2)
					&& result.toString().contains("#know#")
					&& result.toString().contains("#how#")
					&& result.toString().contains("#data#")
					&& result.toString().contains("#used#")
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Check if targets in included in main part of sentence. If so, then highlighting contains 
		// should be expected
		@Test
		public void testHighlightConflicts_TargetsInBenefitPart()
				throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
				CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
			String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Targets_In_Main_Part";
			String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Targets_In_Main_Part\\g22_baseline_pos.json";
			
			String us1 = "#g22# as an it staff member, i want to #know# #how# the #data# is #used#, "
					+ "so that i can determine what kind of basic services and functionalities are required.";
			
			String us2 = "#g22# as a data manager, i want to #know# #how# the #data# is #used#, so that i can develop more"
					+ " detailed usage and support scenarios with researchers.";
			
			String targets1 = "Targets: Link from \"know\" to \"how\" is found.";
			String targets2 = "Targets: Link from \"used\" to \"data\" is found.";
			ReportExtractor cdaConvertor = new ReportExtractor(directoryNamed, jsonFileNameds);
			// System.out.println("[testEmptyJSONFile] json path is: " +
			// cdaConvertor.getAbsoluteFinalReportDir());
			// System.out.println("[testEmptyJSONFile] dir path is: " +
			// cdaConvertor.getAbsoluteDirPath());

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

				assertTrue(result.toString().contains(targets1) 
						&& result.toString().contains(targets2)
						&& result.toString().contains("#know#")
						&& result.toString().contains("#how#")
						&& result.toString().contains("#data#")
						&& result.toString().contains("#used#")
						);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
}