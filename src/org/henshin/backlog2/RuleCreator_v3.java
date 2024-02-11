package org.henshin.backlog2;
//import org.graalvm.compiler.nodes.ReturnNode;
import org.eclipse.emf.henshin.model.compact.CModule;
import org.eclipse.emf.henshin.model.compact.CNode;
import org.eclipse.emf.henshin.model.compact.CRule;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
import java.util.HashMap;


//-----Delete Annotation only for Attributes actions and entities which have target edges 
public class RuleCreator_v3 {
	public static CModule module = new CModule("backlog_v3");
	private static final Logger LOGGER = Logger.getLogger(RuleCreator_v3.class.getName());
	private Map<String, CNode> entityMap = new HashMap<>();
	private Map<String, CNode> actionMap = new HashMap<>();

	public static void main(String[] args) {
		JSONArray jsonArray = null;
		// JSONObject jsonObject = null;
		String fileName = "C:\\Users\\amirr\\eclipse-workspace_new\\org.henshin.backlog2\\g03_baseline_pos_num.json";

		try (FileReader reader = new FileReader(fileName)) {
			JSONTokener tokener = new JSONTokener(reader);

			// Read JSON file
			jsonArray = new JSONArray(tokener);
		} catch (IOException e) {
			e.printStackTrace();
		}
		module.addImportsFromFile("Backlog_v2.3.ecore");
		RuleCreator_v3 ruleCreator = new RuleCreator_v3();
		// ruleCreator.processJsonFile(jsonObject);
		ruleCreator.processJsonFile(jsonArray);
		module.save();

	}

	public void processJsonFile(JSONArray json) {
		for (int i = 0; i < json.length(); i++) {
			JSONObject jsonObject = json.getJSONObject(i);
			processRules(jsonObject);
			processTargetsEdges(jsonObject);
			processContainsEdges(jsonObject);

		}
	}

	private void processContainsEdges(JSONObject jsonObject) {
		if (jsonObject.has("Contains")) {
			JSONArray containsArray = jsonObject.getJSONArray("Contains");
			for (int i = 0; i < containsArray.length(); i++) {
				JSONArray currentArray = containsArray.getJSONArray(i);
				String firstEntity = currentArray.getString(0);
				String secondEntity = currentArray.getString(1);
				LOGGER.info("FirstEntity : " + firstEntity + " SecondEntity : " + secondEntity);
				if ((entityMap.get(firstEntity) != null) && (entityMap.get(secondEntity) != null)) {
					CNode nodefirstEntity = entityMap.get(firstEntity);
					CNode nodeSecondEntity = entityMap.get(secondEntity);
					nodefirstEntity.createEdge(nodeSecondEntity, "contains");
					LOGGER.info("[Seccess] FirstEntity created: " + firstEntity + " .SecondEntity created: "
							+ secondEntity);
				} else {
					LOGGER.info("[ERROR] Entities in Contains are not found!");
				}
			}

		} else {
			LOGGER.info("Contains has not found!");
		}
	}

	private void processTargetsEdges(JSONObject jsonObject) {
		if (jsonObject.has("Targets")) {
			JSONArray targetsArray = jsonObject.getJSONArray("Targets");
			for (int i = 0; i < targetsArray.length(); i++) {
				JSONArray currentArray = targetsArray.getJSONArray(i);
				String action = currentArray.getString(0);
				LOGGER.info("Action to read is: " + action);

				String entity = currentArray.getString(1);
				LOGGER.info("Entity to read is: " + entity);
				if ((actionMap.get(action) != null) && (entityMap.get(entity) != null)) {

					CNode nodeAction = actionMap.get(action);
					CNode nodeEntity = entityMap.get(entity);

					nodeAction.createEdge(nodeEntity, "targets","delete");


					LOGGER.info(
							"Entity is: " + entity + " and Action : " + action + " Succesful targests edge creation!");
					;
				} else {
					LOGGER.info("[ERROR]Action or Entity in Targets are not found!");
				}
			}

		} else {
			LOGGER.info("Targets has not found!");
		}

	}

