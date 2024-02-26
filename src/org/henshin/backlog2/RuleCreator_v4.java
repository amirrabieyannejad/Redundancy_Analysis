package org.henshin.backlog2;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.eclipse.emf.henshin.model.compact.CModule;
import org.eclipse.emf.henshin.model.compact.CNode;
import org.eclipse.emf.henshin.model.compact.CRule;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/*  Delete Annotation for Attributes actions and entities
 *  plus their edges which have target edges or triggers edge
 *  This version is include US "Text"
 */
public class RuleCreator_v4 {
	private String jsonFile;
	private String henshinFile;
	private String eCoreFile;

	public RuleCreator_v4(String JsonFileName, String henshinFileName, String eCoreFileName) {
		jsonFile = JsonFileName;
		henshinFile = henshinFileName;
		eCoreFile = eCoreFileName;
	}

	public String getJsonFile() {
		return jsonFile;
	}

	public String getJsonFileAbsolutePath() throws EmptyOrNotExistJsonFile {
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_new\\" + "org.henshin.backlog2\\" + getJsonFile());
		if (Files.exists(path)) {
			return path.toString();

		} else {
			throw new EmptyOrNotExistJsonFile();
		}

	}

	public String getEcoreFileAbsolutePath() {
		Path path = Paths.get("C:\\Users\\amirr\\eclipse-workspace_new\\" + "org.henshin.backlog2\\" + getEcoreFile());
		if (Files.exists(path)) {
			return path.toString();

		}
		return null;

	}

	public String getHenshinFile() {
		return henshinFile;
	}

	public String getEcoreFile() {
		return eCoreFile;
	}

	// private static final Logger LOGGER =
	// Logger.getLogger(RuleCreator_v4.class.getName());

