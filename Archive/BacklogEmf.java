package org.henshin.backlog2.Archive;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

// create ECore Model automatically
public class BacklogEmf {


	//public static final String PATH = "src/org/henshin/backlog/";
	/**
	 * Run the bank example.
	 * @param path Relative path to the model files.
	 * @param saveResult Whether the result should be saved.
	 */
	public static void run( ) {

		// Create a resource set with a base directory:
		//HenshinResourceSet resourceSet = new HenshinResourceSet(path);

		// Load the module:
		//CModule module = CModule.loadFromFile("backlog.henshin");
		EcoreFactory eFactory = EcoreFactory.eINSTANCE;

		 EPackage ePackage = eFactory.createEPackage();
		ePackage.setName("product");
		ePackage.setNsPrefix("product");
		ePackage.setNsURI("http://product");

		// Personas
		EClass persona = eFactory.createEClass();
		persona.setName("Persona");
		ePackage.getEClassifiers().add(persona);

		// Personas sub-Class persona1
		EClass persona1 = eFactory.createEClass();
		persona1.setName("UI designer");
		//persona1.getESuperTypes().add(persona);
		ePackage.getEClassifiers().add(persona1);
		//EReference persona
		EReference refPersona = eFactory.createEReference();
		refPersona.setName("personas");
		refPersona.setEType(persona1);
		refPersona.setLowerBound(0);
		refPersona.setUpperBound(-1);
		refPersona.setContainment(true);
		persona.getEStructuralFeatures().add(refPersona);

		//Actions
		EClass action = eFactory.createEClass();
		action.setName("Action");
		ePackage.getEClassifiers().add(action);

		//Actions Sub-Classes primaryAction
		EClass primaryAction = eFactory.createEClass();
		primaryAction.setName("Primary Action");
		//primaryAction.getESuperTypes().add(action);
		ePackage.getEClassifiers().add(primaryAction);
		//EReference primaryAction
		EReference refPrimaryAction = eFactory.createEReference();
		refPrimaryAction.setName("primary actions");
		refPrimaryAction.setEType(primaryAction);
		refPrimaryAction.setLowerBound(0);
		refPrimaryAction.setUpperBound(-1);
		refPrimaryAction.setContainment(true);
		action.getEStructuralFeatures().add(refPrimaryAction);


		//Actions Sub-Classes primaryAction1
		EClass primaryAction1 = eFactory.createEClass();
		primaryAction1.setName("create");
		//primaryAction1.getESuperTypes().add(primaryAction);
		ePackage.getEClassifiers().add(primaryAction1);
		//EReference primaryAction1
		EReference refPrimaryAction1 = eFactory.createEReference();
		refPrimaryAction1.setName("verbs_us_01");
		refPrimaryAction1.setEType(primaryAction1);
		refPrimaryAction1.setLowerBound(0);
		refPrimaryAction1.setUpperBound(-1);
		refPrimaryAction1.setContainment(true);
		primaryAction.getEStructuralFeatures().add(refPrimaryAction1);

		//Actions Sub-Classes primaryAction2
		EClass primaryAction2 = eFactory.createEClass();
		primaryAction2.setName("delete");
		//primaryAction2.getESuperTypes().add(primaryAction);
		ePackage.getEClassifiers().add(primaryAction2);

		//EReference primaryAction2
		EReference refPrimaryAction2 = eFactory.createEReference();
		refPrimaryAction2.setName("verbs_us_02");
		refPrimaryAction2.setEType(primaryAction2);
		refPrimaryAction2.setLowerBound(0);
		refPrimaryAction2.setUpperBound(-1);
		refPrimaryAction2.setContainment(true);
		primaryAction.getEStructuralFeatures().add(refPrimaryAction2);

		//Actions Sub-Classes secondaryAction
		EClass secondaryAction = eFactory.createEClass();
		secondaryAction.setName("Secondary Action");
		//secondaryAction.getESuperTypes().add(action);
		ePackage.getEClassifiers().add(secondaryAction);
		//EReference secondaryAction
		EReference refsecondaryAction = eFactory.createEReference();
		refsecondaryAction.setName("secondary actions");
		refsecondaryAction.setEType(secondaryAction);
		refsecondaryAction.setLowerBound(0);
		refsecondaryAction.setUpperBound(-1);
		refsecondaryAction.setContainment(true);
		action.getEStructuralFeatures().add(refsecondaryAction);

		//Actions Sub-Classes secondaryAction1
		EClass secondaryAction1 = eFactory.createEClass();
		secondaryAction1.setName("validate");
		//secondaryAction1.getESuperTypes().add(secondaryAction);
		ePackage.getEClassifiers().add(secondaryAction1);
		// EReference secondaryAction1
		EReference refsecondaryAction1 = eFactory.createEReference();
		refsecondaryAction1.setName("verbs");
		refsecondaryAction1.setEType(secondaryAction1);
		refsecondaryAction1.setLowerBound(0);
		refsecondaryAction1.setUpperBound(-1);
		refsecondaryAction1.setContainment(true);
		secondaryAction.getEStructuralFeatures().add(refsecondaryAction1);

		// Entities
		EClass entity = eFactory.createEClass();
		entity.setName("Entity");
		ePackage.getEClassifiers().add(entity);

		//Entity sub-Classes primaryEntity
		EClass primaryEntity = eFactory.createEClass();
		primaryEntity.setName("Primary Entity");
		//primaryEntity.getESuperTypes().add(entity);
		ePackage.getEClassifiers().add(primaryEntity);
		// EReference primary Entity
		EReference refPrimaryEntity = eFactory.createEReference();
		refPrimaryEntity.setName("primary entities");
		refPrimaryEntity.setEType(primaryEntity);
		refPrimaryEntity.setLowerBound(0);
		refPrimaryEntity.setUpperBound(-1);
		refPrimaryEntity.setContainment(true);
		entity.getEStructuralFeatures().add(refPrimaryEntity);

		// Entity sub-Classes primaryEntity1
		EClass primaryEntity1 = eFactory.createEClass();
		primaryEntity1.setName("user testing");
		//primaryEntity1.getESuperTypes().add(primaryEntity);
		ePackage.getEClassifiers().add(primaryEntity1);
		// EReference primary Entity1
		EReference refPrimaryEntity1 = eFactory.createEReference();
		refPrimaryEntity1.setName("nouns");
		refPrimaryEntity1.setEType(primaryEntity1);
		refPrimaryEntity1.setLowerBound(0);
		refPrimaryEntity1.setUpperBound(-1);
		refPrimaryEntity1.setContainment(true);
		primaryEntity.getEStructuralFeatures().add(refPrimaryEntity1);

		//Entity sub-Classes secondaryEntity
		EClass secondaryEntity = eFactory.createEClass();
		secondaryEntity.setName("Secondary Entity");
		//secondaryEntity.getESuperTypes().add(entity);
		ePackage.getEClassifiers().add(secondaryEntity);
		// EReference secondary Entity
		EReference refSecondaryEntity = eFactory.createEReference();
		refSecondaryEntity.setName("secondary entities");
		refSecondaryEntity.setEType(secondaryEntity);
		refSecondaryEntity.setLowerBound(0);
		refSecondaryEntity.setUpperBound(-1);
		refSecondaryEntity.setContainment(true);
		entity.getEStructuralFeatures().add(refSecondaryEntity);

		//Entity sub-Classes secondaryEntity
		EClass secondaryEntity1 = eFactory.createEClass();
		secondaryEntity1.setName("stakeholder UI improvement requests");
		//secondaryEntity1.getESuperTypes().add(secondaryEntity);
		ePackage.getEClassifiers().add(secondaryEntity1);

		//EReference secondary Entity1
		EReference refSecondaryEntity1 = eFactory.createEReference();
		refSecondaryEntity1.setName("nouns");
		refSecondaryEntity1.setEType(secondaryEntity1);
		refSecondaryEntity1.setLowerBound(0);
		refSecondaryEntity1.setUpperBound(-1);
		refSecondaryEntity1.setContainment(true);
		secondaryEntity.getEStructuralFeatures().add(refSecondaryEntity1);

		EReference triggers1 = eFactory.createEReference();
		triggers1.setName("triggers_us_01");
		triggers1.setEType(primaryAction1);
		triggers1.setLowerBound(0);
		triggers1.setUpperBound(-1);
		persona1.getEStructuralFeatures().add(triggers1);


		EReference targest1 = eFactory.createEReference();
		targest1.setName("targets_us_01");
		targest1.setEType(primaryEntity1);
		targest1.setLowerBound(0);
		targest1.setUpperBound(-1);
		primaryAction1.getEStructuralFeatures().add(targest1);

		EReference triggers2 = eFactory.createEReference();
		triggers2.setName("triggers_us_02");
		triggers2.setEType(primaryAction2);
		triggers2.setLowerBound(0);
		triggers2.setUpperBound(-1);
		persona1.getEStructuralFeatures().add(triggers2);

		EReference targest2 = eFactory.createEReference();
		targest2.setName("targets_us_02");
		targest2.setEType(primaryEntity1);
		targest2.setLowerBound(0);
		targest2.setUpperBound(-1);
		primaryAction2.getEStructuralFeatures().add(targest2);


		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl());

		Resource resource = resourceSet.createResource(URI.createURI("Backlog_v3.ecore"));
		resource.getContents().add(ePackage);

		try {
            resource.save(null);
            System.out.println("Ecore model saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }






	}

	public static void main(String[] args) {
		run();
	}


}
