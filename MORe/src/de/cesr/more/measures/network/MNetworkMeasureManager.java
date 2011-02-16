/**
 * This file is part of
 * 
 * MORe - Managing Ongoing Relationships
 *
 * Copyright (C) 2010 Center for Environmental Systems Research, Kassel, Germany
 * 
 * MORe - Managing Ongoing Relationships is free software: You can redistribute 
 * it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *  
 * MORe - Managing Ongoing Relationships is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Center for Environmental Systems Research, Kassel
 * 
 * Created by Sascha Holzhauer on 16.11.2010
 */
package de.cesr.more.measures.network;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.measures.MAbstractMeasureManager;
import de.cesr.more.measures.MMeasureBundle;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.MoreMeasureManagerListener;
import de.cesr.more.measures.measures.MAbstractNetworkMeasure;
import de.cesr.more.measures.measures.MoreMeasure;
import de.cesr.more.measures.network.supply.MBasicNetworkMeasureSupplier;
import de.cesr.more.measures.node.MNodeMeasureManager;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.measures.util.MoreSchedule;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.util.Log4jLogger;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 16.11.2010
 * 
 */
public class MNetworkMeasureManager extends MAbstractMeasureManager {

	protected static MNetworkMeasureManager	instance;

	/**
	 * Standard keys for measure action parameters
	 */
	public enum ParameterKeys {
		/**
		 * Interval to calculate measure
		 */
		INTERVAL,

		/**
		 * First timestep to calculate measure
		 */
		START,

		/**
		 * Last timestep to calculate measure
		 */
		END,
		
		/**
		 * Priority in queue of specific tick
		 */
		PRIORITY;
	}

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(MNetworkMeasureManager.class);

	/**
	 * Instantiates a new <code>NetworkMeasureUtilities</code> which is only possible when the {@link ScheduleAdapted}
	 * was set before. Otherwise a {@link IllegalArgumentException} is thrown.
	 */
	private MNetworkMeasureManager() {
		addMeasureSupplier(new MBasicNetworkMeasureSupplier());
	}

	/**
	 * @date 15.08.2008
	 * 
	 * @return The current instance of <code>NetworkMeasureUtilities</code>.
	 */
	public static MNetworkMeasureManager getInstance() {
		if (instance == null) {
			instance = new MNetworkMeasureManager();
		}
		return instance;
	}

	/**
	 * 
	 * @param <T> Type of nodes)
	 * @param <E> Edge type
	 * @param network the network to add the measure for
	 * @param measureDesc the measure description of the measure to add
	 * @param params parameter map of options for calculation
	 * @return true if adding the measure calculation was successful
	 */
	public <T, E extends MoreEdge> boolean addMeasureCalculation(MoreNetwork<T, E> network,
			MMeasureDescription measureDesc, Map<String, Object> params) {

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Add calculation (" + measureDesc + ") for network " + network);
		}
		// LOGGING ->
		
		MoreSchedule schedule = MManager.getSchedule();

		MoreMeasure measure = findMeasure(measureDesc);

		if (params == null) {
			params = measure.getParameters();
		}

