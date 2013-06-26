package org.jalview.jalscape.internal.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.model.events.RowsSetEvent;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

/**
 * This object maintains the selection state between JalView objects and Cytoscape objects.
 */

public class SelectionListener implements RowsSetListener {
	private final JalScapeManager manager;

	public SelectionListener(JalScapeManager manager) {
		this.manager = manager;
	}

	public void handleEvent(RowsSetEvent e) {
		CyTable source = e.getSource();

		CyNetwork net = manager.getNetwork(source);
		if (net == null)
			return;

		if (!e.containsColumn(CyNetwork.SELECTED)) {
			return;
		}

		Map<Long, Boolean> selectedRows = new HashMap<Long, Boolean>();
		Collection<RowSetRecord> records = e.getColumnRecords(CyNetwork.SELECTED);
		for (RowSetRecord record : records) {
			CyRow row = record.getRow();
			// This is a hack to avoid double selection...
			if (row.toString().indexOf("FACADE") >= 0)
				continue;
			selectedRows.put(row.get(CyIdentifiable.SUID, Long.class),
					(Boolean) record.getValue());
		}
		if (selectedRows.size() == 0) {
			return;
		}

		// Now select the right sequences in JalView
	}

}