	public static void main(String[] args) throws IOException, EcoreFileNotFound, EmptyOrNotExistJsonFile,
			PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound,
			TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		RuleCreator_v4 ruleCreator = new RuleCreator_v4("Datasets\\g03_baseline_pos_num.json", "backlog_v4",
				"Backlog_v2.3.ecore");
		JSONArray jsonArray = ruleCreator.readJsonArrayFromFile();
		CModule cModule = ruleCreator.processJsonFile(jsonArray);
		cModule.save();

	}

	public JSONArray readJsonArrayFromFile() throws EmptyOrNotExistJsonFile {
		JSONArray jsonArray;
		try (FileReader reader = new FileReader(getJsonFileAbsolutePath())) {
			JSONTokener tokener = new JSONTokener(reader);
			if (!tokener.more()) {
				throw new EmptyOrNotExistJsonFile();

			}
			// Read JSON file
			jsonArray = new JSONArray(tokener);

			return jsonArray;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public CModule assignCmodule() throws EcoreFileNotFound {
		CModule module = new CModule(getHenshinFile());
		if (getEcoreFileAbsolutePath() == null) {
			throw new EcoreFileNotFound();
		}
		module.addImportsFromFile(getEcoreFile());
		return module;

	}

	public CModule processJsonFile(JSONArray json) throws EcoreFileNotFound, PersonaInJsonFileNotFound,
			UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound,
			ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound {
		CModule cModule = assignCmodule();
		String usNrM = null;
		CRule userStoryM = null;
		JSONArray personaM = null;
		JSONObject actionM = null;
		JSONObject entityM = null;
		JSONArray targetsArrayM = null;
		JSONArray containsArrayM = null;
		String textM = null;
		for (int i = 0; i < json.length(); i++) {
			try {
				JSONObject jsonObject = json.getJSONObject(i);

				if (jsonObject.has("US_Nr")) {

					usNrM = jsonObject.getString("US_Nr");
					userStoryM = processRule(jsonObject, usNrM, cModule);
				} else {
					throw new UsNrInJsonFileNotFound();
				}

				if (jsonObject.has("Persona")) {
					personaM = jsonObject.getJSONArray("Persona");
				} else {
					throw new PersonaInJsonFileNotFound();
				}

				if (jsonObject.has("Action")) {
					actionM = jsonObject.getJSONObject("Action");
				} else {
					throw new ActionInJsonFileNotFound();
				}

				if (jsonObject.has("Entity")) {
					entityM = jsonObject.getJSONObject("Entity");

				} else {
					throw new EntityInJsonFileNotFound();
				}
				if (jsonObject.has("Triggers")) {
					targetsArrayM = jsonObject.getJSONArray("Triggers");
				} else {
					throw new TriggersInJsonFileNotFound();
				}
				if (jsonObject.has("Targets")) {
					targetsArrayM = jsonObject.getJSONArray("Targets");
				} else {
					throw new TargetsInJsonFileNotFound();
				}
				if (jsonObject.has("Contains")) {
					containsArrayM = jsonObject.getJSONArray("Contains");
				} else {
					throw new ContainsInJsonFileNotFound();
				}
				if (jsonObject.has("Text")) {
					textM = jsonObject.getString("Text");
				} else {
					throw new TextInJsonFileNotFound();
				}

				CNode personaNode = processPersona(jsonObject, personaM, userStoryM, usNrM);
				// store all entities in one map which the string is the name of entities and
				// CNode correspond to their CNode Object
				Map<String, CNode> entityMap = new HashMap<>();
				// store all actions in one map which the string is the name of actions and
				// CNode correspond to their CNode Object
				Map<String, CNode> actionMap = new HashMap<>();
				processText(jsonObject, userStoryM, textM);
				processActions(jsonObject, userStoryM, actionM, personaNode, actionMap, usNrM);
				processEntities(jsonObject, userStoryM, entityM, targetsArrayM, entityMap, usNrM);
				processTargetsEdges(jsonObject, targetsArrayM, entityMap, actionMap);
				processContainsEdges(jsonObject, containsArrayM, targetsArrayM, entityMap);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		return cModule;
	}

	// Make rules which as name have Us_Nr Object in JSON file
	private CRule processRule(JSONObject jsonObject, String usNr, CModule module) {
		try {

			CRule userStory = module.createRule(usNr);
			return userStory;

		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Create node as a story with an attribute called "text" that contains the "Text"
	// JSONObject of the JSON file
	private void processText(JSONObject jsonObject, CRule userStory, String text) {
		try {
			CNode nodeText = userStory.createNode("Story");
			nodeText.createAttribute("text", "\"" + text.toLowerCase() + "\"");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private CNode processPersona(JSONObject jsonObject, JSONArray persona, CRule userStory, String usNr) {
		try {

			// LOGGER.info("Persona is: " + persona.getString(0) + " length is: " +
			// persona.length());
			CNode nodePersona = userStory.createNode("Persona");
			nodePersona.createAttribute("name", "\"" + persona.getString(0).toLowerCase() + "\"");
			return nodePersona;

		} catch (JSONException e) {
			e.printStackTrace();

		}
		return null;
	}

	private void processActions(JSONObject jsonObject, CRule userStory, JSONObject action, CNode nodePersona,
			Map<String, CNode> actionMap, String usNrM) throws ActionInJsonFileNotFound {
		try {
			// CNode abstractAction = userStory.createNode("Action");
			if (action.has("Primary Action")) {
				JSONArray primaryAction = action.getJSONArray("Primary Action");
				// Creating Nodes for Primary Action/s
				for (int i = 0; i < primaryAction.length(); i++) {

					CNode cNode = userStory.createNode("Primary Action");

					cNode.createAttribute("name", "\"" + primaryAction.getString(i).toLowerCase() + "\"", "delete");

					// Create Edges from Action to Primary Action names
					// primary actions
					nodePersona.createEdge(cNode, "triggers", "delete");
					actionMap.put(primaryAction.getString(i), cNode);

				}
			} else {
				throw new ActionInJsonFileNotFound("Primary Action in JOSNObject not found!");
			}
			if (action.has("Secondary Action")) {
				// Creating Nodes for Secondary Action/s
				JSONArray secondaryAction = action.getJSONArray("Secondary Action");
				for (int i = 0; i < secondaryAction.length(); i++) {

					CNode cNode = userStory.createNode("Secondary Action");
					cNode.createAttribute("name", "\"" + secondaryAction.getString(i).toLowerCase() + "\"", "delete");

					actionMap.put(secondaryAction.getString(i), cNode);
				}
			} else {
				System.out.println("Secondary Action not found!");
				throw new ActionInJsonFileNotFound("Secondary Action in JOSNObject not found!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void processEntities(JSONObject jsonObject, CRule userStory, JSONObject entity, JSONArray targetsArray,
			Map<String, CNode> entityMap, String usNrM) throws EntityInJsonFileNotFound {
		try {

			if (entity.has("Primary Entity")) {
				JSONArray primaryEntity = entity.getJSONArray("Primary Entity");

				// Creating Nodes for Primary Entity/s
				for (int i = 0; i < primaryEntity.length(); i++) {
					CNode cNode = null;
					// check if entity exist in Hashmap
					if (checkEntityIsTarget(primaryEntity.getString(i), targetsArray)) {
						cNode = userStory.createNode("Primary Entity");
						cNode.createAttribute("name", "\"" + primaryEntity.getString(i).toLowerCase() + "\"", "delete");
						entityMap.put(primaryEntity.getString(i), cNode);
					} else {
						cNode = userStory.createNode("Primary Entity");
						cNode.createAttribute("name", "\"" + primaryEntity.getString(i).toLowerCase() + "\"");
						entityMap.put(primaryEntity.getString(i), cNode);
					}
				}
			} else {
				throw new EntityInJsonFileNotFound("Primary Entity in JSON file not found!");
			}
			if (entity.has("Secondary Entity")) {
				// Creating Nodes for Secondary Entity/ies
				JSONArray secondaryEntity = entity.getJSONArray("Secondary Entity");
				// Creating Nodes for Primary Entity/s
				for (int i = 0; i < secondaryEntity.length(); i++) {
					// LOGGER.info("Primary Entity is: " + secondaryEntity.getString(i) + " length
					// is: "
					// + secondaryEntity.length());
					CNode cNode = null;
					// check if entity exist in Hashmap
					if (checkEntityIsTarget(secondaryEntity.getString(i), targetsArray)) {
						// LOGGER.info("[CreateDeleteAttribute] SecondaryEntity is: " +
						// secondaryEntity.getString(i));
						cNode = userStory.createNode("Secondary Entity");
						cNode.createAttribute("name", "\"" + secondaryEntity.getString(i).toLowerCase() + "\"",
								"delete");
						entityMap.put(secondaryEntity.getString(i), cNode);
					} else {
						// LOGGER.info("[CreatePreserveAttribute] " + "SecondaryEntity *NOT* "
						// + "exist in entityMap which is: " + secondaryEntity.getString(i));
						cNode = userStory.createNode("Secondary Entity");
						cNode.createAttribute("name", "\"" + secondaryEntity.getString(i).toLowerCase() + "\"");
						entityMap.put(secondaryEntity.getString(i), cNode);

					}

				}
			} else {

				throw new EntityInJsonFileNotFound("Secondary Entity in JSON file not found!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void processTargetsEdges(JSONObject jsonObject, JSONArray targetsArray, Map<String, CNode> entityMap,
			Map<String, CNode> actionMap) throws EntityInJsonFileNotFound {

		for (int i = 0; i < targetsArray.length(); i++) {
			JSONArray currentArray = targetsArray.getJSONArray(i);
			String action = currentArray.getString(0);

			String entity = currentArray.getString(1);
			if ((actionMap.get(action) != null) && (entityMap.get(entity) != null)) {
				CNode nodeAction = actionMap.get(action);
				CNode nodeEntity = entityMap.get(entity);

				nodeAction.createEdge(nodeEntity, "targets", "delete");
			} else {
				throw new EntityInJsonFileNotFound();
			}
		}

	}

	private void processContainsEdges(JSONObject jsonObject, JSONArray containsArray, JSONArray targetsArray,
			Map<String, CNode> entityMap) throws EntityInJsonFileNotFound {

		// iterate through contains JSONArray
		for (int i = 0; i < containsArray.length(); i++) {
			JSONArray currentArray = containsArray.getJSONArray(i);

			// consider the first element of array as firstEnttiy
			String firstEntity = currentArray.getString(0);
			// consider the first element of array as secondEnttiy
			String secondEntity = currentArray.getString(1);

			// make sure that both entity is already listed in entityMap
			if ((entityMap.get(firstEntity) != null) && (entityMap.get(secondEntity) != null)) {
				CNode nodefirstEntity = entityMap.get(firstEntity);
				CNode nodeSecondEntity = entityMap.get(secondEntity);

				// Check if any Entity in Contains is already exist in Targets
				// if so annotate the contains edge as <delete>
				// otherwise annotate the contains edge as <preserve>
				if (checkEntityIsTarget(firstEntity, targetsArray) || checkEntityIsTarget(secondEntity, targetsArray)) {

					// add an edge from first Entity to second Entity and annotated it as<delete>
					nodefirstEntity.createEdge(nodeSecondEntity, "contains", "delete");

				} else {
					// add an edge from first Entity to second Entity and annotated it as<preserve>
					nodefirstEntity.createEdge(nodeSecondEntity, "contains");

				}
			} else {
				throw new EntityInJsonFileNotFound();
			}
		}

	}

// check if Entity is 
	private static boolean checkEntityIsTarget(String entity, JSONArray targets) {
		for (int j = 0; j < targets.length(); j++) {
			// iterate through each element in array
			JSONArray currentArray = targets.getJSONArray(j);
			String targetEntity = currentArray.getString(1);
			if (targetEntity.equals(entity)) {
				return true;
			}
		}
		return false;
	}

}
