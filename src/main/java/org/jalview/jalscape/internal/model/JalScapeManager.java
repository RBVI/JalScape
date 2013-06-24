package org.jalview.jalscape.internal.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;

/**
 * This object maintains the relationship between JalView objects and Cytoscape objects.
 */

public class JalScapeManager {
	static final String[] defaultSequenceKeys = { "Sequence", "sequence" };
	private final BundleContext bundleContext;
	private final boolean haveGUI;

	public JalScapeManager(BundleContext bc, boolean haveGUI) {
		this.bundleContext = bc;
		this.haveGUI = haveGUI;
	}

	public Map<CyIdentifiable, String> getSequences(CyNetwork network, List<CyIdentifiable> nodeList) {
		Map<CyIdentifiable, String> seqMap = new HashMap<CyIdentifiable, String>();
		// TODO: Get the sequences
		if (network == null) return null;

		CyTable nodeTable = network.getDefaultNodeTable();
		List<String> attrFound = getMatchingAttributes(nodeTable, getCurrentSequenceKeys(network));
		for (CyIdentifiable node: nodeList) {
			for (String attr: attrFound) {
				seqMap.put(node, nodeTable.getRow(node.getSUID()).get(attr, String.class));
			}
		}
		
		return seqMap;
	}

	public void launchJalViewDialog(Map<CyIdentifiable, String> mapSequences) {
		System.out.println("Launching jalview with: ");
		for (CyIdentifiable key: mapSequences.keySet()) {
			System.out.println(key.getSUID()+": "+mapSequences.get(key));
		}
	}

	private List<String> getCurrentSequenceKeys(CyNetwork network) {
		return Arrays.asList(defaultSequenceKeys);
	}

	private List<String> getMatchingAttributes(CyTable table, List<String> columns) {
		Set<String> columnNames = CyTableUtil.getColumnNames(table);
		List<String> columnsFound = new ArrayList<String>();
		for (String attribute : columns) {
			if (columnNames.contains(attribute)) // TODO: make this case-independent
				columnsFound.add(attribute);
		}
		return columnsFound;
	}
}
