package org.henshin.backlog2.Archive;

//import org.graalvm.compiler.nodes.ReturnNode;
import org.eclipse.emf.henshin.model.compact.CModule;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.multicda.*;
import org.eclipse.emf.henshin.multicda.cda.*;
import org.eclipse.emf.henshin.multicda.cda.conflict.*;
import org.eclipse.emf.henshin.multicda.cda.units.*;
import org.eclipse.emf.henshin.multicda.cda.unitTest.*;
import org.eclipse.emf.henshin.multicda.cda.computation.*;
import org.eclipse.emf.henshin.multicda.cda.compareLogger.*;
import org.eclipse.emf.henshin.model.compact.CNode;
import org.eclipse.emf.henshin.model.compact.CRule;

import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//-----Delete Annotation for Attributes actions and entities plus their entities which have target edges or triggers edge 
public class CDA {
	


	public static void main(String[] args) {
		String Path = "/";
		String henshinFileName = "backlog_v4.henshin";
		HenshinResourceSet rS = new HenshinResourceSet(Path);
		Module module = rS.getModule(henshinFileName, false);
		List<Rule> rules = new LinkedList<Rule>();
		for (Unit unit: module.getUnits()) {
			if(unit instanceof Rule)
				rules.add((Rule) unit);
		}
		
		

	}

	

}
