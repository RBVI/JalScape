package org.jalview.jalscape.internal.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;

import org.cytoscape.model.events.RowsCreatedEvent;
import org.cytoscape.model.events.RowsCreatedListener;

/**
 * This object maintains the selection state between JalView objects and Cytoscape objects.
 */

public class CySelectionListener implements RowsSetListener {
	private final JalScapeManager manager;
	private boolean silenced = false;

	public CySelectionListener(JalScapeManager manager) {
		System.out.println("selection listener started");
		this.manager = manager;
	}

	public void handleEvent(RowsSetEvent e) {
		System.out.println("handing "+e);
		CyTable source = e.getSource();

		if (silenced)
			return;

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
	}

	public void silence() {
		silenced = true;
	}

	public void unsilence() {
		silenced = false;
	}

}
