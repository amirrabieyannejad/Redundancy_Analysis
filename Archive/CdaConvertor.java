package org.henshin.backlog2.Archive;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

// Make CDA Report which all Maximal Conflict going to be reported
public class CdaConvertor {
	private static String dirName;

	public CdaConvertor(String directroyName) {
		dirName = directroyName;
	}

	private static FileWriter cdaWriter = null;
	private static ArrayList<String> arrayMaximal;
	private static ArrayList<String> arrayExMax;
	private static ArrayList<String> arrayDelDelConflict;
	private static ArrayList<String> arrayDelUseConflict;
	private static List<org.henshin.backlog2.SecondaryEntity> secondaryEntity;
	private static List<org.henshin.backlog2.PrimaryEntity> primaryEntity;
	private static List<org.henshin.backlog2.SecondaryAction> secondaryAction;
	private static List<org.henshin.backlog2.PrimaryAction> primaryAction;

	public static void main(String[] args) throws IOException {

		CdaConvertor cdaConvertor = new CdaConvertor("2024.02.05_13.11.22");
		cdaConvertor.extractReports();

	}

	public void extractReports() throws IOException {
		String path = "C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\" + dirName;

		File main = new File(path);
		try {
			File totalCda = new File(path + "\\Textual_Report_v1.txt");
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
		for (String element : criticalPairsDir) {

			File conflictReasonDir = new File(path + "\\" + element);

			if (!conflictReasonDir.isFile()) {

				String[] conflictReasonListing = conflictReasonDir.list();
				// Iterate through conflict reasons if there is more than one conflict_reason.

				if (conflictReasonListing.length > 1) {
					cdaWriter.write("\n------------------[Potential-Critical-Pair-Found]--------------------------\n"
							+ element + "\n  ");
					arrayMaximal = new ArrayList<>();
					arrayExMax = new ArrayList<>();
					secondaryEntity = new ArrayList<>();
					primaryEntity = new ArrayList<>();
					secondaryAction = new ArrayList<>();
					primaryAction = new ArrayList<>();
					String conflictReason = null;

					cdaWriter.write("\nConflict-Reasons are: ");
					for (String element2 : conflictReasonListing) {
						arrayDelDelConflict = new ArrayList<>();
						arrayDelUseConflict = new ArrayList<>();
						File minimalModelEcoreFile = new File(path + "\\" + element + "\\"
								+ element2 + "\\minimal-model.ecore");
						try {
							if (minimalModelEcoreFile.exists()) {
								Resource.Factory.Registry resourceFactoryRegistry = Resource.Factory.Registry.INSTANCE;
								resourceFactoryRegistry.getExtensionToFactoryMap().put("ecore",
										new XMIResourceFactoryImpl());
								ResourceSet resourceSet = new ResourceSetImpl();
								Resource resource = resourceSet
										.getResource(URI.createFileURI(minimalModelEcoreFile.getAbsolutePath()), true);
								if (resource != null && !resource.getContents().isEmpty()) {

									for (EObject eObject : resource.getContents()) {
										EPackage minimalPackage = (EPackage) eObject;

										// check whther the conflict is del-use or del-del conflict
										conflictReason = element2;
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

						if (conflictReason.equals("Delete - Delete conflict reason")
								&& !arrayDelDelConflict.isEmpty()) {

							cdaWriter.write("\nThere is " + conflictReason + " between " + element
									+ ". If the following elements are deleted or modified, "
									+ "the other user story can no longer be applied: " + arrayDelDelConflict.toString()
									+ "\n");

						} else if (conflictReason.equals("Delete conflict reason") && !arrayDelUseConflict.isEmpty()) {
							checkWhichUsCauseConflict(element, conflictReason);

						}

					}

					writeUsText(element, arrayExMax);
					cdaWriter.write("\n\nMaximal conflict element between " + element + " is: "
							+ arrayMaximal.size() + "\n");

				}
			}
		}
		cdaWriter.close();
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
						// cdaWriter.write("\n\n " + us1 + ": " + jsonObject.getString("Text"));
					} else if (usNr.equals(us2)) {
						String highlightedUs2 = highlightConflict(jsonObject.getString("Text").toLowerCase());
						cdaWriter.write("\n\n " + us2 + ": " + highlightedUs2);
						// cdaWriter.write("\n\n " + us2 + ": " + jsonObject.getString("Text"));
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

		for (org.henshin.backlog2.PrimaryAction action : primaryAction) {
			us = us.replaceFirst("\\b" + action.getName() + "\\b", "#" + action.getName() + "#");

		}
		for (org.henshin.backlog2.PrimaryEntity entity : primaryEntity) {
			us = us.replaceFirst("\\b" + entity.getName() + "\\b", "#" + entity.getName() + "#");

		}
		int firstComma = us.indexOf(',');
		int secondComma = us.indexOf(',', firstComma + 1);
		String subString = us.substring(secondComma + 1);
		for (org.henshin.backlog2.SecondaryAction secondaryAction : secondaryAction) {
			subString = subString.replaceFirst("\\b" + secondaryAction.getName() + "\\b",
					"#" + secondaryAction.getName() + "#");
			us = us.substring(0, secondComma + 1) + subString;
		}
		for (org.henshin.backlog2.SecondaryEntity secondaryEntity : secondaryEntity) {
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
						cdaWriter.write("\n* " + className + ": " + attName);
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
							primaryAction.add(new org.henshin.backlog2.PrimaryAction(attName));
							break;
						case "Secondary Action":
							secondaryAction.add(new org.henshin.backlog2.SecondaryAction(attName));
							break;
						case "Secondary Entity":
							secondaryEntity.add(new org.henshin.backlog2.SecondaryEntity(attName));
							break;
						case "Primary Entity":
							primaryEntity.add(new org.henshin.backlog2.PrimaryEntity(attName));
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
						cdaWriter.write("\n* " + className + ": " + refName);

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
