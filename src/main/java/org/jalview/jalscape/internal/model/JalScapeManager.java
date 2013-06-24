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

/**
 * This object maintains the relationship between JalView objects and Cytoscape objects.
 */

public class JalScapeManager {
	static final String[] defaultSequenceKeys = { "Sequence" };
	private final BundleContext bundleContext;
	private final boolean haveGUI;

	public JalScapeManager(BundleContext bc, boolean haveGUI) {
		this.bundleContext = bc;
		this.haveGUI = haveGUI;
	}

	public Map<CyIdentifiable, String> getSequences(CyNetwork network, List<CyIdentifiable> nodeList) {
		Map<CyIdentifiable, String> seqMap = new HashMap<CyIdentifiable, String>();
		// TODO: Get the sequences
		return seqMap;
	}

	public void launchJalViewDialog(Map<CyIdentifiable, String> mapSequences) {
	}
}
