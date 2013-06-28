package org.jalview.jalscape.internal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;

import jalview.datamodel.AlignmentI;
import jalview.datamodel.SequenceI;
import jalview.datamodel.Sequence;

/**
 * This object maintains the relationship between JalView objects and Cytoscape objects.
 */

public class JalScapeManager {
	static final String[] defaultSequenceKeys = { "Sequence", "sequence" };
	private final BundleContext bundleContext;
	private final boolean haveGUI;
	private Map<CyIdentifiable, Set<CyNetwork>> idToNetMap;
	private Map<SequenceI, CyIdentifiable> seqToIdMap;
	private Map<CyTable, CyNetwork> netMap;

	public JalScapeManager(BundleContext bc, boolean haveGUI) {
		this.bundleContext = bc;
		this.haveGUI = haveGUI;
		idToNetMap = new HashMap<CyIdentifiable, Set<CyNetwork>>();
		seqToIdMap = new HashMap<SequenceI, CyIdentifiable>();
		netMap = new HashMap<CyTable, CyNetwork>();
	}

	public Map<CyIdentifiable, String> getSequences(CyNetwork network, List<CyIdentifiable> nodeList) {
		Map<CyIdentifiable, String> seqMap = new IdentityHashMap<CyIdentifiable, String>();
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

	public void launchJalViewDialog(CyNetwork network, Map<CyIdentifiable, String> mapSequences) {
		CyTable nodeTable = network.getDefaultNodeTable();
		String networkName = network.getRow(network).get(CyNetwork.NAME, String.class);
		netMap.put(nodeTable, network);

		AlignmentI al;
		SequenceI[] sq = new SequenceI[mapSequences.size()];
		int i=0;
		for (CyIdentifiable key: mapSequences.keySet()) {
			String name = nodeTable.getRow(key.getSUID()).get(CyNetwork.NAME, String.class);


			System.out.println(name+": "+mapSequences.get(key));
			sq[i++] = new jalview.datamodel.Sequence(name,mapSequences.get(key));

			// Update all of our internal data structures
			if (!idToNetMap.containsKey(key))
				idToNetMap.put(key, new HashSet<CyNetwork>());
			idToNetMap.get(key).add(network);
			seqToIdMap.put(sq[i-1], key);

			// Reverse map?
		}
		al = new jalview.datamodel.Alignment(sq);
		try {
		  jalview.bin.Jalview.main(new String[] {});
		  while (jalview.gui.Desktop.instance==null || !jalview.gui.Desktop.instance.isVisible())
		  {
		    try {
		      Thread.sleep(500);
		    } catch (InterruptedException q) {};
		  }
		  jalview.gui.AlignFrame af = new jalview.gui.AlignFrame(al, 600, 400);
		  jalview.gui.Desktop.addInternalFrame(af, networkName,600,400);
		  af.getViewport().getStructureSelectionManager().addSelectionListener(new CySelectionListener(this));
		  
//		    jalview.bin.Jalview.main(new String[] {});
		} catch (Exception x) { x.printStackTrace();};
	}

	public CyIdentifiable getIdForSeq(SequenceI seq) {
		if (seqToIdMap.containsKey(seq))
			seqToIdMap.get(seq);
		return null;
	}

	public Set<CyNetwork> getNetworkForId(CyIdentifiable id) {
		if (idToNetMap.containsKey(id))
			return idToNetMap.get(id);
		return null;
	}

	public CyNetwork getNetwork(CyTable table) {
		if (netMap.containsKey(table))
			return netMap.get(table);
		return null;
	}

	public Collection<CyIdentifiable> getAllIds() {
		return seqToIdMap.values();
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
