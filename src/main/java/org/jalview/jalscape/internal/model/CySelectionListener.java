package org.jalview.jalscape.internal.model;

import jalview.api.AlignViewportI;
import jalview.datamodel.AlignmentI;
import jalview.datamodel.ColumnSelection;
import jalview.datamodel.SequenceGroup;
import jalview.datamodel.SequenceI;
import jalview.structure.SelectionListener;
import jalview.structure.SelectionSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;

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
    Map<CyNetwork, Set<CyIdentifiable>> networks = new HashMap<CyNetwork, Set<CyIdentifiable>>();
    if (arg2 != manager)
    {
      AlignmentI visal = null;
      if (arg2 instanceof AlignViewportI)
      {
        // Selection from specific view
        visal = ((AlignViewportI) arg2).getAlignment();
      }
      if ((arg0 == null || arg0.getSize() == 0)
              && (arg1 == null || arg1.getSelected() == null || arg1
                      .getSelected().size() == 0))
      {
        // clear selection;
      }
      else
      {
        List<CyIdentifiable> cyids = new ArrayList<CyIdentifiable>();
        for (SequenceI sq : arg0.getSequences())
        {
          SequenceI ds = sq;
          while (ds.getDatasetSequence() != null)
          {
            ds = ds.getDatasetSequence();
          }
          CyIdentifiable cyid = manager.getIdForSeq(ds);
          if (cyid != null)
          {
            Set<CyNetwork> network = manager.getNetworkForId(cyid);
            if (network != null)
            {
              for (CyNetwork netw : network)
              {
                Set<CyIdentifiable> netid = networks.get(netw);
                if (netid == null)
                {
                  networks.put(netw, netid = new HashSet<CyIdentifiable>());
                }
                netid.add(cyid);
              }
            }
          }
        }

        if (arg1 != null)
        {
          // extract ranges on sq that have been selected
          if (arg1.getSelected().size() == visal.getHeight())
          {
            // code here should we ever need to do this
            // deparse the colsel into positions on the vamsas alignment
            // sequences
            if (arg1.getSelected() != null && arg1.getSelected().size() > 0
                    && visal != null && arg0.getSize() == visal.getHeight())
            {
              // gather selected columns outwith the sequence positions
              // too

              Enumeration cols = arg1.getSelected().elements();
              while (cols.hasMoreElements())
              {
                int ival = ((Integer) cols.nextElement()).intValue();
                // ival is a position numbered from 0 in visal's column
                // coordinates that is selected
              }
            }
            else
            {
              int[] intervals = arg1.getVisibleContigs(arg0.getStartRes(),
                      arg0.getEndRes() + 1);
              // intervals is a series of start/end positions that are visible
              // in the alignment view and intersect with the selected rectangle
              
            }
          }

          // resolve ds against manager's set of CyIdentifiables and mark the
          // selection
        }
      }
    }
    cySelect(networks);
  }

}