		boolean cancel = promptForParameters(params);
		if (cancel == false) {
			// go on if user did not cancel the dialog:
			MoreAction action = ((MAbstractNetworkMeasure) measure).getAction(network, params);
			if (!measureActions.containsKey(network)) {
				measureActions.put(network, new HashMap<MMeasureDescription, MoreAction>());
			}
			measureActions.get(network).put(measure.getMeasureDescription(), action);

			// inform listeners:
			for (MoreMeasureManagerListener listener : listeners) {
				listener.networkMeasureCalcAdded(network, measureDesc);
			}

			double interval = 1.0;
			if (params != null && params.containsKey(ParameterKeys.INTERVAL.toString())) {
				Object o = params.get(ParameterKeys.INTERVAL.toString());
				if (o instanceof Number) {
					interval = ((Number) o).doubleValue();
				}
			}

			double start = 0.0;
			if (params != null && params.containsKey(ParameterKeys.START.toString())) {
				Object s = params.get(ParameterKeys.START.toString());
				if (s instanceof Number) {
					start = ((Number) s).doubleValue();
				}
			}
			
			double end = MScheduleParameters.END_TICK;
			if (params != null && params.containsKey(ParameterKeys.END.toString())) {
				Object e = params.get(ParameterKeys.END.toString());
				if (e instanceof Number) {
					end = ((Number) e).doubleValue();
				}
			}
			
			double priority = MScheduleParameters.LAST_PRIORITY;
			if (params != null && params.containsKey(ParameterKeys.PRIORITY.toString())) {
				Object e = params.get(ParameterKeys.PRIORITY.toString());
				if (e instanceof Number) {
					priority = ((Number) e).doubleValue();
				}
			}

			// TODO optional execution here (for instance to display results right after)
			// action.execute();
			schedule.schedule(MScheduleParameters.getScheduleParameter(start, interval, end, priority), action);

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(schedule.getScheduleInfo());
			}
			// LOGGING ->

			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param params
	 * @return Created by Sascha Holzhauer on 17.11.2010
	 */
	private boolean promptForParameters(Map<String, Object> params) {
		boolean cancel = false;
		if (promptForMeasureParameters && params != null && params.size() > 0) {
			// prompt expected AND there are parameters to request:
			for (String key : params.keySet()) {
				Object value = null;
				if (params.get(key) instanceof Double) {
					// request a double value:
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
					// request an integer value:
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
					// request a boolean value:
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
		return cancel;
	}

	public <T, E extends MoreEdge> boolean addMeasureCalculation(String network, MMeasureDescription measureDesc,
			Map<String, Object> params) {
		return addMeasureCalculation((MoreNetwork<T, E>) MNetworkManager.getNetwork(network), measureDesc, params);
	}

	/**
	 * Takes a short description instead of a {@link MeasureDescription}.
	 * 
	 * @see MNodeMeasureManager#addMeasureCalculation(ContextContextJungNetwork,
	 *      de.cesr.more.measures.node.MNodeMeasureManager.sh.soneta.measures.NetworkMeasureUtilities.MeasureDescription,
	 *      Map)
	 */
	public <T, E extends MoreEdge> boolean addMeasureCalculation(String network, String shortname,
			Map<String, Object> params) {
		return addMeasureCalculation((MoreNetwork<T, E>) MNetworkManager.getNetwork(network), new MMeasureDescription(
				shortname), params);
	}

	/**
	 * Takes a short description instead of a {@link MeasureDescription}.
	 * 
	 * @see MNodeMeasureManager#addMeasureCalculation(ContextContextJungNetwork,
	 *      de.cesr.more.measures.node.MNodeMeasureManager.sh.soneta.measures.NetworkMeasureUtilities.MeasureDescription,
	 *      Map)
	 */
	public <T, E extends MoreEdge> boolean addMeasureCalculation(MoreNetwork<T, E> network, String shortname,
			Map<String, Object> params) {
		return addMeasureCalculation(network, new MMeasureDescription(shortname), params);
	}

	/**
	 * Takes a short description instead of a {@link MeasureDescription} and uses default parameter map.
	 * 
	 * @see MNodeMeasureManager#addMeasureCalculation(ContextContextJungNetwork,
	 *      de.cesr.more.measures.node.MNodeMeasureManager.sh.soneta.measures.NetworkMeasureUtilities.MeasureDescription,
	 *      Map)
	 */
	public <T, E extends MoreEdge> boolean addMeasureCalculation(MoreNetwork<T, E> network, String shortname) {
		return addMeasureCalculation(network, new MMeasureDescription(shortname), null);
	}

	/**
	 * Takes a short description instead of a {@link MeasureDescription} and uses default parameter map.
	 * 
	 * @see MNodeMeasureManager#addMeasureCalculation(ContextContextJungNetwork,
	 *      de.cesr.more.measures.node.MNodeMeasureManager.sh.soneta.measures.NetworkMeasureUtilities.MeasureDescription,
	 *      Map)
	 */
	public <T, E extends MoreEdge> boolean addMeasureCalculation(String network, String shortname) {
		return addMeasureCalculation((MoreNetwork<T, E>) MNetworkManager.getNetwork(network), new MMeasureDescription(
				shortname), null);
	}

	/**
	 * Returns a <code>Set</code> of <code>MeasureDescription</code>s that are dedicated to be calculated for the given
	 * <code>ContextContextJungNetwork</code>.
	 * 
	 * @date 21.06.2008
	 * 
	 * @param <T> Parameter of <code>ContextContextContextJungNetwork</code> (type of nodes)
	 * @param network
	 * @return set of <code>Measures</code>
	 */
	@Override
	public Set<MMeasureDescription> getMeasureCalculations(MoreNetwork<?, ?> network) {
		if (measureActions.containsKey(network)) {
			return measureActions.get(network).keySet();
		} else {
			return new HashSet<MMeasureDescription>();
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
	public <T, E> boolean removeMeasureCalculation(MoreNetwork<T, E> network, MMeasureDescription key) {
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
		for (MoreMeasureManagerListener listener : listeners) {
			listener.networkMeasureCalcRemoved(network, key);
		}
		return removed;
	}

	public <T, E extends MoreEdge> boolean removeMeasureCalculation(MoreNetwork<T, E> network, String shortName) {
		return removeMeasureCalculation(network, new MMeasureDescription(shortName));
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
}
