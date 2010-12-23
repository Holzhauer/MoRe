/**
 * Social Network Analysis and Visualization Library
 * for RepastJ Models (SoNetA)
 * 
 * [see license.txt in the root directory of this library
 *  for additional important notes]
 *
 * @author Sascha Holzhauer
 * @date 23.05.2008
 * 
 */
package de.cesr.more.measures.node;



import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import de.cesr.more.measures.MAbstractMeasureManager;
import de.cesr.more.measures.MMeasureBundle;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.MMeasureSelectorListener;
import de.cesr.more.measures.MoreMeasureManagerListener;
import de.cesr.more.measures.measures.MAbstractNodeMeasure;
import de.cesr.more.measures.measures.MoreMeasure;
import de.cesr.more.measures.network.MNetworkMeasureManager.ParameterKeys;
import de.cesr.more.measures.node.supply.MBasicNodeMeasureSupplier;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.networks.MoreNetwork;



/**
 * The <code>NetworkMeasureUtilites</code> manage network measure calculation. {@link NetworkMeasureSupplier}s are
 * registered here. This class also implements the {@link MMeasureSelectorListener} interface for adding measures for
 * computation via the {@link MeasureChooser}. It holds all scheduled actions that calculate measures.
 * 
 * @author Sascha Holzhauer
 * @version 1.0
 * @date 23.05.2008
 */
public class MNodeMeasureManager extends MAbstractMeasureManager {

	protected static MNodeMeasureManager	instance;

	/**
	 * Instantiates a new <code>NetworkMeasureUtilities</code> which is only possible when the {@link ScheduleAdapted}
	 * was set before. Otherwise a {@link IllegalArgumentException} is thrown.
	 */
	private MNodeMeasureManager() {
		addMeasureSupplier(new MBasicNodeMeasureSupplier());
	}

	/**
	 * @date 15.08.2008
	 * 
	 * @return The current instance of <code>NetworkMeasureUtilities</code>.
	 */
	public static MNodeMeasureManager getInstance() {
		if (instance == null) {
			if (schedule == null) {
				throw new IllegalStateException("The MoreScheduler has not been set before!");
			}
			instance = new MNodeMeasureManager();
		}
		return instance;
	}

