package org.henshin.backlog.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.emf.henshin.model.compact.CModule;
import org.henshin.backlog.code.evaluation.Evaluation;
import org.henshin.backlog.code.rule.ActionInJsonFileNotFound;
import org.henshin.backlog.code.rule.ContainsInJsonFileNotFound;
import org.henshin.backlog.code.rule.EcoreFileNotFound;
import org.henshin.backlog.code.rule.EdgeWithSameSourceAndTarget;
import org.henshin.backlog.code.rule.EmptyOrNotExistJsonFile;
import org.henshin.backlog.code.rule.EntityInJsonFileNotFound;
import org.henshin.backlog.code.rule.JsonFileNotFound;
import org.henshin.backlog.code.rule.PersonaInJsonFileNotFound;
import org.henshin.backlog.code.rule.RuleCreator_v4;
import org.henshin.backlog.code.rule.TargetsInJsonFileNotFound;
import org.henshin.backlog.code.rule.TextInJsonFileNotFound;
import org.henshin.backlog.code.rule.TriggersInJsonFileNotFound;
import org.henshin.backlog.code.rule.UsNrInJsonFileNotFound;
import org.json.JSONArray;
import org.junit.Test;

public class RuleCreator_v4Test {

	// testAssignCmodule& assing a dummy ECore model &Through an exception:
	// \textit{EcoreFileNotFound.class}&Check whether the ECore model already exists
	// and CModule is correctly assigned \\
	@Test(expected = EcoreFileNotFound.class)
	public void testAssignCmodule()
			throws EcoreFileNotFound, EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound,
			ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
			TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\g03_baseline_pos_num.json",
				"backlog_v4", "dummy.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);

	}

	@Test(expected = EmptyOrNotExistJsonFile.class)
	public void testEmptyOrNotExistJsonFile() throws EmptyOrNotExistJsonFile, JsonFileNotFound, IOException {

		// Read the JSON_report_Test file to verify the results
		RuleCreator_v4 creator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\empty_json_file.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		creator_v4.readJsonArrayFromFile();

	}

	// TODO: add this to paper
	@Test(expected = EmptyOrNotExistJsonFile.class)
	public void testReadJsonArrayFromFile() throws EmptyOrNotExistJsonFile, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\dummy_json_file.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		ruleCreator_v4.readJsonArrayFromFile();

	}

	// UsNrNotExist \newline(testProcessJsonFile)&JSON file with a US without
	// \enquote{Us\_Nr} entry&Through an exception:
	// \textit{UsNrInJsonFileNotFound.class}&Check whether there is an entry
	// \enquote{US\_Nr} in the related US in JSON file\\
	@Test(expected = UsNrInJsonFileNotFound.class)
	public void testProcessJsonFile_UsNrNotExist()
			throws EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound,
			ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
			TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\US_Nr_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// testProcessJsonFile (PersonaNotExist)&A JSON file with a US without
	// \enquote{Persona} entry &Through an exception:
	// \textit{PersonaInJsonFileNotFound.class} & Check whether there is an entry
	// \enquote{Persona} in the related US in JSON file\\
	@Test(expected = PersonaInJsonFileNotFound.class)
	public void testProcessJsonFile_PersonaNotExist()
			throws EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound,
			ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
			TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\Persona_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);

	}

	// testProcessJsonFile (ActionNotExist)&Specify an action that is not contained
	// as \textit{Action} in the JSON file, but appears as \textit{Contains, Targets
	// or Triggers}&Through an exception: \textit{ActionInJsonFileNotFound.class}&
	// Check whether the action that appears in the \enquote{Targets},
	// \enquote{Contains} or \enquote{Triggers} entries has already been identified
	// as an action
	@Test(expected = ActionInJsonFileNotFound.class)
	public void testProcessJsonFile_ActionNotExist()
			throws EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound,
			ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
			TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\Action_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);

	}

	// testProcessJsonFile (EntityNotExist)&A JSON file with a US without
	// \enquote{Entity} entry &Through an
	// exception:\textit{EntityInJsonFileNotFound.class} & Check whether there is an
	// entry \enquote{Entity} in the related US\\
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testProcessJsonFile_EntityNotExist()
			throws EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound,
			ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
			TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\Entity_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// TriggersNotExist (testProcessJsonFile)&JSON file with a US without
	// \enquote{Triggers} entry &Through an exception:
	// \textit{TriggersInJsonFileNotFound.class}&Check whether there is an entry
	// \enquote{Triggers} in the related US in JSON file\\
	@Test(expected = TriggersInJsonFileNotFound.class)
	public void testProcessJsonFile_TriggersNotExist()
			throws EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound,
			ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
			TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\Triggers_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// testProcessJsonFile (TargetsNotExist)&A JSON file with a US without
	// \enquote{Targets} entry &Through an exception:
	// \textit{TargetsInJsonFileNotFound.class} & Check whether there is an entry
	// \enquote{Targets} in the related US in JSON file\\
	@Test(expected = TargetsInJsonFileNotFound.class)
	public void testProcessJsonFile_TargetsNotExist()
			throws EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound,
			ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
			TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\Targets_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// testProcessJsonFile (ContainsNotExist)&A JSON file with a US without
	// \enquote{Contains} entry &Through an
	// exception:\textit{ContainsInJsonFileNotFound.class} & Check whether there is
	// an entry \enquote{Contains} in the related US\\
	@Test(expected = ContainsInJsonFileNotFound.class)
	public void testProcessJsonFile_ContainsNotExist()
			throws EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound,
			ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
			TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\Contains_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// TextNotExist (testProcessJsonFile) &JSON file with a US without
	// \enquote{Text} entry &Through an exception:
	// \textit{TextInJsonFileNotFound.class}&Check whether there is an entry
	// \enquote{Text} in the related US in JSON file\\
	@Test(expected = TextInJsonFileNotFound.class)
	public void testProcessJsonFile_TextNotExist()
			throws EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound,
			ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
			TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\Text_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// testProcessContainsEdges (UndefindedEntity)& Specify an entity that is not
	// contained as \enquote{Entity} in the JSON file, but appears as
	// \enquote{Contains}& Through an exception:
	// \textit{EntityInJsonFileNotFound.class}& Check whether the entity that
	// appears in the \textit{Contains} entry has already been identified as an
	// entity \\
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testProcessContainsEdges_UndefindedEntity()
			throws EcoreFileNotFound, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
			EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound,
			TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\UndefindedEntity_in_Contains.json",
				"backlog_v4", "Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);

	}

	// TODO: containsEdge should be in Target
	@Test
	public void testProcessContainsEdges_ContainsInTargets()
			throws EcoreFileNotFound, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
			EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound,
			TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile, EdgeWithSameSourceAndTarget, IOException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\ContainsInTargets.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		// ruleCreator_v4.processJsonFile(jsonArray);
		// ruleCreator_v4.createRules("03");
		RuleCreator_v4.createRules("03");
		Path filePath = Paths.get("Henshin_backlog_g03.henshin");

		// Assert that the file exits
		assertTrue("File does not exist", Files.exists(filePath));

	}
	// TODO: check if Triggers is duplicate
	@Test(expected= EdgeWithSameSourceAndTarget.class)
	public void testProcessContainsEdges_DuplicateTriggers()
			throws EcoreFileNotFound, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
			EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound,
			TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile, EdgeWithSameSourceAndTarget, IOException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\DuplicateTriggers.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		System.out.println(jsonArray.toString());
		 ruleCreator_v4.processJsonFile(jsonArray);
		// ruleCreator_v4.createRules("03");