	public void processRules(JSONObject jsonObject) {
		String usNr = null;
		CNode nodePersona = null;
		if (jsonObject.has("US_Nr")) {

			try {
				usNr = jsonObject.getString("US_Nr");
				LOGGER.info("Processing rule with US_Nr: " + usNr);
				CRule userStory = module.createRule(usNr);
				if (jsonObject.has("Persona")) {
					JSONArray persona = jsonObject.getJSONArray("Persona");
					for (int i = 0; i < persona.length(); i++) {
						LOGGER.info("Persona is: " + persona.getString(i) + " length is: " + persona.length());
						nodePersona = userStory.createNode("Persona");
						nodePersona.createAttribute("name", "\"" + persona.getString(i).toLowerCase() + "\"");
					}
				} else {
					LOGGER.info("Persona in User Story: " + usNr + " has not found!");
				}
				if (jsonObject.has("Action")) {
					JSONObject action = jsonObject.getJSONObject("Action");
					// CNode abstractAction = userStory.createNode("Action");
					if (action.has("Primary Action")) {
						JSONArray primaryAction = action.getJSONArray("Primary Action");
						// Creating Nodes for Primary Action/s
						for (int i = 0; i < primaryAction.length(); i++) {
							LOGGER.info("Primary Aciton is: " + primaryAction.getString(i) + " length is: "
									+ primaryAction.length());
							CNode cNode = userStory.createNode("Primary Action");
							// CNode cNode = userStory.createNode("Primary Action");
							cNode.createAttribute("name", "\"" + primaryAction.getString(i).toLowerCase() + "\"",
									"delete");

							// Create Edges from Action to Primary Action names primary actions
							nodePersona.createEdge(cNode, "triggers","delete");
							actionMap.put(primaryAction.getString(i), cNode);

						}
					} else {
						LOGGER.info("Primary Action in User Story: " + usNr + " has not found!");
					}
					if (action.has("Secondary Action")) {
						// Creating Nodes for Secondary Action/s
						JSONArray secondaryAction = action.getJSONArray("Secondary Action");
						for (int i = 0; i < secondaryAction.length(); i++) {
							LOGGER.info("Secondary Aciton is: " + secondaryAction.getString(i) + " length is: "
									+ secondaryAction.length());
							CNode cNode = userStory.createNode("Secondary Action");
							// CNode cNode = userStory.createNode("Secondary Action");
							cNode.createAttribute("name", "\"" + secondaryAction.getString(i).toLowerCase() + "\"",
									"delete");

							actionMap.put(secondaryAction.getString(i), cNode);
						}
					} else {
						LOGGER.info("Secondary Action in User Story: " + usNr + " has not found!");
					}

				}
				if (jsonObject.has("Entity")) {
					JSONObject entity = jsonObject.getJSONObject("Entity");
					JSONArray targetsArray = jsonObject.getJSONArray("Targets");
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
								cNode.createAttribute("name", "\"" + primaryEntity.getString(i).toLowerCase() + "\"",
										"delete");
								entityMap.put(primaryEntity.getString(i), cNode);
							} else {
								LOGGER.info(
										"[CreatePreserveAttribute] SecondaryEntity *NOT* exist in entityMap which is: "
												+ primaryEntity.getString(i));
								cNode = userStory.createNode("Primary Entity");
								cNode.createAttribute("name", "\"" + primaryEntity.getString(i).toLowerCase() + "\"");
								entityMap.put(primaryEntity.getString(i), cNode);

							}

						}
					} else {
						LOGGER.info("Primary Entity in User Story: " + usNr + " has not found!");
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
								LOGGER.info(
										"[CreateDeleteAttribute] SecondaryEntity is: " + secondaryEntity.getString(i));
								cNode = userStory.createNode("Secondary Entity");
								cNode.createAttribute("name", "\"" + secondaryEntity.getString(i).toLowerCase() + "\"",
										"delete");
								entityMap.put(secondaryEntity.getString(i), cNode);
							} else {
								LOGGER.info(
										"[CreatePreserveAttribute] SecondaryEntity *NOT* exist in entityMap which is: "
												+ secondaryEntity.getString(i));
								cNode = userStory.createNode("Secondary Entity");
								cNode.createAttribute("name", "\"" + secondaryEntity.getString(i).toLowerCase() + "\"");
								entityMap.put(secondaryEntity.getString(i), cNode);

							}

						}
					} else {

						LOGGER.info("Secondary Entity in User Story: " + usNr + " has not found!");
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
			LOGGER.info("US_Nr has not found!");
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
