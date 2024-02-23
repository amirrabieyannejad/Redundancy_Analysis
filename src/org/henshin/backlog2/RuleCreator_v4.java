package org.henshin.backlog2;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

//import org.graalvm.compiler.nodes.ReturnNode;
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
	public static CModule module = new CModule("backlog_v4");
	private static final Logger LOGGER = Logger.getLogger(RuleCreator_v4.class.getName());
	private Map<String, CNode> entityMap = new HashMap<>();
	private Map<String, CNode> actionMap = new HashMap<>();
	String usNrM = null;
	CRule userStoryM = null;
	JSONArray personaM = null;
	JSONObject actionM = null;
	JSONObject entityM = null;
	JSONArray targetsArrayM = null;
	JSONArray containsArrayM = null;
	String textM = null;

	public static void main(String[] args) {
		JSONArray jsonArray = null;
		// JSONObject jsonObject = null;
		String fileName = "C:\\Users\\amirr\\eclipse-workspace_new\\"
				+ "org.henshin.backlog2\\g03_baseline_pos_num.json";

		try (FileReader reader = new FileReader(fileName)) {
			JSONTokener tokener = new JSONTokener(reader);

			// Read JSON file
			jsonArray = new JSONArray(tokener);
		} catch (IOException e) {
			e.printStackTrace();
		}
		module.addImportsFromFile("Backlog_v2.3.ecore");
		RuleCreator_v4 ruleCreator = new RuleCreator_v4();
		// ruleCreator.processJsonFile(jsonObject);
		ruleCreator.processJsonFile(jsonArray);
		module.save();

	}

	public void processJsonFile(JSONArray json) {
		for (int i = 0; i < json.length(); i++) {
			try {
				JSONObject jsonObject = json.getJSONObject(i);

				if (jsonObject.has("US_Nr")) {
					LOGGER.info("Processing rule with US_Nr: " + usNrM);
					usNrM = jsonObject.getString("US_Nr");
					userStoryM = processRule(jsonObject, usNrM);
				} else {
					LOGGER.info("[Error] Secondary Entity in User Story: " + usNrM + " has not found!");
				}

				if (jsonObject.has("Persona")) {
					personaM = jsonObject.getJSONArray("Persona");
				} else {
					LOGGER.info("[Error] Persona in User Story: " + usNrM + " has not found! ");

				}

				if (jsonObject.has("Action")) {
					actionM = jsonObject.getJSONObject("Action");
				} else {
					LOGGER.info("[Error] Action in User Story: " + usNrM + " has not found! ");
				}

				if (jsonObject.has("Entity")) {
					entityM = jsonObject.getJSONObject("Entity");

				} else {
					LOGGER.info("[Error] Entity in User Story: " + usNrM + " has not found! ");
				}

				if (jsonObject.has("Targets")) {
					targetsArrayM = jsonObject.getJSONArray("Targets");
				} else {
					LOGGER.info("[Error] Targets has not found!");
				}

				if (jsonObject.has("Contains")) {
					containsArrayM = jsonObject.getJSONArray("Contains");
				} else {
					LOGGER.info("[Error] Contains has not found!");
				}

				if (jsonObject.has("Text")) {
					textM = jsonObject.getString("Text");
				} else {
					LOGGER.info("[Error] Text has not found!");
				}

				CNode personaNode = processPersona(jsonObject, personaM, userStoryM, usNrM);

				processText(jsonObject, userStoryM, textM);
				processActions(jsonObject, userStoryM, actionM, personaNode);
				processEntities(jsonObject, userStoryM, entityM, targetsArrayM);
				processTargetsEdges(jsonObject, targetsArrayM);
				processContainsEdges(jsonObject, containsArrayM, targetsArrayM);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	private CRule processRule(JSONObject jsonObject, String usNr) {
		try {

			CRule userStory = module.createRule(usNr);
			return userStory;

		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

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
			if (jsonObject.has("Persona")) {
				LOGGER.info("Persona is: " + persona.getString(0) + " length is: " + persona.length());
				CNode nodePersona = userStory.createNode("Persona");
				nodePersona.createAttribute("name", "\"" + persona.getString(0).toLowerCase() + "\"");
				return nodePersona;

			} else {
				LOGGER.info("Persona in User Story: " + usNr + " has not found! Try to create dummy Persona Node");
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void processActions(JSONObject jsonObject, CRule userStory, JSONObject action, CNode nodePersona) {
		try {
			// CNode abstractAction = userStory.createNode("Action");
			if (action.has("Primary Action")) {
				JSONArray primaryAction = action.getJSONArray("Primary Action");
				// Creating Nodes for Primary Action/s
				for (int i = 0; i < primaryAction.length(); i++) {
					LOGGER.info("Primary Aciton is: " + primaryAction.getString(i) + " length is: "
							+ primaryAction.length());
					CNode cNode = userStory.createNode("Primary Action");
					// CNode cNode = userStory.createNode("Primary Action");
					cNode.createAttribute("name", "\"" + primaryAction.getString(i).toLowerCase() + "\"", "delete");

					// Create Edges from Action to Primary Action names
					// primary actions
					nodePersona.createEdge(cNode, "triggers", "delete");
					actionMap.put(primaryAction.getString(i), cNode);

				}
			} else {
				LOGGER.info("Primary Action in User Story: " + usNrM + " has not found!");
			}
			if (action.has("Secondary Action")) {
				// Creating Nodes for Secondary Action/s
				JSONArray secondaryAction = action.getJSONArray("Secondary Action");
				for (int i = 0; i < secondaryAction.length(); i++) {
					LOGGER.info("Secondary Aciton is: " + secondaryAction.getString(i) + " length is: "
							+ secondaryAction.length());
					CNode cNode = userStory.createNode("Secondary Action");
					// CNode cNode = userStory.createNode("Secondary Action");
					cNode.createAttribute("name", "\"" + secondaryAction.getString(i).toLowerCase() + "\"", "delete");

					actionMap.put(secondaryAction.getString(i), cNode);
				}
			} else {
				LOGGER.info("Secondary Action in User Story: " + usNrM + " has not found!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void processEntities(JSONObject jsonObject, CRule userStory, JSONObject entity, JSONArray targetsArray) {
		try {

			// CNode abstractEntity = userStory.createNode("Entity");
			if (entity.has("Primary Entity")) {
				JSONArray primaryEntity = entity.getJSONArray("Primary Entity");

				// Creating Nodes for Primary Entity/s
				for (int i = 0; i < primaryEntity.length(); i++) {
					LOGGER.info("Primary Entity is: " + primaryEntity.getString(i) + " length is: "
							+ primaryEntity.length());
					CNode cNode = null;
					// check if entity exist in Hashmap
					if (checkEntityIsTarget(primaryEntity.getString(i), targetsArray)) {
						LOGGER.info("[CreateDeleteAttribute] primaryEntity is: " + primaryEntity.getString(i));
						cNode = userStory.createNode("Primary Entity");
						cNode.createAttribute("name", "\"" + primaryEntity.getString(i).toLowerCase() + "\"", "delete");
						entityMap.put(primaryEntity.getString(i), cNode);
					} else {
						LOGGER.info("[CreatePreserveAttribute] " + "SecondaryEntity *NOT*"
								+ " exist in entityMap which is: " + primaryEntity.getString(i));
						cNode = userStory.createNode("Primary Entity");
						cNode.createAttribute("name", "\"" + primaryEntity.getString(i).toLowerCase() + "\"");
						entityMap.put(primaryEntity.getString(i), cNode);

					}

				}
			} else {
				LOGGER.info("Primary Entity in User Story: " + usNrM + " has not found!");
			}
			if (entity.has("Secondary Entity")) {
				// Creating Nodes for Secondary Entity/s
				JSONArray secondaryEntity = entity.getJSONArray("Secondary Entity");
				// Creating Nodes for Primary Entity/s
				for (int i = 0; i < secondaryEntity.length(); i++) {
					LOGGER.info("Primary Entity is: " + secondaryEntity.getString(i) + " length is: "
							+ secondaryEntity.length());
					CNode cNode = null;
					// check if entity exist in Hashmap
					if (checkEntityIsTarget(secondaryEntity.getString(i), targetsArray)) {
						LOGGER.info("[CreateDeleteAttribute] SecondaryEntity is: " + secondaryEntity.getString(i));
						cNode = userStory.createNode("Secondary Entity");
						cNode.createAttribute("name", "\"" + secondaryEntity.getString(i).toLowerCase() + "\"",
								"delete");
						entityMap.put(secondaryEntity.getString(i), cNode);
					} else {
						LOGGER.info("[CreatePreserveAttribute] " + "SecondaryEntity *NOT* "
								+ "exist in entityMap which is: " + secondaryEntity.getString(i));
						cNode = userStory.createNode("Secondary Entity");
						cNode.createAttribute("name", "\"" + secondaryEntity.getString(i).toLowerCase() + "\"");
						entityMap.put(secondaryEntity.getString(i), cNode);

					}

				}
			} else {

				LOGGER.info("Secondary Entity in User Story: " + usNrM + " has not found!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void processTargetsEdges(JSONObject jsonObject, JSONArray targetsArray) {

		for (int i = 0; i < targetsArray.length(); i++) {
			JSONArray currentArray = targetsArray.getJSONArray(i);
			String action = currentArray.getString(0);
			LOGGER.info("Action to read is: " + action);

			String entity = currentArray.getString(1);
			LOGGER.info("Entity to read is: " + entity);
			if ((actionMap.get(action) != null) && (entityMap.get(entity) != null)) {

				CNode nodeAction = actionMap.get(action);
				CNode nodeEntity = entityMap.get(entity);

				nodeAction.createEdge(nodeEntity, "targets", "delete");

				LOGGER.info("Entity is: " + entity + " and Action : " + action + " Succesful targests edge creation!");

			} else {
				LOGGER.info("[ERROR]Action or Entity in Targets are not found!");
			}
		}

	}

	private void processContainsEdges(JSONObject jsonObject, JSONArray containsArray, JSONArray targetsArray) {

		for (int i = 0; i < containsArray.length(); i++) {
			JSONArray currentArray = containsArray.getJSONArray(i);
			String firstEntity = currentArray.getString(0);
			String secondEntity = currentArray.getString(1);
			LOGGER.info("FirstEntity : " + firstEntity + " SecondEntity : " + secondEntity);
			if ((entityMap.get(firstEntity) != null) && (entityMap.get(secondEntity) != null)) {
				CNode nodefirstEntity = entityMap.get(firstEntity);
				CNode nodeSecondEntity = entityMap.get(secondEntity);
				if (checkEntityIsTarget(firstEntity, targetsArray) || checkEntityIsTarget(secondEntity, targetsArray)) {
					nodefirstEntity.createEdge(nodeSecondEntity, "contains", "delete");
					LOGGER.info("[CreateDeleteEdge] FirstEntity created: " + firstEntity + " .SecondEntity created: "
							+ secondEntity);
				} else {
					nodefirstEntity.createEdge(nodeSecondEntity, "contains");
					LOGGER.info("[PreserveEdge] FirstEntity created: " + firstEntity + " .SecondEntity created: "
							+ secondEntity);
				}
			} else {
				LOGGER.info("[ERROR] Entities in Contains are not found!");
			}
		}

	}

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
