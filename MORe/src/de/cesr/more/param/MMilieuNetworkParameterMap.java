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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
		LinkedHashMap<Integer, Map<PmParameterDefinition, Object>> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MMilieuNetworkParameterMap.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5395092541374359151L;

	private static Map<PmParameterDefinition, Set<Integer>>	warningsReducerMap	= new HashMap<PmParameterDefinition, Set<Integer>>();


	public int getK(int milieu) {
		return warnDefault(MNetBuildBhPa.K, milieu) ? 
				((Integer)PmParameterManager.getParameter(MNetBuildBhPa.K)).intValue() :
		((Integer) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.K)).intValue();
	}

	public void setK(int milieu, int k) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildBhPa.K,
				new Integer(k));
	}

	public double getP_Rewire(int milieu) {
		return warnDefault(MNetBuildBhPa.P_REWIRE, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.P_REWIRE)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.P_REWIRE)).doubleValue();
	}

	public void setP_Rewire(int milieu, double p) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.P_REWIRE, new Double(p));
	}

	public double getSearchRadius(int milieu) {
		return warnDefault(MNetBuildBhPa.SEARCH_RADIUS, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.SEARCH_RADIUS)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.SEARCH_RADIUS)).doubleValue();
	}

	public void setSearchRadius(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.SEARCH_RADIUS,
				new Double(radius));
	}

	public double getXSearchRadius(int milieu) {
		return warnDefault(MNetBuildBhPa.X_SEARCH_RADIUS, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.X_SEARCH_RADIUS)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.X_SEARCH_RADIUS))
				.doubleValue();
	}

	public void setXSearchRadius(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.X_SEARCH_RADIUS,
				new Double(radius));
	}

	public double getMaxSearchRadius(int milieu) {
		return warnDefault(MNetBuildBhPa.MAX_SEARCH_RADIUS, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.MAX_SEARCH_RADIUS)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.MAX_SEARCH_RADIUS))
				.doubleValue();
	}

	public void setMaxSearchRadius(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.MAX_SEARCH_RADIUS,
				new Double(radius));
	}

	public double getExtendingSearchFraction(int milieu) {
		return warnDefault(MNetBuildBhPa.EXTENDING_SEARCH_FRACTION, milieu) ?
				((Double) PmParameterManager.getParameter(MNetBuildBhPa.EXTENDING_SEARCH_FRACTION)).doubleValue() :
				((Double) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.EXTENDING_SEARCH_FRACTION))
						.doubleValue();
	}

	public void setExtendingSearchFraction(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.EXTENDING_SEARCH_FRACTION,
				new Double(radius));
	}
	
	public double getDistanceProbExp(int milieu) {
		return warnDefault(MNetBuildBhPa.DISTANCTE_PROBABILITY_EXPONENT, milieu) ?
				((Double) PmParameterManager.getParameter(MNetBuildBhPa.DISTANCTE_PROBABILITY_EXPONENT)).doubleValue() :
				((Double) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.DISTANCTE_PROBABILITY_EXPONENT))
						.doubleValue();
	}

	public void setDistanceProbExp(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.DISTANCTE_PROBABILITY_EXPONENT,
				new Double(radius));
	}

	public double getDimWeightGeo(int milieu) {
		return warnDefault(MNetBuildBhPa.DIM_WEIGHTS_GEO, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.DIM_WEIGHTS_GEO)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.DIM_WEIGHTS_GEO))
				.doubleValue();
	}

	public void setDimWeightGeo(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.DIM_WEIGHTS_GEO,
				new Double(weight));
	}

	public double getDimWeightMilieu(int milieu) {
		return warnDefault(MNetBuildBhPa.DIM_WEIGHTS_MILIEU, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetBuildBhPa.DIM_WEIGHTS_MILIEU)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.DIM_WEIGHTS_MILIEU))
				.doubleValue();
	}

	public void setDimWeightMilieu(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.DIM_WEIGHTS_MILIEU,
				new Double(weight));
	}

	
	public double getDynProbReciprocity(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_PROP_RECIPROCITY, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetManipulatePa.DYN_PROP_RECIPROCITY)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
					MNetManipulatePa.DYN_PROP_RECIPROCITY))
				.doubleValue();
	}

	public void setDynProbReciprocity(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_PROP_RECIPROCITY,
				new Double(weight));
	}
	
	public double getDynProbTransitivity(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_PROP_TRANSITIVIY, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetManipulatePa.DYN_PROP_TRANSITIVIY)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
					MNetManipulatePa.DYN_PROP_TRANSITIVIY))
				.doubleValue();
	}

	public void setDynProbTransitivity(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_PROP_TRANSITIVIY,
				new Double(weight));
	}

	public double getDynProbGlobal(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_PROP_GLOBAL, milieu) ?  
				((Double)PmParameterManager.getParameter(MNetManipulatePa.DYN_PROP_GLOBAL)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
					MNetManipulatePa.DYN_PROP_GLOBAL))
				.doubleValue();
	}

	public void setDynProbGlobal(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_PROP_GLOBAL,
				new Double(weight));
	}
	
	public double getDynProbLocal(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_PROP_LOCAL, milieu) ?
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_PROP_LOCAL)).doubleValue() :
				((Double) this.get(new Integer(milieu)).get(
						MNetManipulatePa.DYN_PROP_LOCAL))
						.doubleValue();
	}

	public void setDynProbLocal(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_PROP_LOCAL,
				new Double(weight));
	}

	public double getDynLocalRadius(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_LOCAL_RADIUS, milieu) ?
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_LOCAL_RADIUS)).doubleValue() :
				((Double) this.get(new Integer(milieu)).get(
						MNetManipulatePa.DYN_LOCAL_RADIUS))
						.doubleValue();
	}

	public void setDynLocalRadius(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_LOCAL_RADIUS,
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
						MNetBuildBhPa.P_MILIEUS)).get(
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
			this.put(new Integer(ownMilieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		if (!this.get(new Integer(ownMilieu)).containsKey(
				MNetBuildBhPa.P_MILIEUS)) {
			this.get(new Integer(ownMilieu)).put(
					MNetBuildBhPa.P_MILIEUS,
					new HashMap<Integer, Double>());
		}
		((Map<Integer, Double>) this.get(new Integer(ownMilieu)).get(
				MNetBuildBhPa.P_MILIEUS)).put(new Integer(
				otherMilieu), new Double(p));
	}
	
	public int getDynEdgeUpdatingInverval(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_INTERVAL_EDGE_UPDATING, milieu) ?
				((Integer) PmParameterManager.getParameter(MNetManipulatePa.DYN_INTERVAL_EDGE_UPDATING)).intValue() :
				((Integer) this.get(new Integer(milieu)).get(
						MNetManipulatePa.DYN_INTERVAL_EDGE_UPDATING))
						.intValue();
	}

	public void setDynEdgeUpdatingInverval(int milieu, int interval) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_INTERVAL_EDGE_UPDATING,
				new Integer(interval));
	}

	public int getDynLinkManagementInverval(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_INTERVAL_LINK_MANAGEMENT, milieu) ?
				((Integer) PmParameterManager.getParameter(MNetManipulatePa.DYN_INTERVAL_LINK_MANAGEMENT)).intValue() :
					((Integer) this.get(new Integer(milieu)).get(
							MNetManipulatePa.DYN_INTERVAL_LINK_MANAGEMENT))
							.intValue();
	}

	public void setDynLinkManagementInverval(int milieu, int interval) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_INTERVAL_LINK_MANAGEMENT,
				new Integer(interval));
	}

	// INCREASE
	
	public double getDynIncreaseAmount(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_INCREASE_AMOUNT, milieu) ?
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_INCREASE_AMOUNT)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(
							MNetManipulatePa.DYN_INCREASE_AMOUNT))
							.doubleValue();
	}

	public void setDynIncreaseAmount(int milieu, double amount) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_INCREASE_AMOUNT,
				new Double(amount));
	}
	
	public double getDynIncreaseThreshold(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_INCREASE_THRESHOLD, milieu) ?
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_INCREASE_THRESHOLD)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(
							MNetManipulatePa.DYN_INCREASE_THRESHOLD))
							.doubleValue();
	}

	public void setDynIncreaseThreshold(int milieu, double threshold) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_INCREASE_THRESHOLD,
				new Double(threshold));
	}
	
	// DECRESE
	public double getDynDecreaseAmount(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_DECREASE_AMOUNT, milieu) ?
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_DECREASE_AMOUNT)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(
							MNetManipulatePa.DYN_DECREASE_AMOUNT))
							.doubleValue();
	}

	public void setDynDecreaseAmount(int milieu, double amount) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_DECREASE_AMOUNT,
				new Double(amount));
	}
	
	public double getDynDecreaseThreshold(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_DECREASE_THRESHOLD, milieu) ?
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_DECREASE_THRESHOLD)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(
							MNetManipulatePa.DYN_DECREASE_THRESHOLD))
							.doubleValue();
	}

	public void setDynDecreaseThreshold(int milieu, double threshold) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_DECREASE_THRESHOLD,
				new Double(threshold));
	}

	public double getDynFadeOutAmount(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_FADE_OUT_AMOUNT, milieu) ?
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_FADE_OUT_AMOUNT)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(
							MNetManipulatePa.DYN_FADE_OUT_AMOUNT))
							.doubleValue();
	}
	
	public void setDynFadeOutAmount(int milieu, double amount) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_FADE_OUT_AMOUNT,
				new Double(amount));
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set fade out amount: " + amount);
		}
		// LOGGING ->

	}
	
	public void setDynFadeOutInterval(int milieu, double amount) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_FADE_OUT_INTERVAL,
				new Double(amount));
	}

	public double getDynFadeOutInterval(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_FADE_OUT_INTERVAL, milieu) ?
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_FADE_OUT_INTERVAL)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(
							MNetManipulatePa.DYN_FADE_OUT_INTERVAL))
							.doubleValue();
	}
	
	public void setDynEdgeManageOptimum(int milieu, double amount) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_EDGE_MANAGE_OPTIMUM,
				new Double(amount));
	}

	public double getDynEdgeManageOptimum(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_EDGE_MANAGE_OPTIMUM, milieu) ?
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_EDGE_MANAGE_OPTIMUM)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(
							MNetManipulatePa.DYN_EDGE_MANAGE_OPTIMUM))
							.doubleValue();
	}
	
	public int getNetUpdateInterval(int milieu) {
		return warnDefault(MDonNetworksPa.PERCEIVE_SOCNET_INTERVAL, milieu) ?
				((Integer) PmParameterManager.getParameter(MDonNetworksPa.PERCEIVE_SOCNET_INTERVAL)).intValue() :
					((Integer) this.get(new Integer(milieu)).get(MDonNetworksPa.PERCEIVE_SOCNET_INTERVAL))
							.intValue();
	}

	public void setNetUpdateInterval(int milieu, int interval) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MDonNetworksPa.PERCEIVE_SOCNET_INTERVAL,
				new Integer(interval));
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
			if (!(warningsReducerMap.containsKey(null))) {
				warningsReducerMap.put(null, new HashSet<Integer>());
			}
			if (!warningsReducerMap.get(null).contains(new Integer(milieu))) {
				warningsReducerMap.get(null).add(new Integer(milieu));
				logger.warn("There are less milieus in the map than reqested (map size: " + this.size()
						+ " / requested milieu: " +
						milieu + "; parameter: " + definition.toString() + ")");
			}
			return true;
		} else if (this.get(new Integer(milieu)).get(
					definition) == null) {
			if (!(warningsReducerMap.containsKey(definition))) {
				warningsReducerMap.put(definition, new HashSet<Integer>());
			}
			if (!warningsReducerMap.get(definition).contains(new Integer(milieu))) {
				warningsReducerMap.get(definition).add(new Integer(milieu));
				logger.warn("No value for " + definition.toString() + "(milieu: " + milieu
						+ ") defined. Using default (" +
						PmParameterManager.getParameter(definition) + ")!");
			}
			return true;
		} 
		return false;
	}
}
