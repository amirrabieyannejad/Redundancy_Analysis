package org.henshin.backlog.code.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.eclipse.core.internal.resources.Resource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.henshin.backlog.code.evaluation.Evaluation;
import org.henshin.backlog.code.rule.ConfigLoader;
//import org.graalvm.compiler.hotspot.nodes.PatchReturnAddressNode;
import org.henshin.backlog.code.rule.EmptyOrNotExistJsonFile;
import org.henshin.backlog.code.rule.JsonFileNotFound;
import org.henshin.backlog.code.rule.TextInJsonFileNotFound;
import org.henshin.backlog.code.rule.UsNrInJsonFileNotFound;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Set;
import java.util.HashSet;

/**
 * Report maximal overlap if and only if in Minimal Models at least Entity,
 * Action and Triggers exist. With Table summary
 * 
 * @author Amir Rabieyan Nejad
 *
 */
public class ReportExtractor {
	private static String jsonDatasetFile;
	private String cdaReportDirName;

	public ReportExtractor(String directroyName, String jsonFileName) {
		cdaReportDirName = directroyName;
		jsonDatasetFile = jsonFileName;
	}

	public static void main(String[] args) throws IOException, NullPointerException, EmptyOrNotExistJsonFile,
			CdaReportDirNotFound, JsonFileNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {

		String[] datasets = ConfigLoader.getInstance().getProjectIds();
		reportExtractor(datasets);
	}

	public static void reportExtractor(String[] datasets)
			throws IOException, NullPointerException, EmptyOrNotExistJsonFile, CdaReportDirNotFound, JsonFileNotFound,
			CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
		// make path for output reports
		String baseDirectory = ConfigLoader.getInstance().getBaseDirectory();
		String cdaReportsDirectory = ConfigLoader.getInstance().getCdaReportsDirectroy();
		Path path = Paths.get(baseDirectory, "\\Final_Reports\\");

