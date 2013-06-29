package org.jalview.jalscape.internal.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.jalview.jalscape.internal.model.JalScapeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAlignmentTask extends AbstractTask {
  private static Logger logger = LoggerFactory
          .getLogger(CreateAlignmentTask.class);
	// private List<CyNode> nodeList;
	private CyNetwork net;
	private JalScapeManager jalscapeManager;
	private Map<CyIdentifiable, String> mapSequences;

	public CreateAlignmentTask(List<CyIdentifiable> nodeList, CyNetwork net, JalScapeManager jalscapeManager) {
		// this.nodeList = nodeList;
		this.net = net;
		this.jalscapeManager = jalscapeManager;
		mapSequences = jalscapeManager.getSequences(net, nodeList);
	}

	public void run(TaskMonitor taskMonitor) {
		taskMonitor.setTitle("Create Alignments");
		taskMonitor.setStatusMessage("Opening jalview ...");

		// open dialog
		jalscapeManager.launchJalViewDialog(net, mapSequences);
	}

}
