package org.henshin.backlog2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;
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

	// Method to printout all Conflicting Items
	public void printConflictingItems(FileWriter cdaWriter, List<TargetPair> targetsPairs,
			List<ContainsPair> containsPairs) throws IOException {

		List<SecondaryEntity> secondaryEntities = getSecondaryEntity();
		List<SecondaryAction> secondaryActions = getSecondaryAction();
		List<PrimaryEntity> primaryEntities = getPrimaryEntity();
		List<PrimaryAction> primaryActions = getPrimaryActions();
		List<Targets> targets = getTargets();
		List<Triggers> triggers = getTriggers();
		List<Contains> contains = getContains();
		maxConflictCount = 0;

		if (!secondaryEntities.isEmpty()) {
			for (SecondaryEntity secondaryEntity : secondaryEntities) {
				if (isInCommonTargets(secondaryEntity.getName(), secondaryEntity.getType(), targetsPairs)) {
					cdaWriter.write("\n* " + secondaryEntity.getType() + ": " + secondaryEntity.getName());
					maxConflictCount++;
				}

			}

		}
		if (!secondaryActions.isEmpty()) {
			for (SecondaryAction secondaryAction : secondaryActions) {
				if (isInCommonTargets(secondaryAction.getName(), secondaryAction.getType(), targetsPairs)) {
					cdaWriter.write("\n* " + secondaryAction.getType() + ": " + secondaryAction.getName());
					maxConflictCount++;
				}
			}

		}
		if (!primaryEntities.isEmpty()) {
			for (PrimaryEntity primaryEntity : primaryEntities) {
				if (isInCommonTargets(primaryEntity.getName(), primaryEntity.getType(), targetsPairs)) {
					cdaWriter.write("\n* " + primaryEntity.getType() + ": " + primaryEntity.getName());
					maxConflictCount++;
				}
			}

		}
		if (!primaryActions.isEmpty()) {
			for (PrimaryAction primaryAction : primaryActions) {
				if (isInCommonTargets(primaryAction.getName(), primaryAction.getType(), targetsPairs)) {
					cdaWriter.write("\n* " + primaryAction.getType() + ": " + primaryAction.getName());
					maxConflictCount++;
				}
			}

		}

		if (!targets.isEmpty()) {
			for (Targets target : targets) {
				if (!primaryActions.isEmpty() && !primaryEntities.isEmpty()) {
					Set<String> writtenPrimaryActions = new HashSet<>();
					Set<String> writtenPrimaryEntities = new HashSet<>();

					for (PrimaryAction primaryAction : primaryActions) {
						for (PrimaryEntity primaryEntity : primaryEntities) {
							if (isInCommonTargets(primaryAction.getName(), primaryAction.getType(), targetsPairs)
									&& isInCommonTargets(primaryEntity.getName(), primaryEntity.getType(),
											targetsPairs)) {
								if (!writtenPrimaryActions.contains(primaryAction.getName())
										&& !writtenPrimaryEntities.contains(primaryEntity.getName())) {
									cdaWriter.write(
											"\n* " + target.getName() + ": Link from \"" + primaryAction.getName()
													+ "\" to \"" + primaryEntity.getName() + "\" is found.");
									maxConflictCount++;
									writtenPrimaryActions.add(primaryAction.getName());
									writtenPrimaryEntities.add(primaryAction.getName());
								}
							}
						}
					}
				}

				if (!secondaryActions.isEmpty() && !secondaryEntities.isEmpty()) {
					Set<String> writtenSecondaryActions = new HashSet<>();
					Set<String> writtenSecondaryEntities = new HashSet<>();
					for (SecondaryAction secondaryAction : secondaryActions) {
						for (SecondaryEntity secondaryEntity : secondaryEntities) {
							if (isInCommonTargets(secondaryAction.getName(), secondaryAction.getType(), targetsPairs)
									&& isInCommonTargets(secondaryEntity.getName(), secondaryEntity.getType(),
											targetsPairs)) {
								if (!writtenSecondaryActions.contains(secondaryAction.getName())
										&& !writtenSecondaryEntities.contains(secondaryEntity.getName())) {
									cdaWriter.write(
											"\n* " + target.getName() + ": Link from \"" + secondaryAction.getName()
													+ "\" to \"" + secondaryEntity.getName() + "\" is found.");
									maxConflictCount++;
									writtenSecondaryActions.add(secondaryAction.getName());
									writtenSecondaryEntities.add(secondaryEntity.getName());
								}
							}
						}
					}
				}
			}
		}
		// In this scenario I assume that the relationship between
		// Primary Action and Secondary Entity as Targets node is
		// not allowed. According to https://github.com/ace-design/nlp-stories
		if (!triggers.isEmpty()) {
			Set<String> writtenPrimaryActions = new HashSet<>();
			// Attention: it can be more than one triggers
			if (!primaryActions.isEmpty() && !primaryEntities.isEmpty()) {
				for (Triggers trigger : triggers) {
					for (PrimaryAction primaryAction : primaryActions) {
						for (PrimaryEntity primaryEntity : primaryEntities) {
							if (isInCommonTargets(primaryAction.getName(), primaryAction.getType(), targetsPairs)
									&& isInCommonTargets(primaryEntity.getName(), primaryEntity.getType(),
											targetsPairs)) {
								if (!writtenPrimaryActions.contains(primaryAction.getName())) {
									cdaWriter.write("\n* " + trigger.getName() + ": " + ": Link from Persona to \""
											+ primaryAction.getName() + "\" is found.");
									maxConflictCount++;
									writtenPrimaryActions.add(primaryAction.getName());
								}
							}
						}
					}
				}
			}

		}
		// Check if there is conflict element listed in "Contains" if yes, show their
		// link
		if (!contains.isEmpty()) {
			for (Contains contain : contains) {
				if (!secondaryEntities.isEmpty() && !primaryEntities.isEmpty()) {
					Set<String> writtenPrimaryEntities = new HashSet<>();
					Set<String> writtenSecondaryEntities = new HashSet<>();

					for (SecondaryEntity secondaryEntity : secondaryEntities) {
						for (PrimaryEntity primaryEntity : primaryEntities) {
							if (isInCommonContains(secondaryEntity.getName(),containsPairs) !=null
									|| isInCommonContains( primaryEntity.getName(),containsPairs) !=null) {
								if (!writtenSecondaryEntities.contains(secondaryEntity.getName())
										&& !writtenPrimaryEntities.contains(primaryEntity.getName())) {
									cdaWriter.write(
											"\n* " + contain.getName() + ": Link between \"" + primaryEntity.getName()
													+ "\" to \"" + secondaryEntity.getName() + "\" is found.");
									maxConflictCount++;
									writtenPrimaryEntities.add(primaryEntity.getName());
									writtenSecondaryEntities.add(secondaryEntity.getName());
								}
							}
						}
					}
				}
				if (!secondaryEntities.isEmpty()) {
					Set<String> processedPairs = new HashSet<>();
					for (SecondaryEntity secondaryEntity : secondaryEntities) {
						
							String pair = secondaryEntity.getName();
							String secondPair = isInCommonContains(secondaryEntity.getName(),
									 containsPairs);
							if (secondPair!=null) {
								if (!processedPairs.contains(pair)) {
									cdaWriter.write(
											"\n* " + contain.getName() + ": Link between \"" + secondaryEntity.getName()
													+ "\" to \"" + secondPair + "\" is found.");
									maxConflictCount++;
									processedPairs.add(pair);

								}
							}
						}
					}
				}
			}
		}

	

	// Iterate through Targets Array of related user stories in json file
	// return true if name of elements which contains in conflicting items
	// whether exist or not
	public boolean isInCommonTargets(String name, String type, List<TargetPair> targetsPairs) {

		// Iterate through list of common Targets
		for (TargetPair targetsPair : targetsPairs) {

			// check whether type of class in action or entity
			if (type.toLowerCase().contains("action")) {
				String action = targetsPair.getAction().toLowerCase();

				// check if name of action(the first element) is exist in Targets Array
				if (action.equals(name.toLowerCase())) {
					return true;
				}
			} else if (type.toLowerCase().contains("entity")) {
				String entity = targetsPair.getEntity().toLowerCase();
				// check if name of entity(the second element) is exist in Targets Array
				if (entity.equals(name.toLowerCase())) {
					return true;
				}
			}

		}
		return false;
	}

	// Iterate through Contains Array of related user stories in json file
	// return true if name of elements which contains in conflicting items
	// whether exist or not
	public String isInCommonContains(String name,  List<ContainsPair> containsPairs) {
		// Iterate through list of common Contains
		for (ContainsPair containsPair : containsPairs) {
			String parentEntity = containsPair.getParentEntity().toLowerCase();
			String childEntity = containsPair.getChildEntity().toLowerCase();
			
			if(parentEntity.equalsIgnoreCase(name)) {
				return childEntity;
			}else if(childEntity.equalsIgnoreCase(name)){
				return parentEntity;
			}
			
		}

		return null;
	}

}
