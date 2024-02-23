package org.henshin.backlog2.Archive;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

//-----Delete Annotation for Attributes actions and entities plus their entities which have target edges or triggers edge
public class CDA {



	public static void main(String[] args) {
		String Path = "/";
		String henshinFileName = "backlog_v4.henshin";
		HenshinResourceSet rS = new HenshinResourceSet(Path);
		Module module = rS.getModule(henshinFileName, false);
		List<Rule> rules = new LinkedList<>();
		for (Unit unit: module.getUnits()) {
			if(unit instanceof Rule)
				rules.add((Rule) unit);
		}



	}



}
