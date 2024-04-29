package org.henshin.backlog.test;

import static org.junit.Assert.assertTrue;
import java.io.FileReader;
import java.io.IOException;

import org.henshin.backlog.code.evaluation.Evaluation;
import org.henshin.backlog.code.rule.EmptyOrNotExistJsonFile;
import org.henshin.backlog.code.rule.JsonFileNotFound;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

public class EvaluationTest {

	@Test(expected = EmptyOrNotExistJsonFile.class)
	public void testEmptyOrNotExistJsonFile() throws EmptyOrNotExistJsonFile, JsonFileNotFound, IOException {
		String jsonReportPath = "Tests\\Evaluation\\Dummy_JSON_File\\dummy_empty.json";
		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		evaluation.readJsonArrayFromFile(jsonReportPath);

	}

	@Test(expected = IOException.class)
	public void testJsonFile() throws EmptyOrNotExistJsonFile, JsonFileNotFound, IOException {
		String jsonReportPath = "Tests\\Evaluation\\Dummy_JSON_File\\dummy_corrupt1.json";
		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		evaluation.readJsonArrayFromFile(jsonReportPath);
	}

	// Benefit partially redundant
	// commonContains: null
	@Test
	public void testEvaluateRedundancyCriteria_benefitParial()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Benefit_Partially_Redundant\\Case1\\JSON_Report_g03.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_12", "user_story_39");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Benefit Part Partially Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

	// Benefit fully redundant
	// commonContains: null
	@Test
	public void testEvaluateRedundancyCriteria_benefitFull_Case1()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Benefit_Fully_Redundant\\Case1\\JSON_Report_g04.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_05", "user_story_12");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Benefit Part Fully Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

	// Benefit fully redundant
	// commonContains: not null
	// allTargets: null but AllTargets US1 not null
	@Test
	public void testEvaluateRedundancyCriteria_benefitFull_Case2()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Benefit_Fully_Redundant\\Case2\\JSON_Report_g04.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_05", "user_story_12");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Benefit Part Fully Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

	// Benefit fully redundant
	// commonContains: not null
	// AllTargets US1/US2 not null
	@Test
	public void testEvaluateRedundancyCriteria_benefitFull_Case3()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Benefit_Fully_Redundant\\Case3\\JSON_Report_g04.json";
		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_05", "user_story_12");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Benefit Part Fully Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

	// Benefit fully redundant
	// commonContains: not null
	// allTargets: not null
	@Test
	public void testEvaluateRedundancyCriteria_benefitFull_Case4()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Benefit_Fully_Redundant\\Case4\\JSON_Report_g04.json";
		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_05", "user_story_12");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Benefit Part Fully Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

// Main partially redundant	
// commonContains and commonTargets not null
	@Test
	public void testEvaluateRedundancyCriteria_mainPartial_Case1()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Partially_Redundant\\Case1\\JSON_Report_g04.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_09", "user_story_10");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Main Part Partially Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

	// Main partially redundant
	// commonContains and commonTargets not null
	@Test
	public void testEvaluateRedundancyCriteria_mainPartial_Case2()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Partially_Redundant\\Case2\\JSON_Report_g04.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_09", "user_story_10");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Main Part Partially Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

	// Main even not partially redundant
	// commonContains and commonTargets are null
	// allContainsUs1/Us2 and allTargetsUs1/Us2 are null
	@Test
	public void testEvaluateRedundancyCriteria_mainNotRedundant()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Not_Redundant\\Case1\\JSON_Report_g04.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_09", "user_story_10");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Main Part Partially Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == false);
	}

	// Main fully redundant
	// commonContains: null
	@Test
	public void testEvaluateRedundancyCriteria_mainFull_Case1()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Fully_Redundant\\Case1\\JSON_Report_g14.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_01", "user_story_02");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Main Part Fully Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

	// Main fully redundant
	// All Targets_Us1 is not null but commonTargets is null
	// Common Contains are identical
	@Test
	public void testEvaluateRedundancyCriteria_mainFull_Case2()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Fully_Redundant\\Case2\\JSON_Report_g14.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_01", "user_story_02");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Main Part Fully Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

	// Main fully redundant
	// commonContains is identical
	// allTargets US1 & US2 are null
	@Test
	public void testEvaluateRedundancyCriteria_mainFull_Case3()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Fully_Redundant\\Case3\\JSON_Report_g14.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_01", "user_story_02");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Main Part Fully Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}
	// Main fully redundant
	// commonContains is identical
	// allContainsUs2 Fully redundant
	// allTargets US1 & US2 are null
	@Test
	public void testEvaluateRedundancyCriteria_mainFull_Case4()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Fully_Redundant\\Case4\\JSON_Report_g14.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_01", "user_story_02");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean status = jsonStatus.getBoolean("Main Part Fully Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(status == true);
	}

	// Main fully redundant
	// commonContains: null
	@Test
	public void testEvaluateRedundancyCriteria_mainBenefitFull()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Benefit_Fully_Redundant\\Case1\\JSON_Report_g14.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_22", "user_story_24");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean statusMain = jsonStatus.getBoolean("Main Part Fully Redundant");
		boolean statusBenefit = jsonStatus.getBoolean("Benefit Part Fully Redundant");
		boolean statusMainPartial = jsonStatus.getBoolean("Main Part Partially Redundant");
		boolean statusBenefitPartial = jsonStatus.getBoolean("Main Part Partially Redundant");
		// check if maximal number of redundancy clause barbells are match
		assertTrue(statusMain == true && statusBenefit == true && statusBenefitPartial == false
				&& statusMainPartial == false);
	}

	// Main fully redundant
	// Benefit partially redundant
	@Test
	public void testEvaluateRedundancyCriteria_mainPartialBenefitFull()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Partial_Benefit_Full\\Case1\\JSON_Report_g05.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_09", "user_story_11");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean statusMain = jsonStatus.getBoolean("Main Part Partially Redundant");
		boolean statusBenefit = jsonStatus.getBoolean("Benefit Part Fully Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(statusMain == true && statusBenefit == true);
	}

	// Main partially redundant
	// Benefit fully redundant
	@Test
	public void testEvaluateRedundancyCriteria_mainFullBenefitPartial()
			throws NullPointerException, EmptyOrNotExistJsonFile, IOException, JSONException, JsonFileNotFound {
		String jsonReportPath = "Tests\\Evaluation\\Main_Full_Benefit_Partial\\Case1\\JSON_Report_g14.json";

		// Read the JSON_report_Test file to verify the results
		Evaluation evaluation = new Evaluation();
		JSONArray jsonArray = evaluation.readJsonArrayFromFile(jsonReportPath);
		// System.out.println("[testEmptyJSONFile] dir path is: " +
		// jsonArray.toString());

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		evaluation.evaluateRedundancyCriteria(jsonObject, "user_story_22", "user_story_24");
		JSONObject jsonStatus = jsonObject.getJSONObject("Status");
		boolean statusMain = jsonStatus.getBoolean("Main Part Fully Redundant");
		boolean statusBenefit = jsonStatus.getBoolean("Benefit Part Partially Redundant");

		// check if maximal number of redundancy clause barbells are match
		assertTrue(statusMain == true && statusBenefit == true);
	}
}
