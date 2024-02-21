package org.henshin.backlog2.Archive;
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

public class CreateRuleV3 {
	
	//public static final String PATH = "src/org/henshin/backlog/";
	/**
	 * Run the bank example.
	 * @param path Relative path to the model files.
	 * @param saveResult Whether the result should be saved.
	 */
	public static CModule module = new CModule("backlog_v3");
	 
	public static void createFirstRule( ) {
		
		// Create a resource set with a base directory:
		//HenshinResourceSet resourceSet = new HenshinResourceSet(path);
		
		// Load the module:
		//CModule module = CModule.loadFromFile("backlog.henshin");
		//CModule module = new CModule("backlog");
		//module.addImportsFromFile("Backlog_v3.ecore");
		//module =  CModule.loadFromFile(path+"/backlog.henshin");
		
		
				
		CRule userStory = module.createRule("userStory_01");
		
		// Create Parameters
		//userStory.createParameter("in", "persona", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "primaryAction", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "primaryEntity", EcorePackage.Literals.ESTRING);
		//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);
	
		// Create Nodes
		CNode persona = userStory.createNode("Persona");
		CNode persona1 = userStory.createNode("UI designer");
		CNode primaryAction = userStory.createNode("Primary Action");
		CNode primaryAction1= userStory.createNode("create");
		CNode secondaryAction = userStory.createNode("Secondary Action");
		CNode secondaryAction1 = userStory.createNode("validate");
		CNode primaryEntity = userStory.createNode("Primary Entity");
		CNode primaryEntity1 = userStory.createNode("user testing");
		CNode secondaryEntity = userStory.createNode("Secondary Entity");
		CNode secondaryEntity1 = userStory.createNode("stakeholder UI improvement requests");
		
		//CNode secondaryEntity = userStory.createNode("Secondary Entity");
		//CNode story = userStory.createNode("Story");
		
		//Create Attributes
		//persona.createAttribute("name", "persona");
		//primaryAction.createAttribute("name", "primaryAction");
		//primaryEntity.createAttribute("name", "primaryEntity");
		//secondaryEntity.createAttribute("name", "secondaryEntity");
	
		persona1.createEdge(primaryAction1, "triggers_us_01");
		primaryAction1.createEdge(primaryEntity1, "targets_us_01");
		
		primaryAction.createEdge(primaryAction1, "verbs_us_01");
		secondaryAction.createEdge(secondaryAction1, "verbs");
		primaryEntity.createEdge(primaryEntity1, "nouns");
		secondaryEntity.createEdge(secondaryEntity1, "nouns");
		persona.createEdge(persona1, "personas");
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
			
			//module.addImportsFromFile("Backlog_v3.ecore");
			//module =  CModule.loadFromFile(path+"/backlog.henshin");
			
			
					
			CRule userStory = module.createRule("userStory_02");
			
			// Create Parameters
			//userStory.createParameter("in", "persona", EcorePackage.Literals.ESTRING);
			//userStory.createParameter("in", "primaryAction", EcorePackage.Literals.ESTRING);
			//userStory.createParameter("in", "primaryEntity", EcorePackage.Literals.ESTRING);
			//userStory.createParameter("in", "secondaryEntity", EcorePackage.Literals.ESTRING);
		
			// Create Nodes
			CNode persona = userStory.createNode("Persona");
			CNode persona1 = userStory.createNode("UI designer");
			CNode primaryAction = userStory.createNode("Primary Action");
			CNode primaryAction1= userStory.createNode("delete");
			CNode secondaryAction = userStory.createNode("Secondary Action");
			CNode secondaryAction1 = userStory.createNode("validate");
			CNode primaryEntity = userStory.createNode("Primary Entity");
			CNode primaryEntity1 = userStory.createNode("user testing");
			CNode secondaryEntity = userStory.createNode("Secondary Entity");
			CNode secondaryEntity1 = userStory.createNode("stakeholder UI improvement requests");
			
			persona1.createEdge(primaryAction1, "triggers_us_02");
			primaryAction1.createEdge(primaryEntity1, "targets_us_02");
			
			primaryAction.createEdge(primaryAction1, "verbs_us_02");
			secondaryAction.createEdge(secondaryAction1, "verbs");
			primaryEntity.createEdge(primaryEntity1, "nouns");
			secondaryEntity.createEdge(secondaryEntity1, "nouns");
			persona.createEdge(persona1, "personas");
			
			//CNode secondaryEntity = userStory.createNode("Secondary Entity");
			//CNode story = userStory.createNode("Story");
			
			//Create Attributes
			//persona.createAttribute("name", "persona");
			//primaryAction.createAttribute("name", "primaryAction");
			//primaryEntity.createAttribute("name", "primaryEntity");
			//secondaryEntity.createAttribute("name", "secondaryEntity");
		
			
			//story.createEdge(, "targets");
			
			
			
			System.out.println("Done! ");
			//EGraph graph = new EGraphImpl(resourceSet.getResource("backlogs.xmi"));
			//module = new CModule(resourceSet.getModule("backlog.henshin"));
		
	
	}
	
	public static void main(String[] args) {
		module.addImportsFromFile("Backlog_v3.ecore");
		createFirstRule();
		createSecondRule();
		module.save();
		
		
	}


}
