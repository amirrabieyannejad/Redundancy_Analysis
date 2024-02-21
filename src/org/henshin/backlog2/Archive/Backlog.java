package org.henshin.backlog2;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.model.compact.CModule;
import org.eclipse.emf.henshin.model.compact.CNode;
import org.eclipse.emf.henshin.model.compact.CRule;
import org.eclipse.emf.henshin.model.compact.CUnit;

public class Backlog {
	
	//public static final String PATH = "src/org/henshin/backlog/";
	/**
	 * Run the bank example.
	 * @param path Relative path to the model files.
	 * @param saveResult Whether the result should be saved.
	 */
	public static CModule module = new CModule("backlog_v4");
	public static void createFirstRule( ) {
		
		// Create a resource set with a base directory:
		//HenshinResourceSet resourceSet = new HenshinResourceSet(path);
		
		// Load the module:
		//CModule module = CModule.loadFromFile("backlog.henshin");
		
		//module =  CModule.loadFromFile(path+"/backlog.henshin");
		
		
				
		CRule userStory = module.createRule("userStory_01");
		
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
		CNode primaryEntity = userStory.createNode("Primary Entity");
		CNode secondaryEntity = userStory.createNode("Secondary Entity");
		//CNode secondaryEntity = userStory.createNode("Secondary Entity");
		//CNode story = userStory.createNode("Story");
		
		//Create Attributes
		persona.createAttribute("name", "\"UI designer\"");
		primaryAction.createAttribute("name", "\"delete\"");
		secondaryAction.createAttribute("name", "\"validate\"");
		primaryEntity.createAttribute("name", "\"user testing\"");
		secondaryEntity.createAttribute("name", "\"stakeholder UI improvement requests\"");
		
		//secondaryEntity.createAttribute("name", "secondaryEntity");
	
		persona.createEdge(primaryAction, "triggers");
		entity.createEdge(primaryEntity, "primary entities");
		entity.createEdge(secondaryEntity, "secondary entities");
		primaryAction.createEdge(primaryEntity, "targets");
		action.createEdge(primaryAction, "primary actions");
		action.createEdge(secondaryAction, "secondary actions");
		//story.createEdge(, "targets");
		
		
		
		System.out.println("Done! ");
		//EGraph graph = new EGraphImpl(resourceSet.getResource("backlogs.xmi"));
		//module = new CModule(resourceSet.getModule("backlog.henshin"));
		
	
	}
public static void createSecondRule( ) {
		
		// Create a resource set with a base directory:
		//HenshinResourceSet resourceSet = new HenshinResourceSet(path);
		
		// Load the module:
		//CModule module = CModule.loadFromFile("backlog.henshin");
		
		//module =  CModule.loadFromFile(path+"/backlog.henshin");
		
		
				
		CRule userStory = module.createRule("userStory_02");
		
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
		CNode primaryEntity = userStory.createNode("Primary Entity");
		CNode secondaryEntity = userStory.createNode("Secondary Entity");
		//CNode secondaryEntity = userStory.createNode("Secondary Entity");
		//CNode story = userStory.createNode("Story");
		
		//Create Attributes
		persona.createAttribute("name", "\"UI designer\"");
		primaryAction.createAttribute("name", "\"create\"");
		secondaryAction.createAttribute("name", "\"validate\"");
		primaryEntity.createAttribute("name", "\"user testing\"");
		secondaryEntity.createAttribute("name", "\"stakeholder UI improvement requests\"");
		
		//secondaryEntity.createAttribute("name", "secondaryEntity");
	
		persona.createEdge(primaryAction, "triggers");
		entity.createEdge(primaryEntity, "primary entities");
		entity.createEdge(secondaryEntity, "secondary entities");
		primaryAction.createEdge(primaryEntity, "targets");
		action.createEdge(primaryAction, "primary actions");
		action.createEdge(secondaryAction, "secondary actions");
		//story.createEdge(, "targets");
		
		
		
		System.out.println("Done! ");
		//EGraph graph = new EGraphImpl(resourceSet.getResource("backlogs.xmi"));
		//module = new CModule(resourceSet.getModule("backlog.henshin"));
		
	
	}
	
	public static void main(String[] args) {
		module.addImportsFromFile("Backlog_v2.ecore");
		createFirstRule();
		createSecondRule();
		module.save();
	}


}
