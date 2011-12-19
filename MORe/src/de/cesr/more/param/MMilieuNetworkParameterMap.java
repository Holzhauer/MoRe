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
 * Created by holzhauer on 27.07.2011
 */
package de.cesr.more.param;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * MoRe
 * 
 * TODO provide default values! TODO exception management
 * 
 * @author Sascha Holzhauer
 * @date 27.07.2010
 * 
 */
public class MMilieuNetworkParameterMap extends
		HashMap<Integer, Map<String, Object>> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MMilieuNetworkParameterMap.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5395092541374359151L;


	public int getK(int milieu) {
		return warnDefault(MNetBuildBhPa.K, milieu) ? 
				((Integer)PmParameterManager.getParameter(MNetBuildBhPa.K)).intValue() :
		((Integer) this.get(new Integer(milieu)).get(
				MNetBuildBhPa.K.name())).intValue();
	}

	public void setK(int milieu, int k) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap<String, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildBhPa.K.name(),
				new Integer(k));
	}

	public double getP_Rewire(int milieu) {
		return warnDefault(MNetBuildBhPa.P_REWIRE, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.P_REWIRE)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
				MNetBuildBhPa.P_REWIRE.name())).doubleValue();
	}

	public void setP_Rewire(int milieu, double p) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap<String, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.P_REWIRE.name(), new Double(p));
	}

	public double getSearchRadius(int milieu) {
		return warnDefault(MNetBuildBhPa.SEARCH_RADIUS, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.SEARCH_RADIUS)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
				MNetBuildBhPa.SEARCH_RADIUS.name())).doubleValue();
	}

	public void setSearchRadius(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap<String, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.SEARCH_RADIUS.name(),
				new Double(radius));
	}

	public double getXSearchRadius(int milieu) {
		return warnDefault(MNetBuildBhPa.X_SEARCH_RADIUS, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.X_SEARCH_RADIUS)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
				MNetBuildBhPa.X_SEARCH_RADIUS.name()))
				.doubleValue();
	}

	public void setXSearchRadius(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap<String, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.X_SEARCH_RADIUS.name(),
				new Double(radius));
	}

	public double getMaxSearchRadius(int milieu) {
		return warnDefault(MNetBuildBhPa.MAX_SEARCH_RADIUS, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.MAX_SEARCH_RADIUS)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
				MNetBuildBhPa.MAX_SEARCH_RADIUS.name()))
				.doubleValue();
	}

	public void setMaxSearchRadius(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap<String, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.MAX_SEARCH_RADIUS.name(),
				new Double(radius));
	}

	public double getDimWeightGeo(int milieu) {
		return warnDefault(MNetBuildBhPa.DIM_WEIGHTS_GEO, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.DIM_WEIGHTS_GEO)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
				MNetBuildBhPa.DIM_WEIGHTS_GEO.name()))
				.doubleValue();
	}

	public void setDimWeightGeo(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap<String, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.DIM_WEIGHTS_GEO.name(),
				new Double(weight));
	}

	public double getDimWeightMilieu(int milieu) {
		return warnDefault(MNetBuildBhPa.DIM_WEIGHTS_MILIEU, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.DIM_WEIGHTS_MILIEU)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
				MNetBuildBhPa.DIM_WEIGHTS_MILIEU.name()))
				.doubleValue();
	}

	public void setDimWeightMilieu(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap<String, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.DIM_WEIGHTS_MILIEU.name(),
				new Double(weight));
	}

	/**
	 * @param ownMilieu
	 *            milieu id (starting with 1!)
	 * @param otherMilieu
	 *            milieu id (starting with 1!)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public double getP_Milieu(int ownMilieu, int otherMilieu) {
		return warnDefault(MNetBuildBhPa.P_MILIEUS, ownMilieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.P_MILIEUS)).doubleValue() :
			((Map<Integer, Double>) this.get(new Integer(ownMilieu)).get(
				MNetBuildBhPa.P_MILIEUS.name())).get(
				new Integer(otherMilieu)).doubleValue();
	}

	/**
	 * @param ownMilieu
	 * @param otherMilieu
	 * @param p
	 */
	@SuppressWarnings("unchecked")
	public void setP_Milieu(int ownMilieu, int otherMilieu, double p) {
		if (!this.containsKey(new Integer(ownMilieu))) {
			this.put(new Integer(ownMilieu), new HashMap<String, Object>());
		}
		if (!this.get(new Integer(ownMilieu)).containsKey(
				MNetBuildBhPa.P_MILIEUS.name())) {
			this.get(new Integer(ownMilieu)).put(
					MNetBuildBhPa.P_MILIEUS.name(),
					new HashMap<Integer, Double>());
		}
		((Map<Integer, Double>) this.get(new Integer(ownMilieu)).get(
				MNetBuildBhPa.P_MILIEUS.name())).put(new Integer(
				otherMilieu), new Double(p));
	}
	
	/**
	 * Checks if the requested value is defined in the map and
	 * issues a warning otherwise
	 * @param definition
	 * @param milieu
	 * @return true if no value is defined in the map
	 */
	protected boolean warnDefault(PmParameterDefinition definition, int milieu) {
		if (this.size() < milieu) {
			logger.warn("There are less milieus in the map the reqested (map size: " + this.size() + " / requested milieu: " +
					milieu + "; parameter: " + definition.toString() + ")");
		} else {
			if (this.get(new Integer(milieu)).get(
					MNetBuildBhPa.K.name()) == null) {
				logger.warn("No value for " + definition.toString() + "(milieu: " + milieu + ") defined. Using default (" + 
						PmParameterManager.getParameter(definition) + ")!");
				return true;
			}
		} 
		return false;
	}
}
