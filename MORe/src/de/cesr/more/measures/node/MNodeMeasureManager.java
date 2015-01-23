/**
 * Social Network Analysis and Visualisation Library
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

import org.apache.log4j.Logger;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MAbstractMeasureManager;
import de.cesr.more.measures.MMeasureBundle;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.MMeasureSelectorListener;
import de.cesr.more.measures.MoreMeasure;
import de.cesr.more.measures.MoreMeasureManagerListener;
import de.cesr.more.measures.network.MNetworkMeasureManager.ParameterKeys;
import de.cesr.more.measures.node.supply.MBasicNodeMeasureSupplier;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.measures.util.MoreSchedule;
import de.cesr.more.util.Log4jLogger;



/**
 * 
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
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MNodeMeasureManager.class);

	/**
	 * @date 15.08.2008
	 * 
	 * @return The current instance of <code>NetworkMeasureUtilities</code>.
	 */
	public static MNodeMeasureManager getInstance() {
		if (instance == null) {
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
	public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> boolean addMeasureCalculation(MoreNetwork<T, E> network,
			MMeasureDescription measureDesc, Map<String, Object> params) {

		MoreSchedule schedule = MManager.getSchedule();
		MoreMeasure measure = findMeasure(measureDesc);
		
		// <- LOGGING
		if (measure == null) {
			logger.error("MoreMeasure for key " + measureDesc.getShort() + " could not be found!");
			throw new IllegalStateException("MoreMeasure for key " + measureDesc.getShort() + " could not be found!");
		}
		// LOGGING ->
		
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
			if (params != null && params.containsKey(ParameterKeys.INTERVAL.name())) {
				Object o = params.get(ParameterKeys.INTERVAL.name());
				if (o instanceof Number) {
					interval = ((Number)o).doubleValue();
				}
			}
			
			Double start = null;
			if (params != null && params.containsKey(ParameterKeys.START.name())) {
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
	 * 
	 * @see MNodeMeasureManager#addMeasureCalculation(MoreNetwork<T, E>,
	 *      de.cesr.more.measures.MMeasureDescription, Map)
	 * 
	 * @param <T> agent type
	 * @param <E> edge type
	 * @param network 
	 * @param measureDesc measure description to add
	 * @param params parameter map
	 * @return true if measure could be added
	 */
	public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> boolean addMeasureCalculation(String network, MMeasureDescription measureDesc,
			Map<String, Object> params) {
		return addMeasureCalculation((MoreNetwork<T, E>) MNetworkManager.getNetwork(network), measureDesc, params);
	}
	
	/**
	 * Takes a short description instead of a {@link MeasureDescription} and uses default parameter map.
	 * 
	 * @see MNodeMeasureManager#addMeasureCalculation(MoreNetwork<T, E>,
	 *      de.cesr.more.measures.MMeasureDescription, Map)
	 *      
	 * @param <T> agent type
	 * @param <E> edge type
	 * @param network 
	 * @param measureDesc measure description to add
	 * @return true if measure could be added
	 */
	public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> boolean addMeasureCalculation(MoreNetwork<T, E> network, String shortname) {
		return addMeasureCalculation(network, new MMeasureDescription(shortname), null);
	}

	
	/**
	 * Takes a short description instead of a {@link MeasureDescription}.
	 * 
	 * @see MNodeMeasureManager#addMeasureCalculation(MoreNetwork<T, E>,
	 *      de.cesr.more.measures.MMeasureDescription, Map)
	 */
	public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> boolean addMeasureCalculation(String network, String shortname,
			Map<String, Object> params) {
		return addMeasureCalculation((MoreNetwork<T, E>) MNetworkManager.getNetwork(network), new MMeasureDescription(
				shortname), params);
	}

	
	/**
	 * @param network
	 * @param key
	 * @return true if the given measure description is active
	 */
	public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> boolean hasMeasureCalculation(
			MoreNetwork<T, E> network,
			String key) {
		return this.hasMeasureCalculation(network, new MMeasureDescription(key));
	}

	/**
	 * @param network
	 * @param key
	 * @return true if the given measure description is active
	 */
	public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> boolean hasMeasureCalculation(
			MoreNetwork<T, E> network,
			MMeasureDescription key) {
		return this.measureActions.get(network).containsKey(key);
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
	public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> boolean removeMeasureCalculation(MoreNetwork<T, E> network,
			MMeasureDescription key) {
		MoreAction action = measureActions.get(network).get(key);
		MManager.getSchedule().removeAction(action);
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
	 * @see MNodeMeasureManager#removeMeasureCalculation(MoreNetwork, MMeasureDescription)
	 * 
	 * @param <T> Type of elements in the given <code>ContextContextContextJungNetwork</code> that should implement
	 *            <code>NetworkMeasureSupport</code>
	 * @param network The network the measure is associated with
	 * @param key The key for the measure to remove from calculation
	 * 
	 * @return true, if there was a measure that could be removed
	 */
	public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> boolean removeMeasureCalculation(MoreNetwork<T, E> network,
			String key) {
		return this.removeMeasureCalculation(network, new MMeasureDescription(key));
	}

	/**
	 * @see edu.MMeasureSelectorListener.sh.soneta.gui.MeasureChooserListener#setMeasureBundle(edu.MMeasureBundle.sh.soneta.measures.MeasureBundle,
	 *      boolean)
	 */
	@Override
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
	public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> void addMeasureCalculation(MoreNetwork<T, E> network,
			String shortname, Map<String, Object> params) {
		addMeasureCalculation(network, new MMeasureDescription(shortname), params);
	}

	/**
	 * Reset the static instance variable. Called by {@link MManager#reset()}.
	 */
	public static void reset() {
		instance = null;
	}
}
