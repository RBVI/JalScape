package org.jalview.jalscape.internal.model;

import jalview.datamodel.ColumnSelection;
import jalview.datamodel.SequenceGroup;
import jalview.datamodel.SequenceI;
import jalview.structure.SelectionListener;
import jalview.structure.SelectionSource;

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

public class CySelectionListener implements RowsSetListener, SelectionListener {
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * jalview.structure.SelectionListener#selection(jalview.datamodel.SequenceGroup
   * , jalview.datamodel.ColumnSelection, jalview.structure.SelectionSource)
   */
  public void selection(SequenceGroup arg0, ColumnSelection arg1,
          SelectionSource arg2)
  {
    if (arg2 != manager)
    {
      if (arg0 == null || arg0.getSize() == 0)
      {
        // clear selection;
      }
      else
      {
        for (SequenceI sq : arg0.getSequences())
        {
          SequenceI ds = sq.getDatasetSequence();
          while (ds.getDatasetSequence() != null)
          {
            ds = ds.getDatasetSequence();
          }
          // resolve ds against manager's set of CyIdentifiables and mark the
          // selection
        }
      }
    }
  }
}
