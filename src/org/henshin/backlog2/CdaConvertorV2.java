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
	private static String jsonFile;

	public CdaConvertorV2(String directroyName, String jsonFileName) {
		dirName = directroyName;
		jsonFile = jsonFileName;
	}

	public static void main(String[] args) throws IOException, NullPointerException, EmptyOrNotExistJsonFile {

		// Version of data set
		String version = "16";
		CdaConvertorV2 cdaConvertor = new CdaConvertorV2("CDA_Report_backlog_g" + version,
				"Datasets\\g" + version + "_baseline_pos.json");
		// Create text file in order to report to user a readable format
		File cdaReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_g" + version + ".txt");
		FileWriter fileWriter = cdaConvertor.createOrOverwriteReportFile(cdaReport);

		// Create JSON File in order to have systematic overview of result
		File jsonReport = new File(cdaConvertor.getAbsoluteDirPath() + "\\Textual_Report_g" + version + ".json");
		FileWriter jsonWriter = cdaConvertor.createOrOverwriteReportFile(jsonReport);
		List<ConflictPair> listConflictPairs = cdaConvertor.extractReports(fileWriter, jsonWriter);

		cdaConvertor.writeTable(cdaReport, listConflictPairs);

	}

	public String getDirName() {
		return dirName;
	}

	public String getJsonFile() {
		return jsonFile;
	}

	public String getAbsoluteDirPath() {
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_2023_12\\CDA_Reports\\" + getDirName());
		if (Files.exists(path)) {

			return path.toString();
		} else {
			return null;
		}

	}

	public String getAbsoluteJsonFileDir() {
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\" + getJsonFile());
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
							List<String> highlightedUssTexts = writeUsText(confPair, arrayMaximalElementsNames,
									conflictPairs, conflictingItems, fileWriter);
							// Add JSONObject Texts of two user story and store them into Text JSONObject
							JSONObject jsonText = new JSONObject();
							jsonText.put("First UserStory", highlightedUssTexts.get(0));
							jsonText.put("Second UserStory", highlightedUssTexts.get(1));
							jsonConflictPair.put("Text", jsonText);
							
							if (highlightedUssTexts.size() != 0) {
								fileWriter.write("\n\nThe following sentence parts are" + " candidates for possible"
										+ " redundancies between user stories:\n\n");
								writeUsSentencePart(confPair, highlightedUssTexts, fileWriter, jsonConflictPair);
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
	private void writeUsSentencePart(String string, List<String> usTexts, FileWriter fileWriter, JSONObject jsonObject)
			throws IOException {
		String usNum1 = getUsName1(string);
		String usNum2 = getUsName2(string);
		String usText1 = usTexts.get(0);
		String usText2 = usTexts.get(1);
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
		for (String part : parts2) {
			Matcher matcher = pattern.matcher(part);
			int count = 0;
			while (matcher.find()) {
				count++;
				if (count >= 2 && !writtenUserStoryPart2.contains(part)) {
					fileWriter.write(usNum1 + ": " + part + "\n");
					// Write sentence Part first user story on sentence part A
					jsonPartOfSentence.put("Second UserStory", new JSONArray().put(part));
					writtenUserStoryPart2.add(part);
				}
			}
		}
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

		jsonObject.put("Part of Sentence", jsonPartOfSentence);

	}

	public JSONArray readJsonArrayFromFile() throws EmptyOrNotExistJsonFile {
		JSONArray jsonArray;
		try (FileReader reader = new FileReader(getAbsoluteJsonFileDir())) {
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

	private List<String> writeUsText(String usPair, ArrayList<String> arrayMax, List<ConflictPair> conflictPairs,
			ConflictingItems conflictingItems, FileWriter fileWriter) throws IOException, EmptyOrNotExistJsonFile {
		List<String> highlightedElements = new ArrayList<>();
		JSONArray json = null;
		String usNr = null;
		String us1 = getUsName1(usPair);
		String us2 = getUsName2(usPair);

		// add conflict pairs with maximal overlapping and save it
		// in order to filling table
		ConflictPair conflictPair = new ConflictPair();
		conflictPair.setConflictPair1(us1);
		conflictPair.setConflictPair2(us2);
		conflictPair.setMaximal(conflictingItems.getMaxConflictCount());
		conflictPairs.add(conflictPair);

		try {

			json = readJsonArrayFromFile();
			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonObject = json.getJSONObject(i);

				// check if two related object are exist in JSON file
				// if so
				if (jsonObject.has("US_Nr") && jsonObject.has("Text")) {
					usNr = jsonObject.getString("US_Nr");
					if (usNr.equals(us1)) {
						String highlightedUs1 = highlightConflict(jsonObject.getString("Text").toLowerCase(),
								conflictingItems, us1, usPair);
						fileWriter.write("\n\n " + us1 + ": " + highlightedUs1.toLowerCase());
						highlightedElements.add(highlightedUs1);
					} else if (usNr.equals(us2)) {
						String highlightedUs2 = highlightConflict(jsonObject.getString("Text").toLowerCase(),
								conflictingItems, us2, usPair);
						fileWriter.write("\n\n " + us2 + ": " + highlightedUs2.toLowerCase());
						highlightedElements.add(highlightedUs2);
					}
				} else {
					fileWriter.write("US_Nr or Text Element not found in JSON-Data!");
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();

		}

		return highlightedElements;
	}

	// Separate user story "Text" with comma and then find in each area related
	// element.
	// For example if we are in second region (which means it need to looking for
	// primary
	// action/entity) after finding it check if the element contains in Targets, if
	// so,
	// add hash symbol(#) at the beginning and ending of the words.
	// For secondary action entity we looking ad the third region of sentence.
	private String highlightConflict(String usText, ConflictingItems conflictingItems, String usNr, String usPair)
			throws IOException, EmptyOrNotExistJsonFile {
		if (usText.length() <= 0) {
			return null;
		}
		List<TargetPair> targetsPairs = getCommonTargets(usPair);

		// TODO should I highlight contains elements?
		List<ContainsPair> containsPairs = getCommonContains(usPair);

		// find the index of first comma
		int firstComma = usText.indexOf(',');
		if (firstComma == -1) {
			return usText;
		}
		// Substring from the first comma until the end of the usText
		String subStringSecond = usText.substring(firstComma);
		for (PrimaryAction primaryAction : conflictingItems.getPrimaryActions()) {
			// check if related conflicting item is already build triple with
			// Entity/Targets/Action
			if (conflictingItems.isInCommonTargets(primaryAction.getName(), primaryAction.getType(), "", "",
					targetsPairs) && !primaryAction.getName().contains("#")) {
				subStringSecond = subStringSecond.replaceFirst("\\b" + primaryAction.getName() + "\\b",
						"#" + primaryAction.getName() + "#");
			}

		}
		for (PrimaryEntity primaryEntity : conflictingItems.getPrimaryEntity()) {

			// check if related conflicting item is already build triple with
			// Entity/Targets/Action
			if (conflictingItems.isInCommonTargets(primaryEntity.getName(), primaryEntity.getType(), "", "",
					targetsPairs)) {
				subStringSecond = subStringSecond.replaceFirst("\\b" + primaryEntity.getName() + "\\b",
						"#" + primaryEntity.getName() + "#");
				String contains = conflictingItems.isInCommonContains(primaryEntity.getName(), containsPairs);
				// check if primary entity exist in contains list
				if (contains != null) {
					subStringSecond = subStringSecond.replaceFirst("\\b" + contains + "\\b", "#" + contains + "#");
				}
			}

		}
		for (SecondaryAction secondaryAction : conflictingItems.getSecondaryAction()) {
			// check if related conflicting item is already build triple with
			// Entity/Targets/Action
			if (conflictingItems.isInCommonTargets(secondaryAction.getName(), secondaryAction.getType(), "", "",
					targetsPairs)) {
				subStringSecond = subStringSecond.replaceFirst("\\b" + secondaryAction.getName() + "\\b",
						"#" + secondaryAction.getName() + "#");
			}
		}

		for (SecondaryEntity secondaryEntity : conflictingItems.getSecondaryEntity()) {
			// check if related conflicting item is already build triple with
			// Entity/Targets/Action
			if (conflictingItems.isInCommonTargets(secondaryEntity.getName(), secondaryEntity.getType(), "", "",
					targetsPairs)) {
				subStringSecond = subStringSecond.replaceFirst("\\b" + secondaryEntity.getName() + "\\b",
						"#" + secondaryEntity.getName() + "#");
				String contains = conflictingItems.isInCommonContains(secondaryEntity.getName(), containsPairs);

				// check if primary entity exist in contains list
				if (contains != null) {
					subStringSecond = subStringSecond.replaceFirst("\\b" + contains + "\\b", "#" + contains + "#");
				}
			}
		}
		// receive the index of second comma
		int secondComma = subStringSecond.indexOf(',', 1);

		// if indexOf return -1 which means user story doesn't have Benefit
		if (secondComma == -1) {
			usText = usText.substring(0, firstComma) + subStringSecond;
			usText = usText.replaceAll("#+", "#");
			return usText;
		}
		usText = usText.substring(0, firstComma) + subStringSecond.substring(0, secondComma + 1);

		// starting the search from the index right after the third comma.
		String subStringThird = subStringSecond.substring(secondComma);
		for (SecondaryAction secondaryAction : conflictingItems.getSecondaryAction()) {
			// check if related conflicting item is already build triple with
			// Entity/Targets/Action
			if (conflictingItems.isInCommonTargets(secondaryAction.getName(), secondaryAction.getType(), "", "",
					targetsPairs)) {
				subStringThird = subStringThird.replaceFirst("\\b" + secondaryAction.getName() + "\\b",
						"#" + secondaryAction.getName() + "#");
			}
		}

		for (SecondaryEntity secondaryEntity : conflictingItems.getSecondaryEntity()) {
			// check if related conflicting item is already build triple with
			// Entity/Targets/Action
			if (conflictingItems.isInCommonTargets(secondaryEntity.getName(), secondaryEntity.getType(), "", "",
					targetsPairs)) {
				subStringThird = subStringThird.replaceFirst("\\b" + secondaryEntity.getName() + "\\b",
						"#" + secondaryEntity.getName() + "#");
				String contains = conflictingItems.isInCommonContains(secondaryEntity.getName(), containsPairs);
				// check if primary entity exist in contains list
				if (contains != null) {
					subStringThird = subStringThird.replaceFirst("\\b" + contains + "\\b", "#" + contains + "#");
				}
			}

		}
		usText = usText.substring(0, firstComma + secondComma) + subStringThird;
		usText = usText.replaceAll("#+", "#");
		return usText;
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
