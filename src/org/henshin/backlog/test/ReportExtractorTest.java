package org.henshin.backlog.test;

import org.henshin.backlog.code.report.CdaReportDirIsEmpty;
import org.henshin.backlog.code.report.CdaReportDirIsNotADirectory;
import org.henshin.backlog.code.report.CdaReportDirNotFound;
import org.henshin.backlog.code.report.RedundantPair;
import org.henshin.backlog.code.report.ReportExtractor;
import org.henshin.backlog.code.rule.EmptyOrNotExistJsonFile;
import org.henshin.backlog.code.rule.JsonFileNotFound;
import org.henshin.backlog.code.*;
import org.henshin.backlog.code.evaluation.Evaluation;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReportExtractorTest {

	// completeMajorElements\_edge \newline(testExtractReports)&Provision of a CDA
	// report for US-pair containing exactly one \enquote{Targets} edge&Inclusion of
	// this US-pair in the text report with the nodes \enquote{Action},
	// \enquote{Entity} and \enquote{Targets} edge.
	@Test
	public void testExtractReports_completeMajorElements_edge() throws EmptyOrNotExistJsonFile, NullPointerException,
			CdaReportDirNotFound, JsonFileNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty, IOException {
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

	// TODO: Add this to paper
	// completeMajorElements\_edge \newline(testExtractReports)&Provision of a CDA
	// report for US-pair containing exactly one \enquote{Targets} edge&Inclusion of
	// this US-pair in the text report with the nodes \enquote{Action},
	// \enquote{Entity} and \enquote{Targets} edge.
	@Test
	public void testReportExtractor() throws EmptyOrNotExistJsonFile, NullPointerException, CdaReportDirNotFound,
			JsonFileNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty, IOException {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\extractReports\\ExactMatch";
		String jsonFileName = "Tests\\ReportExtractor\\g03_baseline_pos_num.json";

		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json");
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);
		// cdaConvertor.extractReports(fileWrite, jsonWriter);
		String[] dataset = { "03" };
		ReportExtractor.reportExtractor(dataset);
		assertTrue(true);

	}

	@Test(expected = JSONException.class)
	public void testJsonFile() throws EmptyOrNotExistJsonFile, JsonFileNotFound, IOException {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\ExactMatch_testInvalidJsonFile";
		String jsonFileName = "Tests\\ReportExtractor\\dummy_corrupt.json";
		// Read the JSON_report_Test file to verify the results
		ReportExtractor reportExtractor = new ReportExtractor(directoryName, jsonFileName);
		reportExtractor.readJsonArrayFromFile(jsonFileName);
	}

// completeMajorElements\_upperEdge\newline(testExtractReports)&Provide a CDA report for a US-pair that contains at least one \enquote{Targets} edge and other redundancy elements such as \enquote{Triggers} edges&Generated textual report contains information about Secondary Entities, Secondary Actions, Targets, and \enquote{Triggers}&Verifies extractReports method when all major elements are present in the CDA report and the upper edge case is reached\\
	@Test
	public void testExtractReports_completeMajorElements_upperEdge()
			throws EmptyOrNotExistJsonFile, NullPointerException, CdaReportDirNotFound, JsonFileNotFound,
			CdaReportDirIsNotADirectory, CdaReportDirIsEmpty, IOException {
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

// 	notCompleteMajorElements\newline(testExtractReports)&Provide a CDA report for a US-pair without \enquote{Targets} edge, but with action and entity&The US-pair should not reported&Verifies the behavior of the extractReports methodwhen not all major elements are present in the input data\\
	@Test
	public void testExtractReports_notCompleteMajorElements() throws EmptyOrNotExistJsonFile, NullPointerException,
			CdaReportDirNotFound, JsonFileNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty, IOException {
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

	// testEmptyDirectroy&Assing a dummy directory&Through an exception:
	// \textit{CdaReportDirIsEmpty.class}&Check if CDA Report directory is empty\\
	@Test(expected = CdaReportDirIsEmpty.class)
	public void testEmptyDirectroy() throws EmptyOrNotExistJsonFile, CdaReportDirNotFound, NullPointerException,
			IOException, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\EmptyDirectory";
		String jsonFileName = "dummy";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
		new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
	}

	// TODO: Add it to document
	// testCdaDirNotDirectroy&Assing not directory&Through an exception:
	// \textit{CdaReportDirIsEmpty.class}&Check if CDA Report directory is empty\\
	@Test(expected = CdaReportDirIsNotADirectory.class)
	public void testCdaDirNotDirectroy() throws EmptyOrNotExistJsonFile, CdaReportDirNotFound, NullPointerException,
			IOException, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\EmptyDirectory\\Textual_Report_Test.txt";
		String jsonFileName = "dummy";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
		new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
	}

	// TODO: Add it to document
	// testEmptyDirectroy&Assing not directory&Through an exception:
	// \textit{CdaReportDirIsEmpty.class}&Check if CDA Report directory is empty\\
	@Test(expected = CdaReportDirNotFound.class)
	public void testCdaDirectroy() throws EmptyOrNotExistJsonFile, CdaReportDirNotFound, NullPointerException,
			IOException, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\NotAccessibleDirectory";
		String jsonFileName = "dummy";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
		new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
	}

	// TODO: Add it to document
	// Check if final reports directory all null or not
	@Test
	public void testFinalReportDir() throws EmptyOrNotExistJsonFile, CdaReportDirNotFound, NullPointerException,
			IOException, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\NotAccessibleDirectory";
		String jsonFileName = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_persona_redundancy\\g05_baseline_pos.json";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\Final_Reports\\");
		Path pathDummy = Paths.get("dummy");
		String report = cdaConvertor.getFinalReportDir(path);
		String reportDummy = cdaConvertor.getFinalReportDir(pathDummy);
		// System.out.println("path: "+ path);
		// System.out.println("report: "+report);
		assertEquals(report, path.toString());
		assertNull(reportDummy);
	}

	// TODO: Add it to document
	// check minimalEcore are exist
	@Test
	public void testMinimalEcoreExist() throws EmptyOrNotExistJsonFile, CdaReportDirNotFound, NullPointerException,
			IOException, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\ExactMatch_testEmptyJSONFile";
		String jsonFileName = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_persona_redundancy\\g05_baseline_pos.json";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\Final_Reports\\");

		boolean eCoreExist = cdaConvertor.minimalEcoreExist("dummy", "dummy");

		// System.out.println("path: "+ path);
		// System.out.println("report: "+report);
		assertFalse(eCoreExist);

	}

	// TODO: Add it to document
	// check minimalEcore are exist
	@Test
	public void testWriteTable() throws EmptyOrNotExistJsonFile, CdaReportDirNotFound, NullPointerException,
			IOException, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty, JsonFileNotFound {
		String directoryName = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\writeTable\\ExactMatch";
		String jsonFileName = "Tests\\ReportExtractor\\writeTable\\ExactMatch\\g03_baseline_pos.json";
		ReportExtractor cdaConvertor = new ReportExtractor(directoryName, jsonFileName);

		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_Test.txt");
		FileWriter fileWrite = cdaConvertor.createOrOverwriteReportFile(cdaReport);
		File jsonReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\JSON_Report_Test.json");
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);

		// This line should throw EmptyJsonFile exception
		List<RedundantPair> listConflictPairs = cdaConvertor.extractReports(fileWrite, jsonWriter);
		cdaConvertor.writeTable(cdaReport, listConflictPairs);
		// System.out.println("path: "+ path);
		// System.out.println("report: "+report);
		try {

			// Read the Textual_report_Test file to verify the results
			BufferedReader reader = new BufferedReader(new FileReader(cdaReport));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
			reader.close();
			fileWrite.close();

			assertTrue(result.toString().contains("Table of potential redundancies"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// testEmptyJSONFile&Assing an empty JSON dataset file&Through an exception:
	// \textit{EmptyOrNotExistJsonFile.class}&Check if JSON dataset file is empty\\
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
		// String[] dataset= {"empty_json_file"};
		// ReportExtractor.reportExtractor(dataset);
	}

	// highlightPersona\newline(testExtractReports)&Providing a CDA report for a
	// US-pair with redundancy clause in \enquote{Triggers}edge (from Persona to
	// Primary Action)&The persona should only be marked with a hash symbol if there
	// is a redundant clause in the main part&Checks the behaviour of the
	// extractReports method when highlighting redundant personas in USs\\
	// check if potential redundant user stories has same Persona, if so, check
	// if persona will be highlighted
	@Test
	public void testExtractReports_highlightPersona() throws NullPointerException, EmptyOrNotExistJsonFile, IOException,
			JSONException, CdaReportDirNotFound, CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_persona_redundancy";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_persona_redundancy\\g05_baseline_pos.json";
		String persona = "Triggers: Link from \"data publishing user\" to \"import\" is found";
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
			assertTrue(result.toString().contains(persona) && result.toString().contains("#data publishing user#"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

// noBenefitInUs1\newline(testhigHlightRedundancies)&Provision of a CDA report for a US-pair where only the first US do have the \enquote{benefit} part&Search only the main part of USs and mark redundancy clauses with a hash symbol&Verifies the behaviour of the \textit{highlightRedundancies} method when only the first US have benefit\\
	@Test
	public void testHighlightRedundancies_noBenefitInUs1()
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

// noBenefitInUs2\newline(testhigHlightRedundancies)&Provision of a CDA report for a US-pair where only the first US have the \enquote{benefit} part&Search only the main part of USs and mark redundancy clauses with a hash symbol&Verifies the behaviour of the \textit{highlightRedundancies} method when only the first US have benefit\\
	@Test
	public void testHighlightRedundancies_noBenefitInUs2()
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

// noBenefitInBothUss\newline(testhigHlightRedundancies)&Provision of a CDA report for a US-pair where both USs do not have the \enquote{benefit} part&Search only the main part of USs and mark redundancy clauses with a hash symbol if they occur&Verifies the behaviour of the \textit{highlightRedundancies} method when both USs don't have benefits\\
	@Test
	public void testHighlightRedundancies_noBenefitInBothUss()
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

	// BenefitInBothUss (testHighlightConflicts)&Provision of a CDA report for a
	// US-pair where both USs have \enquote{benefit} segment&Generated textual
	// report contains both USs with their respective \enquote{main} parts and
	// \enquote{benefits}&Verifies the behaviour of the \textit{highlightConflicts}
	// method when both USs have benefits\\
	@Test
	public void testHighlightRedundancies_BenefitInBothUss()
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

	// BenefitInBothUss (testHighlightConflicts)&Provision of a CDA report for a
	// US-pair where both USs have \enquote{benefit} segment&Generated textual
	// report contains both USs with their respective \enquote{main} parts and
	// \enquote{benefits}&Verifies the behaviour of the \textit{highlightConflicts}
	// method when both USs have benefits\\
	@Test
	public void testHighlightRedundancies_TextsLenghtsNull()
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
	// getTotalRedundanciesElements\newline(testExtractReports)&Provision of a CDA
	// report for a US-pair that contains redundancy clauses in the
	// \enquote{Main}and \enquote{Benefit} parts&Check whether the count of
	// redundancy clauses in the main and benefit parts of the USs matches the value
	// \enquote{Total Redundancy Clause} specified in the JSON\_Report file&Verifies
	// the behaviour of the extractReports method when there are redundancy elements
	// in the main and benefit parts of USs\\
	@Test
	public void testExtractReports_getTotalRedundancyElements()
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
	// getMainPartRedundanciesElements\newline(testExtractReports)&Provision of a
	// CDA report for a US-pair that only contains redundancy clauses in the
	// \enquote{Main} part&Check whether the count of redundancy clauses in the main
	// part of the USs matches the value \enquote{Main Part Redundancy Clause}
	// specified in the JSON\_Report file&Verifies the behaviour of the
	// extractReports method when there are redundancy elements only in the main
	// parts of USs\\
	@Test
	public void testExtractReports_getMainPartRedundanciesElements()
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

	// getBenefitPartRedundanciesElements\newline(testExtractReports)&Provision of a
	// CDA report for a US-pair that only contains redundancy clauses in the
	// \enquote{benefit} segment&Check whether the count of redundancy clauses in
	// the benefit part of the USs matches the value \enquote{Benefit Part
	// Redundancy Clause} specified in the
	// JSON\_Report file&Verifies the behaviour of the extractReports method when
	// there are redundancy elements in the benefit parts of USs\\
	@Test
	public void testExtractReports_getBenefitPartRedundanciesElements()
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

// ContainInBenefitPart\newline(testHighlightRedundancies)&Provide a CDA report for a US-pair with redundancy clauses of \enquote{Contains} within \enquote{Benefit} part&The entities included in Contains should be marked with hash symbol&Checks the behaviour of the \textit{highlightRedundancies} method when highlighting redundant personas in USs\\
	@Test
	public void testHighlightRedundancies_ContainInBenefitPart()
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

			assertTrue(result.toString().contains(contains) && result.toString().contains("#validity#")
					&& result.toString().contains("#trust#"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Check if contains in included in main part of sentence. If so, then
	// highlighting contains
	// should be expected
	// ContainInMainPart\newline(testHighlightRedundancies)&Provide a CDA report for
	// a US-pair with redundancy clauses of \enquote{Contains} within \enquote{Main}
	// part&The entities included in Contains should be marked with hash
	// symbol&Checks the behaviour of the \textit{highlightRedundancies} method when
	// highlighting redundant entities included in the Contains\\
	@Test
	public void testHighlightRedundancies_ContainInMainPart()
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

			assertTrue(result.toString().contains(contains) && result.toString().contains("#experiment#")
					&& result.toString().contains("#log book page#"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Check if more than one targets in main part of sentence will be highlighted
	// as expected
	@Test
	public void testHighlightRedundancies_TargetsInMainPart()
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

			assertTrue(result.toString().contains(targets1) && result.toString().contains(targets2)
					&& result.toString().contains("#know#") && result.toString().contains("#how#")
					&& result.toString().contains("#data#") && result.toString().contains("#used#"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Check if more than one targets is included in benefit part of sentence. If
	// so, then highlighting clauses accordingly
	// should be expected
	@Test
	public void testHighlightRedundancies_TargetsInBenefitPart()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, CdaReportDirNotFound,
			CdaReportDirIsNotADirectory, JsonFileNotFound, CdaReportDirIsEmpty {
		String directoryNamed = "eclipse-workspace_new\\org.henshin.backlog2\\Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Targets_In_Benefit_Part";
		String jsonFileNameds = "Tests\\ReportExtractor\\highlightConflicts\\CDA_Report_gTest_Targets_In_Benefit_Part\\g22_baseline_pos.json";

		String us1 = "#g22# as a data manager, i want to know which formats are used, so that i #know# what #technology#"
				+ " or #background information# might be necessary to use the data.";

		String us2 = "#g22# as a data manager, i want to know with which software or technology the data is produced or used, so that i #know# what #technology# "
				+ "or #background information# might be necessary to (re-)use the data.";

		String targets1 = "Targets: Link from \"know\" to \"background information\" is found.";
		String targets2 = "Targets: Link from \"know\" to \"technology\" is found.";
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

			assertTrue(result.toString().contains(targets1) && result.toString().contains(targets2)
					&& result.toString().contains("#know#") && result.toString().contains("#technology#")
					&& result.toString().contains("#background information#"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}