package org.henshin.backlog.code.evaluation;

import java.io.FileReader;
import java.io.IOException;

import org.henshin.backlog.code.rule.EmptyOrNotExistJsonFile;
import org.henshin.backlog.code.rule.JsonFileNotFound;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Evaluation {

	public Evaluation() {

	}

	// - two user stories are fully redundant("Full Redundant in Main Part" : true):
	// if in Main Part: if JSONArray "Triggers" is not empty or null.
	// if "All Targets/Contains in Main Part" of at lease one of to USs are equal to
	// "Common Targets/Contains in Main Part"
//	   Otherwise, "Full Redundant in Main Part" : false and
	// "Partially Redundant in Main Part" : true.
	public void evaluateRedundancyCriteria(JSONObject jsonData, String us1, String us2) {

		JSONObject allTargets = jsonData.getJSONObject("All Targets");
		JSONObject allContains = jsonData.getJSONObject("All Contains");
		JSONArray triggers = jsonData.getJSONArray("Triggers");

		JSONObject commonTargets = jsonData.getJSONObject("Common Targets");
		JSONObject commonContains = jsonData.getJSONObject("Common Contains");

		// Extract data for both user stories in all targets
		JSONObject targetsAllUs1Data = allTargets.getJSONObject(us1);
		JSONObject targetsAllUs2Data = allTargets.getJSONObject(us2);

		// Get Main and Benefit parts in All targets
		JSONArray targetsAllUs1MainPart = targetsAllUs1Data.getJSONArray("Main Part");
		JSONArray targetsAllUs1BenefitPart = getJSONArraySafely(targetsAllUs1Data, "Benefit Part");

		JSONArray targetsAllUs2MainPart = targetsAllUs2Data.getJSONArray("Main Part");
		JSONArray targetsAllUs2BenefitPart = getJSONArraySafely(targetsAllUs2Data, "Benefit Part");

		// Get Main and Benefit Parts in Common Targets
		JSONArray targetsCommonMainPart = commonTargets.getJSONArray("Main Part");
		// JSONArray targetsCommonBenefitPart = commonTargets.getJSONArray("Benefit
		// Part");
		JSONArray targetsCommonBenefitPart = getJSONArraySafely(commonTargets, "Benefit Part");
		// Extract data for both user stories in All contains
		JSONObject containsAllUs1Data = allContains.getJSONObject(us1);
		JSONObject containsAllUs2Data = allContains.getJSONObject(us2);

		// Get Main and Benefit parts in All contains
		JSONArray containsAllUs1MainPart = containsAllUs1Data.getJSONArray("Main Part");
		JSONArray containsAllUs1BenefitPart = getJSONArraySafely(containsAllUs1Data, "Benefit Part");
		JSONArray containsAllUs2MainPart = containsAllUs2Data.getJSONArray("Main Part");
		JSONArray containsAllUs2BenefitPart = getJSONArraySafely(containsAllUs2Data, "Benefit Part");

		// Get main and benefit parts in common Contains
		JSONArray containsCommonMainPart = commonContains.getJSONArray("Main Part");
		JSONArray containsCommonBenefitPart = getJSONArraySafely(commonContains, "Benefit Part");

		// Check Full redundancy
		// Check targets, Contains and Trigger in main part, if they are equal

		boolean isTriggersRedundant = triggers.isEmpty();
		boolean isTargetsMainFullyRedundant;
		boolean isContainsMainFullyRedundant;
//		System.out.println(redPair);
		// if there is no targets in common Main part and one of user stories don't have
		// target elements

		// Targets in Main
		if ((targetsCommonMainPart.length() == 0
				&& (targetsAllUs1MainPart.length() != 0 && targetsAllUs2MainPart.length() == 0))
				|| (targetsCommonMainPart.length() == 0
						&& (targetsAllUs1MainPart.length() == 0 && targetsAllUs2MainPart.length() != 0))) {
			isTargetsMainFullyRedundant = true;
			// if there is no targets in common Main part and one
		} else if (targetsAllUs1MainPart.length() == 0 && targetsAllUs2MainPart.length() == 0) {
			isTargetsMainFullyRedundant = true;
		} else {
			isTargetsMainFullyRedundant = checkFullRedaundat(targetsAllUs1MainPart, targetsCommonMainPart)
					|| checkFullRedaundat(targetsAllUs2MainPart, targetsCommonMainPart);
		}
		// Contains in Main
		if ((containsCommonMainPart.length() == 0
				&& (containsAllUs1MainPart.length() == 0 && containsAllUs2MainPart.length() != 0))
				|| (containsCommonMainPart.length() == 0
						&& (containsAllUs1MainPart.length() != 0 && containsAllUs2MainPart.length() == 0))) {
			isContainsMainFullyRedundant = true;
		} else if (containsAllUs1MainPart.length() == 0 && containsAllUs2MainPart.length() == 0) {
			isContainsMainFullyRedundant = true;
		} else {
			isContainsMainFullyRedundant = checkFullRedaundat(containsAllUs1MainPart, containsCommonMainPart)
					|| checkFullRedaundat(containsAllUs2MainPart, containsCommonMainPart);
		}

//		System.out.println("isTargetsMainRedundant "+ isTargetsMainFullyRedundant);
//		System.out.println("isContainsMainRedundant "+isContainsMainFullyRedundant);
		boolean isMainFullRedundant = isTargetsMainFullyRedundant && isContainsMainFullyRedundant
				&& !isTriggersRedundant && (targetsCommonMainPart.length() != 0 || containsCommonMainPart.length() != 0)
				&& ((targetsAllUs1MainPart.length() >= targetsAllUs2MainPart.length()
						&& containsAllUs1MainPart.length() >= containsAllUs2MainPart.length())
						|| (targetsAllUs2MainPart.length() >= targetsAllUs1MainPart.length()
								&& containsAllUs2MainPart.length() >= containsAllUs1MainPart.length()));

		// Check targets, Contains and Trigger in benefit part, if they are equal
		boolean isBenefitFullRedundant = false;
		// if there is no common contains but one the USs has contains, it means there
		// is no full
		// redundant in contains benefit
		// System.out.println(redPair);
		boolean isTargetsBenefitRedundant;
////		System.out.println("targetsAllUs1BenefitPart: " + targetsAllUs1BenefitPart.length());
//		System.out.println("targetsAllUs2BenefitPart: " + targetsAllUs2BenefitPart.length());
//		System.out.println("containsAllUs1BenefitPart: " + containsAllUs1BenefitPart.length());
//		System.out.println("containsAllUs2BenefitPart: " + containsAllUs2BenefitPart.length());
		// Targets in Benefit
		if ((targetsCommonBenefitPart.length() == 0
				&& (targetsAllUs1BenefitPart.length() != 0 && targetsAllUs2BenefitPart.length() == 0))
				|| (targetsCommonBenefitPart.length() == 0
						&& (targetsAllUs1BenefitPart.length() == 0 && targetsAllUs2BenefitPart.length() != 0))) {
			isTargetsBenefitRedundant = true;
//			System.out.println("isTargetsBenefitRedundant_if: " + isTargetsBenefitRedundant);
		} else if (targetsAllUs1BenefitPart.length() == 0 && targetsAllUs2BenefitPart.length() == 0) {
			isTargetsBenefitRedundant = true;
//			System.out.println("isTargetsBenefitRedundant_esle_if: " + isTargetsBenefitRedundant);
		} else {
			isTargetsBenefitRedundant = checkFullRedaundat(targetsAllUs1BenefitPart, targetsCommonBenefitPart)
					|| checkFullRedaundat(targetsAllUs2BenefitPart, targetsCommonBenefitPart);
//			System.out.println("isTargetsBenefitRedundant_else: " + isTargetsBenefitRedundant);
		}
		boolean isContainsBenefitRedundant;

		// Contains in Benefit
		if ((containsCommonBenefitPart.length() == 0
				&& (containsAllUs1BenefitPart.length() == 0 && containsAllUs2BenefitPart.length() != 0))
				|| (containsCommonBenefitPart.length() == 0
						&& (containsAllUs1BenefitPart.length() != 0 && containsAllUs2BenefitPart.length() == 0))) {
			isContainsBenefitRedundant = true;
//			System.out.println("isContainsBenefitRedundant_if: " + isContainsBenefitRedundant);
		} else if (containsAllUs1BenefitPart.length() == 0 && containsAllUs2BenefitPart.length() == 0) {
			isContainsBenefitRedundant = true;
//			System.out.println("isContainsBenefitRedundant_else_if: " + isContainsBenefitRedundant);

		} else {
			isContainsBenefitRedundant = (checkFullRedaundat(containsAllUs1BenefitPart, containsCommonBenefitPart)
					|| checkFullRedaundat(containsAllUs2BenefitPart, containsCommonBenefitPart));
//			System.out.println("isContainsBenefitRedundant_else: " + isContainsBenefitRedundant);
		}

		// System.out.println("isContainsBenefitRedundant" +
		// isContainsBenefitRedundant);
		// benefit part is full redundant if all elements of contains and targets are
		// redundant

		// Assume: if US_X contains US_Y then all elements(in targets "AND" contains)
		// in US_Y should be equal or less than elements(in targets "AND" contains) and
		// vice versa
		isBenefitFullRedundant = isTargetsBenefitRedundant && isContainsBenefitRedundant
				&& (targetsCommonBenefitPart.length() != 0 || containsCommonBenefitPart.length() != 0)
				&& ((targetsAllUs1BenefitPart.length() >= targetsAllUs2BenefitPart.length()
						&& containsAllUs1BenefitPart.length() >= containsAllUs2BenefitPart.length())
						|| (targetsAllUs2BenefitPart.length() >= targetsAllUs1BenefitPart.length()
								&& containsAllUs2BenefitPart.length() >= containsAllUs1BenefitPart.length()));

		// Evaluate partial redundancy
		boolean isMainPartiallyRedundant = false;
		if (isMainFullRedundant || (targetsAllUs1MainPart.length()==0 && targetsAllUs2MainPart.length()==0
				&& containsAllUs1MainPart.length() == 0 && containsAllUs2MainPart.length() == 0)) {
			isMainPartiallyRedundant = false;
		} else {
			isMainPartiallyRedundant = checkPartialRedundancy(targetsAllUs1MainPart, targetsCommonMainPart)
					|| checkPartialRedundancy(targetsAllUs2MainPart, targetsCommonMainPart)
					|| checkPartialRedundancy(containsAllUs1MainPart, containsCommonMainPart)
					|| checkPartialRedundancy(containsAllUs2MainPart, containsCommonMainPart);

		}
		boolean isBenefitPartiallyRedundant = false;
		if (isBenefitFullRedundant || (targetsAllUs1BenefitPart.isEmpty() && targetsAllUs2BenefitPart.isEmpty()
				&& containsAllUs1BenefitPart.isEmpty() && containsAllUs2BenefitPart.isEmpty())) {
			isBenefitPartiallyRedundant = false;
		} else {
			isBenefitPartiallyRedundant = checkPartialRedundancy(targetsAllUs1BenefitPart, targetsCommonBenefitPart)
					|| checkPartialRedundancy(targetsAllUs2BenefitPart, targetsCommonBenefitPart)
					|| checkPartialRedundancy(containsAllUs1BenefitPart, containsCommonBenefitPart)
					|| checkPartialRedundancy(containsAllUs2BenefitPart, containsCommonBenefitPart);

		}
		// Add redundancy status to the JSON object
		JSONObject status = jsonData.getJSONObject("Status");
		status.put("Main Part Fully Redundant", isMainFullRedundant);
		status.put("Main Part Partially Redundant", isMainPartiallyRedundant);
		status.put("Benefit Part Fully Redundant", isBenefitFullRedundant);
		status.put("Benefit Part Partially Redundant", isBenefitPartiallyRedundant);
	}

	private JSONArray getJSONArraySafely(JSONObject jsonObject, String key) {
		return jsonObject.has(key) ? jsonObject.getJSONArray(key) : new JSONArray();
	}

	// checking partial redundancy by counting matches between the elements
	// of the given JSONArrays and updating the JSON object with both full
	// and partial redundancy statuses.
	private boolean checkPartialRedundancy(JSONArray part1, JSONArray part2) {
		int matchCount = 0;

		if (part1.length() > 0 && part2.length() > 0) {
			for (int i = 0; i < part1.length(); i++) {
				JSONArray jsonArrayI = part1.getJSONArray(i);
				String elementI1 = jsonArrayI.getString(0);
				String elementI2 = jsonArrayI.getString(1);
				for (int j = 0; j < part2.length(); j++) {
					JSONArray jsonArrayJ = part2.getJSONArray(j);
					String elementJ1 = jsonArrayJ.getString(0);
					String elementJ2 = jsonArrayJ.getString(1);
					if (elementI1.equals(elementJ1) && elementI2.equals(elementJ2)) {
						matchCount++;
						break;
					}
				}
			}
		}
		return matchCount > 0;
	}

	// contains/targets are full redundant if their length and their elements are
	// are the same
	private boolean checkFullRedaundat(JSONArray allElements, JSONArray commonElements) {
		if (allElements.length() != commonElements.length()) {
			return false;
		}
//		System.out.println("allElements: " + allElements);
//		System.out.println("commonElements: " + commonElements);
		int length = allElements.length();
		int count = 0;
		for (int i = 0; i < allElements.length(); i++) {
			JSONArray jsonArrayAll = allElements.getJSONArray(i);

			for (int j = 0; j < commonElements.length(); j++) {
				JSONArray jsonArrayCommon = commonElements.getJSONArray(j);
//				System.out.println("second Loop_jsonArrayAll: "+ jsonArrayAll);
//				System.out.println("second Loop_jsonArrayCommon: "+jsonArrayCommon);
				if (jsonArrayAll.toString().equalsIgnoreCase(jsonArrayCommon.toString())) {
//					System.out.println("second Loop_inside_if_jsonArrayAll: "+jsonArrayAll);
//					System.out.println("second Loop_inside_if_jsonArrayCommon: "+jsonArrayCommon);
					count++;
//					System.out.println("count: " + count);
					break;
				}
			}

		}
		return length == count;
	}

	public JSONArray readJsonArrayFromFile(String path) throws EmptyOrNotExistJsonFile, JsonFileNotFound, IOException {
		JSONArray jsonArray;
		try (FileReader reader = new FileReader(path)) {
			JSONTokener tokener = new JSONTokener(reader);
			if (!tokener.more()) {
				throw new EmptyOrNotExistJsonFile();

			}
			// Read JSON file
			jsonArray = new JSONArray(tokener);

			return jsonArray;
		} catch (IOException e) {
			throw e;
		}
	}

}