//		RuleCreator_v4.createRules("03");
//		Path filePath = Paths.get("Henshin_backlog_g03.henshin");

		// Assert that the file exits
//		assertTrue("File does not exist", Files.exists(filePath));
	}
	
	// UndefindedEntity \newline(testProcessTargetsEdges) &Specify an entity that is
	// not contained as \enquote{Entity} in the JSON file, but appears as
	// \enquote{Targets}&Through an exception:
	// \textit{EntityInJsonFileNotFound.class}&Check whether the entity that appears
	// in the \textit{Targets} entry has already been identified as an entity\\
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testProcessTargetsEdges_UndefindedEntity()
			throws EcoreFileNotFound, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
			EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound,
			TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\UndefindedEntity_in_Targets.json",
				"backlog_v4", "Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// PrimaryEntityNotFound \newline(testProcessTargetsEdges)&Specify a primary
	// entity that is not contained as \enquote{Primary Entity} in the JSON file,
	// but appears as \enquote{Targets}&Through an exception:
	// \textit{EntityInJsonFileNotFound.class}&Check whether the primary entity that
	// appears in the \textit{Targets} entry has already been identified as a
	// primary entity\\
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testProcessTargetsEdges_PrimaryEntityNotFound()
			throws EcoreFileNotFound, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
			EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound,
			TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\PrimaryEntityNotFound.json",
				"backlog_v4", "Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// SecondaryEntityNotFound \newline(testProcessTargetsEdges)&Specify a secondary
	// entity that is not contained as \enquote{Secondary Entity} in the JSON file,
	// but appears as \enquote{Targets}&Through an exception:
	// \textit{EntityInJsonFileNotFound.class}&Check whether the secondary entity
	// that appears in the \textit{Targets} entry has already been identified as a
	// secondary entity\\
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testProcessTargetsEdges_SecondaryEntityNotFound()
			throws EcoreFileNotFound, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
			EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound,
			TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\SecondaryEntityNotFound.json",
				"backlog_v4", "Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// PrimaryActionNotFound \newline(testProcessTargetsEdges)&Specify an entity
	// that is not contained as \enquote{Primary Action} in the JSON file, but
	// appears as \enquote{Targets}&Through an exception:
	// \textit{ActionInJsonFileNotFound.class}&Check whether the primary action that
	// appears in the \textit{Targets} entry has already been identified as a
	// primary action\\
	@Test(expected = ActionInJsonFileNotFound.class)
	public void testProcessTargetsEdges_PrimaryActionNotFound()
			throws EcoreFileNotFound, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
			EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound,
			TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\PrimaryActionNotFound.json",
				"backlog_v4", "Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	//TODO: Add this to paper
	// PrimaryActionNotFound \newline(testProcessTargetsEdges)&Specify an entity
	// that is not contained as \enquote{Primary Action} in the JSON file, but
	// appears as \enquote{Targets}&Through an exception:
	// \textit{ActionInJsonFileNotFound.class}&Check whether the primary action that
	// appears in the \textit{Targets} entry has already been identified as a
	// primary action\\
	@Test(expected = ActionInJsonFileNotFound.class)
	public void testProcessTargetsEdges_ActionNotFound()
			throws EcoreFileNotFound, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
			EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound,
			TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\ActionInTargetsNotFound.json",
				"backlog_v4", "Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

	// SecondaryActionNotFound \newline(testProcessTargetsEdges)&Specify a secondary
	// action that is not contained as \enquote{Secondary Action} in the JSON file,
	// but appears as \enquote{Targets}&Through an exception:
	// \textit{ActionInJsonFileNotFound.class}&Check whether the secondary action
	// that appears in the \textit{Targets} entry has already been identified as a
	// secondary action\\
	@Test(expected = ActionInJsonFileNotFound.class)
	public void testProcessTargetsEdges_SecondaryActionNotFound()
			throws EcoreFileNotFound, PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
			EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound,
			TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile, EdgeWithSameSourceAndTarget, FileNotFoundException {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\RuleCreator\\SecondrayActionNotFound.json",
				"backlog_v4", "Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

}
