package org.henshin.backlog2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.Set;
import java.util.HashSet;

/** Report maximal overlap if and only if in Minimal Models
 * at least Entity, Action and Triggers exist. With Table summary
 */
/**
 * @author amirr
 *
 */
public class CdaConvertorV2 {
	private static String dirName;
	private static String jsonDatasetFile;

	public CdaConvertorV2(String directroyName, String jsonFileName) {
		dirName = directroyName;
		jsonDatasetFile = jsonFileName;
	}

	public static void main(String[] args) throws IOException, NullPointerException, EmptyOrNotExistJsonFile {

		// String[] datasets = { "03", "04", "05", "08", "10", "11", "12", "14", "16",
		// "18", "19", "21", "22", "23", "24",
		// "25", "26", "27", "28" };
		String[] datasets = { "03", "04", "05" };
		// Version of data set
		for (int i = 0; i < datasets.length; i++) {
			CdaConvertorV2 cdaConvertor = new CdaConvertorV2("CDA_Report_backlog_g" + datasets[i],
					"Textual_Report_g" + datasets[i] + "\\g" + datasets[i] + "_baseline_pos.json");

			// Create text file in order to report to user a readable format
			File cdaReport = new File(cdaConvertor.getFinalReportDir() + "\\Textual_Report_g" + datasets[i]
					+ "\\Textual_Report_g" + datasets[i] + ".txt");
			FileWriter fileWriter = cdaConvertor.createOrOverwriteReportFile(cdaReport);

			// Create JSON File in order to have systematic overview of result
			File jsonReport = new File(cdaConvertor.getFinalReportDir() + "\\Textual_Report_g" + datasets[i]
					+ "\\JSON_Report_g" + datasets[i] + ".json");
			FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);
			List<ConflictPair> listConflictPairs = cdaConvertor.extractReports(fileWriter, jsonWriter);

			cdaConvertor.writeTable(cdaReport, listConflictPairs);
		}
	}

	public String getDirName() {
		return dirName;
	}

	public String getJsonDatasetFile() {
		return jsonDatasetFile;
	}

	public String getAbsoluteDirPath() {
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_2023_12\\CDA_Reports\\" + getDirName());
		if (Files.exists(path)) {

			return path.toString();
		} else {
			return null;
		}

	}

	public String getAbsoluteFinalReportDir() {
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\Final_Reports\\"
				+ getJsonDatasetFile());
		if (Files.exists(path)) {

			return path.toString();
		} else {
			return null;
		}
	}

	public String getFinalReportDir() {
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\Final_Reports\\");
		if (Files.exists(path)) {

			return path.toString();
		} else {
			return null;
		}
	}

	// Create or overwrite report file which return/pass the FileWriter object
	public FileWriter createOrOverwriteReportFile(File totalCda) {
		FileWriter cdaWriter = null;
		try {
			if (totalCda.createNewFile()) {
				System.out.println("File created succesfully: " + totalCda.getName());
				cdaWriter = new FileWriter(totalCda);
				return cdaWriter;

			} else {
				cdaWriter = new FileWriter(totalCda);
				System.out.println("File already exists. Try to overwrite..!");
				return cdaWriter;
			}
		} catch (IOException e) {
			System.out.println("An error occured.");
			e.printStackTrace();
		}
		return null;
	}

	public List<ConflictPair> extractReports(FileWriter fileWriter, FileWriter jsonWriter)
			throws IOException, NullPointerException, EmptyOrNotExistJsonFile {

		// create JSON array in order to contains all conflict pairs and their
		JSONArray jsonArray = new JSONArray();

		ConflictingItems conflictingItems;
		List<ConflictPair> conflictPairs;
		ArrayList<String> arrayMaximalElements;
		ArrayList<String> arrayMaximalElementsNames;
		List<String> pairList = new ArrayList<>();
		File main = new File(getAbsoluteDirPath());
		// Instantiate conflictPairs
		conflictPairs = new ArrayList<>();
		// Iterate through critical pairs
		if (main.exists() && main.list().length != 0) {
			for (String confPair : main.list()) {
				if (!checkIfReportExist(confPair, pairList) && containsAnd(confPair)) {
					File conflictReasonDir = new File(getAbsoluteDirPath() + "\\" + confPair);
					if (!conflictReasonDir.isFile()) {
						JSONObject jsonConflictPair = new JSONObject();
						arrayMaximalElementsNames = new ArrayList<>();
						arrayMaximalElements = new ArrayList<>();
						conflictingItems = new ConflictingItems();
						String[] conflictReasonListing = conflictReasonDir.list();
						// Iterate through conflict reasons if there is more than one conflict_reason.
						if (conflictReasonListing.length > 1) {
							for (String conflictReason : conflictReasonListing) {
								// Check if minimal ECore exist
								if (minimalEcoreExist(confPair, conflictReason)) {
									File minimalModelEcoreFile = new File(getAbsoluteDirPath() + "\\" + confPair + "\\"
											+ conflictReason + "\\minimal-model.ecore");
									try {
										processMinimalModels(minimalModelEcoreFile, fileWriter, arrayMaximalElements,
												arrayMaximalElementsNames, conflictingItems);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						// Write only the Elements which contains Primary/Secondary Action and
						// Primary/Secondary Entity
						if (hasEntitys(arrayMaximalElementsNames, conflictingItems)
								&& hasActions(arrayMaximalElementsNames, conflictingItems)
								&& hasTargets(arrayMaximalElementsNames, conflictingItems)) {
							fileWriter.write("\n------------------[Potentially redundant user"
									+ " stories found]--------------------------\n{" + confPair + "}\n  ");
							jsonConflictPair.put("Conflict Pair", confPair);

							fileWriter.write("\nRedundants elements are: ");
							conflictingItems.printConflictingItems(fileWriter, getCommonTargets(confPair),
									getCommonContains(confPair), getCommonTriggers(confPair), jsonConflictPair);

							// add JSON Object into main JSON array
							jsonConflictPair.put("Conflicted Elements", conflictingItems.getMaxConflictCount());

							// receive text of user stories which are highlighted
							writeUsText(confPair, arrayMaximalElementsNames, conflictPairs, conflictingItems,
									fileWriter);

							if (conflictingItems.getTextUs1() != null && conflictingItems.getTextUs2() != null) {
								fileWriter.write("\n\nThe following sentence parts are" + " candidates for possible"
										+ " redundancies between user stories:\n\n");
								// here we should write USsSentenceParts
								writeUsSentencePart(confPair, conflictingItems, fileWriter, jsonConflictPair);
								// Add JSONObject Texts of two user story and store them into Text JSONObject
								JSONObject jsonText = new JSONObject();
								jsonText.put("First UserStory",
										getUsName1(confPair) + ": " + conflictingItems.getTextUs1());
								jsonText.put("Second UserStory",
										getUsName2(confPair) + ": " + conflictingItems.getTextUs2());
								jsonConflictPair.put("Text", jsonText);
							}

							// add JSON Object into main JSON array
							jsonArray.put(jsonConflictPair);

						}

					}
				}
			}

			if (conflictPairs.size() == 0) {
				fileWriter.write("No redundancy found between user stories!");

			}
			jsonWriter.write(jsonArray.toString(4));
			jsonWriter.close();
			fileWriter.close();

		}

		return conflictPairs;

	}

	// receive a conflict Pair and a String which corresponds to "Targets" array
	// Object in JSON file
	private List<TargetPair> getCommonTargets(String confPair) throws EmptyOrNotExistJsonFile {
		String us1 = getUsName1(confPair);
		String us2 = getUsName2(confPair);
		List<TargetPair> targetPairs = new ArrayList<>();
		JSONArray us1TargetArray = null;
		JSONArray us2TargetArray = null;
		JSONArray jsonArray = readJsonArrayFromFile();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject.has("US_Nr") && jsonObject.has("Targets")) {
				String usNr = jsonObject.getString("US_Nr");
				if (usNr.equals(us1)) {
					us1TargetArray = jsonObject.getJSONArray("Targets");

				} else if (usNr.equals(us2)) {
					us2TargetArray = jsonObject.getJSONArray("Targets");

				}
			}
		}
		if (us2TargetArray != null && us1TargetArray != null) {
			for (int i = 0; i < us1TargetArray.length(); i++) {
				JSONArray jsonArrayUs1 = us1TargetArray.getJSONArray(i);
				String actionUs1 = jsonArrayUs1.getString(0);
				String enttiyUs1 = jsonArrayUs1.getString(1);
				for (int j = 0; j < us2TargetArray.length(); j++) {
					JSONArray jsonArrayUs2 = us2TargetArray.getJSONArray(j);
					String actionUs2 = jsonArrayUs2.getString(0);
					String enttiyUs2 = jsonArrayUs2.getString(1);
					if (enttiyUs2.equalsIgnoreCase(enttiyUs1) && actionUs2.equalsIgnoreCase(actionUs1.toLowerCase())) {
						targetPairs.add(new TargetPair(actionUs1, enttiyUs1));
						break;
					}
				}
			}
		}
		return targetPairs;
	}

	// receive a conflict Pair and a String which corresponds to "Triggers" array
	// Object in JSON file
	private List<TriggerPair> getCommonTriggers(String confPair) throws EmptyOrNotExistJsonFile {
		String us1 = getUsName1(confPair);
		String us2 = getUsName2(confPair);
		List<TriggerPair> triggersPairs = new ArrayList<>();
		JSONArray us1TriggersArray = null;
		JSONArray us2TriggersArray = null;
		JSONArray jsonArray = readJsonArrayFromFile();

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
			for (int i = 0; i < us1TriggersArray.length(); i++) {
				JSONArray jsonArrayUs1 = us1TriggersArray.getJSONArray(i);
				String personaUs1 = jsonArrayUs1.getString(0);
				String enttiyUs1 = jsonArrayUs1.getString(1);
				for (int j = 0; j < us2TriggersArray.length(); j++) {
					JSONArray jsonArrayUs2 = us2TriggersArray.getJSONArray(j);
					String personaUs2 = jsonArrayUs2.getString(0);
					String enttiyUs2 = jsonArrayUs2.getString(1);
					if (enttiyUs2.equalsIgnoreCase(enttiyUs1)
							&& personaUs2.equalsIgnoreCase(personaUs1.toLowerCase())) {
						triggersPairs.add(new TriggerPair(personaUs1, enttiyUs1));
						break;
					}
				}
			}
		}
		return triggersPairs;
	}

	// receive a conflict Pair and a String which corresponds to "Contains" array
	// Object in JSON file
	private List<ContainsPair> getCommonContains(String confPair) throws EmptyOrNotExistJsonFile {
		String us1 = getUsName1(confPair);
		String us2 = getUsName2(confPair);
		List<ContainsPair> containsPairs = new ArrayList<>();
		JSONArray us1ContainsArray = null;
		JSONArray us2ContainsArray = null;
		JSONArray jsonArray = readJsonArrayFromFile();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject.has("US_Nr") && jsonObject.has("Contains")) {
				String usNr = jsonObject.getString("US_Nr");
				if (usNr.equals(us1)) {
					us1ContainsArray = jsonObject.getJSONArray("Contains");

				} else if (usNr.equals(us2)) {
					us2ContainsArray = jsonObject.getJSONArray("Contains");

				}
			}
		}
		if (us2ContainsArray != null && us1ContainsArray != null) {
			for (int i = 0; i < us1ContainsArray.length(); i++) {
				JSONArray jsonArrayUs1 = us1ContainsArray.getJSONArray(i);
				String actionUs1 = jsonArrayUs1.getString(0);
				String enttiyUs1 = jsonArrayUs1.getString(1);
				for (int j = 0; j < us2ContainsArray.length(); j++) {
					JSONArray jsonArrayUs2 = us2ContainsArray.getJSONArray(j);
					String actionUs2 = jsonArrayUs2.getString(0);
					String enttiyUs2 = jsonArrayUs2.getString(1);
					if (enttiyUs2.equalsIgnoreCase(enttiyUs1) && actionUs2.equalsIgnoreCase(actionUs1.toLowerCase())) {
						containsPairs.add(new ContainsPair(actionUs1, enttiyUs1));
						break;
					}
				}
			}
		}
		return containsPairs;
	}

	private boolean minimalEcoreExist(String confPair, String conflictReason) {
		Path path = Paths.get(getAbsoluteDirPath() + "\\" + confPair + "\\" + conflictReason + "\\minimal-model.ecore");
		if (Files.exists(path)) {
			return true;
		} else {
			return false;
		}

	}

	private boolean containsAnd(String folder) {
		if (folder.toLowerCase().contains("and")) {
			return true;
		} else {
			return false;
		}
	}

	private void processMinimalModels(File minimalModelEcoreFile, FileWriter cdaWriter,
			ArrayList<String> arrayMaximalElements, ArrayList<String> arrayMaximalElementsNames,
			ConflictingItems conflictingItems) throws IOException {
		if (minimalModelEcoreFile.exists() && minimalModelEcoreFile.length() > 0) {
			Resource.Factory.Registry resourceFactoryRegistry = Resource.Factory.Registry.INSTANCE;
			resourceFactoryRegistry.getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.getResource(URI.createFileURI(minimalModelEcoreFile.getAbsolutePath()),
					true);
			if (resource != null && !resource.getContents().isEmpty()) {

				for (EObject eObject : resource.getContents()) {
					EPackage minimalPackage = (EPackage) eObject;

					iteratePackages(minimalPackage, arrayMaximalElements, arrayMaximalElementsNames, conflictingItems);

				}
			} else {
				System.out.println("minimal-model.ecore not found!");
			}
		} else {

			System.out.println("No registered resource factory founded: " + minimalModelEcoreFile.getAbsolutePath());
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
	private void writeUsSentencePart(String string, ConflictingItems conflictingItems, FileWriter fileWriter,
			JSONObject jsonObject) throws IOException {
		String usNum1 = getUsName1(string);
		String usNum2 = getUsName2(string);
		String usText1 = conflictingItems.getTextUs1();
		String usText2 = conflictingItems.getTextUs2();
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

	public JSONArray readJsonArrayFromFile() throws EmptyOrNotExistJsonFile {
		JSONArray jsonArray;
		try (FileReader reader = new FileReader(getAbsoluteFinalReportDir())) {
			JSONTokener tokener = new JSONTokener(reader);
			if (!tokener.more()) {
				throw new EmptyOrNotExistJsonFile();

			}
			// Read JSON file
			jsonArray = new JSONArray(tokener);

			return jsonArray;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// in order to insert the table at the very first place of the file
	// we need to first store the report at StringBuilder and then make
	// file again but at this time writing the table first
	private void writeTable(File totalCda, List<ConflictPair> conflictPairs) throws IOException {

		List<String> pairListSeperate = new ArrayList<>();
		StringBuilder report = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(totalCda));
		String line;
		while ((line = reader.readLine()) != null) {
			report.append(line).append("\n");
		}
		reader.close();
		try (FileWriter writer = new FileWriter(totalCda)) {

			// display the table
			StringBuilder table = new StringBuilder();
			// add new line before the table
			table.append("* Table of potential redundancies between user stories"
					+ " and the number of their overlapping elements\n\n");
			// table.append("\t");
			for (ConflictPair conflictPair : conflictPairs) {
				if (!pairListSeperate.contains(conflictPair.getConflictPair1())) {
					pairListSeperate.add(conflictPair.getConflictPair1());
				}
				if (!pairListSeperate.contains(conflictPair.getConflictPair2())) {
					pairListSeperate.add(conflictPair.getConflictPair2());
				}
			}
			String[][] stringTable = createTable(pairListSeperate, conflictPairs);
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

		} catch (

		IOException e) {
			e.printStackTrace();
		}

	}

	private String[][] createTable(List<String> pairListSeperate2, List<ConflictPair> conflictPairs2) {
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
				int maximal = findMaximal(conflictPairs2, pair1, pair2);
				table[i + 1][j + 1] = String.valueOf(maximal);
			}

		}
		return table;
	}

	private int findMaximal(List<ConflictPair> cPs, String pair1, String pair2) {
		for (ConflictPair cp : cPs) {
			if ((cp.getConflictPair1().equals(pair1) && cp.getConflictPair2().equals(pair2))
					|| (cp.getConflictPair1().equals(pair2) && cp.getConflictPair2().equals(pair1))) {
				return cp.getMaximal();
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

	private boolean hasTargets(ArrayList<String> arrayMaximalElementsNames, ConflictingItems conflictingItems) {
		for (String item : arrayMaximalElementsNames) {
			for (Targets target : conflictingItems.getTargets()) {
				if (item.equals(target.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasActions(ArrayList<String> arrayMaximalElementsNames, ConflictingItems conflictingItems) {
		for (String item : arrayMaximalElementsNames) {
			for (SecondaryAction secondaryAction : conflictingItems.getSecondaryAction()) {
				if (item.equals(secondaryAction.getName())) {
					return true;
				}
			}
			for (PrimaryAction primaryAction : conflictingItems.getPrimaryActions()) {
				if (item.equals(primaryAction.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasEntitys(ArrayList<String> arrayMaximalElementsNames, ConflictingItems conflictingItems) {
		for (String item : arrayMaximalElementsNames) {
			for (SecondaryEntity secondaryEntity : conflictingItems.getSecondaryEntity()) {
				if (item.equals(secondaryEntity.getName())) {
					return true;
				}
			}
			for (PrimaryEntity primaryEntity : conflictingItems.getPrimaryEntity()) {
				if (item.equals(primaryEntity.getName())) {
					return true;
				}
			}
		}

		return false;
	}

	// get USs Text from JSON File and add them into conflictingItems
	private void getUssTexts(String usPair, ConflictingItems conflictingItems) {
		JSONArray json = null;
		String us1 = getUsName1(usPair);
		String us2 = getUsName2(usPair);
		try {

			json = readJsonArrayFromFile();
			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonObject = json.getJSONObject(i);

				// check if two related object are exist in JSON file
				// if so
				if (jsonObject.has("US_Nr")) {
					if (jsonObject.has("Text")) {
						String usNr = jsonObject.getString("US_Nr");
						String usText = jsonObject.getString("Text");
						if (usNr.equals(us1)) {
							conflictingItems.setTextUs1(usText);

						} else if (usNr.equals(us2)) {

							conflictingItems.setTextUs2(usText);

						}
					} else {

						throw new TextInJsonFileNotFound();
					}
				} else {
					throw new UsNrInJsonFileNotFound();
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();

		} catch (TextInJsonFileNotFound e) {
			e.printStackTrace();
		} catch (UsNrInJsonFileNotFound e) {
			e.printStackTrace();
		} catch (EmptyOrNotExistJsonFile e) {

			e.printStackTrace();
		}

	}

	private void writeUsText(String usPair, ArrayList<String> arrayMax, List<ConflictPair> conflictPairs,
			ConflictingItems conflictingItems, FileWriter fileWriter) throws IOException, EmptyOrNotExistJsonFile {
		String us1 = getUsName1(usPair);
		String us2 = getUsName2(usPair);

		// TODO: Check if conflictPairs can be replace with conflictingItems
		// add conflict pairs with maximal overlapping and save it
		// in order to filling table
		ConflictPair conflictPair = new ConflictPair();
		conflictPair.setConflictPair1(us1);
		conflictPair.setConflictPair2(us2);
		conflictPair.setMaximal(conflictingItems.getMaxConflictCount());
		conflictPairs.add(conflictPair);

		// TODO here i try to add US_Nr from USs into the highlightConflicts
		conflictingItems.setUsNr1(us1);
		conflictingItems.setUsNr2(us2);

		// get USs Text from JSON File and add them into conflictingItems
		getUssTexts(usPair, conflictingItems);
		try {
			// here I want to send both USs as parameter for highlightingConflicts
			highlightConflicts(conflictingItems, usPair);

			String highlightedUs2 = conflictingItems.getTextUs2();
			fileWriter.write("\n\n " + us2 + ": " + highlightedUs2.toLowerCase());

			String highlightedUs1 = conflictingItems.getTextUs1();
			fileWriter.write("\n\n " + us1 + ": " + highlightedUs1.toLowerCase());

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();

		}

	}

	// Separate user story "Text" with comma and then find in each area related
	// element.
	// For example if we are in second region (which means it need to looking for
	// primary
	// action/entity) after finding it check if the element contains in Targets, if
	// so,
	// add hash symbol(#) at the beginning and ending of the words.
	// For secondary action entity we looking ad the third region of sentence.
	private ConflictingItems highlightConflicts(ConflictingItems conflictingItems, String usPair)
			throws IOException, EmptyOrNotExistJsonFile {

		String textUs1 = conflictingItems.getTextUs1();
		String textUs2 = conflictingItems.getTextUs2();
		if (textUs1.length() <= 0 && textUs2.length() <= 0) {
			return null;
		}
		List<TargetPair> targetsPairs = getCommonTargets(usPair);
		List<ContainsPair> containsPairs = getCommonContains(usPair);

		// find the index of first comma
		int firstCommaUs1 = textUs1.indexOf(',');
		int firstCommaUs2 = textUs2.indexOf(',');
		if (firstCommaUs1 == -1 || firstCommaUs2 == -1) {
			return conflictingItems;
		}
		// receive the index of first comma in both Text in USs
		// subString will be like this: I want to be able to ....
		String subStringFirstUs1 = textUs1.substring(0, firstCommaUs1);
		String subStringFirstUs2 = textUs2.substring(0, firstCommaUs2);
		System.out.println("subStringFirstUs1: " + subStringFirstUs1);
		System.out.println("subStringFirstUs2: " + subStringFirstUs2);

		// receive the index of second comma
		int secondCommaUs1 = textUs1.indexOf(',', firstCommaUs1 + 1);
		int secondCommaUs2 = textUs2.indexOf(',', firstCommaUs2 + 1);
		System.out.println("second Comma Index: " + secondCommaUs1);
		System.out.println("second Comma Index: " + secondCommaUs1);

		// if indexOf return -1 which means user story doesn't have Benefit
		if (secondCommaUs1 == -1 || secondCommaUs2 == -1) {
			// add only from first comma until the appearance of second comma(until benefit)
			String subStringSecondUs1 = textUs1.substring(firstCommaUs1);
			String subStringSecondUs2 = textUs2.substring(firstCommaUs2);
			System.out.println("subStringSecondUs1: " + subStringSecondUs1);
			System.out.println("subStringSecondUs2: " + subStringSecondUs2);

			String[] usText = applyHashSymbol(targetsPairs, containsPairs, conflictingItems, subStringSecondUs1,
					subStringSecondUs2);

			// try to assign return values from applyHashSymbol into the textUs1/2
			textUs1 = textUs1.substring(0, firstCommaUs1) + usText[0];
			textUs2 = textUs2.substring(0, firstCommaUs2) + usText[1];

			System.out.println("textUs1 " + textUs1);
			System.out.println("textUs2 " + textUs2);
			// textUs1 = textUs1.substring(0, firstCommaUs1) + subStringFirstUs1;
			// textUs1 = textUs1.replaceAll("#+", "#");
			// textUs2 = textUs2.substring(0, firstCommaUs2) + subStringFirstUs2;
			// textUs2 = textUs2.replaceAll("#+", "#");
			return conflictingItems;
		}

		// I want to check both user stories and separate their parts of sentence
		// through comma and first try to find the match is in secondPartOfSentence of
		// both user
		// stories. I should check US1_Part_1 vs US2_part_1, then if US1_Part_2 &&
		// US2_Part_2
		// are exist then try to file the match is in thirdPartOfSentence of both USs
		// then try
		// to replace
		String subStringSecondUs1 = textUs1.substring(firstCommaUs1 + 1, secondCommaUs1);
		String subStringSecondUs2 = textUs2.substring(firstCommaUs2 + 1, secondCommaUs2);
		System.out.println("subStringSecondUs1AfterFirstCondition: " + subStringSecondUs1);
		System.out.println("subStringSecondUs2AfterFirstCondition: " + subStringSecondUs2);
		// I want to check if targetsPair.aciton/entity are already exist
		// in this sentence part, if so then add hash symbol
		String[] usText = applyHashSymbol(targetsPairs, containsPairs, conflictingItems, subStringSecondUs1,
				subStringSecondUs2);

		textUs1 = subStringFirstUs1 + "," + usText[0];
		textUs2 = subStringFirstUs2 + "," + usText[1];
		System.out.println("Combine US1: " + textUs1);
		System.out.println("Combine US2: " + textUs2);
		// subString from second comma until the end of story ;)
		String subStringBenefitUs1 = conflictingItems.getTextUs1().substring(secondCommaUs1);
		String subStringBenefitUs2 = conflictingItems.getTextUs2().substring(secondCommaUs2);
		System.out.println("subStringBenefitUs1: " + subStringBenefitUs1 + 1);
		System.out.println("subStringBenefitUs2: " + subStringBenefitUs2 + 1);
		if (subStringBenefitUs1 != null && subStringBenefitUs2 != null) {
			// I want to check if targetsPair.aciton/entity are already exist
			// in this sentence part, if so then add hash symbol
			usText = applyHashSymbol(targetsPairs, containsPairs, conflictingItems, subStringBenefitUs1,
					subStringBenefitUs2);
			textUs1 = textUs1 + usText[0];
			textUs2 = textUs2 + usText[1];
			System.out.println("textUS1 with benefit: " + textUs1);
			System.out.println("textUS2 with benefit: " + textUs2);
		}
		conflictingItems.setTextUs1(textUs1);
		conflictingItems.setTextUs2(textUs2);
		return conflictingItems;

	}

	private String[] applyHashSymbol(List<TargetPair> targetsPairs, List<ContainsPair> containsPairs,
			ConflictingItems conflictingItems, String subStringUs1, String subStringUs2) {
		String[] usTexts = new String[2];
		// I want to check if targetsPair.aciton/entity are already exist
		// in this sentence part, if so then add hash symbol
		for (TargetPair targetspair : targetsPairs) {
			// replace all pairs in this part of sentence if it
			// exist in both sentence parts
			if ((subStringUs1.contains(targetspair.getAction()) && subStringUs1.contains(targetspair.getEntity()))
					&& (subStringUs2.contains(targetspair.getAction())
							&& subStringUs2.contains(targetspair.getEntity()))) {
				// US_1 add hash symbol at the beginning and ending of each word
				subStringUs1 = subStringUs1.replaceFirst("\\b" + targetspair.getAction() + "\\b",
						"#" + targetspair.getAction() + "#");
				subStringUs1 = subStringUs1.replaceFirst("\\b" + targetspair.getEntity() + "\\b",
						"#" + targetspair.getEntity() + "#");

				// US_2 add hash symbol at the beginning and ending of each word
				subStringUs2 = subStringUs2.replaceFirst("\\b" + targetspair.getAction() + "\\b",
						"#" + targetspair.getAction() + "#");
				subStringUs2 = subStringUs2.replaceFirst("\\b" + targetspair.getEntity() + "\\b",
						"#" + targetspair.getEntity() + "#");

				// check if entity exist in contains list
				String contains = conflictingItems.isInCommonContains(targetspair.getEntity(), containsPairs);
				if (contains != null && subStringUs1.contains(contains)) {
					subStringUs1 = subStringUs1.replaceFirst("\\b" + contains + "\\b", "#" + contains + "#");
				}
				// check if entity exist in contains list
				if (contains != null && subStringUs2.contains(contains)) {
					subStringUs2 = subStringUs2.replaceFirst("\\b" + contains + "\\b", "#" + contains + "#");
				}
			}

		}
		usTexts[0] = subStringUs1;
		usTexts[1] = subStringUs2;
		return usTexts;
	}

	private void iteratePackages(EPackage minimalPackage, ArrayList<String> arrayMaximalElements,
			ArrayList<String> arrayMaximalElementsNames, ConflictingItems conflictingItems) throws IOException {

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
							conflictingItems.addPrimaryAction(new PrimaryAction(attName));
							break;
						case "Secondary Action":
							conflictingItems.addSecondaryAction(new SecondaryAction(attName));
							break;
						case "Secondary Entity":
							conflictingItems.addSecondaryEntity(new SecondaryEntity(attName));
							break;
						case "Primary Entity":
							conflictingItems.addPrimaryEntity(new PrimaryEntity(attName));
							break;
						default:
							break;
						}

					}
				}

				for (EReference eReference : eClass.getEReferences()) {
					if (eReference.getName().contains("#") && !arrayMaximalElements.contains(eReference.getName())) {

						String refName = getRefName(eReference.getName());
						arrayMaximalElements.add(eReference.getName());
						arrayMaximalElementsNames.add(refName);

						if (refName.equals("triggers")) {

							conflictingItems.addTriggers(new Triggers(refName, className));

						} else if (refName.equals("targets")) {

							conflictingItems.addTargets(new Targets(refName, className));

						} else if (refName.equals("contains")) {
							conflictingItems.addContains(new Contains(refName, className));

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
