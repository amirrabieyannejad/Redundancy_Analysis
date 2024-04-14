package org.henshin.backlog.code.report;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class RedundancyItems {
	private List<SecondaryEntity> secondaryEntity;
	private List<PrimaryEntity> primaryEntity;
	private List<SecondaryAction> secondaryAction;
	private List<PrimaryAction> primaryAction;
	private List<Triggers> triggers;
	private List<Targets> targets;
	private List<Contains> contains;
	private int maxRedundancyCount;
	private String textUs1;
	private String textUs2;
	private String UsNr1;
	private String UsNr2;

	// this values are used to find out how many conflict pairs
	// are found in which sentence part(main vs benefit)
	private int mainRedundancyCount;
	private int benefitRedundancyCount;

	public int getMainRedundancyCount() {
		return mainRedundancyCount;
	}

	public void setMainRedundancyCount(int mainRedundancyCount) {
		this.mainRedundancyCount = mainRedundancyCount;
	}

	public int getBenefitRedundancyCount() {
		return benefitRedundancyCount;
	}

	public void setBenefitRedundancyCount(int benefitRedundancyCount) {
		this.benefitRedundancyCount = benefitRedundancyCount;
	}

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

	public RedundancyItems() {
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

	public void setMaxRedundancyCount(int maxRedundancyCount) {
		this.maxRedundancyCount = maxRedundancyCount;
	}

	public List<Targets> getTargets() {
		return targets;
	}

	public List<Contains> getContains() {
		return contains;
	}

	public int getTotalRedundancyCount() {
		return maxRedundancyCount;
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
	public void printRedundantItems(FileWriter cdaWriter, List<TargetsPair> targetsPairs,
			List<ContainsPair> containsPairs, List<TriggersPair> triggersPairs, JSONObject jsonRedundancyPair)
			throws IOException {

		List<SecondaryEntity> secondaryEntities = getSecondaryEntity();
		List<SecondaryAction> secondaryActions = getSecondaryAction();
		List<PrimaryEntity> primaryEntities = getPrimaryEntity();
		List<PrimaryAction> primaryActions = getPrimaryActions();

		// Add main Action which contains Primary and secondary Action
		JSONObject action = new JSONObject();

		// Add main Entity which contains Primary and secondary Entity
		JSONObject entity = new JSONObject();

		if (!secondaryActions.isEmpty()) {
			for (SecondaryAction secondaryAction : secondaryActions) {
				String secondaryActionName=secondaryAction.getName();
				String secondaryActionClass=secondaryAction.getClassType();
				if (isInCommonTargets(secondaryActionName, secondaryActionClass, "", "", targetsPairs)) {
					cdaWriter.write("\n* " + secondaryAction.getClassType() + ": " + secondaryAction.getName());
					// put each secondary Actions into JSON data as an array
					action.put(secondaryAction.getClassType(), new JSONArray().put(secondaryAction.getName()));

				}
			}
		}

		if (!secondaryEntities.isEmpty()) {
			for (SecondaryEntity secondaryEntity : secondaryEntities) {
				if (isInCommonTargets(secondaryEntity.getName(), secondaryEntity.getClassType(), "", "", targetsPairs)) {
					cdaWriter.write("\n* " + secondaryEntity.getClassType() + ": " + secondaryEntity.getName());
					// put each primary entity into JSON data as an array
					entity.put(secondaryEntity.getClassType(), new JSONArray().put(secondaryEntity.getName()));

				}

			}

		}
		if (!primaryActions.isEmpty()) {
			for (PrimaryAction primaryAction : primaryActions) {
				if (isInCommonTargets(primaryAction.getName(), primaryAction.getClassType(), "", "", targetsPairs)) {
					cdaWriter.write("\n* " + primaryAction.getClassType() + ": " + primaryAction.getName());
					// put each primary action into JSON data as an array
					action.put(primaryAction.getClassType(), new JSONArray().put(primaryAction.getName()));

				}
			}

		}
		if (!primaryEntities.isEmpty()) {
			for (PrimaryEntity primaryEntity : primaryEntities) {
				if (isInCommonTargets(primaryEntity.getName(), primaryEntity.getClassType(), "", "", targetsPairs)) {
					cdaWriter.write("\n* " + primaryEntity.getClassType() + ": " + primaryEntity.getName());
					// put each primary action into JSON data as an array
					entity.put(primaryEntity.getClassType(), new JSONArray().put(primaryEntity.getName()));
				}
			}
		}
		// add all primary/secondary Action/Entity into JSON object
		jsonRedundancyPair.put("Entity", entity);
		jsonRedundancyPair.put("Action", action);

		if (!targetsPairs.isEmpty()) {
			// Add Common Targets of both user stories
			JSONArray jsonTargets = new JSONArray();
			for (TargetsPair targetPair : targetsPairs) {
				// Write it also in JSON Array Targets
				JSONArray jsonTargetsPair = new JSONArray().put(targetPair.getAction()).put(targetPair.getEntity());
				jsonTargets.put(jsonTargetsPair);

				// Write it on Report if any
				cdaWriter.write("\n* Targets: Link from \"" + targetPair.getAction() + "\" to \""
						+ targetPair.getEntity() + "\" is found.");

			}
			jsonRedundancyPair.put("Targets", jsonTargets);
		}

		// In this scenario I assume that the relationship between
		// Primary Action and Secondary Entity as Targets node is
		// not allowed. According to https://github.com/ace-design/nlp-stories
		if (!triggersPairs.isEmpty()) {
			// Add Common Triggers of both user stories
			JSONArray jsonTriggers = new JSONArray();
			for (TriggersPair triggerPair : triggersPairs) {
				String triggerPersona= triggerPair.getPersona();
				String triggerAction = triggerPair.getAction();
				
				// make sure that action is
				// also belongs to common targets if any
					// Write it also in JSON Array Triggers
					JSONArray jsonTriggersPair = new JSONArray().put(triggerPersona).put(triggerAction);
					jsonTriggers.put(jsonTriggersPair);

					// Write it on Report if any
					cdaWriter.write("\n* Triggers: Link from \"" + triggerPersona + "\" to \""
							+ triggerAction + "\" is found.");
			}
			jsonRedundancyPair.put("Triggers", jsonTriggers);
		}else {
			jsonRedundancyPair.put("Triggers", "");
		}
		
		// Check if there is conflict element listed in "Contains" and if the containing
		// element
		// is in Targets if yes, write the contains edge as well
		// link
		if (!containsPairs.isEmpty()) {
			JSONArray jsonContains = new JSONArray();
			for (ContainsPair contain : containsPairs) {
				JSONArray jsonContainsPair = new JSONArray().put(contain.getParentEntity())
						.put(contain.getChildEntity());
				jsonContains.put(jsonContainsPair);
				cdaWriter.write("\n* Contains: Link between \"" + contain.getParentEntity() + "\" and \""
						+ contain.getChildEntity() + "\" is found.");

			}
			jsonRedundancyPair.put("Contains", jsonContains);

		}

	}

	// Iterate through Targets Array of related user stories in json file
	// return true if name of elements which contains is in conflicting items
	// whether exist or not
	public boolean isInCommonTargets(String name1, String type1, String name2, String type2,
			List<TargetsPair> targetsPairs) {

		// Iterate through list of common Targets
		for (TargetsPair targetsPair : targetsPairs) {

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
