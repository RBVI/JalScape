package org.jalview.jalscape.internal.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import org.jalview.jalscape.internal.model.JalScapeManager;

public class CreateAlignmentTask extends AbstractTask {
	// private List<CyNode> nodeList;
	private CyNetworkView netView;
	private JalScapeManager jalscapeManager;
	private Map<CyIdentifiable, String> mapSequences;

	public CreateAlignmentTask(List<CyIdentifiable> nodeList, CyNetworkView netView, JalScapeManager jalscapeManager) {
		// this.nodeList = nodeList;
		this.netView = netView;
		this.jalscapeManager = jalscapeManager;
		mapSequences = jalscapeManager.getSequences(netView.getModel(), nodeList);
	}

	public void run(TaskMonitor taskMonitor) {
		taskMonitor.setTitle("Create Alignments");
		taskMonitor.setStatusMessage("Opening jalview ...");

		// open dialog
		jalscapeManager.launchJalViewDialog(mapSequences);
	}

}