		for (String dataset : datasets) {
			System.out.println("Dataset: " + dataset);
			long startTime = System.nanoTime();
			ReportExtractor reportExtractor = new ReportExtractor(
					cdaReportsDirectory + "\\CDA_Report_backlog_g" + dataset, baseDirectory
							+ "\\Final_Reports\\Textual_Report_g" + dataset + "\\g" + dataset + "_baseline_pos.json");

			// Create text file in order to report to user a readable format
			File cdaReport = new File(reportExtractor.getFinalReportDir(path) + "\\Textual_Report_g" + dataset
					+ "\\Textual_Report_g" + dataset + ".txt");
			FileWriter fileWriter = reportExtractor.createOrOverwriteReportFile(cdaReport);

			// Create JSON File in order to have systematic overview of result
			File jsonReport = new File(reportExtractor.getFinalReportDir(path) + "\\Textual_Report_g" + dataset
					+ "\\JSON_Report_g" + dataset + ".json");
			FileWriter jsonWriter = reportExtractor.createOrOverwriteReportFile(jsonReport);
			List<RedundantPair> listConflictPairs = reportExtractor.extractReports(fileWriter, jsonWriter);
			reportExtractor.writeTable(cdaReport, listConflictPairs);
			long endTime = System.nanoTime();
			double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
			System.out.println("Processing time: " + elapsedTimeInSeconds + " seconds");
		}

	}

	private String getCdaReportsDir() {
		return cdaReportDirName;
	}

	private String getJsonDatasetFile() {
		return jsonDatasetFile;
	}

	private String getAbsoluteDirPath()
			throws CdaReportDirNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty, IOException {
		// get CDA report
		Path path = Paths.get(getCdaReportsDir());
		if (Files.exists(path)) {
			if (Files.isDirectory(path)) { // Check if it's a directory
				DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
				boolean isEmpty = true;
				boolean hasSubDirectories = false;

				for (Path entry : directoryStream) {
					if (Files.isDirectory(entry)) {
						hasSubDirectories = true;
					}
					isEmpty = false;
				}

				// if (isEmpty) {
				// throw new CdaReportDirIsEmpty("CDA Report Directory is empty!");
				// }

				if (!hasSubDirectories) {
					throw new CdaReportDirIsEmpty("CDA Report Directory found but doesn't have any subdirectories!");
				} else {
					return path.toString();
				}

			} else {
				throw new CdaReportDirIsNotADirectory();
			}
		} else {
			throw new CdaReportDirNotFound();
		}
	}

	public String getAbsoluteFinalReportDir() throws JsonFileNotFound, IOException {
		Path path = Paths.get(getJsonDatasetFile());
		if (Files.exists(path)) {

			return path.toString();
		} else {
			throw new JsonFileNotFound();
		}
	}

	public String getFinalReportDir(Path path) {

		if (Files.exists(path)) {

//			System.out.println(path.toString());
			return path.toString();
		} else {
			return null;
		}
	}

	// Create or overwrite report file which return/pass the FileWriter object
	public FileWriter createOrOverwriteReportFile(File totalCda) throws IOException {
		FileWriter cdaWriter = null;
		if (totalCda.createNewFile()) {
			// System.out.println("File created succesfully: " + totalCda.getName());
			cdaWriter = new FileWriter(totalCda);
			return cdaWriter;

		} else {
			cdaWriter = new FileWriter(totalCda);
			// System.out.println("File already exists. Try to overwrite..!");
			return cdaWriter;
		}
	}

	public List<RedundantPair> extractReports(FileWriter fileWriter, FileWriter jsonWriter)
			throws IOException, NullPointerException, EmptyOrNotExistJsonFile, CdaReportDirNotFound, JsonFileNotFound,
			CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {

		// create JSON array in order to contains all redundant pairs and their
		JSONArray jsonArray = new JSONArray();

		RedundancyItems redundancyItems;
		List<RedundantPair> redundantPairs;
		ArrayList<String> arrayTotalElements;
		ArrayList<String> arrayTotalElementsNames;
		List<String> pairList = new ArrayList<>();
		File main = new File(getAbsoluteDirPath());
		// Instantiate redundantPairs
		redundantPairs = new ArrayList<>();
		// Iterate through critical pairs
		if (main.exists() && main.list().length != 0) {
			for (String redPair : main.list()) {
				if (!checkIfReportExist(redPair, pairList) && containsAnd(redPair)) {
					File conflictReasonDir = new File(getAbsoluteDirPath() + "\\" + redPair);
					if (!conflictReasonDir.isFile()) {
						JSONObject jsonRedundancyPair = new JSONObject();
						arrayTotalElementsNames = new ArrayList<>();
						arrayTotalElements = new ArrayList<>();
						redundancyItems = new RedundancyItems();
						String[] conflictReasonListing = conflictReasonDir.list();
						// Iterate through conflict reasons if there is more than one conflict_reason.
						if (conflictReasonListing.length > 1) {
							for (String conflictReason : conflictReasonListing) {
								// Check if minimal ECore exist
								if (minimalEcoreExist(redPair, conflictReason)) {
									File minimalModelEcoreFile = new File(getAbsoluteDirPath() + "\\" + redPair + "\\"
											+ conflictReason + "\\minimal-model.ecore");

									processMinimalModels(minimalModelEcoreFile, arrayTotalElements,
											arrayTotalElementsNames, redundancyItems);

								}
							}
						}
						// Write only the Elements which contains Primary/Secondary Action and
						// Primary/Secondary Entity
						if (hasEntitys(arrayTotalElementsNames, redundancyItems)
								&& hasActions(arrayTotalElementsNames, redundancyItems)
								&& hasTargets(arrayTotalElementsNames, redundancyItems)) {
							fileWriter.write("\n------------------[Potential Redundant User"
									+ " Stories found]--------------------------\n{" + redPair + "}\n  ");
							jsonRedundancyPair.put("Potential Redundant User Stories", redPair);

							fileWriter.write("\nRedundant clauses within user stories are: ");
							List<TargetsPair> targetsPairs = getCommonTargets(redPair, jsonRedundancyPair);
							List<ContainsPair> containsPairs = getCommonContains(redPair, jsonRedundancyPair);
							List<TriggersPair> triggersPairs = getCommonTriggers(redPair);
							redundancyItems.printRedundantItems(fileWriter, targetsPairs, containsPairs, triggersPairs,
									jsonRedundancyPair);

							// receive text of user stories as input
							// in order to highlight the redundancy clauses in each text
							writeUsText(redPair, arrayTotalElementsNames, redundantPairs, redundancyItems, fileWriter,
									targetsPairs, triggersPairs, containsPairs, jsonRedundancyPair);

							// add JSON Object into main JSON array
							JSONObject jsonConflictStatus = getRedundancyStatus(redundancyItems);

							// put all in Status as subPart of "Status"
							jsonRedundancyPair.put("Status", jsonConflictStatus);

							if (redundancyItems.getTextUs1() != null && redundancyItems.getTextUs2() != null) {
								fileWriter.write("\n\nThe following sentence parts are" + " candidates for possible"
										+ " redundancies between user stories:\n\n");

								// here we should write USsSentenceParts
								writeUsSentencePart(redPair, redundancyItems, fileWriter, jsonRedundancyPair);

								// Try to evaluate, if main or benefit part are fully/partially redundant
								String us1 = getUsName1(redPair);
								String us2 = getUsName2(redPair);
								Evaluation evaluation = new Evaluation();
								evaluation.evaluateRedundancyCriteria(jsonRedundancyPair, us1, us2);
								// Add JSONObject Texts of two user story and store them into Text JSONObject
								JSONObject jsonText = new JSONObject();
								jsonText.put("First UserStory",
										getUsName1(redPair) + ": " + redundancyItems.getTextUs1());
								jsonText.put("Second UserStory",
										getUsName2(redPair) + ": " + redundancyItems.getTextUs2());
								jsonRedundancyPair.put("Text", jsonText);

								// select project Number and save it to JSONFile
								String projectNr = redundancyItems.getTextUs1().replaceAll(".*#(g\\d\\d)#.*", "$1");
								jsonRedundancyPair.put("Project Number", projectNr);
							}

							// add JSON Object into main JSON array
							jsonArray.put(jsonRedundancyPair);

						}

					}
				}
			}

			if (redundantPairs.size() == 0) {
				fileWriter.write("No redundancy found between user stories!");

			}
			jsonWriter.write(jsonArray.toString(4));
			jsonWriter.close();
			fileWriter.close();

		}
		return redundantPairs;
	}

	// Add Status Elements(Main/Benefit/Total Part Conflicted Elements) into JSON
	// data
	private JSONObject getRedundancyStatus(RedundancyItems redundancyItems) {
		JSONObject jsonRedundancyStatus = new JSONObject();

		// add observed conflicted pairs in Main part sentence
		jsonRedundancyStatus.put("Main Part Redundancy Clauses", redundancyItems.getMainRedundancyCount());

		// add observed conflicted pairs in Benefit part sentence
		jsonRedundancyStatus.put("Benefit Part Redundancy Clause", redundancyItems.getBenefitRedundancyCount());

		// add observed total conflicted pairs
		jsonRedundancyStatus.put("Total Redundancy Clauses", redundancyItems.getTotalRedundancyCount());
		return jsonRedundancyStatus;
	}

	// receive a conflict Pair and a String which corresponds to "Targets" array
	// Object in JSON file
	private List<TargetsPair> getCommonTargets(String redPair, JSONObject jsonRedundancyPair)
			throws EmptyOrNotExistJsonFile, JsonFileNotFound, IOException {
		String us1 = getUsName1(redPair);
		String us2 = getUsName2(redPair);
		List<TargetsPair> targetPairs = new ArrayList<>();
		JSONArray us1TargetsArray = null;
		JSONArray us2TargetsArray = null;
		JSONArray jsonTargetsMainUs1 = new JSONArray();
		JSONArray jsonTargetsBenefitUs1 = new JSONArray();
		JSONArray jsonTargetsMainUs2 = new JSONArray();
		JSONArray jsonTargetsBenefitUs2 = new JSONArray();
		String us1Text = null;
		String us2Text = null;
		JSONArray jsonArray = readJsonArrayFromFile(getAbsoluteFinalReportDir());

		// Check if there US_Nr and Targets Objects exists in JOSN file
		// if so return the ArrayObjects of Targets
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject.has("US_Nr") && jsonObject.has("Targets")) {
				String usNr = jsonObject.getString("US_Nr");
				if (usNr.equals(us1)) {
					us1TargetsArray = jsonObject.getJSONArray("Targets");
					us1Text = jsonObject.getString("Text").toLowerCase();

				} else if (usNr.equals(us2)) {
					us2TargetsArray = jsonObject.getJSONArray("Targets");
					us2Text = jsonObject.getString("Text").toLowerCase();

				}
			}
		}
		// check if TargetArrays of both USs not null then find the Common
		// Targets Pairs and add it to TargetPairs List
		if (us2TargetsArray != null && us1TargetsArray != null) {
			for (int i = 0; i < us1TargetsArray.length(); i++) {
				JSONArray jsonArrayUs1 = us1TargetsArray.getJSONArray(i);
				String actionUs1 = jsonArrayUs1.getString(0).toLowerCase();
				String entityUs1 = jsonArrayUs1.getString(1).toLowerCase();
				JSONArray mainTargetsUs1 = new JSONArray();
				mainTargetsUs1 = getAllTargetsInMain(actionUs1, entityUs1, us1Text, jsonRedundancyPair);
				if (mainTargetsUs1 != null) {
					jsonTargetsMainUs1.put(mainTargetsUs1);
				}
				JSONArray benefitTargetsUs1 = new JSONArray();
				benefitTargetsUs1 = getAllTargetsInBenefit(actionUs1, entityUs1, us1Text, jsonRedundancyPair);
				if (benefitTargetsUs1 != null) {
					jsonTargetsBenefitUs1.put(benefitTargetsUs1);
				}
				for (int j = 0; j < us2TargetsArray.length(); j++) {
					JSONArray jsonArrayUs2 = us2TargetsArray.getJSONArray(j);
					String actionUs2 = jsonArrayUs2.getString(0).toLowerCase();
					String enttiyUs2 = jsonArrayUs2.getString(1).toLowerCase();
					if (enttiyUs2.equalsIgnoreCase(entityUs1) && actionUs2.equalsIgnoreCase(actionUs1.toLowerCase())) {
						targetPairs.add(new TargetsPair(actionUs1, entityUs1));
						break;
					}
				}
			}
			// iterate through other US and gather all Targets in there
			for (int j = 0; j < us2TargetsArray.length(); j++) {
				JSONArray jsonArrayUs2 = us2TargetsArray.getJSONArray(j);
				String actionUs2 = jsonArrayUs2.getString(0).toLowerCase();
				String enttiyUs2 = jsonArrayUs2.getString(1).toLowerCase();
				JSONArray mainTargetsUs2 = new JSONArray();
				mainTargetsUs2 = getAllTargetsInMain(actionUs2, enttiyUs2, us2Text, jsonRedundancyPair);
				if (mainTargetsUs2 != null) {
					jsonTargetsMainUs2.put(mainTargetsUs2);
				}

				JSONArray benefitTargetsUs2 = new JSONArray();
				benefitTargetsUs2 = getAllTargetsInBenefit(actionUs2, enttiyUs2, us2Text, jsonRedundancyPair);
				if (benefitTargetsUs2 != null) {
					jsonTargetsBenefitUs2.put(benefitTargetsUs2);
				}
			}

		}
		JSONObject jsonAllTargets = new JSONObject();

		JSONObject jsonObjectAllTargetsUS1 = new JSONObject();
		JSONObject jsonObjectAllTargetsUS2 = new JSONObject();

		JSONObject jsonAllTargetsUS2Main = new JSONObject();
		JSONObject jsonAllTargetsUS2Benefit = new JSONObject();

		jsonObjectAllTargetsUS1.put("Main Part", jsonTargetsMainUs1);
		jsonObjectAllTargetsUS1.put("Benefit Part", jsonTargetsBenefitUs1);

		jsonAllTargetsUS2Main.put("Main Part", jsonTargetsMainUs2);
		jsonAllTargetsUS2Benefit.put("Benefit Part", jsonTargetsBenefitUs2);

		jsonObjectAllTargetsUS2.put("Main Part", jsonTargetsMainUs2);
		jsonObjectAllTargetsUS2.put("Benefit Part", jsonTargetsBenefitUs2);

		jsonAllTargets.put(us1, jsonObjectAllTargetsUS1);
		jsonAllTargets.put(us2, jsonObjectAllTargetsUS2);
		jsonRedundancyPair.put("All Targets", jsonAllTargets);

		return targetPairs;
	}

	private JSONArray getAllTargetsInBenefit(String action, String entity, String usText,
			JSONObject jsonRedundancyPair) {
		int benefitIndex = usText.indexOf("so that");
		JSONArray jsonBenefit = new JSONArray();
		String benefitPart = "";
		if (benefitIndex != -1) {
			benefitPart = usText.substring(benefitIndex);
			jsonBenefit = processElements(action, entity, benefitPart, jsonRedundancyPair);
			return jsonBenefit;
		}

		return null;

	}

	private JSONArray getAllTargetsInMain(String action, String entity, String usText, JSONObject jsonRedundancyPair) {
		int benefitIndex = usText.indexOf("so that");
		String mainPart = usText;
		JSONArray jsonMain = new JSONArray();
		if (benefitIndex != -1) {

			mainPart = usText.substring(0, benefitIndex);
		}
		jsonMain = processElements(action, entity, mainPart, jsonRedundancyPair);
		return jsonMain;

	}

	// receive a conflict Pair and a String which corresponds to "Triggers" array
	// Object in JSON file
	private List<TriggersPair> getCommonTriggers(String redPair)
			throws EmptyOrNotExistJsonFile, JsonFileNotFound, IOException {
		String us1 = getUsName1(redPair);
		String us2 = getUsName2(redPair);
		List<TriggersPair> triggersPairs = new ArrayList<>();

		JSONArray us1TriggersArray = null;
		JSONArray us2TriggersArray = null;
		JSONArray jsonArray = readJsonArrayFromFile(getAbsoluteFinalReportDir());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject.has("US_Nr") && jsonObject.has("Triggers")) {
				String usNr = jsonObject.getString("US_Nr");
				if (usNr.equals(us1)) {
					us1TriggersArray = jsonObject.getJSONArray("Triggers");

				} else if (usNr.equals(us2)) {
					us2TriggersArray = jsonObject.getJSONArray("Triggers");

				}
			}
		}
		if (us2TriggersArray != null && us1TriggersArray != null) {
			// first check if entity in Triggers is also exist in common Targets

			for (int i = 0; i < us1TriggersArray.length(); i++) {
				JSONArray jsonArrayUs1 = us1TriggersArray.getJSONArray(i);
				String personaUs1 = jsonArrayUs1.getString(0).toLowerCase();
				String actionUs1 = jsonArrayUs1.getString(1).toLowerCase();
				for (int j = 0; j < us2TriggersArray.length(); j++) {
					JSONArray jsonArrayUs2 = us2TriggersArray.getJSONArray(j);
					String personaUs2 = jsonArrayUs2.getString(0).toLowerCase();
					String actionUs2 = jsonArrayUs2.getString(1).toLowerCase();
					// check if both action and persona in triggers from USs
					// are the same
					if (actionUs2.equalsIgnoreCase(actionUs1) && personaUs2.equalsIgnoreCase(personaUs1)) {
						triggersPairs.add(new TriggersPair(personaUs1, actionUs1));
						break;
						// }
//						}
					}
				}
			}

		}
		return triggersPairs;
	}

	// receive a conflict Pair and a String which corresponds to "Contains" array
	// Object in JSON file
	private List<ContainsPair> getCommonContains(String redPair, JSONObject jsonRedundancyPair)
			throws EmptyOrNotExistJsonFile, JsonFileNotFound, IOException {
		String us1 = getUsName1(redPair);
		String us2 = getUsName2(redPair);
		List<ContainsPair> containsPairs = new ArrayList<>();
		JSONArray us1ContainsArray = null;
		JSONArray us2ContainsArray = null;
		JSONArray jsonContainsMainUs1 = new JSONArray();
		JSONArray jsonContainsBenefitUs1 = new JSONArray();
		JSONArray jsonContainsMainUs2 = new JSONArray();
		JSONArray jsonContainsBenefitUs2 = new JSONArray();
		String us1Text = null;
		String us2Text = null;
		JSONArray jsonArray = readJsonArrayFromFile(getAbsoluteFinalReportDir());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject.has("US_Nr") && jsonObject.has("Contains")) {
				String usNr = jsonObject.getString("US_Nr");
				if (usNr.equals(us1)) {
					us1ContainsArray = jsonObject.getJSONArray("Contains");
					us1Text = jsonObject.getString("Text").toLowerCase();

				} else if (usNr.equals(us2)) {
					us2ContainsArray = jsonObject.getJSONArray("Contains");
					us2Text = jsonObject.getString("Text").toLowerCase();
				}
			}
		}
		if (us2ContainsArray != null && us1ContainsArray != null) {
			for (int i = 0; i < us1ContainsArray.length(); i++) {
				JSONArray jsonArrayUs1 = us1ContainsArray.getJSONArray(i);
				String parentUs1 = jsonArrayUs1.getString(0).toLowerCase();
				String childUs1 = jsonArrayUs1.getString(1).toLowerCase();
				// add each founded element into JSON report according
				// to their occurrence into main or benefit part
				JSONArray mainContainsUs1 = new JSONArray();
				mainContainsUs1 = getAllContainsInMain(parentUs1, childUs1, us1Text, jsonRedundancyPair);
				if (mainContainsUs1 != null) {
					jsonContainsMainUs1.put(mainContainsUs1);
				}
				JSONArray benefitContainsUs1 = new JSONArray();
				benefitContainsUs1 = getAllContainsInBenefit(parentUs1, childUs1, us1Text, jsonRedundancyPair);
				if (benefitContainsUs1 != null) {
					jsonContainsBenefitUs1.put(benefitContainsUs1);
				}
				for (int j = 0; j < us2ContainsArray.length(); j++) {
					JSONArray jsonArrayUs2 = us2ContainsArray.getJSONArray(j);
					String parentUs2 = jsonArrayUs2.getString(0).toLowerCase();
					String childUs2 = jsonArrayUs2.getString(1).toLowerCase();
					if (childUs2.equalsIgnoreCase(childUs1) && parentUs2.equalsIgnoreCase(parentUs1.toLowerCase())) {
						containsPairs.add(new ContainsPair(parentUs1, childUs1));
					}
				}
			}
			// iterate through other US and gather all contains in there
			for (int j = 0; j < us2ContainsArray.length(); j++) {
				JSONArray jsonArrayUs2 = us2ContainsArray.getJSONArray(j);
				String parentUs2 = jsonArrayUs2.getString(0).toLowerCase();
				String childUs2 = jsonArrayUs2.getString(1).toLowerCase();
				JSONArray mainContainsUs2 = new JSONArray();
				mainContainsUs2 = getAllContainsInMain(parentUs2, childUs2, us2Text, jsonRedundancyPair);
				if (mainContainsUs2 != null) {
					jsonContainsMainUs2.put(mainContainsUs2);
				}

				JSONArray benefitContainsUs2 = new JSONArray();
				benefitContainsUs2 = getAllContainsInBenefit(parentUs2, childUs2, us2Text, jsonRedundancyPair);
				if (benefitContainsUs2 != null) {
					jsonContainsBenefitUs2.put(benefitContainsUs2);
				}
			}

		}

		JSONObject jsonAllContains = new JSONObject();

		JSONObject jsonObjectAllContainsUS1 = new JSONObject();
		JSONObject jsonObjectAllContainsUS2 = new JSONObject();

		JSONObject jsonAllContainsUS2Main = new JSONObject();
		JSONObject jsonAllContainsUS2Benefit = new JSONObject();

		jsonObjectAllContainsUS1.put("Main Part", jsonContainsMainUs1);
		jsonObjectAllContainsUS1.put("Benefit Part", jsonContainsBenefitUs1);

		jsonAllContainsUS2Main.put("Main Part", jsonContainsMainUs2);
		jsonAllContainsUS2Benefit.put("Benefit Part", jsonContainsBenefitUs2);

		jsonObjectAllContainsUS2.put("Main Part", jsonContainsMainUs2);
		jsonObjectAllContainsUS2.put("Benefit Part", jsonContainsBenefitUs2);

		jsonAllContains.put(us1, jsonObjectAllContainsUS1);
		jsonAllContains.put(us2, jsonObjectAllContainsUS2);
		jsonRedundancyPair.put("All Contains", jsonAllContains);

		return containsPairs;
	}

	private JSONArray getAllContainsInBenefit(String parentUs, String childUs, String usText,
			JSONObject jsonRedundancyPair) {
		int benefitIndex = usText.indexOf("so that");
		JSONArray jsonBenefit = new JSONArray();
		String benefitPart = "";
		if (benefitIndex != -1) {
			benefitPart = usText.substring(benefitIndex);
			jsonBenefit = processElements(parentUs, childUs, benefitPart, jsonRedundancyPair);
			return jsonBenefit;
		}

		return null;

	}

	private JSONArray getAllContainsInMain(String parentUs, String childUs, String usText,
			JSONObject jsonRedundancyPair) {
		int benefitIndex = usText.indexOf("so that");
		String mainPart = usText;
		JSONArray jsonMain = new JSONArray();
		if (benefitIndex != -1) {
			mainPart = usText.substring(0, benefitIndex);
		}
		jsonMain = processElements(parentUs, childUs, mainPart, jsonRedundancyPair);
		return jsonMain;

	}

	private JSONArray processElements(String parentUs, String childUs, String UsPart, JSONObject jsonRedundancyPair) {
		if (UsPart.contains(parentUs) && UsPart.contains(childUs)) {
			JSONArray contain = new JSONArray().put(parentUs).put(childUs);
			return contain;
		}
		return null;
	}

	public boolean minimalEcoreExist(String redPair, String conflictReason)
			throws CdaReportDirNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty, IOException {
		Path path = Paths.get(getAbsoluteDirPath() + "\\" + redPair + "\\" + conflictReason + "\\minimal-model.ecore");
		if (Files.exists(path)) {
			return true;
		} else {
			return false;
		}

	}

