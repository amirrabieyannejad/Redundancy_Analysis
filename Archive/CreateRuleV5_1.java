package org.henshin.backlog2.Archive;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.compact.CModule;
import org.eclipse.emf.henshin.model.compact.CNode;
import org.eclipse.emf.henshin.model.compact.CRule;

// Henshin Rules: Backlog_v5
// ECore Modell: Backlog_v2
public class CreateRuleV5_1 {

	//public static final String PATH = "src/org/henshin/backlog/";
	/**
	 * Run the bank example.
	 * @param path Relative path to the model files.
	 * @param saveResult Whether the result should be saved.
	 */
	public static CModule module = new CModule("backlog_v5");
	public static void deleteMyLandmark( ) {

		// Create a resource set with a base directory:
		//HenshinResourceSet resourceSet = new HenshinResourceSet(path);

		// Load the module:
		//CModule module = CModule.loadFromFile("backlog.henshin");

		//module =  CModule.loadFromFile(path+"/backlog.henshin");



		CRule userStory = module.createRule("delete_my_landmark");

		// Create Parameters
		userStory.createParameter("in", "uid", EcorePackage.Literals.EINT);
		//userStory.createParameter("in", "primaryAction", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "secondaryAction", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "primaryEntity", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);

		//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);

		// Create Nodes
		CNode persona = userStory.createNode("Persona");
		CNode action = userStory.createNode("Action");
		CNode primaryAction = userStory.createNode("Primary Action");
		CNode secondaryAction = userStory.createNode("Secondary Action");
		CNode entity = userStory.createNode("Entity","delete");
		CNode primaryEntity = userStory.createNode("Primary Entity","delete");
		CNode secondaryEntity = userStory.createNode("Secondary Entity");
		//CNode secondaryEntity = userStory.createNode("Secondary Entity");
		//CNode story = userStory.createNode("Story");

		//Create Attributes
		persona.createAttribute("name", "\"user\"");
		primaryAction.createAttribute("name", "\"delete\"");
		persona.createAttribute("uid", "uid");
		secondaryAction.createAttribute("name", "\"manage\"");
		primaryEntity.createAttribute("name", "\"landmark\"");
		secondaryEntity.createAttribute("name", "\"map\"");

		//secondaryEntity.createAttribute("name", "secondaryEntity");

		persona.createEdge(primaryAction, "triggers");
		persona.createEdge(primaryEntity, "owns","delete");
		entity.createEdge(primaryEntity, "primary entities","delete");
		entity.createEdge(secondaryEntity, "secondary entities");
		primaryAction.createEdge(primaryEntity, "targets","delete");
		action.createEdge(primaryAction, "primary actions");
		action.createEdge(secondaryAction, "secondary actions");
		//story.createEdge(, "targets");


		System.out.println("Done! ");
		//EGraph graph = new EGraphImpl(resourceSet.getResource("backlogs.xmi"));
		//module = new CModule(resourceSet.getModule("backlog.henshin"));


	}
public static void deleteAnyLandmark( ) {

		// Create a resource set with a base directory:
		//HenshinResourceSet resourceSet = new HenshinResourceSet(path);

		// Load the module:
		//CModule module = CModule.loadFromFile("backlog.henshin");

		//module =  CModule.loadFromFile(path+"/backlog.henshin");



		CRule userStory = module.createRule("delete_any_landmark");

		// Create Parameters
		//userStory.createParameter("in", "persona", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "primaryAction", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "secondaryAction", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "primaryEntity", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);

		//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);

		// Create Nodes
		CNode persona = userStory.createNode("Persona");
		CNode action = userStory.createNode("Action");
		CNode primaryAction = userStory.createNode("Primary Action");
		CNode secondaryAction = userStory.createNode("Secondary Action");
		CNode entity = userStory.createNode("Entity");
		CNode primaryEntity = userStory.createNode("Primary Entity","delete");
		CNode secondaryEntity = userStory.createNode("Secondary Entity");
		//CNode secondaryEntity = userStory.createNode("Secondary Entity");
		//CNode story = userStory.createNode("Story");

		//Create Attributes
		persona.createAttribute("name", "\"user\"");
		primaryAction.createAttribute("name", "\"delete\"");
		secondaryAction.createAttribute("name", "\"manage\"");
		primaryEntity.createAttribute("name", "\"landmark\"");
		secondaryEntity.createAttribute("name", "\"map\"");

		//secondaryEntity.createAttribute("name", "secondaryEntity");

		persona.createEdge(primaryAction, "triggers");
		persona.createEdge(primaryEntity, "owns","delete");
		entity.createEdge(primaryEntity, "primary entities","delete");
		entity.createEdge(secondaryEntity, "secondary entities");
		primaryAction.createEdge(primaryEntity, "targets","delete");
		action.createEdge(primaryAction, "primary actions");
		action.createEdge(secondaryAction, "secondary actions");
		//story.createEdge(, "targets");



		System.out.println("Done! ");
		//EGraph graph = new EGraphImpl(resourceSet.getResource("backlogs.xmi"));
		//module = new CModule(resourceSet.getModule("backlog.henshin"));


	}
public static void modifyAnyLandmark( ) {

	// Create a resource set with a base directory:
	//HenshinResourceSet resourceSet = new HenshinResourceSet(path);

	// Load the module:
	//CModule module = CModule.loadFromFile("backlog.henshin");

	//module =  CModule.loadFromFile(path+"/backlog.henshin");



	CRule userStory = module.createRule("modify_any_landmark");

	// Create Parameters
	//userStory.createParameter("in", "persona", EcorePackage.Literals.ESTRING);
	//userStory.createParameter("in", "primaryAction", EcorePackage.Literals.ESTRING);
	//userStory.createParameter("in", "secondaryAction", EcorePackage.Literals.ESTRING);
	userStory.createParameter("in", "landName", EcorePackage.Literals.ESTRING);
	//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);

	//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);

	// Create Nodes
	CNode persona = userStory.createNode("Persona");
	CNode action = userStory.createNode("Action");
	CNode primaryAction = userStory.createNode("Primary Action");
	CNode secondaryAction = userStory.createNode("Secondary Action");
	CNode entity = userStory.createNode("Entity");
	CNode primaryEntity = userStory.createNode("Primary Entity");
	CNode secondaryEntity = userStory.createNode("Secondary Entity");

	//Create Attributes
	persona.createAttribute("name", "\"user\"");
	primaryAction.createAttribute("name", "\"modify\"");
	secondaryAction.createAttribute("name", "\"manage\"");
	primaryEntity.createAttribute("name", "landName");
	secondaryEntity.createAttribute("name", "\"map\"");

	persona.createEdge(primaryAction, "triggers");
	entity.createEdge(primaryEntity, "primary entities");
	entity.createEdge(secondaryEntity, "secondary entities");
	primaryAction.createEdge(primaryEntity, "targets");
	action.createEdge(primaryAction, "primary actions");
	action.createEdge(secondaryAction, "secondary actions");

	System.out.println("Done! ");
	//EGraph graph = new EGraphImpl(resourceSet.getResource("backlogs.xmi"));
	//module = new CModule(resourceSet.getModule("backlog.henshin"));


}
public static void addAnyLandmark( ) {

	// Create a resource set with a base directory:
	//HenshinResourceSet resourceSet = new HenshinResourceSet(path);

	// Load the module:
	//CModule module = CModule.loadFromFile("backlog.henshin");

	//module =  CModule.loadFromFile(path+"/backlog.henshin");



	CRule userStory = module.createRule("add_any_landmark");

	// Create Parameters
	//userStory.createParameter("in", "persona", EcorePackage.Literals.ESTRING);
	//userStory.createParameter("in", "primaryAction", EcorePackage.Literals.ESTRING);
	//userStory.createParameter("in", "secondaryAction", EcorePackage.Literals.ESTRING);
	userStory.createParameter("in", "landName", EcorePackage.Literals.ESTRING);
	//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);

	//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);

	// Create Nodes
	CNode persona = userStory.createNode("Persona");
	CNode action = userStory.createNode("Action");
	CNode primaryAction = userStory.createNode("Primary Action");
	CNode secondaryAction = userStory.createNode("Secondary Action");
	CNode entity = userStory.createNode("Entity");
	CNode primaryEntity = userStory.createNode("Primary Entity","create");
	CNode secondaryEntity = userStory.createNode("Secondary Entity");

	//Create Attributes
	persona.createAttribute("name", "\"user\"");
	primaryAction.createAttribute("name", "\"add\"");
	secondaryAction.createAttribute("name", "\"manage\"");
	primaryEntity.createAttribute("name", "landName");
	secondaryEntity.createAttribute("name", "\"map\"");

	// Create Edges
	persona.createEdge(primaryAction, "triggers");
	persona.createEdge(primaryEntity, "owns","create");
	entity.createEdge(primaryEntity, "primary entities","create");
	entity.createEdge(secondaryEntity, "secondary entities");
	primaryAction.createEdge(primaryEntity, "targets","create");
	action.createEdge(primaryAction, "primary actions");
	action.createEdge(secondaryAction, "secondary actions");

	System.out.println("Done! ");
}

	public static void main(String[] args) {
		module.addImportsFromFile("Backlog_v2.ecore");
		deleteMyLandmark();
		deleteAnyLandmark();
		modifyAnyLandmark();
		addAnyLandmark();
		module.save();
	}


}
