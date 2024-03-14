package org.henshin.backlog2;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

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

	public static void main(String[] args) throws IOException, NullPointerException, EmptyOrNotExistJsonFile,
			CdaReportDirNotFound, JsonFileNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {

		String[] datasets = { "03", "04", "05", "08", "10", "11", "12", "14", "16", "18", "19", "21", "22", "23", "24",
				"25", "26", "27", "28" };
		// String[] datasets = { "05" };
		// Version of data set
		for (int i = 0; i < datasets.length; i++) {
			CdaConvertorV2 cdaConvertor = new CdaConvertorV2(
					"eclipse-workspace_2023_12\\CDA_Reports\\CDA_Report_backlog_g" + datasets[i],
					"Final_Reports\\Textual_Report_g" + datasets[i] + "\\g" + datasets[i] + "_baseline_pos.json");

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

	public String getAbsoluteDirPath() throws CdaReportDirNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
	    Path path = Paths.get("C:\\Users\\amirr\\" + getDirName());
	    if (Files.exists(path)) {
	        if (Files.isDirectory(path)) { // Check if it's a directory
	            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
	                boolean isEmpty = true;
	                boolean hasSubDirectories = false;
	                
	                for (Path entry : directoryStream) {
	                    if (Files.isDirectory(entry)) {
	                        hasSubDirectories = true;
	                    }
	                    isEmpty = false;
	                }
	                
	                if (isEmpty) {
	                    throw new CdaReportDirIsEmpty("CDA Report Directory is empty!");
	                } else if (!hasSubDirectories) {
	                    throw new CdaReportDirIsEmpty("CDA Report Directory found but doesn't have any subdirectories!");
	                } else {
	                    return path.toString();
	                }
	            } catch (IOException e) {
	                throw new CdaReportDirNotFound("Error checking directory content: " + e.getMessage());
	            }
	        } else {
	            throw new CdaReportDirIsNotADirectory();
	        }
	    } else {
	        throw new CdaReportDirNotFound();
	    }
	}

	public String getAbsoluteFinalReportDir() throws JsonFileNotFound {
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\" + getJsonDatasetFile());
		if (Files.exists(path)) {

			return path.toString();
		} else {
			throw new JsonFileNotFound();
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
			throws IOException, NullPointerException, EmptyOrNotExistJsonFile, CdaReportDirNotFound, JsonFileNotFound,
			CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {

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

							// receive text of user stories as input
							// in order to highlight the redundancy clauses in each text
							writeUsText(confPair, arrayMaximalElementsNames, conflictPairs, conflictingItems,
									fileWriter);

							// add JSON Object into main JSON array
							JSONObject jsonConflictStatus = getConflictStatus(conflictingItems);

							// put all in Status as subPart of "Status"
							jsonConflictPair.put("Status", jsonConflictStatus);

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

								// select project Number and save it to JSONFile
								String projectNr = conflictingItems.getTextUs1().replaceAll(".*#(g\\d\\d)#.*", "$1");
								jsonConflictPair.put("Project Number", projectNr);
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

	// Add Status Elements(Main/Benefit/Total Part Conflicted Elements) into JSON
	// data
	private JSONObject getConflictStatus(ConflictingItems conflictingItems) {
		JSONObject jsonConflictStatus = new JSONObject();

		// add observed conflicted pairs in Main part sentence
		jsonConflictStatus.put("Main Part Conflicted Elements", conflictingItems.getMainConflictCount());

		// add observed conflicted pairs in Benefit part sentence
		jsonConflictStatus.put("Benefit Part Conflicted Elements", conflictingItems.getBenefitConflictCount());

		// int total = conflictingItems.getBenefitConflictCount()
		// + conflictingItems.getMainConflictCount();

		// conflictingItems.setMaxConflictCount(total);

		// add observed total conflicted pairs
		jsonConflictStatus.put("Total Conflicted Elements", conflictingItems.getMaxConflictCount());
		return jsonConflictStatus;
	}

	// receive a conflict Pair and a String which corresponds to "Targets" array
	// Object in JSON file
	private List<TargetPair> getCommonTargets(String confPair) throws EmptyOrNotExistJsonFile, JsonFileNotFound {
		String us1 = getUsName1(confPair);
		String us2 = getUsName2(confPair);
		List<TargetPair> targetPairs = new ArrayList<>();
		JSONArray us1TargetArray = null;
		JSONArray us2TargetArray = null;
		JSONArray jsonArray = readJsonArrayFromFile();

		// Check if there US_Nr and Targets Objects exists in JOSN file
		// if so return the ArrayObjects of Targets
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
		// check if TargetArrays of both USs not null then find the Common
		// Targets Pairs and add it to TargetPairs List
		if (us2TargetArray != null && us1TargetArray != null) {
			for (int i = 0; i < us1TargetArray.length(); i++) {
				JSONArray jsonArrayUs1 = us1TargetArray.getJSONArray(i);
				String actionUs1 = jsonArrayUs1.getString(0).toLowerCase();
				String enttiyUs1 = jsonArrayUs1.getString(1).toLowerCase();
				for (int j = 0; j < us2TargetArray.length(); j++) {
					JSONArray jsonArrayUs2 = us2TargetArray.getJSONArray(j);
					String actionUs2 = jsonArrayUs2.getString(0).toLowerCase();
					String enttiyUs2 = jsonArrayUs2.getString(1).toLowerCase();
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
	private List<TriggerPair> getCommonTriggers(String confPair) throws EmptyOrNotExistJsonFile, JsonFileNotFound {
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
						triggersPairs.add(new TriggerPair(personaUs1, actionUs1));
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
	private List<ContainsPair> getCommonContains(String confPair) throws EmptyOrNotExistJsonFile, JsonFileNotFound {
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
				String actionUs1 = jsonArrayUs1.getString(0).toLowerCase();
				String enttiyUs1 = jsonArrayUs1.getString(1).toLowerCase();
				for (int j = 0; j < us2ContainsArray.length(); j++) {
					JSONArray jsonArrayUs2 = us2ContainsArray.getJSONArray(j);
					String actionUs2 = jsonArrayUs2.getString(0).toLowerCase();
					String enttiyUs2 = jsonArrayUs2.getString(1).toLowerCase();
					if (enttiyUs2.equalsIgnoreCase(enttiyUs1) && actionUs2.equalsIgnoreCase(actionUs1.toLowerCase())) {
						containsPairs.add(new ContainsPair(actionUs1, enttiyUs1));
						break;
					}
				}
			}
		}
		return containsPairs;
	}

	private boolean minimalEcoreExist(String confPair, String conflictReason)
			throws CdaReportDirNotFound, CdaReportDirIsNotADirectory, CdaReportDirIsEmpty {
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

	public JSONArray readJsonArrayFromFile() throws EmptyOrNotExistJsonFile, JsonFileNotFound {
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

	private int findMaximal(List<ConflictPair> conflictPairs, String pair1, String pair2) {
		for (ConflictPair conflictPair : conflictPairs) {
			if ((conflictPair.getConflictPair1().equals(pair1) && conflictPair.getConflictPair2().equals(pair2))
					|| (conflictPair.getConflictPair1().equals(pair2)
							&& conflictPair.getConflictPair2().equals(pair1))) {
				return conflictPair.getMaximal();
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
	private void getUssTexts(String usPair, ConflictingItems conflictingItems) throws JsonFileNotFound {
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
						// all words should be lower case to avoid mismatching
						String usText = jsonObject.getString("Text").toLowerCase();
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
			ConflictingItems conflictingItems, FileWriter fileWriter)
			throws IOException, EmptyOrNotExistJsonFile, JsonFileNotFound {
		String us1 = getUsName1(usPair);
		String us2 = getUsName2(usPair);

		// TODO: Check if conflictPairs can be replace with conflictingItems
		// add conflict pairs with maximal overlapping and save it
		// in order to filling table
		ConflictPair conflictPair = new ConflictPair();

		// TODO here i try to add US_Nr from USs into the highlightConflicts
		conflictingItems.setUsNr1(us1);
		conflictingItems.setUsNr2(us2);

		// get USs Text from JSON File and add them into conflictingItems
		getUssTexts(usPair, conflictingItems);
		try {
			// here I want to send both USs as parameter for highlightingConflicts
			conflictingItems = highlightConflicts(conflictingItems, usPair);

			String highlightedUs2 = conflictingItems.getTextUs2();
			fileWriter.write("\n\n " + us2 + ": " + highlightedUs2.toLowerCase());

			String highlightedUs1 = conflictingItems.getTextUs1();
			fileWriter.write("\n\n " + us1 + ": " + highlightedUs1.toLowerCase());
			conflictPair.setConflictPair1(us1);
			conflictPair.setConflictPair2(us2);
			conflictPair.setMaximal(conflictingItems.getMaxConflictCount());
			conflictPairs.add(conflictPair);

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
	// I want to check both user stories and separate their parts of sentence
	// through comma and first try to find the match is in secondPartOfSentence of
	// both user
	// stories. I should check US1_Part_1 vs US2_part_1, then if US1_Part_2 &&
	// US2_Part_2
	// are exist then try to file the match is in thirdPartOfSentence of both USs
	// then try
	// to replace
	private ConflictingItems highlightConflicts(ConflictingItems conflictingItems, String usPair)
			throws IOException, EmptyOrNotExistJsonFile, JsonFileNotFound {

		String textUs1 = conflictingItems.getTextUs1();
		String textUs2 = conflictingItems.getTextUs2();
		int mainConflict = conflictingItems.getMainConflictCount();
		int benefitConflict = conflictingItems.getBenefitConflictCount();
		if (textUs1.length() <= 0 && textUs2.length() <= 0) {
			return null;
		}
		List<TargetPair> targetsPairs = getCommonTargets(usPair);
		List<ContainsPair> containsPairs = getCommonContains(usPair);
		List<TriggerPair> triggersPairs = getCommonTriggers(usPair);

		// find the index of first comma
		int firstCommaUs1 = textUs1.indexOf(',');
		int firstCommaUs2 = textUs2.indexOf(',');
		// if there is no main part in the sentence
		// which include triggers and targets
		if (firstCommaUs1 == -1 || firstCommaUs2 == -1) {
			return conflictingItems;
		}
		// receive the index of first comma in both Text in USs
		// subString will be like this: I want to be able to ....
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
			// in this sentence part, if so then add hash symbol
			String[] usText = applyHashSymbolTargets(targetsPairs, containsPairs, conflictingItems, subStringSecondUs1,
					subStringSecondUs2);
			textUs1 = subStringFirstUs1 + "," + usText[0];
			textUs2 = subStringFirstUs2 + "," + usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// subString from second comma until the end of story ;)
			String subStringBenefitUs2 = conflictingItems.getTextUs2().substring(benefitPlaceHolderUs2);

			// check if there are hash symbol more than 3 pair first and main section
			// if so then highlight the Persona
			if (hasMoreThanSixHashMarks(textUs1)) {
				usText = highlightPersona(triggersPairs, targetsPairs, conflictingItems, textUs1, textUs2);
				textUs1 = usText[0];
				textUs2 = usText[1];
				// add the number of conflict pairs from main sentence
				mainConflict = mainConflict + Integer.parseInt(usText[2]);
			}
			// Apply hash symbol to common element in the both user stories if any
			usText = applyHashSymbolContaians(containsPairs, conflictingItems, textUs1, textUs2);
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
			String[] usText = applyHashSymbolTargets(targetsPairs, containsPairs, conflictingItems, subStringSecondUs1,
					subStringSecondUs2);
			textUs1 = subStringFirstUs1 + "," + usText[0];
			textUs2 = subStringFirstUs2 + "," + usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// subString from second comma until the end of story ;)
			String subStringBenefitUs1 = conflictingItems.getTextUs1().substring(benefitPlaceHolderUs1);

			// check if there are hash symbol more than 3 pair first and main section
			// if so then highlight the Persona
			if (hasMoreThanSixHashMarks(textUs1)) {
				usText = highlightPersona(triggersPairs, targetsPairs, conflictingItems, textUs1, textUs2);
				textUs1 = usText[0];
				textUs2 = usText[1];
				// add the number of conflict pairs from main sentence
				mainConflict = mainConflict + Integer.parseInt(usText[2]);

			}

			// Apply hash symbol to common element in the both user stories if any
			usText = applyHashSymbolContaians(containsPairs, conflictingItems, textUs1, textUs2);
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
			String[] usText = applyHashSymbolTargets(targetsPairs, containsPairs, conflictingItems, subStringSecondUs1,
					subStringSecondUs2);
			textUs1 = subStringFirstUs1 + "," + usText[0];
			textUs2 = subStringFirstUs2 + "," + usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// check if there are hash symbol more than 3 pair first and main section
			// if so then highlight the Persona
			if (hasMoreThanSixHashMarks(textUs1)) {
				usText = highlightPersona(triggersPairs, targetsPairs, conflictingItems, textUs1, textUs2);
				textUs1 = usText[0];
				textUs2 = usText[1];
				// add the number of conflict pairs from main sentence
				mainConflict = mainConflict + Integer.parseInt(usText[2]);
			}

			// subString from second comma until the end of story ;)
			// I want to check if targetsPair.aciton/entity are already exist
			// in this sentence part, if so then add hash symbol

			// Apply hash symbol to common elements in the both user stories if any
			usText = applyHashSymbolContaians(containsPairs, conflictingItems, textUs1, textUs2);
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
			String[] usText = applyHashSymbolTargets(targetsPairs, containsPairs, conflictingItems, subStringSecondUs1,
					subStringSecondUs2);

			textUs1 = usText[0];
			textUs2 = usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// check if there are hash symbol more than 3 pair first and main section
			// if so then highlight the Persona
			if (hasMoreThanFourHashMarks(textUs1)) {
				usText = highlightPersona(triggersPairs, targetsPairs, conflictingItems, subStringFirstUs1,
						subStringFirstUs2);
				subStringFirstUs1 = usText[0];
				subStringFirstUs2 = usText[1];
				// add the number of conflict pairs from main sentence
				mainConflict = mainConflict + Integer.parseInt(usText[2]);
			}

			// Apply hash symbol to common elements in the both user stories if any
			usText = applyHashSymbolContaians(containsPairs, conflictingItems, textUs1, textUs2);
			textUs1 = usText[0];
			textUs2 = usText[1];
			// add the number of conflict pairs from main sentence
			mainConflict = mainConflict + Integer.parseInt(usText[2]);

			// subString from second comma until the end of story ;)
			String subStringBenefitUs1 = conflictingItems.getTextUs1().substring(benefitPlaceHolderUs1);
			String subStringBenefitUs2 = conflictingItems.getTextUs2().substring(benefitPlaceHolderUs2);

			// I want to check if targetsPair.aciton/entity are already exist
			// in this sentence part, if so then add hash symbol
			// first try to find conflict pairs at the benefit sentence
			usText = applyHashSymbolTargets(targetsPairs, containsPairs, conflictingItems, subStringBenefitUs1,
					subStringBenefitUs2);
			subStringBenefitUs1 = usText[0];
			subStringBenefitUs2 = usText[1];
			// textUs1 = textUs1 + usText[0];
			// textUs2 = textUs2 + usText[1];
			// add the number of conflict pairs from benefit sentence
			benefitConflict = benefitConflict + Integer.parseInt(usText[2]);

			// Apply hash symbol to common elements in the both user stories if any
			usText = applyHashSymbolContaians(containsPairs, conflictingItems, subStringBenefitUs1,
					subStringBenefitUs2);
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
			usText = applyHashSymbolContaians(containsPairs, conflictingItems, textUs1, textUs2);
			textUs1 = subStringFirstUs1 + "," + usText[0];
			textUs2 = subStringFirstUs2 + "," + usText[1];
			// textUs1 = usText[0];
			// textUs2 = usText[1];

		}

		// add amount of founded conflict pairs from main/benefit sentence
		// into Benefit/MainConflict Count
		conflictingItems.setBenefitConflictCount(benefitConflict);
		conflictingItems.setMainConflictCount(mainConflict);
		conflictingItems.setMaxConflictCount(mainConflict + benefitConflict);
		conflictingItems.setTextUs1(textUs1);
		conflictingItems.setTextUs2(textUs2);

		return conflictingItems;

	}

	private boolean hasMoreThanFourHashMarks(String textUs1) {
		long count = textUs1.chars().filter(ch -> ch == '#').count();
		return count >= 4;

	}

	private String[] highlightPersona(List<TriggerPair> triggers, List<TargetPair> targetsPairs,
			ConflictingItems conflictingItems, String subStringFirstUs1, String subStringFirstUs2) {
		String[] usTexts = new String[4];
		int conflictCount = 0;

		// I want to check if TriggersPair persona/action are already exist
		// in this sentence part, if so then add hash symbol
		// Add if and only if the action in triggers pair are exist in
		// conflicted targets pairs
		for (TriggerPair triggerPair : triggers) {
			String persona = triggerPair.getPersona();
			// replace all pairs in this part of sentence if it
			// exist in both sentence parts
			if (subStringFirstUs1.contains(persona) && subStringFirstUs2.contains(persona)) {

				// US_1/US_2 add hash symbol at the beginning and ending of each word
				String[] matches = { persona };
				subStringFirstUs1 = addHashSymbols(subStringFirstUs1, matches);
				subStringFirstUs2 = addHashSymbols(subStringFirstUs2, matches);

				// count conflicts in this sentence part in order to know which sentence
				// part how many conflict pairs exist
				// after highlighting persona it should be check if
				// to pairs of triggers elements exist, then increase the count
				// of elements if and only if the action is also in targets
				conflictCount++;

			}

		}
		usTexts[0] = subStringFirstUs1.replaceAll("#+", "#");
		usTexts[1] = subStringFirstUs2.replaceAll("#+", "#");
		usTexts[2] = String.valueOf(conflictCount);
		return usTexts;
	}

	// replace hash symbol at beginning and ending of founded element
	private String addHashSymbols(String subString, String[] matches) {
		Arrays.sort(matches, Comparator.comparing(String::length).reversed());
		for (String match : matches) {
			subString = subString.replaceAll("\\b" + match + "\\b", "#" + match + "#");
		}
		return subString;
	}

	// Check in the main part of sentence if there is any redundancy
	// more than 6 hash mark exist
	private boolean hasMoreThanSixHashMarks(String textUs1) {
		long count = textUs1.chars().filter(ch -> ch == '#').count();
		return count >= 6;
	}

	private String[] applyHashSymbolTargets(List<TargetPair> targetsPairs, List<ContainsPair> containsPairs,
			ConflictingItems conflictingItems, String subStringUs1, String subStringUs2) {
		String[] usTexts = new String[4];
		int conflictCount = 0;
		// I want to check if targetsPair.aciton/entity are already exist
		// in this sentence part, if so then add hash symbol
		for (TargetPair targetsPair : targetsPairs) {
			// replace all pairs in this part of sentence if it
			// exist in both sentence parts
			String action = targetsPair.getAction();
			String entity = targetsPair.getEntity();
			if ((subStringUs1.contains(action) && subStringUs1.contains(entity))
					&& (subStringUs2.contains(action) && subStringUs2.contains(entity))) {
				// US_1 add hash symbol at the beginning and ending of each word
				String[] actionMatches = { action };
				String[] entityMatches = { entity };
				subStringUs1 = addHashSymbols(subStringUs1, actionMatches);
				subStringUs1 = addHashSymbols(subStringUs1, entityMatches);

				// US_2 add hash symbol at the beginning and ending of each word
				subStringUs2 = addHashSymbols(subStringUs2, actionMatches);
				subStringUs2 = addHashSymbols(subStringUs2, entityMatches);

				// count conflicts in this sentence part in order to know which sentence
				// part how many conflict pairs exist
				conflictCount++;
				// check if entity exist in contains list
			}
		}
		usTexts[0] = subStringUs1.replaceAll("#+", "#");
		usTexts[1] = subStringUs2.replaceAll("#+", "#");
		usTexts[2] = String.valueOf(conflictCount);
		return usTexts;
	}

	private String[] applyHashSymbolContaians(List<ContainsPair> containsPairs, ConflictingItems conflictingItems,
			String subStringUs1, String subStringUs2) {
		String[] usTexts = new String[4];
		int conflictCount = 0;
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
				subStringUs1 = addHashSymbols(subStringUs1, matches);
				subStringUs2 = addHashSymbols(subStringUs2, matches);

				conflictCount++;
				// break in order to avoid of counting redundant items
			}
		}
		usTexts[0] = subStringUs1.replaceAll("#+", "#");
		usTexts[1] = subStringUs2.replaceAll("#+", "#");
		usTexts[2] = String.valueOf(conflictCount);
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
				// add all EReferences from EClass into corresponding
				// type(Contains, Targets, Triggers)
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