	/**
	 * The Type of nodes in the given <code>MoreNetwork</code> needs to extend 
	 * {@link MoreNodeMeasureSupport} since the <code>MNodeMeasureManager</code> requires
	 * access to methods that get and set measures at the node.
	 * 
	 * @date 26.11.2008
	 * 
	 * @param <T> Type of nodes)
	 * @param <E> Edge type
	 * @param network the network to add the measure for
	 * @param measureDesc the measure description of the measure to add
	 * @param params parameter map of options for calculation
	 * @return true if adding the measure calculation was successful
	 */
	public <T extends MoreNodeMeasureSupport, E> boolean addMeasureCalculation(MoreNetwork<T, E> network,
			MMeasureDescription measureDesc, Map<String, Object> params) {

		MoreMeasure measure = findMeasure(measureDesc);
		boolean cancel = false;

		if (params == null) {
			params = measure.getParameters();
		}
		if (promptForMeasureParameters && params != null && params.size() > 0) {
			for (String key : params.keySet()) {
				Object value = null;
				if (params.get(key) instanceof Double) {
					while (!(value instanceof Double)) {
						String input = JOptionPane.showInputDialog("Input a value for parameter " + key + ":", params
								.get(key));
						if (input == null) {
							cancel = true;
							break;
						}
						try {
							value = Double.valueOf(input);
						} catch (NumberFormatException e) {
						}
					}
					params.put(key, value);
				}
				if (params.get(key) instanceof Integer) {
					while (!(value instanceof Integer)) {
						String input = JOptionPane.showInputDialog("Input a value for parameter " + key + ":", params
								.get(key));
						if (input == null) {
							cancel = true;
							break;
						} else {
							try {
								value = Integer.valueOf(input);
							} catch (NumberFormatException e) {
							}
						}
					}
					params.put(key, value);
				}
				if (params.get(key) instanceof Boolean) {
					while (!(value instanceof Boolean)) {
						String input = JOptionPane.showInputDialog("Input a value for parameter " + key + ":", params
								.get(key));
						if (input == null) {
							cancel = true;
							break;
						}
						value = Boolean.valueOf(input);
					}
					params.put(key, value);
				}
			}
		}

		if (cancel == false) {
			MoreAction action = ((MAbstractNodeMeasure) measure).getAction(network, params);
			if (!measureActions.containsKey(network)) {
				measureActions.put(network, new HashMap<MMeasureDescription, MoreAction>());
			}
			measureActions.get(network).put(measure.getMeasureDescription(), action);
			
			Double interval = null;
			if (params != null & params.containsKey(ParameterKeys.INTERVAL.name())) {
				Object o = params.get(ParameterKeys.INTERVAL.name());
				if (o instanceof Number) {
					interval = ((Number)o).doubleValue();
				}
			}
			
			Double start = null;
			if (params != null & params.containsKey(ParameterKeys.START.name())) {
				Object s = params.get(ParameterKeys.START.name());
				if (s instanceof Number) {
					start = ((Number)s).doubleValue();
				}
			}


			for (MoreMeasureManagerListener listener : listeners) {
				listener.networkMeasureCalcAdded(network, measureDesc);
			}

			// execute the action once to calculate measures without the need to step further
			// to display the values:
			action.execute();
			
			if (start != null) {
				schedule.schedule(MScheduleParameters.getEverlastingRandomScheduleParameter(start.doubleValue(), interval.doubleValue()), action);
			}
			else {
			// TODO check if start=0 always works
				schedule.schedule(MScheduleParameters.getUnboundedRandomMScheduleParameters(interval == null ? 1.0 : interval.doubleValue()), action);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Removes <code>BasicAction</code> that calculates the measure for the given network for the given measure key from
	 * the <code>Schedule</code> to stop computation of that measure. Furthermore its sets the associated measures at
	 * the node to <code>Double.NaN</code> or <code>null</code> respectively.
	 * 
	 * @date 24.05.2008
	 * 
	 * @param <T> Type of elements in the given <code>ContextContextContextJungNetwork</code> that should implement
	 *            <code>NetworkMeasureSupport</code>
	 * @param network The network the measure is associated with
	 * @param key The key for the measure to remove from calculation
	 * 
	 * @return true, if there was a measure that could be removed
	 */
	public <T extends MoreNodeMeasureSupport, E> boolean removeMeasureCalculation(MoreNetwork<T, E> network,
			MMeasureDescription key) {
		MoreAction action = measureActions.get(network).get(key);
		schedule.removeAction(action);
		boolean removed = false;
		if (measureActions.get(network).remove(key) != null) {
			removed = true;
		}
		// tidy up map if there is no entry for the given network anymore:
		if (measureActions.get(network).isEmpty()) {
			measureActions.remove(network);
		}
		// set measures at node to NaN since these values become illegal in further steps:
		for (MoreNodeMeasureSupport node : network.getNodes()) {
			node.setNetworkMeasureObject(network, key, null);
		}
		for (MoreMeasureManagerListener listener : listeners) {
			listener.networkMeasureCalcRemoved(network, key);
		}
		return removed;
	}

	/**
	 * @see edu.MMeasureSelectorListener.sh.soneta.gui.MeasureChooserListener#setMeasureBundle(edu.MMeasureBundle.sh.soneta.measures.MeasureBundle,
	 *      boolean)
	 */
	public boolean setMeasureBundle(MMeasureBundle bundle, boolean remove) {
		if (remove) {
			return removeMeasureCalculation(bundle.getNetwork(), bundle.getMeasure());
		} else {
			return addMeasureCalculation(bundle.getNetwork(), bundle.getMeasure(), bundle.getParams());
		}
	}

	/**
	 * Takes a short description instead of a {@link MeasureDescription}.
	 * 
	 * @see MNodeMeasureManager#addMeasureCalculation(ContextContextJungNetwork,
	 *      de.cesr.more.measures.node.MNodeMeasureManager.sh.soneta.measures.NetworkMeasureUtilities.MeasureDescription,
	 *      Map)
	 */
	public <T extends MoreNodeMeasureSupport, E> void addMeasureCalculation(MoreNetwork<T, E> network,
			String shortname, Map<String, Object> params) {
		addMeasureCalculation(network, new MMeasureDescription(shortname), params);
	}

}
