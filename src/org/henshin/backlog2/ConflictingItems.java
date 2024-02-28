package org.henshin.backlog2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;

public class ConflictingItems {
	private List<SecondaryEntity> secondaryEntity;
	private List<PrimaryEntity> primaryEntity;
	private List<SecondaryAction> secondaryAction;
	private List<PrimaryAction> primaryAction;
	private List<Triggers> triggers;
	private List<Targets> targets;
	private int maxConflictCount;

	public ConflictingItems() {
		this.secondaryEntity = new ArrayList<>();
		this.primaryEntity = new ArrayList<>();
		this.secondaryAction = new ArrayList<>();
		this.primaryAction = new ArrayList<>();
		this.triggers = new ArrayList<>();
		this.targets = new ArrayList<>();
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
	public int getMaxConflictCount() {
		return maxConflictCount;
	}

	// Method to printout all Conflicting Items
	public void printConflictingItems(FileWriter cdaWriter, JSONArray jsonArrayTargets) throws IOException {

		List<SecondaryEntity> secondaryEntities = getSecondaryEntity();
		List<SecondaryAction> secondaryActions = getSecondaryAction();
		List<PrimaryEntity> primaryEntities = getPrimaryEntity();
		List<PrimaryAction> primaryActions = getPrimaryActions();
		List<Targets> targets = getTargets();
		List<Triggers> triggers = getTriggers();
		maxConflictCount = 0;

		if (!secondaryEntities.isEmpty()) {
			for (SecondaryEntity secondaryEntity : secondaryEntities) {
				if (isInTargets(secondaryEntity.getName(), secondaryEntity.getType(), jsonArrayTargets)) {
					cdaWriter.write("\n* " + secondaryEntity.getType() + ": " + secondaryEntity.getName());
					maxConflictCount ++;
				}

			}

		}
		if (!secondaryActions.isEmpty()) {
			for (SecondaryAction secondaryAction : secondaryActions) {
				if (isInTargets(secondaryAction.getName(), secondaryAction.getType(), jsonArrayTargets)) {
					cdaWriter.write("\n* " + secondaryAction.getType() + ": " + secondaryAction.getName());
					maxConflictCount ++;
				}
			}

		}
		if (!primaryEntities.isEmpty()) {
			for (PrimaryEntity primaryEntity : primaryEntities) {
				if (isInTargets(primaryEntity.getName(), primaryEntity.getType(), jsonArrayTargets)) {
					cdaWriter.write("\n* " + primaryEntity.getType() + ": " + primaryEntity.getName());
					maxConflictCount ++;
				}
			}

		}
		if (!primaryActions.isEmpty()) {
			for (PrimaryAction primaryAction : primaryActions) {
				if (isInTargets(primaryAction.getName(), primaryAction.getType(), jsonArrayTargets)) {
					cdaWriter.write("\n* " + primaryAction.getType() + ": " + primaryAction.getName());
					maxConflictCount ++;
				}
			}

		}
		if (!targets.isEmpty()) {
			for (Targets target : targets) {
				cdaWriter.write("\n* Link from " + target.getType() + ": " + target.getName());
				maxConflictCount ++;
			}

		}
		// In this scenario I assume that the relationship between
		// Primary Action and Secondary Entity as Targets node is
		// not allowed. According to https://github.com/ace-design/nlp-stories
		if (!triggers.isEmpty()) {
			// Attention: it can be more than one triggers
			if (!primaryActions.isEmpty() && !primaryEntities.isEmpty()) {
				for (Triggers trigger : triggers) {
					for (PrimaryAction primaryAction : primaryActions) {
						for (PrimaryEntity primaryEntity : primaryEntities) {
							if (isInTargets(primaryAction.getName(), primaryAction.getType(), jsonArrayTargets)
									&& isInTargets(primaryEntity.getName(), primaryEntity.getType(),
											jsonArrayTargets)) {
								cdaWriter.write("\n* " + trigger.getName() + ": Link from " + primaryAction.getName()
										+ " to " + primaryEntity.getName() + "is found.");
								maxConflictCount ++;
							}
						}
					}
				}
			}

		}

	}

	// Iterate through Targets Array one of related user stories in json file
	// return true if name of elements which contains in conflicting items
	// whether exist or not
	public boolean isInTargets(String name, String type, JSONArray jsonArrayTargets) {
		// Iterate through "Targets" which is JSONArray
		// and also contains arrays which have two element
		for (int i = 0; i < jsonArrayTargets.length(); i++) {
			JSONArray elements = jsonArrayTargets.getJSONArray(i);

			// check whether type of class in action or entity
			if (type.toLowerCase().contains("action")) {
				String action = elements.getString(0).toLowerCase();

				// check if name of action(the first element) is exist in Targets Array
				if (action.equals(name.toLowerCase())) {
					return true;
				}
			} else if (type.toLowerCase().contains("entity")) {
				String entity = elements.getString(1).toLowerCase();
				// check if name of entity(the second element) is exist in Targets Array
				if (entity.equals(name.toLowerCase())) {
					return true;
				}
			}

		}
		return false;
	}

}
