package org.henshin.backlog2;

//import org.eclipse.core.internal.resources.Resource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import java.io.FileReader;
import java.io.File;
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

//-----Delete Annotation for Attributes actions and entities plus their entities which have target edges or triggers edge 
public class CdaConvertor {
	private static String dirName;

	public CdaConvertor(String directroyName) {
		dirName = directroyName;
	}

	private static FileWriter cdaWriter = null;
	private static int maximal;
	private static ArrayList<String> arrayMaximal;
	private static ArrayList<String> arrayMinimal;

	public static void main(String[] args) throws IOException {

		// TODO: Create a parameter to input in Terminal and add the file CDA_Result
		// into the Directory

		CdaConvertor cdaConvertor = new CdaConvertor("2024.02.05_13.11.22");
		cdaConvertor.extractReports();

	}

	public void extractReports() throws IOException {
		String path = "C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\" + dirName;

		File main = new File(path);
		try {
			File totalCda = new File(path + "\\CDA_Result.txt");
			if (totalCda.createNewFile()) {
				System.out.println("CDA file created succesfully: " + totalCda.getName());
				cdaWriter = new FileWriter(totalCda);
			} else {
				cdaWriter = new FileWriter(totalCda);
				System.out.println("File already exists.");
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
			System.out.println("iterate throuh critical pairs: ");
			if (!conflictReasonDir.isFile()) {
				// List conflict reason
				String[] conflictReasonListing = conflictReasonDir.list();
				// Iterate through conflict reasons
				cdaWriter.write("\n------------------[Critical-Pair-Found]--------------------------\n"
						+ criticalPairsDir[i] + "\n  ");
				maximal = 0;
				arrayMaximal = new ArrayList<String>();
				for (int j = 0; j < conflictReasonListing.length; j++) {
					File minimalModelEcoreFile = new File(path + "\\" + criticalPairsDir[i] + "\\"
							+ conflictReasonListing[j] + "\\minimal-model.ecore");
					try {
						if (minimalModelEcoreFile.exists()) {
							System.out.println("minimal-model.ecore found!");
							cdaWriter.write("\n                  [Conflict-Reason-Found]                          \n"
									+ criticalPairsDir[i] + "\n  " + conflictReasonListing[j] + "\n");
							Resource.Factory.Registry resourceFactoryRegistry = Resource.Factory.Registry.INSTANCE;
							resourceFactoryRegistry.getExtensionToFactoryMap().put("ecore",
									new XMIResourceFactoryImpl());
							ResourceSet resourceSet = new ResourceSetImpl();
							Resource resource = resourceSet
									.getResource(URI.createFileURI(minimalModelEcoreFile.getAbsolutePath()), true);
							if (resource != null && !resource.getContents().isEmpty()) {
								int count = 1;
								for (EObject eObject : resource.getContents()) {
									EPackage minimalPackage = (EPackage) eObject;
									System.out.println(
											"[Minimal-Model-Found] " + minimalModelEcoreFile.getAbsolutePath());

									System.out.println("  [Start] ************* Minimal-Package: " + "[" + count + "]");
									cdaWriter.write("\n  [Start] ************* Minimal-Package: " + "[" + count + "]");
									iteratePackages(minimalPackage);
									System.out.println("  [End] *************** Minimal-Package\n");
									cdaWriter.write("  [End] *************** Minimal-Package\n");
									count++;
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
				if (maximal > 1) {

					cdaWriter.write("Maximal conflict element between: " + criticalPairsDir[i] + " is: " + maximal);
				}
			}
		}
		cdaWriter.close();

	}

	

	private static void iteratePackages(EPackage minimalPackage) throws IOException {
		arrayMinimal = new ArrayList<String>();
		System.out.println("  Package: " + minimalPackage.getName());
		cdaWriter.write("\n  Package: " + minimalPackage.getName());
		int conflict = 0;
		for (EClassifier eClassifier : minimalPackage.getEClassifiers()) {
			if (eClassifier instanceof EClass) {
				EClass eClass = (EClass) eClassifier;
				if (eClass.getName().contains("#")) {
					// conflict++;

					cdaWriter.write("\n  EClass: " + eClass.getName());
					EAttribute attribute = (EAttribute) eClass.getEStructuralFeature(0);
					if (attribute != null) {
						cdaWriter.write("\n  EAttribute: " + attribute.getName());
						conflict++;
						arrayMinimal.add(attribute.getName());
					} else {
						cdaWriter.write("Attribute not found!\n");
					}

				}

//				for (EAttribute eAttribute : eClass.getEAttributes()) {
//					cdaWriter.write("  EAttribute: " + eAttribute.getName() + "\n");
//					
//
//				}
				for (EReference eReference : eClass.getEReferences()) {
					if (eReference.getName().contains("#")) {
						conflict++;
						arrayMinimal.add(eReference.getName());
						cdaWriter.write("\n  EClass: " + eClass.getName());
						cdaWriter.write("\n  ERefenrence: " + eReference.getName());
					}

				}
			}

		}
		cdaWriter.write("\n  Founded conflicts-Elements in MinimalPackage: " + conflict + "\n");
		if (maximal == 0 || conflict > maximal) {
			maximal = conflict;
			arrayMaximal = arrayMinimal;
		}
		if (conflict == maximal && conflict > 1) {
			// Compare arrays if the items are equivalent
			Collections.sort(arrayMinimal);
			Collections.sort(arrayMaximal);
			if (!arrayMinimal.equals(arrayMaximal)) {
				cdaWriter.write("\n  Another Maximal-Graph found: " + arrayMinimal.toString() + "\n");
			}
			// if they are equivalent, just ignore this
			// if they are not equivalen then print both
		}

	}

}
