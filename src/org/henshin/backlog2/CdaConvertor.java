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
import org.eclipse.emf.henshin.model.compact.CNode;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//-----Delete Annotation for Attributes actions and entities plus their entities which have target edges or triggers edge 
public class CdaConvertor {
	private static String dirName;

	public CdaConvertor(String directroyName) {
		dirName = directroyName;
	}

	private static FileWriter cdaWriter = null;
	private static ArrayList<String> arrayMaximal;
	private static ArrayList<String> arrayExMax;
	private static List<SecondaryEntity> secondaryEntity;
	private static List<PrimaryEntity> primaryEntity;
	private static List<SecondaryAction> secondaryAction;
	private static List<PrimaryAction> primaryAction;

	public static void main(String[] args) throws IOException {

		CdaConvertor cdaConvertor = new CdaConvertor("2024.02.05_13.11.22");
		cdaConvertor.extractReports();

	}

	public void extractReports() throws IOException {
		String path = "C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\" + dirName;

		File main = new File(path);
		try {
			File totalCda = new File(path + "\\Textual_Report.txt");
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

			File conflictReasonDir = new File(path + "\\" + criticalPairsDir[i]);

			if (!conflictReasonDir.isFile()) {

				String[] conflictReasonListing = conflictReasonDir.list();
				// Iterate through conflict reasons if there is more than one conflict_reason.

				if (conflictReasonListing.length > 1) {
					cdaWriter.write("\n------------------[Potential-Critical-Pair-Found]--------------------------\n"
							+ criticalPairsDir[i] + "\n  ");
					arrayMaximal = new ArrayList<String>();
					arrayExMax = new ArrayList<String>();
					secondaryEntity = new ArrayList<>();
					primaryEntity = new ArrayList<>();
					secondaryAction = new ArrayList<>();
					primaryAction = new ArrayList<>();

					cdaWriter.write("\nConflict-Reasons are: ");
					for (int j = 0; j < conflictReasonListing.length; j++) {

						File minimalModelEcoreFile = new File(path + "\\" + criticalPairsDir[i] + "\\"
								+ conflictReasonListing[j] + "\\minimal-model.ecore");
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

										iteratePackages(minimalPackage);

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
					writeUsText(criticalPairsDir[i], arrayExMax);
					cdaWriter.write("\n\nMaximal conflict element between " + criticalPairsDir[i] + " is: "
							+ arrayMaximal.size() + "\n");

				}
			}
		}
		cdaWriter.close();
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

		for (PrimaryAction action : primaryAction) {
			us = us.replaceFirst("\\b" + action.getName() + "\\b", "#" + action.getName() + "#");

		}
		for (PrimaryEntity entity : primaryEntity) {
			us = us.replaceFirst("\\b" + entity.getName() + "\\b", "#" + entity.getName() + "#");

		}
		int firstComma = us.indexOf(',');
		int secondComma = us.indexOf(',', firstComma + 1);
		String subString = us.substring(secondComma + 1);
		for (SecondaryAction secondaryAction : secondaryAction) {
			subString = subString.replaceFirst("\\b" + secondaryAction.getName() + "\\b",
					"#" + secondaryAction.getName() + "#");
			us = us.substring(0, secondComma + 1) + subString;
		}
		for (SecondaryEntity secondaryEntity : secondaryEntity) {
			subString = subString.replaceFirst("\\b" + secondaryEntity.getName() + "\\b",
					"#" + secondaryEntity.getName() + "#");

		}
		us = us.substring(0, secondComma + 1) + subString;
		return us;
	}

	private static void iteratePackages(EPackage minimalPackage) throws IOException {

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
						switch (className) {
						case "Primary Action":
							primaryAction.add(new PrimaryAction(attName));
							break;
						case "Secondary Action":
							secondaryAction.add(new SecondaryAction(attName));
							break;
						case "Secondary Entity":
							secondaryEntity.add(new SecondaryEntity(attName));
							break;
						case "Primary Entity":
							primaryEntity.add(new PrimaryEntity(attName));
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
