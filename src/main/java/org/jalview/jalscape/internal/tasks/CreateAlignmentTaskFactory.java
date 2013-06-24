package org.jalview.jalscape.internal.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import org.jalview.jalscape.internal.model.JalScapeManager;

public class CreateAlignmentTaskFactory extends AbstractTaskFactory implements
		NetworkTaskFactory {

	private JalScapeManager jalscapeManager;

	public CreateAlignmentTaskFactory(JalScapeManager jalscapeManager) {
		this.jalscapeManager = jalscapeManager;
	}

	public TaskIterator createTaskIterator() {
		return null;
	}

	public boolean isReady(CyNetwork net) {
		// Get all of the selected nodes/edges

		/*
		 TODO: We need to do something like this
		List<CyIdentifiable> selectedList = new ArrayList<CyIdentifiable>();
		selectedList.addAll(CyTableUtil.getNodesInState(netView.getModel(), CyNetwork.SELECTED, true));
		Map<CyIdentifiable, List<String>> mapObjNames = new HashMap<CyIdentifiable, List<String>>();
		jalscapeManager.getObjNames(mapObjNames, netView.getModel(), selectedList, false);
		if (mapChimObjNames.size() > 0) {
			return true;
		}
		return false;
		*/
		return true;
	}

	public TaskIterator createTaskIterator(CyNetwork net) {
		// Get all of the selected nodes
		List<CyIdentifiable> selectedList = new ArrayList<CyIdentifiable>();
		selectedList.addAll(CyTableUtil.getNodesInState(net, CyNetwork.SELECTED, true));
		return new TaskIterator(new CreateAlignmentTask(selectedList, net, jalscapeManager));
	}
}
