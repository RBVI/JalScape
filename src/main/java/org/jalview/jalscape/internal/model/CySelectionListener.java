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
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object maintains the selection state between JalView objects and Cytoscape objects.
 */

public class CySelectionListener implements RowsSetListener, SelectionListener {
  private static Logger logger = LoggerFactory
          .getLogger(CySelectionListener.class);
	private final JalScapeManager manager;
	private volatile boolean silenced = false;

	public CySelectionListener(JalScapeManager manager) {
	  logger.trace("selection listener started");
		this.manager = manager;
	}

	public void handleEvent(RowsSetEvent e) {
		CyTable source = e.getSource();

		if (silenced)
			return;

		CyNetwork net = manager.getNetwork(source);
		if (net == null)
			return;

		if (!e.containsColumn(CyNetwork.SELECTED)) {
			return;
		}

		List<CyNode> nodeList = CyTableUtil.getNodesInState(net, CyNetwork.SELECTED, true);

		selectSuidInJalview(nodeList);
	}

	public void silence() {
		silenced = true;
	}

	public void unsilence() {
		silenced = false;
	}
	
	private void selectSuidInJalview(List<CyNode> toselect)
	{
	  logger.trace("Selecting "+toselect.size()+" sequences");
	  SequenceGroup jselection;
	  ColumnSelection csel;
	  List<SequenceI> seqs = new ArrayList<SequenceI>();
	  boolean aligned=false;
          int maxWidth = 0;
	  for (CyNode node:toselect)
	  {
			Long cySuid = node.getSUID();
	    SequenceI sq = manager.getSeqForId(cySuid);
	    if (sq!=null)
	    {
	      logger.trace("SUID = "+cySuid+" "+sq.getName());
	      seqs.add(sq);
	      if (sq.getDatasetSequence()!=null)
	      {
	        aligned=true;
	      }
	    }
	  }
	  {
              jselection = new SequenceGroup();
              for (SequenceI seq:seqs)
              {
              jselection.addSequence(seq,
                      false);
              int mw = seq.getLength();
              if (mw>maxWidth) {
                maxWidth = mw;
              };
              }
       }
	  csel = new ColumnSelection();
	  if (maxWidth>0)
	  {
	    jselection.setStartRes(0);
	    jselection.setEndRes(maxWidth);
	    
	    for (int i = 0; i<maxWidth;i++)
	    {
	      csel.addElement(i);
	    }
	  }
    manager.getStructureSelectionManager().sendSelection(jselection, csel,
            manager);
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
    // need a logger for this
    if (logger.isTraceEnabled())
    {
      logger.trace("Selecting from Jalview: "+((arg0!=null && arg0.getSequences()!=null) ? arg0.getSequences().size()+" sequences" : "No sequences")+" from "+arg2.getClass().getName());
      logger.trace("Manager knows about :"+manager.getAllIds().size()+" CyIds.");
    }
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
        // need to add bound network for this alignment to list
        
      }
      else
      {
        //  
        for (SequenceI sq : arg0.getSequences())
        {
          SequenceI ds = sq;
          List<CyIdentifiable> ids = new ArrayList<CyIdentifiable>();
          {
            CyIdentifiable cyid = manager.getIdForSeq(ds);
            if (cyid != null)
            {
              if (logger.isTraceEnabled()) {logger.trace((ds.getDatasetSequence()!=null ? "Dataset" : "Alignment")+" sequence: "+ds+" has ID "+cyid); }
              ids.add(cyid);
            }
            while (ds.getDatasetSequence() != null)
            {
              ds = ds.getDatasetSequence();
              cyid = manager.getIdForSeq(ds);
              if (cyid != null)
              {
                if (logger.isTraceEnabled()) {logger.trace("Dataset sequence: "+ds+" has ID "+cyid);}
                ids.add(cyid);
              }
            }
          }
          for (CyIdentifiable cyid : ids)
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
      logger.trace("Resolved "+networks.size()+" networks for selection.");
      cySelect(networks);
    }
  }

	private void cySelect(Map<CyNetwork, Set<CyIdentifiable>> networks) {
		if (networks == null || networks.isEmpty())
			return;

		silence();
		// Deselect all of our nodes	
		Collection<CyIdentifiable> nodes = manager.getAllIds();
		for (CyIdentifiable id: nodes) {
			Set<CyNetwork> nets = manager.getNetworkForId(id);
			for (CyNetwork net: nets) {
				net.getRow(id).set(CyNetwork.SELECTED, false);
			}
		}

		// Select the ones that were selected by jalview
		for (CyNetwork network: networks.keySet()) {
			for (CyIdentifiable node: networks.get(network)) {
				network.getRow(node).set(CyNetwork.SELECTED, true);
			}
		}
		unsilence();
	}

}
