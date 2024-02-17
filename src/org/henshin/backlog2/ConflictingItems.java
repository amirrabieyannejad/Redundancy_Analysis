package org.henshin.backlog2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConflictingItems {
	private List<SecondaryEntity> secondaryEntity;
	private List<PrimaryEntity> primaryEntity;
	private List<SecondaryAction> secondaryAction;
	private List<PrimaryAction> primaryAction;
	private List<Triggers> triggers;
	private List<Targets> targets;

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

	// Method to printout all Conflicting Items
	public void printConflictingItems(FileWriter cdaWriter) throws IOException {
		List<SecondaryEntity> secondaryEntities = getSecondaryEntity();
		List<SecondaryAction> secondaryActions = getSecondaryAction();
		List<PrimaryEntity> primaryEntities = getPrimaryEntity();
		List<PrimaryAction> primaryActions = getPrimaryActions();
		List<Targets> targets = getTargets();
		List<Triggers> triggers = getTriggers();

		if (!secondaryEntities.isEmpty()) {
			for (SecondaryEntity secondaryEntity : secondaryEntities) {
				cdaWriter.write("\n* " + secondaryEntity.getType() + ": " + secondaryEntity.getName());

			}

		}
		if (!secondaryActions.isEmpty()) {
			for (SecondaryAction secondaryAction : secondaryActions) {
				cdaWriter.write("\n* " + secondaryAction.getType() + ": " + secondaryAction.getName());

			}

		}
		if (!primaryEntities.isEmpty()) {
			for (PrimaryEntity primaryEntity : primaryEntities) {
				cdaWriter.write("\n* " + primaryEntity.getType() + ": " + primaryEntity.getName());

			}

		}
		if (!primaryActions.isEmpty()) {
			for (PrimaryAction primaryAction : primaryActions) {
				cdaWriter.write("\n* " + primaryAction.getType() + ": " + primaryAction.getName());

			}

		}
		if (!targets.isEmpty()) {
			for (Targets target : targets) {
				cdaWriter.write("\n* Link from " + target.getType() + ": " + target.getName());

			}

		}
		if (!triggers.isEmpty()) {
			for (Triggers trigger : triggers) {
				cdaWriter.write("\n* Link from " + trigger.getType() + ": " + trigger.getName());

			}

		}

	}

}
