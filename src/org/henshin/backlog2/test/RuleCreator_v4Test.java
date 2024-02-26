package org.henshin.backlog2.test;

import static org.junit.Assert.*;

import org.eclipse.emf.henshin.model.compact.CModule;
import org.henshin.backlog2.ActionInJsonFileNotFound;
import org.henshin.backlog2.ContainsInJsonFileNotFound;
import org.henshin.backlog2.EcoreFileNotFound;
import org.henshin.backlog2.EmptyOrNotExistJsonFile;
import org.henshin.backlog2.EntityInJsonFileNotFound;
import org.henshin.backlog2.PersonaInJsonFileNotFound;
import org.henshin.backlog2.RuleCreator_v4;
import org.henshin.backlog2.TargetsInJsonFileNotFound;
import org.henshin.backlog2.TextInJsonFileNotFound;
import org.henshin.backlog2.TriggersInJsonFileNotFound;

import org.henshin.backlog2.UsNrInJsonFileNotFound;
import org.json.JSONArray;
import org.junit.Test;

public class RuleCreator_v4Test {

	@Test(expected = EcoreFileNotFound.class)
	public void testAssignCmodule() throws EcoreFileNotFound,
	EmptyOrNotExistJsonFile, PersonaInJsonFileNotFound, 
	UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, 
	TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Datasets\\g03_baseline_pos_num.json", "backlog_v4",
				"dummy.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
		
	}
	@Test(expected = EmptyOrNotExistJsonFile.class)
	public void testReadJsonArrayFromFile() throws EmptyOrNotExistJsonFile {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Datasets\\dummy_json_file.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		ruleCreator_v4.readJsonArrayFromFile();
		
	}
	
	@Test(expected = UsNrInJsonFileNotFound.class)
	public void testprocessJsonFile_UsNrNotExist() throws EmptyOrNotExistJsonFile,PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\US_Nr_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
		
	}
	@Test(expected = PersonaInJsonFileNotFound.class)
	public void testprocessJsonFile_PersonaNotExist() throws EmptyOrNotExistJsonFile,PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\Persona_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
		
	}
	@Test(expected = ActionInJsonFileNotFound.class)
	public void testprocessJsonFile_ActionNotExist() throws EmptyOrNotExistJsonFile,PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\Action_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
		
	}
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testprocessJsonFile_EntityNotExist() throws EmptyOrNotExistJsonFile,PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\Entity_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	@Test(expected = TriggersInJsonFileNotFound.class)
	public void testprocessJsonFile_TriggersNotExist() throws EmptyOrNotExistJsonFile,PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\Triggers_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	@Test(expected = TargetsInJsonFileNotFound.class)
	public void testprocessJsonFile_TargetsNotExist() throws EmptyOrNotExistJsonFile,PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\Targets_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	@Test(expected = ContainsInJsonFileNotFound.class)
	public void testprocessJsonFile_ContainsNotExist() throws EmptyOrNotExistJsonFile,PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\Contains_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	@Test(expected = TextInJsonFileNotFound.class)
	public void testprocessJsonFile_TextNotExist() throws EmptyOrNotExistJsonFile,PersonaInJsonFileNotFound, EcoreFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\Text_Not_Exist.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testProcessContainsEdges_UndefindedEntity() throws EcoreFileNotFound,
	PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
	EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
	TextInJsonFileNotFound, TriggersInJsonFileNotFound,  EmptyOrNotExistJsonFile{
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\UndefindedEntity_in_Contains.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
		
		
	}
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testprocessTargetsEdges_UndefindedEntity() throws EcoreFileNotFound,
	PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
	EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
	TextInJsonFileNotFound, TriggersInJsonFileNotFound,  EmptyOrNotExistJsonFile{
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\UndefindedEntity_in_Targets.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testprocessTargetsEdges_PrimaryEntityNotFound() throws EcoreFileNotFound,
	PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
	EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
	TextInJsonFileNotFound, TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile{
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\PrimaryEntityNotFound.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	@Test(expected = EntityInJsonFileNotFound.class)
	public void testprocessTargetsEdges_SecondaryEntityNotFound() throws EcoreFileNotFound,
	PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
	EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
	TextInJsonFileNotFound, TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile{
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\SecondaryEntityNotFound.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	@Test(expected = ActionInJsonFileNotFound.class)
	public void testprocessTargetsEdges_PrimaryActionNotFound() throws EcoreFileNotFound,
	PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
	EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
	TextInJsonFileNotFound, TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile{
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\PrimaryActionNotFound.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}
	@Test(expected = ActionInJsonFileNotFound.class)
	public void testprocessTargetsEdges_SecondaryActionNotFound() throws EcoreFileNotFound,
	PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound,
	EntityInJsonFileNotFound, TargetsInJsonFileNotFound, ContainsInJsonFileNotFound,
	TextInJsonFileNotFound, TriggersInJsonFileNotFound, EmptyOrNotExistJsonFile{
		RuleCreator_v4 ruleCreator_v4 = new RuleCreator_v4("Tests\\SecondrayActionNotFound.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator_v4.readJsonArrayFromFile();
		ruleCreator_v4.processJsonFile(jsonArray);
	}

}
