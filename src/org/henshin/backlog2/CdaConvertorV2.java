package org.henshin.backlog2;

//import org.eclipse.core.internal.resources.Resource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
import java.util.List;
import java.io.FileWriter;
import java.util.ArrayList;

//-----Delete Annotation for Attributes actions and entities plus their entities which have target edges or triggers edge 
/**
 * @author amirr
 *
 */
public class CdaConvertorV2 {
	private static String dirName;

	public CdaConvertorV2(String directroyName) {
		dirName = directroyName;
	}

	private static FileWriter cdaWriter = null;
	private static ArrayList<String> arrayMaximal;
	private static ArrayList<String> arrayExMax;
	private static ArrayList<String> arrayDelDelConflict;
	private static ArrayList<String> arrayDelUseConflict;
	private static List<ConflictPair> conflictPairs;
	private static ConflictingItems conflictingItems;
	private static List<String> pairList = new ArrayList<>();
	private static List<String> pairListSeperate = new ArrayList<>();

	public static void main(String[] args) throws IOException {

		CdaConvertorV2 cdaConvertor = new CdaConvertorV2("2024.02.05_13.11.22");
		cdaConvertor.extractReports();

	}

	public void extractReports() throws IOException {
		String path = "C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\" + dirName;
		File main = new File(path);
		File totalCda = null;
		// Instantiate conflictPairs
		conflictPairs = new ArrayList<>();
		try {
			totalCda = new File(path + "\\Textual_Report_v2.txt");
			if (totalCda.createNewFile()) {
				System.out.println("CDA file created succesfully: " + totalCda.getName());
				cdaWriter = new FileWriter(totalCda);
			} else {
				cdaWriter = new FileWriter(totalCda);
				System.out.println("File already exists. Try to overwrite..!");
			}
		} catch (IOException e) {
			System.out.println("An error occured.");
			e.printStackTrace();
		}

		// List critical pairs
		String[] criticalPairsDir = main.list();
		// Iterate through critical pairs
		for (int i = 0; i < criticalPairsDir.length; i++) {
			if (!checkIfReportExist(criticalPairsDir[i])) {

				File conflictReasonDir = new File(path + "\\" + criticalPairsDir[i]);

				if (!conflictReasonDir.isFile()) {
					String conflictReason = null;
					arrayExMax = new ArrayList<String>();
					conflictingItems = new ConflictingItems();
					String[] conflictReasonListing = conflictReasonDir.list();
					// Iterate through conflict reasons if there is more than one conflict_reason.

					if (conflictReasonListing.length > 1) {

						arrayMaximal = new ArrayList<String>();

						conflictReason = null;

						for (int j = 0; j < conflictReasonListing.length; j++) {
							arrayDelDelConflict = new ArrayList<String>();
							arrayDelUseConflict = new ArrayList<String>();
							File minimalModelEcoreFile = new File(path + "\\" + criticalPairsDir[i] + "\\"
									+ conflictReasonListing[j] + "\\minimal-model.ecore");
							try {
								if (minimalModelEcoreFile.exists()) {
									Resource.Factory.Registry resourceFactoryRegistry = Resource.Factory.Registry.INSTANCE;
									resourceFactoryRegistry.getExtensionToFactoryMap().put("ecore",
											new XMIResourceFactoryImpl());
									ResourceSet resourceSet = new ResourceSetImpl();
									Resource resource = resourceSet.getResource(
											URI.createFileURI(minimalModelEcoreFile.getAbsolutePath()), true);
									if (resource != null && !resource.getContents().isEmpty()) {

										for (EObject eObject : resource.getContents()) {
											EPackage minimalPackage = (EPackage) eObject;

											// check whether the conflict is del-use or del-del conflict
											conflictReason = conflictReasonListing[j];
											conflictReason = conflictReason.replaceAll("\\S\\d+\\S\\s(.*)", "$1");

											iteratePackages(minimalPackage, conflictReason);

										}
									} else {
										System.out.println("minimal-model.ecore not found!");
										cdaWriter.write("minimal-model.ecore not found!");
									}
								} else {
									System.err.println("No registered resource factory founded: "
											+ minimalModelEcoreFile.getAbsolutePath());
									cdaWriter.write("No registered resource factory founded: "
											+ minimalModelEcoreFile.getAbsolutePath());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}
					// Write only the Elements which contains Primary/Secondary Action and
					// Primary/Secondary Entity
					if (hasEntitys() && hasActions() && hasTargets()) {
						cdaWriter.write(
								"\n------------------[Potentially redundant user stries found]--------------------------\n"
										+ criticalPairsDir[i] + "\n  ");
						cdaWriter.write("\nConflict-Reasons are: ");
						conflictingItems.printConflictingItems(cdaWriter);
						if (conflictReason.equals("Delete - Delete conflict reason")
								&& !arrayDelDelConflict.isEmpty()) {

							cdaWriter.write("\nThere is " + conflictReason + " between " + criticalPairsDir[i]
									+ ". If the following elements are deleted or modified, "
									+ "the other user story can no longer be applied: " + arrayDelDelConflict.toString()
									+ "\n");

						} else if (conflictReason.equals("Delete conflict reason") && !arrayDelUseConflict.isEmpty()) {
							checkWhichUsCauseConflict(criticalPairsDir[i], conflictReason);

						}
						writeUsText(criticalPairsDir[i], arrayExMax);
						cdaWriter.write("\n\nMaximal number of conflicted elements between " + criticalPairsDir[i]
								+ " is: " + arrayMaximal.size() + "\n");

					}

				}

			}
		}
		cdaWriter.close();
		writeTable(totalCda);

	}

	// in order to insert the table at the very first place of the file
	// we need to first store the report at StringBuilder and then make
	// file again but at this time writing the table first
	private void writeTable(File totalCda) throws IOException {

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
			table.append(
					"* Table of potential redundancies between user stories and the number of overlapping elements\n\n");
			// table.append("\t");
			for (ConflictPair it : conflictPairs) {
				if (!pairListSeperate.contains(it.getConflictPair1())) {
					pairListSeperate.add(it.getConflictPair1());
				}
				if (!pairListSeperate.contains(it.getConflictPair2())) {
					pairListSeperate.add(it.getConflictPair2());
				}
			}
			String[][] stringTable = createTable(pairListSeperate, conflictPairs);
			int numRows = stringTable.length;
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
			table[0][i + 1] = pairListSeperate2.get(i); // header row
			table[i + 1][0] = pairListSeperate2.get(i); // first column
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
	// or user_story_YY_AND_user_story_XX if yes return true,
	// Otherwise return false
	private boolean checkIfReportExist(String usPairs) {
		if (!pairList.contains(usPairs)) {
			String us1 = usPairs.replaceAll("(.*)_AND.*", "$1");
			String us2 = usPairs.replaceAll(".*_AND_(.*)", "$1");
			pairList.add(usPairs);
			// Add also reverse item
			pairList.add(us2 + "_AND_" + us1);

			return false;
		} else {

			return true;
		}

	}

	private boolean hasTargets() {
		for (String item : arrayExMax) {
			for (Targets target : conflictingItems.getTargets()) {
				if (item.equals(target.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasActions() {
		for (String item : arrayExMax) {
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

	private boolean hasEntitys() {
		for (String item : arrayExMax) {
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

	private void checkWhichUsCauseConflict(String string, String conflictReason) throws IOException {
		JSONArray json = null;
		String usNr = null;
		String us1 = string.replaceAll("(.*)_AND.*", "$1");
		String us2 = string.replaceAll(".*_AND_(.*)", "$1");
		String fileName = "C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\g03_baseline_pos_num.json";

		try (FileReader reader = new FileReader(fileName)) {
			JSONTokener tokener = new JSONTokener(reader);

			// Read JSON file
			json = new JSONArray(tokener);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < json.length(); i++) {
			try {
				JSONObject jsonObject = json.getJSONObject(i);

				if (jsonObject.has("US_Nr") && jsonObject.has("Targets")) {
					usNr = jsonObject.getString("US_Nr");
					if (usNr.equals(us1)) {
						JSONArray target = jsonObject.getJSONArray("Targets");
						for (int j = 0; j < target.length(); j++) {
							JSONArray inArray = target.getJSONArray(j);
							for (String item : arrayDelUseConflict) {
								if (inArray.getString(0).toLowerCase().equals(item)
										|| inArray.getString(1).toLowerCase().equals(item)) {
									cdaWriter.write("\nThere is " + conflictReason + " between " + string
											+ ". If the element \"" + item + "\" in " + us1
											+ " deleted or modified that used by " + us2 + ", this" + " means the "
											+ us2 + " can no longer be applied. \n");
								}
							}

						}
					} else if (usNr.equals(us2)) {
						JSONArray target = jsonObject.getJSONArray("Targets");
						for (int j = 0; j < target.length(); j++) {
							JSONArray inArray = target.getJSONArray(j);
							for (String item : arrayDelUseConflict) {
								if (inArray.getString(0).toLowerCase().equals(item)
										|| inArray.getString(1).toLowerCase().equals(item)) {
									cdaWriter.write("\n\nThere is " + conflictReason + " between " + string
											+ ". If the element \"" + item + "\" in " + us2
											+ " deleted or modified that used by " + us1 + ", this" + " means the "
											+ us1 + " can no longer be applied. ");
								}
							}

						}
					}
				} else {
					cdaWriter.write("US_Nr or Targets Element not found in JSON-Data!");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	private void writeUsText(String string, ArrayList<String> arrayMax) throws IOException {
		JSONArray json = null;
		String usNr = null;
		String us1 = string.replaceAll("(.*)_AND.*", "$1");
		String us2 = string.replaceAll(".*_AND_(.*)", "$1");
		// add conflict pairs with maximal overlapping and save it
		// in order to filling table
		ConflictPair cp = new ConflictPair();
		cp.setConflictPair1(us1);
		cp.setConflictPair2(us2);
		cp.setMaximal(arrayMax.size());
		conflictPairs.add(cp);
		String fileName = "C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\g03_baseline_pos_num.json";

		try (FileReader reader = new FileReader(fileName)) {
			JSONTokener tokener = new JSONTokener(reader);

			// Read JSON file
			json = new JSONArray(tokener);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < json.length(); i++) {
			try {
				JSONObject jsonObject = json.getJSONObject(i);

				if (jsonObject.has("US_Nr") && jsonObject.has("Text")) {
					usNr = jsonObject.getString("US_Nr");
					if (usNr.equals(us1)) {
						String highlightedUs1 = highlightConflict(jsonObject.getString("Text").toLowerCase());
						cdaWriter.write("\n\n " + us1 + ": " + highlightedUs1.toLowerCase());

					} else if (usNr.equals(us2)) {
						String highlightedUs2 = highlightConflict(jsonObject.getString("Text").toLowerCase());
						cdaWriter.write("\n\n " + us2 + ": " + highlightedUs2);

					}
				} else {
					cdaWriter.write("US_Nr or Text Element not found in JSON-Data!");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	private String highlightConflict(String us) throws IOException {

		for (PrimaryAction action : conflictingItems.getPrimaryActions()) {
			us = us.replaceFirst("\\b" + action.getName() + "\\b", "#" + action.getName() + "#");

		}
		for (PrimaryEntity entity : conflictingItems.getPrimaryEntity()) {
			us = us.replaceFirst("\\b" + entity.getName() + "\\b", "#" + entity.getName() + "#");
		}

		int firstComma = us.indexOf(',');
		int secondComma = us.indexOf(',', firstComma + 1);
		String subString = us.substring(secondComma + 1);
		for (SecondaryAction secondaryAction : conflictingItems.getSecondaryAction()) {
			subString = subString.replaceFirst("\\b" + secondaryAction.getName() + "\\b",
					"#" + secondaryAction.getName() + "#");
			us = us.substring(0, secondComma + 1) + subString;
		}
		for (SecondaryEntity secondaryEntity : conflictingItems.getSecondaryEntity()) {
			subString = subString.replaceFirst("\\b" + secondaryEntity.getName() + "\\b",
					"#" + secondaryEntity.getName() + "#");

		}
		us = us.substring(0, secondComma + 1) + subString;
		return us;
	}

	private static void iteratePackages(EPackage minimalPackage, String conflictReasonListing) throws IOException {

		String className = null;

		for (EClassifier eClassifier : minimalPackage.getEClassifiers()) {
			if (eClassifier instanceof EClass) {
				EClass eClass = (EClass) eClassifier;
				className = getClassName(eClass.getName());

				if (eClass.getName().contains("#")) {

					EAttribute attribute = (EAttribute) eClass.getEStructuralFeature(0);
					if (attribute != null && !arrayMaximal.contains(attribute.getName())) {

						String attName = getAttName(attribute.getName());
						arrayMaximal.add(attribute.getName());
						arrayExMax.add(attName);
						if (conflictReasonListing.equals("Delete - Delete conflict reason")) {
							arrayDelDelConflict.add(attName);
						} else if (conflictReasonListing.equals("Delete conflict reason")) {
							arrayDelUseConflict.add(attName);
						} else {
							cdaWriter.write("\n[error] either Del-Del or Del-Use Conflict Elements found!\n");
						}

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
					if (eReference.getName().contains("#") && !arrayMaximal.contains(eReference.getName())) {
						String refName = getRefName(eReference.getName());
						arrayMaximal.add(eReference.getName());
						arrayExMax.add(refName);
						arrayDelDelConflict.add(refName);
						if (refName.equals("triggers")) {
							conflictingItems.addTriggers(new Triggers(refName, className));

						} else if (refName.equals("targets")) {
							conflictingItems.addTargets(new Targets(refName, className));

						}

					}

				}
			}

		}

	}

	private static String getRefName(String name) {
		name = name.replaceAll("#", "");
		return name;
	}

	private static String getClassName(String name) {
		name = name.replaceAll("#", "").replaceAll(".*:(.*)", "$1");
		return name;
	}

	private static String getAttName(String name) {
		name = name.replaceAll("name=\"(.*)\"->.*\"(.*).*", "$1");
		return name;
	}

}
