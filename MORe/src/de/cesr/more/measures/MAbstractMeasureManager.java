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
package de.cesr.more.measures;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;

import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.util.MoreAction;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 16.11.2010 
 *
 */
public abstract class MAbstractMeasureManager extends MAbstractMeasureSupplier implements MMeasureSelectorListener {
	
	protected static boolean promptForMeasureParameters;
	
	protected Set<MoreMeasureManagerListener> listeners;
	
	protected Map<MoreNetwork<?, ?>, HashMap<MMeasureDescription, MoreAction>> measureActions;
	
	
	protected MAbstractMeasureManager() {
		listeners = new HashSet<MoreMeasureManagerListener>();
		measureActions = new HashMap<MoreNetwork<?, ?>, 
			HashMap<MMeasureDescription, MoreAction>>();
	}
	
	
	/**
	 * Adds a {@link NetworkMeasureUtilitiesListener}.
	 * @param listener the {@link NetworkMeasureUtilitiesListener} to add
	 * Created by Sascha Holzhauer on 06.04.2010
	 */
	public void addMeasureManagerListener(MoreMeasureManagerListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a {@link NetworkMeasureUtilitiesListener}.
	 * @param listener the {@link NetworkMeasureUtilitiesListener} to remove
	 * Created by Sascha Holzhauer on 06.04.2010
	 */
	public void removeMeasureManagerListener(MoreMeasureManagerListener listener) {
		listeners.remove(listener);
	}
	

	
	/**
	 * The Type that specifies content of <code>ContextContextContextJungNetwork</code> needs to extend 
	 * <code>NetworkMeasureSupport</code> since the <code>NetworkMeasureSupport</code> needs to
	 * access methods to get and set measure at the node.
	 * @date 24.05.2008
	 *
	 * @param <T> Parameter of <code>ContextContextContextJungNetwork</code>  
	 * @param network
	 * @param key
	 * @param params
	 * @return
	 */

	/**
	 * Returns a <code>Set</code> of <code>MeasureDescription</code>s that are dedicated to be calculated
	 * for the given <code>ContextContextJungNetwork</code>. 
	 * @date 21.06.2008
	 *
	 * @param <T> Parameter of <code>ContextContextContextJungNetwork</code> (type of nodes) 
	 * @param network
	 * @return set of <code>Measures</code>. In case there are no measure actions scheduled for the given network an empty set is returned.
	 */
	public Set<MMeasureDescription> getMeasureCalculations(MoreNetwork<?, ?> network) {
		if (measureActions.containsKey(network)) {
			return measureActions.get(network).keySet();
		}
		else {
			return new HashSet<MMeasureDescription>();
		}
	}
	

	/**
	 * 
	 */
	@Override
	public Set<MMeasureDescription> getRemovableMeasures(
			MoreNetwork<?, ?> network) {
		return getMeasureCalculations(network);
	}
	
	/**
	 * @see de.cesr.more.measures.MMeasureSelectorListener#getAddableMeasures(de.cesr.more.basic.network.MoreNetwork)
	 */
	@Override
	public Set<MMeasureDescription> getAddableMeasures(MoreNetwork<?, ?> network) {
		Set<MMeasureDescription> measures = new HashSet<MMeasureDescription>();
		for (MMeasureDescription measure : getMeasureDescriptions()) {
			if (measureActions.containsKey(network)) {
				if (!measureActions.get(network).containsKey(measure)) {
					measures.add(measure);
				}
			} else {
				measures.add(measure);
			}
		}
		return measures;
	}



	/**
	 * @return the promptForMeasureParameters
	 */
	public static boolean isPromptForMeasureParameters() {
		return promptForMeasureParameters;
	}

	/**
	 * @param promptForMeasureParameters the promptForMeasureParameters to set
	 */
	public static void setPromptForMeasureParameters(
			boolean promptForMeasureParameters) {
		MAbstractMeasureManager.promptForMeasureParameters = promptForMeasureParameters;
	}
	
	public void printLatexMeasureDescriptions() {
		System.out.println("Print all available measures:");
		TreeSet<MMeasureDescription> sortedSet = new TreeSet<MMeasureDescription>(getMeasureDescriptions());
		for (MMeasureDescription measure : sortedSet) {
			System.out.println(measure.getShort() + "\t& " + measure.toString() + "\t& " +
					findMeasure(measure).getType().toString() + " \\");
		}
	}
}
