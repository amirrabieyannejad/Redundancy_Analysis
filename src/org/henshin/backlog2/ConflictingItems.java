package org.henshin.backlog2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class ConflictingItems {
	private List<SecondaryEntity> secondaryEntity;
	private List<PrimaryEntity> primaryEntity;
	private List<SecondaryAction> secondaryAction;
	private List<PrimaryAction> primaryAction;
	private List<Triggers> triggers;
	private List<Targets> targets;
	private List<Contains> contains;
	private int maxConflictCount;
	private String textUs1;
	private String textUs2;
	private String UsNr1;
	private String UsNr2;

	

	public String getUsNr1() {
		return UsNr1;
	}

	public void setUsNr1(String usNr1) {
		UsNr1 = usNr1;
	}

	public String getUsNr2() {
		return UsNr2;
	}

	public void setUsNr2(String usNr2) {
		UsNr2 = usNr2;
	}

	public ConflictingItems() {
		this.secondaryEntity = new ArrayList<>();
		this.primaryEntity = new ArrayList<>();
		this.secondaryAction = new ArrayList<>();
		this.primaryAction = new ArrayList<>();
		this.triggers = new ArrayList<>();
		this.targets = new ArrayList<>();
		this.contains = new ArrayList<>();
	}

	public void addSecondaryEntity(SecondaryEntity entity) {
		secondaryEntity.add(entity);

	}

	public void addPrimaryEntity(PrimaryEntity entity) {
		primaryEntity.add(entity);

	}

	public void addSecondaryAction(SecondaryAction action) {
		secondaryAction.add(action);

	}

	public void addPrimaryAction(PrimaryAction action) {
		primaryAction.add(action);
	}

	public void addTriggers(Triggers trigger) {
		triggers.add(trigger);
	}

	public void addTargets(Targets target) {
		targets.add(target);
	}

	public void addContains(Contains contain) {
		contains.add(contain);
	}

	public List<SecondaryEntity> getSecondaryEntity() {
		return secondaryEntity;

	}

	public List<PrimaryEntity> getPrimaryEntity() {
		return primaryEntity;
	}

	public List<SecondaryAction> getSecondaryAction() {
		return secondaryAction;
	}

	public List<PrimaryAction> getPrimaryActions() {
		return primaryAction;
	}

	public List<Triggers> getTriggers() {
		return triggers;
	}

	public List<Targets> getTargets() {
		return targets;
	}

	public List<Contains> getContains() {
		return contains;
	}

	public int getMaxConflictCount() {
		return maxConflictCount;
	}
	
	public String getTextUs1() {
		return textUs1;
	}

	public void setTextUs1(String textUs1) {
		this.textUs1 = textUs1;
	}

	public String getTextUs2() {
		return textUs2;
	}

	public void setTextUs2(String textUs2) {
		this.textUs2 = textUs2;
	}
	// Method to printout all Conflicting Items
	public void printConflictingItems(FileWriter cdaWriter, List<TargetPair> targetsPairs,
			List<ContainsPair> containsPairs, List<TriggerPair> triggersPairs, JSONObject jsonConflictPair)
			throws IOException {

		List<SecondaryEntity> secondaryEntities = getSecondaryEntity();
		List<SecondaryAction> secondaryActions = getSecondaryAction();
		List<PrimaryEntity> primaryEntities = getPrimaryEntity();
		List<PrimaryAction> primaryActions = getPrimaryActions();

		maxConflictCount = 0;

		// Add main Action which contains Primary and secondary Action
		JSONObject action = new JSONObject();

		// Add main Entity which contains Primary and secondary Entity
		JSONObject entity = new JSONObject();

		

		if (!secondaryActions.isEmpty()) {
			for (SecondaryAction secondaryAction : secondaryActions) {
				if (isInCommonTargets(secondaryAction.getName(), secondaryAction.getType(), "", "", targetsPairs)) {
					cdaWriter.write("\n* " + secondaryAction.getType() + ": " + secondaryAction.getName());
					// put each secondary Actions into JSON data as an array
					action.put(secondaryAction.getType(), new JSONArray().put(secondaryAction.getName()));
					
				}
			}
		}

		if (!secondaryEntities.isEmpty()) {
			for (SecondaryEntity secondaryEntity : secondaryEntities) {
				if (isInCommonTargets(secondaryEntity.getName(), secondaryEntity.getType(), "", "", targetsPairs)) {
					cdaWriter.write("\n* " + secondaryEntity.getType() + ": " + secondaryEntity.getName());
					// put each primary entity into JSON data as an array
					entity.put(secondaryEntity.getType(), new JSONArray().put(secondaryEntity.getName()));
					
				}

			}

		}
		if (!primaryActions.isEmpty()) {
			for (PrimaryAction primaryAction : primaryActions) {
				if (isInCommonTargets(primaryAction.getName(), primaryAction.getType(), "", "", targetsPairs)) {
					cdaWriter.write("\n* " + primaryAction.getType() + ": " + primaryAction.getName());
					// put each primary action into JSON data as an array
					action.put(primaryAction.getType(), new JSONArray().put(primaryAction.getName()));
					
				}
			}

		}
		if (!primaryEntities.isEmpty()) {
			for (PrimaryEntity primaryEntity : primaryEntities) {
				if (isInCommonTargets(primaryEntity.getName(), primaryEntity.getType(), "", "", targetsPairs)) {
					cdaWriter.write("\n* " + primaryEntity.getType() + ": " + primaryEntity.getName());
					// put each primary action into JSON data as an array
					entity.put(primaryEntity.getType(), new JSONArray().put(primaryEntity.getName()));
					
				}
			}

		}
		// add all primary/secondary Action/Entity into JSON object
		jsonConflictPair.put("Entity", entity);
		jsonConflictPair.put("Action", action);

		if (!targetsPairs.isEmpty()) {
			// Add Common Targets of both user stories
			JSONArray jsonTargets = new JSONArray();
			for (TargetPair targetPair : targetsPairs) {
				// Write it also in JSON Array Targets
				JSONArray jsonTargetsPair = new JSONArray().put(targetPair.getAction()).put(targetPair.getEntity());
				jsonTargets.put(jsonTargetsPair);

				// Write it on Report if any
				cdaWriter.write("\n* Targets: Link from \"" + targetPair.getAction() + "\" to \""
						+ targetPair.getEntity() + "\" is found.");
				maxConflictCount++;
			}
			jsonConflictPair.put("Targets", jsonTargets);
		}

		// In this scenario I assume that the relationship between
		// Primary Action and Secondary Entity as Targets node is
		// not allowed. According to https://github.com/ace-design/nlp-stories
		if (!triggersPairs.isEmpty()) {
			// Add Common Triggers of both user stories
			JSONArray jsonTriggers = new JSONArray();
			for (TriggerPair triggerPair : triggersPairs) {
				// Write it also in JSON Array Targets
				JSONArray jsonTriggersPair = new JSONArray().put(triggerPair.persona).put(triggerPair.entity);
				jsonTriggers.put(jsonTriggersPair);

				// Write it on Report if any
				cdaWriter.write("\n* Triggers: Link from \"" + triggerPair.getPersona() + "\" to \""
						+ triggerPair.getEntity() + "\" is found.");
				maxConflictCount++;
			}
			jsonConflictPair.put("Triggers", jsonTriggers);

		}
		// Check if there is conflict element listed in "Contains" and if the containing
		// element
		// is in Targets if yes, write the contains edge as well
		// link
		if (!containsPairs.isEmpty()) {
			JSONArray jsonContains = new JSONArray();
			for (ContainsPair contain : containsPairs) {
				JSONArray jsonTriggersPair = new JSONArray().put(contain.getParentEntity())
						.put(contain.getChildEntity());
				jsonContains.put(jsonTriggersPair);
				cdaWriter.write("\n* Contains: Link between \"" + contain.getParentEntity() + "\" to \""
						+ contain.getChildEntity() + "\" is found.");
				maxConflictCount++;

			}
			jsonConflictPair.put("Contains", jsonContains);
		}
	}

	// Iterate through Targets Array of related user stories in json file
	// return true if name of elements which contains in conflicting items
	// whether exist or not
	public boolean isInCommonTargets(String name1, String type1, String name2, String type2,
			List<TargetPair> targetsPairs) {

		// Iterate through list of common Targets
		for (TargetPair targetsPair : targetsPairs) {

			if (name2.isEmpty()) {
				// check whether type of class in action or entity
				if (type1.toLowerCase().contains("action")) {
					String action = targetsPair.getAction().toLowerCase();

					// check if name of action(the first element) is exist in Targets Array
					if (action.equals(name1.toLowerCase())) {
						return true;
					}
				} else if (type1.toLowerCase().contains("entity")) {
					String entity = targetsPair.getEntity().toLowerCase();
					// check if name of entity(the second element) is exist in Targets Array
					if (entity.equals(name1.toLowerCase())) {
						return true;
					}

				}

			} else {
				String action = targetsPair.getAction();
				String entity = targetsPair.getEntity();
				if (name1.equalsIgnoreCase(action) && name2.equalsIgnoreCase(entity)) {
					return true;
				}

			}
		}
		return false;
	}

	// Iterate through Contains Array of related user stories in json file
	// return true if name of elements which contains in conflicting items
	// whether exist or not
	public String isInCommonContains(String name, List<ContainsPair> containsPairs) {
		// Iterate through list of common Contains
		for (ContainsPair containsPair : containsPairs) {
			String parentEntity = containsPair.getParentEntity().toLowerCase();
			String childEntity = containsPair.getChildEntity().toLowerCase();
			if (parentEntity.equalsIgnoreCase(name)) {

				return childEntity;
			} else if (childEntity.equalsIgnoreCase(name)) {

				return parentEntity;
			}
		}
		return null;
	}

}
