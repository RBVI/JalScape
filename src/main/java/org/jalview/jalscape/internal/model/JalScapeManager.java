package org.jalview.jalscape.internal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.HashMap;
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
	private Map<CyNetwork,Map<CyIdentifiable, SequenceI>> netMap;
	private Map<CyNetwork,Map<String, CyIdentifiable>> nameMap;

	public JalScapeManager(BundleContext bc, boolean haveGUI) {
		this.bundleContext = bc;
		this.haveGUI = haveGUI;
		netMap = new HashMap<CyNetwork,Map<CyIdentifiable, SequenceI>>();
		nameMap = new HashMap<CyNetwork,Map<String, CyIdentifiable>>();
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

		if (!netMap.containsKey(network)) {
			netMap.put(network, new IdentityHashMap<CyIdentifiable, SequenceI>());
			nameMap.put(network, new IdentityHashMap<String, CyIdentifiable>());
		}

		Map<CyIdentifiable, SequenceI> seqs = netMap.get(network);
		Map<String, CyIdentifiable> names = nameMap.get(network);
		AlignmentI al;
		SequenceI[] sq = new SequenceI[mapSequences.size()];
		int i=0;
		for (CyIdentifiable key: mapSequences.keySet()) {
			String name = nodeTable.getRow(key.getSUID()).get(CyNetwork.NAME, String.class);

			System.out.println(name+": "+mapSequences.get(key));
			sq[i++] = new jalview.datamodel.Sequence(name,mapSequences.get(key));
			seqs.put(key, sq[i-1]); 
			names.put(name, key);
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
		  jalview.gui.Desktop.addInternalFrame(af, "From Cytoscape",600,400);
		  af.getViewport().getStructureSelectionManager().addSelectionListener(new CySelectionListener(this));
		  
//		    jalview.bin.Jalview.main(new String[] {});
		} catch (Exception x) { x.printStackTrace();};
	}

	public boolean haveNetwork(CyNetwork net) {
		if (netMap.keySet().contains(net))
			return true;

		return false;
	}

	public CyNetwork getNetwork(CyTable table) {
		for (CyNetwork net: netMap.keySet()) {
			if (net.getDefaultNodeTable().equals(table))
				return net;
		}
		return null;
	}

	public boolean haveNode(CyNetwork net, CyIdentifiable id) {
		if (netMap.keySet().contains(net) && netMap.get(net).keySet().contains(id))
			return true;

		return false;
	}

	public Map<CyIdentifiable, SequenceI> getSeqMap(CyNetwork network) {
		if (netMap.containsKey(network))
			return netMap.get(network);
		return new HashMap<CyIdentifiable, SequenceI>();
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