//	This method make sure that folder name is identified
//	with \textit{\_AND\_} due to the fact that through CDA 
//	generated report is formatted like \enquote{user\_story\_\textless 
//		digit \textgreater\_AND\_user\_story\_\textless
	private boolean containsAnd(String folder) {
		if (folder.toLowerCase().contains("and")) {
			return true;
		} else {
			return false;
		}
	}

	private void processMinimalModels(File minimalModelEcoreFile, ArrayList<String> arrayMaximalElements,
			ArrayList<String> arrayMaximalElementsNames, RedundancyItems redundancyItems) throws IOException {
		if (minimalModelEcoreFile.exists() && minimalModelEcoreFile.length() > 0) {
			Resource.Factory.Registry resourceFactoryRegistry = Resource.Factory.Registry.INSTANCE;
			resourceFactoryRegistry.getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.getResource(URI.createFileURI(minimalModelEcoreFile.getAbsolutePath()),
					true);
			if (resource != null && !resource.getContents().isEmpty()) {

				for (EObject eObject : resource.getContents()) {
					EPackage minimalPackage = (EPackage) eObject;

					iteratePackages(minimalPackage, arrayMaximalElements, arrayMaximalElementsNames, redundancyItems);

				}
			}
		}
	}

	private String getUsName1(String us) {
		return us.replaceAll("(.*)_AND.*", "$1");
	}

	private String getUsName2(String us) {
		return us.replaceAll(".*_AND_(.*)", "$1");
	}

	// Receive user stories texts with highlighted elements and
	// return only the part/region of sentences which highlighted elements is
	// appears
	private void writeUsSentencePart(String string, RedundancyItems redundancyItems, FileWriter fileWriter,
			JSONObject jsonObject) throws IOException {
		String usNum1 = getUsName1(string);
		String usNum2 = getUsName2(string);
		String usText1 = redundancyItems.getTextUs1();
		String usText2 = redundancyItems.getTextUs2();
		splitUsText(usText1, usNum1, usText2, usNum2, fileWriter, jsonObject);

	}

	private void splitUsText(String usText1, String usNum1, String usText2, String usNum2, FileWriter fileWriter,
			JSONObject jsonObject) throws IOException {
		String[] parts1 = usText1.split(",", 3);
		String[] parts2 = usText2.split(",", 3);
		// show only part of sentences which include more that two hash symbol pairs
		String regex = "#[^#]+#";
		Set<String> writtenUserStoryPart1 = new HashSet<>();
		Set<String> writtenUserStoryPart2 = new HashSet<>();
		Pattern pattern = Pattern.compile(regex);
		// Initialize JOSN Object as Parts of Sentence
		JSONObject jsonPartOfSentence = new JSONObject();

		// Create separate user story section part
		for (String part : parts1) {
			Matcher matcher = pattern.matcher(part);
			int count = 0;
			while (matcher.find()) {
				count++;
				if (count >= 2 && !writtenUserStoryPart1.contains(part)) {
					fileWriter.write(usNum1 + ": " + part + "\n");
					// Write sentence Part first user story on sentence part A
					jsonPartOfSentence.put("First UserStory", new JSONArray().put(part));
					writtenUserStoryPart1.add(part);
				}
			}
		}

		// Create separate user story section part
		for (String part : parts2) {
			Matcher matcher = pattern.matcher(part);
			int count = 0;
			while (matcher.find()) {
				count++;
				if (count >= 2 && !writtenUserStoryPart2.contains(part)) {
					fileWriter.write(usNum2 + ": " + part + "\n");
					// Write sentence Part first user story on sentence part A
					jsonPartOfSentence.put("Second UserStory", new JSONArray().put(part));
					writtenUserStoryPart2.add(part);
				}
			}
		}
		jsonObject.put("Part of Sentence", jsonPartOfSentence);

	}

	public JSONArray readJsonArrayFromFile(String path) throws EmptyOrNotExistJsonFile, JsonFileNotFound, IOException {
		JSONArray jsonArray;
		FileReader reader = new FileReader(path);
		JSONTokener tokener = new JSONTokener(reader);
		if (!tokener.more()) {
			throw new EmptyOrNotExistJsonFile();

		}
		// Read JSON file
		jsonArray = new JSONArray(tokener);

		return jsonArray;

	}

	// in order to insert the table at the very first place of the file
	// we need to first store the report at StringBuilder and then make
	// file again but at this time writing the table first
	public void writeTable(File totalCda, List<RedundantPair> redundantPairs) throws IOException {

		List<String> pairListSeperate = new ArrayList<>();
		StringBuilder report = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(totalCda));
		String line;
		while ((line = reader.readLine()) != null) {
			report.append(line).append("\n");
		}
		reader.close();
		FileWriter writer = new FileWriter(totalCda);

		// display the table
		StringBuilder table = new StringBuilder();
		// add new line before the table
		table.append("* Table of potential redundancies between user stories"
				+ " and the number of their overlapping elements\n\n");
		// table.append("\t");
		for (RedundantPair redundantPair : redundantPairs) {
			if (!pairListSeperate.contains(redundantPair.getRedundantPair1())) {
				pairListSeperate.add(redundantPair.getRedundantPair1());
			}
			if (!pairListSeperate.contains(redundantPair.getRedundantPair2())) {
				pairListSeperate.add(redundantPair.getRedundantPair2());
			}
		}
		String[][] stringTable = createTable(pairListSeperate, redundantPairs);
		int numCols = stringTable[0].length;

		// find the maximum width for each column
		int[] maxWidths = new int[numCols];
		for (String[] row : stringTable) {
			for (int j = 0; j < numCols; j++) {
				maxWidths[j] = Math.max(maxWidths[j], row[j].length());
			}
		}
		for (String[] row : stringTable) {
			for (int j = 0; j < numCols; j++) {

				table.append(String.format("%-" + (maxWidths[j] + 2) + "s", row[j]));
			}
			table.append("\n");
		}
		writer.write(table.toString());
		writer.write(report.toString());
		writer.close();

	}

	private String[][] createTable(List<String> pairListSeperate2, List<RedundantPair> redundantPairs2) {
		int size = pairListSeperate2.size();
		String[][] table = new String[size + 1][size + 1];

		table[0][0] = "";
		for (int i = 0; i < size; i++) {
			table[0][i + 1] = pairListSeperate2.get(i).replaceAll("user_story", "us"); // header row
			table[i + 1][0] = pairListSeperate2.get(i).replaceAll("user_story", "us"); // first column
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				String pair1 = pairListSeperate2.get(i);
				String pair2 = pairListSeperate2.get(j);
				int maximal = getTotalRedundanciesFromPair(redundantPairs2, pair1, pair2);
				table[i + 1][j + 1] = String.valueOf(maximal);
			}

		}
		return table;
	}

	private int getTotalRedundanciesFromPair(List<RedundantPair> redundantPairs, String pair1, String pair2) {
		for (RedundantPair redundantPair : redundantPairs) {
			if ((redundantPair.getRedundantPair1().equals(pair1) && redundantPair.getRedundantPair2().equals(pair2))
					|| (redundantPair.getRedundantPair1().equals(pair2)
							&& redundantPair.getRedundantPair2().equals(pair1))) {
				return redundantPair.getMaximal();
			}
		}

		return 0;
	}

	// check if critical pair list already contain of
	// whether user_story_XX_AND_user_story_YY
	// or user_story_YY_AND_user_story_XX, if yes return true,
	// Otherwise return false
	private boolean checkIfReportExist(String usPairs, List<String> pairList) {
		if (!pairList.contains(usPairs)) {
			String us1 = getUsName1(usPairs);
			String us2 = getUsName2(usPairs);
			pairList.add(usPairs);
			// Add also reverse item
			pairList.add(us2 + "_AND_" + us1);

			return false;
		} else {

			return true;
		}
	}

	private boolean hasTargets(ArrayList<String> arrayMaximalElementsNames, RedundancyItems redundancyItems) {
		for (String item : arrayMaximalElementsNames) {
			for (Targets target : redundancyItems.getTargets()) {
				if (item.equals(target.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasActions(ArrayList<String> arrayMaximalElementsNames, RedundancyItems redundancyItems) {
		for (String item : arrayMaximalElementsNames) {
			for (SecondaryAction secondaryAction : redundancyItems.getSecondaryAction()) {
				if (item.equals(secondaryAction.getName())) {
					return true;
				}
			}
			for (PrimaryAction primaryAction : redundancyItems.getPrimaryActions()) {
				if (item.equals(primaryAction.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasEntitys(ArrayList<String> arrayMaximalElementsNames, RedundancyItems redundancyItems) {
		for (String item : arrayMaximalElementsNames) {
			for (SecondaryEntity secondaryEntity : redundancyItems.getSecondaryEntity()) {
				if (item.equals(secondaryEntity.getName())) {
					return true;
				}
			}
			for (PrimaryEntity primaryEntity : redundancyItems.getPrimaryEntity()) {
				if (item.equals(primaryEntity.getName())) {
					return true;
				}
			}
		}

		return false;
	}

	// get USs Text from JSON File and add them into redundancyItems
	private void getUssTexts(String usPair, RedundancyItems redundancyItems)
			throws JsonFileNotFound, IOException, EmptyOrNotExistJsonFile {
		JSONArray json = null;
		String us1 = getUsName1(usPair);
		String us2 = getUsName2(usPair);

		json = readJsonArrayFromFile(getAbsoluteFinalReportDir());
		for (int i = 0; i < json.length(); i++) {
			JSONObject jsonObject = json.getJSONObject(i);

			// check if two related object are exist in JSON file
			// if so

			String usNr = jsonObject.getString("US_Nr");
			// all words should be lower case to avoid mismatching
			String usText = jsonObject.getString("Text").toLowerCase();
			if (usNr.equals(us1)) {
				redundancyItems.setTextUs1(usText);

			} else if (usNr.equals(us2)) {

				redundancyItems.setTextUs2(usText);

			}

		}

	}

	private void writeUsText(String usPair, ArrayList<String> arrayMax, List<RedundantPair> redundantPairs,
			RedundancyItems redundancyItems, FileWriter fileWriter, List<TargetsPair> targetsPairs,
			List<TriggersPair> triggersPairs, List<ContainsPair> containsPairs, JSONObject jsonRedundancyPair)
			throws IOException, EmptyOrNotExistJsonFile, JsonFileNotFound {
		String us1 = getUsName1(usPair);
		String us2 = getUsName2(usPair);

		// add conflict pairs with maximal overlapping and save it
		// in order to filling table
		RedundantPair redundantPair = new RedundantPair();

		// here i try to add US_Nr from USs into the highlightConflicts
		redundancyItems.setUsNr1(us1);
		redundancyItems.setUsNr2(us2);

		// get USs Text from JSON File and add them into redundancyItems
		getUssTexts(usPair, redundancyItems);

		// here I want to send both USs as parameter for highlightingConflicts
		redundancyItems = highlightRedundancies(redundancyItems, usPair, targetsPairs, triggersPairs, containsPairs,
				jsonRedundancyPair);

		String highlightedUs2 = redundancyItems.getTextUs2();
		fileWriter.write("\n\n " + us2 + ": " + highlightedUs2.toLowerCase());

		String highlightedUs1 = redundancyItems.getTextUs1();
		fileWriter.write("\n\n " + us1 + ": " + highlightedUs1.toLowerCase());
		redundantPair.setRedundantPair1(us1);
		redundantPair.setRedundantPair2(us2);
		redundantPair.setMaximal(redundancyItems.getTotalRedundancyCount());
		redundantPairs.add(redundantPair);

	}

	// Separate user story "Text" with comma and then find in each area related
	// element.
	// For example if we are in second region (which means it need to looking for
	// primary
	// action/entity) after finding it check if the element contains in Targets, if
	// so,
	// add hash symbol(#) at the beginning and ending of the words.
	// For secondary action entity we looking ad the third region of sentence.
	// I want to check both user stories and separate their parts of sentence
	// through comma and first try to find the match is in secondPartOfSentence of
	// both user
	// stories. I should check US1_Part_1 vs US2_part_1, then if US1_Part_2 &&
	// US2_Part_2
	// are exist then try to file the match is in thirdPartOfSentence of both USs
	// then try
	// to replace
	private RedundancyItems highlightRedundancies(RedundancyItems redundancyItems, String usPair,
			List<TargetsPair> targetsPairs, List<TriggersPair> triggersPairs, List<ContainsPair> containsPairs,
			JSONObject jsonRedundancyPair) throws IOException, EmptyOrNotExistJsonFile, JsonFileNotFound {

		String textUs1 = redundancyItems.getTextUs1();
		String textUs2 = redundancyItems.getTextUs2();
		int mainConflict = redundancyItems.getMainRedundancyCount();
		int benefitConflict = redundancyItems.getBenefitRedundancyCount();
		// if (textUs1.length() <= 0 && textUs2.length() <= 0) {
		// return null;
		// }

		// find the index of first comma
		int firstCommaUs1 = textUs1.indexOf(',');
		int firstCommaUs2 = textUs2.indexOf(',');
		// if there is no main part in the sentence
		// which include triggers and targets
//		if (firstCommaUs1 == -1 || firstCommaUs2 == -1) {
//			return redundancyItems;
		// receive the index of first comma in both Text in USs
		// subString will be like this: I want to be able to ....
//		}
		String subStringFirstUs1 = textUs1.substring(0, firstCommaUs1);
		String subStringFirstUs2 = textUs2.substring(0, firstCommaUs2);

		// receive the index of second comma
		int benefitPlaceHolderUs1 = textUs1.indexOf("so that", firstCommaUs1 + 1);
		int benefitPlaceHolderUs2 = textUs2.indexOf("so that", firstCommaUs2 + 1);

		// if one of USs does't have benefit, then there is no conflicted
		// element at benefit at all just return benefit as it is
		// if US_1 does't have benefit
		if (benefitPlaceHolderUs1 == -1 && benefitPlaceHolderUs2 != -1) {

			String subStringSecondUs1 = textUs1.substring(firstCommaUs1 + 1);
			String subStringSecondUs2 = textUs2.substring(firstCommaUs2 + 1, benefitPlaceHolderUs2);

			// I want to check if targetsPair.aciton/entity are already exist
			// in this main part, if so then add hash symbol
			String[] usText = applyHashSymbolTargetsMain(targetsPairs, containsPairs, redundancyItems,
					subStringSecondUs1, subStringSecondUs2, jsonRedundancyPair);
			textUs1 = subStringFirstUs1 + "," + usText[0];
			textUs2 = subStringFirstUs2 + "," + usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// subString from second comma until the end of story ;)
			String subStringBenefitUs2 = redundancyItems.getTextUs2().substring(benefitPlaceHolderUs2);

			// check if there are hash symbol more than 3 pair first and main section
			// if so then highlight the Persona
			if (hasMoreThanSixHashSymbols(textUs1)) {
				usText = applyHashSymbolPersona(triggersPairs, redundancyItems, textUs1, textUs2);
				textUs1 = usText[0];
				textUs2 = usText[1];
				// add the number of conflict pairs from main sentence
				mainConflict = mainConflict + Integer.parseInt(usText[2]);
			}
			// Apply hash symbol to common element in the both user stories if any
			usText = applyHashSymbolContaiansMain(containsPairs, redundancyItems, textUs1, textUs2, jsonRedundancyPair);
			textUs1 = usText[0];
			textUs2 = usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);
			// I want to check if targetsPair.aciton/entity are already exist
			// in this sentence part, if so then add hash symbol
			textUs2 = textUs2 + subStringBenefitUs2;

		}
		// if US_2 does't have benefit
		if (benefitPlaceHolderUs1 != -1 && benefitPlaceHolderUs2 == -1) {

			String subStringSecondUs1 = textUs1.substring(firstCommaUs1 + 1, benefitPlaceHolderUs1);
			String subStringSecondUs2 = textUs2.substring(firstCommaUs2 + 1);

			// I want to check if targetsPair.aciton/entity are already exist
			// in this sentence part, if so then add hash symbol
			String[] usText = applyHashSymbolTargetsMain(targetsPairs, containsPairs, redundancyItems,
					subStringSecondUs1, subStringSecondUs2, jsonRedundancyPair);
			textUs1 = subStringFirstUs1 + "," + usText[0];
			textUs2 = subStringFirstUs2 + "," + usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// subString from second comma until the end of story ;)
			String subStringBenefitUs1 = redundancyItems.getTextUs1().substring(benefitPlaceHolderUs1);

			// check if there are hash symbol more than 3 pair first and main section
			// if so then highlight the Persona
			if (hasMoreThanSixHashSymbols(textUs1)) {
				usText = applyHashSymbolPersona(triggersPairs, redundancyItems, textUs1, textUs2);
				textUs1 = usText[0];
				textUs2 = usText[1];
				// add the number of conflict pairs from main sentence
				mainConflict = mainConflict + Integer.parseInt(usText[2]);

			}

			// Apply hash symbol to common element in the both user stories if any
			usText = applyHashSymbolContaiansMain(containsPairs, redundancyItems, textUs1, textUs2, jsonRedundancyPair);
			textUs1 = usText[0];
			textUs2 = usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// I want to check if targetsPair.aciton/entity are already exist
			// in this sentence part, if so then add hash symbol
			textUs1 = textUs1 + subStringBenefitUs1;

		}
		// if both does't have benefit
		if (benefitPlaceHolderUs1 == -1 && benefitPlaceHolderUs2 == -1) {
			String subStringSecondUs1 = textUs1.substring(firstCommaUs1 + 1);
			String subStringSecondUs2 = textUs2.substring(firstCommaUs2 + 1);

			// I want to check if targetsPair.aciton/entity are already exist
			// in this sentence part, if so then add hash symbol
			String[] usText = applyHashSymbolTargetsMain(targetsPairs, containsPairs, redundancyItems,
					subStringSecondUs1, subStringSecondUs2, jsonRedundancyPair);
			textUs1 = subStringFirstUs1 + "," + usText[0];
			textUs2 = subStringFirstUs2 + "," + usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// check if there are hash symbol more than 3 pair first and main section
			// if so then highlight the Persona
			if (hasMoreThanSixHashSymbols(textUs1)) {
				usText = applyHashSymbolPersona(triggersPairs, redundancyItems, textUs1, textUs2);
				textUs1 = usText[0];
				textUs2 = usText[1];
				// add the number of conflict pairs from main sentence
				mainConflict = mainConflict + Integer.parseInt(usText[2]);
			}

			// subString from second comma until the end of story ;)
			// I want to check if targetsPair.aciton/entity are already exist
			// in this sentence part, if so then add hash symbol

			// Apply hash symbol to common elements in the both user stories if any
			usText = applyHashSymbolContaiansMain(containsPairs, redundancyItems, textUs1, textUs2, jsonRedundancyPair);
			textUs1 = usText[0];
			textUs2 = usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

		}
		// if both USs have benefit
		if (benefitPlaceHolderUs1 != -1 && benefitPlaceHolderUs2 != -1) {
			String subStringSecondUs1 = textUs1.substring(firstCommaUs1 + 1, benefitPlaceHolderUs1);
			String subStringSecondUs2 = textUs2.substring(firstCommaUs2 + 1, benefitPlaceHolderUs2);
			// I want to check if targetsPair.aciton/entity are already exist
			// in this sentence part, if so then add hash symbol
			// first try to find conflict pairs at the main sentence
			String[] usText = applyHashSymbolTargetsMain(targetsPairs, containsPairs, redundancyItems,
					subStringSecondUs1, subStringSecondUs2, jsonRedundancyPair);

			textUs1 = usText[0];
			textUs2 = usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// check if there are hash symbol more than 3 pair first and main section
			// if so then highlight the Persona
			if (hasMoreThanFourHashSymbols(textUs1)) {
				usText = applyHashSymbolPersona(triggersPairs, redundancyItems, subStringFirstUs1, subStringFirstUs2);
				subStringFirstUs1 = usText[0];
				subStringFirstUs2 = usText[1];
				// add the number of conflict pairs from main sentence
				mainConflict = mainConflict + Integer.parseInt(usText[2]);
			}

			// Apply hash symbol to common elements in Main from the both user stories if
			// any
			usText = applyHashSymbolContaiansMain(containsPairs, redundancyItems, textUs1, textUs2, jsonRedundancyPair);
			textUs1 = usText[0];
			textUs2 = usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// subString from second comma until the end of story ;)
			String subStringBenefitUs1 = redundancyItems.getTextUs1().substring(benefitPlaceHolderUs1);
			String subStringBenefitUs2 = redundancyItems.getTextUs2().substring(benefitPlaceHolderUs2);

			// I want to check if targetsPair.aciton/entity are already exist
			// in this benefit part, if so then add hash symbol
			// first try to find conflict pairs at the benefit sentence
			usText = applyHashSymbolTargetsBenefit(targetsPairs, containsPairs, redundancyItems, subStringBenefitUs1,
					subStringBenefitUs2, jsonRedundancyPair);
			subStringBenefitUs1 = usText[0];
			subStringBenefitUs2 = usText[1];

			// add the number of conflict pairs from benefit sentence
			benefitConflict = benefitConflict + Integer.parseInt(usText[2]);

			// Apply hash symbol to common elements in Benefit part of the both user stories
			// if any
			usText = applyHashSymbolContaiansBenefit(containsPairs, redundancyItems, subStringBenefitUs1,
					subStringBenefitUs2, jsonRedundancyPair);
			subStringBenefitUs1 = usText[0];
			subStringBenefitUs2 = usText[1];
			// add the number of conflict pairs from main sentence
			benefitConflict = benefitConflict + Integer.parseInt(usText[2]);

			// concatenate main part and benefit parts
			textUs1 = textUs1 + subStringBenefitUs1;
			textUs2 = textUs2 + subStringBenefitUs2;

			// if contains elements are distributed in both parts
			// (one element in main and on in benefit), it the conflict element of main
			// should be increased not main and benefit
			// Apply hash symbol to common elements in the both user stories if any
			textUs1 = subStringFirstUs1 + "," + textUs1;
			textUs2 = subStringFirstUs2 + "," + textUs2;

		}

		// add amount of founded conflict pairs from main/benefit sentence
		// into Benefit/MainConflict Count
		redundancyItems.setBenefitRedundancyCount(benefitConflict);
		redundancyItems.setMainRedundancyCount(mainConflict);
		redundancyItems.setMaxRedundancyCount(mainConflict + benefitConflict);
		redundancyItems.setTextUs1(textUs1);
		redundancyItems.setTextUs2(textUs2);

		return redundancyItems;

	}

	private String[] applyHashSymbolContaiansMain(List<ContainsPair> containsPairs, RedundancyItems redundancyItems,
			String subStringUs1, String subStringUs2, JSONObject jsonRedundancyPair) {
		String[] usTexts = new String[4];
		int redundancyCount = 0;
		JSONArray jsonContains = new JSONArray();
		// iterate through commonContains and check other element of pair related to
		// the containsPair if any exist but filter: [containsPair,contain] case
		// which is the same pair in which is already parsed
		for (ContainsPair containsPair2 : containsPairs) {
			String child = containsPair2.getChildEntity();
			String parent = containsPair2.getParentEntity();
			// check if both elements of contains is included in both segment part
			// and check do so if
			if ((subStringUs1.contains(child) && subStringUs2.contains(child) && subStringUs1.contains(parent)
					&& subStringUs2.contains(parent))) {

				// add hash symbol to contains pairs
				String[] matches = { parent, child };
				subStringUs1 = applyHashSymbols(subStringUs1, matches);
				subStringUs2 = applyHashSymbols(subStringUs2, matches);

				redundancyCount++;
				// break in order to avoid of counting redundant items
				// add to JOSN report as Common Contains in Main
				JSONArray jsonContain = new JSONArray().put(parent).put(child);
				jsonContains.put(jsonContain);
			}
		}
		JSONObject jsonCommonContains = new JSONObject();
		jsonCommonContains.put("Main Part", jsonContains);
		jsonRedundancyPair.put("Common Contains", jsonCommonContains);
		usTexts[0] = subStringUs1.replaceAll("#+", "#");
		usTexts[1] = subStringUs2.replaceAll("#+", "#");
		usTexts[2] = String.valueOf(redundancyCount);
		return usTexts;
	}

	private String[] applyHashSymbolTargetsMain(List<TargetsPair> targetsPairs, List<ContainsPair> containsPairs,
			RedundancyItems redundancyItems, String subStringUs1, String subStringUs2, JSONObject jsonRedundancyPair) {
		String[] usTexts = new String[4];
		int redundancyCount = 0;
		JSONArray jsonTargets = new JSONArray();
		// I want to check if targetsPair.aciton/entity are already exist
		// in this sentence part, if so then add hash symbol
		for (TargetsPair targetsPair : targetsPairs) {
			// replace all pairs in this part of sentence if it
			// exist in both sentence parts
			String action = targetsPair.getAction();
			String entity = targetsPair.getEntity();
			if ((subStringUs1.contains(action) && subStringUs1.contains(entity))
					&& (subStringUs2.contains(action) && subStringUs2.contains(entity))) {
				// US_1 add hash symbol at the beginning and ending of each word
				String[] actionMatches = { action };
				String[] entityMatches = { entity };
				subStringUs1 = applyHashSymbols(subStringUs1, actionMatches);
				subStringUs1 = applyHashSymbols(subStringUs1, entityMatches);

				// US_2 add hash symbol at the beginning and ending of each word
				subStringUs2 = applyHashSymbols(subStringUs2, actionMatches);
				subStringUs2 = applyHashSymbols(subStringUs2, entityMatches);

				// count conflicts in this sentence part in order to know which sentence
				// part how many conflict pairs exist
				redundancyCount++;
				// check if entity exist in contains list

				// add to JOSN report as Common Targets in Main
				JSONArray josnTarget = new JSONArray().put(action).put(entity);
				jsonTargets.put(josnTarget);
			}
		}
		JSONObject jsonCommonTargets = jsonRedundancyPair.optJSONObject("Common Targets");
		if (jsonCommonTargets == null) {
			jsonCommonTargets = new JSONObject();
			jsonCommonTargets.put("Main Part", jsonTargets);
			jsonRedundancyPair.put("Common Targets", jsonCommonTargets);
		}
		jsonCommonTargets.put("Main Part", jsonTargets);

		usTexts[0] = subStringUs1.replaceAll("#+", "#");
		usTexts[1] = subStringUs2.replaceAll("#+", "#");
		usTexts[2] = String.valueOf(redundancyCount);
		return usTexts;
	}

	private String[] applyHashSymbolTargetsBenefit(List<TargetsPair> targetsPairs, List<ContainsPair> containsPairs,
			RedundancyItems redundancyItems, String subStringUs1, String subStringUs2, JSONObject jsonRedundancyPair) {
		String[] usTexts = new String[4];
		int redundancyCount = 0;
		JSONArray jsonTargets = new JSONArray();
		// I want to check if targetsPair.aciton/entity are already exist
		// in this sentence part, if so then add hash symbol
		for (TargetsPair targetsPair : targetsPairs) {
			// replace all pairs in this part of sentence if it
			// exist in both sentence parts
			String action = targetsPair.getAction();
			String entity = targetsPair.getEntity();
			if ((subStringUs1.contains(action) && subStringUs1.contains(entity))
					&& (subStringUs2.contains(action) && subStringUs2.contains(entity))) {
				// US_1 add hash symbol at the beginning and ending of each word
				String[] actionMatches = { action };
				String[] entityMatches = { entity };
				subStringUs1 = applyHashSymbols(subStringUs1, actionMatches);
				subStringUs1 = applyHashSymbols(subStringUs1, entityMatches);

				// US_2 add hash symbol at the beginning and ending of each word
				subStringUs2 = applyHashSymbols(subStringUs2, actionMatches);
				subStringUs2 = applyHashSymbols(subStringUs2, entityMatches);

				// count conflicts in this sentence part in order to know which sentence
				// part how many conflict pairs exist
				redundancyCount++;

				// check if entity exist in contains list
				// add to JOSN report as Common Targets in Benefit
				JSONArray josnTarget = new JSONArray().put(action).put(entity);
				jsonTargets.put(josnTarget);
			}
		}

		// JSONObject jsonCommonTargets = new JSONObject();
		JSONObject jsonCommonTargets = jsonRedundancyPair.optJSONObject("Common Targets");
		if (jsonCommonTargets == null) {
			jsonCommonTargets = new JSONObject();
			jsonRedundancyPair.put("Common Targets", jsonCommonTargets);
		}
		jsonCommonTargets.put("Benefit Part", jsonTargets);
		usTexts[0] = subStringUs1.replaceAll("#+", "#");
		usTexts[1] = subStringUs2.replaceAll("#+", "#");
		usTexts[2] = String.valueOf(redundancyCount);
		return usTexts;
	}

	private boolean hasMoreThanFourHashSymbols(String textUs1) {
		long count = textUs1.chars().filter(ch -> ch == '#').count();
		return count >= 4;

	}

	private String[] applyHashSymbolPersona(List<TriggersPair> triggers, RedundancyItems redundancyItems,
			String subStringFirstUs1, String subStringFirstUs2) {
		String[] usTexts = new String[4];
		int redundancyCount = 0;

		// I want to check if TriggersPair persona/action are already exist
		// in this sentence part, if so then add hash symbol
		// Add if and only if the action in triggers pair are exist in
		// *.redundanted targets pairs
		for (TriggersPair triggerPair : triggers) {
			String persona = triggerPair.getPersona();
			// replace all pairs in this part of sentence if it
			// exist in both sentence parts
			if (subStringFirstUs1.contains(persona) && subStringFirstUs2.contains(persona)) {

				// US_1/US_2 add hash symbol at the beginning and ending of each word
				String[] matches = { persona };
				subStringFirstUs1 = applyHashSymbols(subStringFirstUs1, matches);
				subStringFirstUs2 = applyHashSymbols(subStringFirstUs2, matches);

				// count conflicts in this sentence part in order to know which sentence
				// part how many conflict pairs exist
				// after highlighting persona it should be check if
				// to pairs of triggers elements exist, then increase the count
				// of elements if and only if the action is also in targets

				redundancyCount++;

			}

		}
		usTexts[0] = subStringFirstUs1.replaceAll("#+", "#");
		usTexts[1] = subStringFirstUs2.replaceAll("#+", "#");
		usTexts[2] = String.valueOf(redundancyCount);
		return usTexts;
	}

	// replace hash symbol at beginning and ending of founded element
	private String applyHashSymbols(String subString, String[] matches) {
		Arrays.sort(matches, Comparator.comparing(String::length).reversed());
		for (String match : matches) {
			subString = subString.replaceAll("\\b" + match + "\\b", "#" + match + "#");
		}
		return subString;
	}

	// Check in the main part of sentence if there is any redundancy
	// more than 6 hash mark exist
	private boolean hasMoreThanSixHashSymbols(String textUs1) {
		long count = textUs1.chars().filter(ch -> ch == '#').count();
		return count >= 6;
	}

	private String[] applyHashSymbolContaiansBenefit(List<ContainsPair> containsPairs, RedundancyItems redundancyItems,
			String subStringUs1, String subStringUs2, JSONObject jsonRedundancyPair) {
		String[] usTexts = new String[4];
		int redundancyCount = 0;
		JSONArray jsonContains = new JSONArray();
		// iterate through commonContains and check other element of pair related to
		// the containsPair if any exist but filter: [containsPair,contain] case
		// which is the same pair in which is already parsed
		for (ContainsPair containsPair2 : containsPairs) {
			String child = containsPair2.getChildEntity();
			String parent = containsPair2.getParentEntity();
			// check if both elements of contains is included in both segment part
			// and check do so if
			if ((subStringUs1.contains(child) && subStringUs2.contains(child) && subStringUs1.contains(parent)
					&& subStringUs2.contains(parent))
			// && !(subStringUs1.contains("#" + child + "#")
//							&& subStringUs2.contains("#" + child + "#")
//							&& subStringUs1.contains("#" + parent + "#")
//							&& subStringUs2.contains("#" + parent + "#")
			) {

				// add hash symbol to contains pairs
				String[] matches = { parent, child };
				subStringUs1 = applyHashSymbols(subStringUs1, matches);
				subStringUs2 = applyHashSymbols(subStringUs2, matches);

				redundancyCount++;
				// break in order to avoid of counting redundant items
				// add to JOSN report as Common Contains in Benefit
				JSONArray jsonContain = new JSONArray().put(parent).put(child);
				jsonContains.put(jsonContain);
			}
		}

		JSONObject jsonCommonContains = jsonRedundancyPair.optJSONObject("Common Contains");
		if (jsonCommonContains == null) {
			jsonCommonContains = new JSONObject();
			jsonRedundancyPair.put("Common Contains", jsonCommonContains);
		}
		jsonCommonContains.put("Benefit Part", jsonContains);

		usTexts[0] = subStringUs1.replaceAll("#+", "#");
		usTexts[1] = subStringUs2.replaceAll("#+", "#");
		usTexts[2] = String.valueOf(redundancyCount);
		return usTexts;

	}

	private void iteratePackages(EPackage minimalPackage, ArrayList<String> arrayMaximalElements,
			ArrayList<String> arrayMaximalElementsNames, RedundancyItems redundancyItems) throws IOException {

		String className = null;

		for (EClassifier eClassifier : minimalPackage.getEClassifiers()) {
			if (eClassifier instanceof EClass) {
				EClass eClass = (EClass) eClassifier;
				className = getClassName(eClass.getName());

				// if there is conflict between Attributes of tow USs, the hash symbol will
				// appear on the class-Name and not directly on attribute,
				// so we try to verify class name and check if it contains any hash symbol?
				if (eClass.getName().contains("#")) {

					EAttribute attribute = (EAttribute) eClass.getEStructuralFeature(0);
					if (attribute != null && !arrayMaximalElements.contains(attribute.getName())) {

						String attName = getAttName(attribute.getName());

						arrayMaximalElements.add(attribute.getName());
						arrayMaximalElementsNames.add(attName);

						// store conflicted Attributes according to their class
						switch (className) {
						case "Primary Action":
							redundancyItems.addPrimaryAction(new PrimaryAction(attName));
							break;
						case "Secondary Action":
							redundancyItems.addSecondaryAction(new SecondaryAction(attName));
							break;
						case "Secondary Entity":
							redundancyItems.addSecondaryEntity(new SecondaryEntity(attName));
							break;
						case "Primary Entity":
							redundancyItems.addPrimaryEntity(new PrimaryEntity(attName));
							break;
						default:
							break;
						}

					}
				}
				// add all EReferences from EClass into corresponding
				// type(Contains, Targets, Triggers)
				for (EReference eReference : eClass.getEReferences()) {
					if (eReference.getName().contains("#") && !arrayMaximalElements.contains(eReference.getName())) {

						String refName = getRefName(eReference.getName());
						arrayMaximalElements.add(eReference.getName());
						arrayMaximalElementsNames.add(refName);

						if (refName.equals("triggers")) {

							redundancyItems.addTriggers(new Triggers(refName, className));

						} else if (refName.equals("targets")) {

							redundancyItems.addTargets(new Targets(refName, className));

						} else if (refName.equals("contains")) {
							redundancyItems.addContains(new Contains(refName, className));

						}

					}

				}
			}

		}

	}

	private String getRefName(String name) {
		name = name.replaceAll("#", "");
		return name;
	}

	private String getClassName(String name) {
		name = name.replaceAll("#", "").replaceAll(".*:(.*)", "$1");
		return name;
	}

	private String getAttName(String name) {
		name = name.replaceAll("name=\"(.*)\"->.*\"(.*).*", "$1");
		return name;
	}

}
