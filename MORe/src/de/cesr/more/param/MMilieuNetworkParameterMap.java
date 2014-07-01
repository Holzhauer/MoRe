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

import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.building.MGeoRsHomophilyDistanceFfNetworkService;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;


/**
 * MoRe
 *
 * This class provides milieu-specific parameter values. Usually, it is filled by {@link MMilieuNetDataReader}.
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


	private final PmParameterManager								pm;

	public MMilieuNetworkParameterMap(PmParameterManager pm) {
		this.pm = pm;
	}

	public MMilieuNetworkParameterMap() {
		this(PmParameterManager.getInstance(null));
	}

	/**
	 * Generic function to set milieu-specific parameter values
	 *
	 * @param definition
	 * @param milieu
	 * @param value
	 */
	public void setMilieuParam(PmParameterDefinition definition, int milieu, Object value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(definition, value);
	}

	public Object getMilieuParam(PmParameterDefinition definition, int milieu) {
		return warnDefault(definition, milieu) ?
				pm.getParam(definition) :
				this.get(new Integer(milieu)).get(definition);
	}

	/**
	 * @param milieu
	 * @return
	 *
	 * @deprecated use {@link #getMilieuParam(PmParameterDefinition, int)} instead!
	 */
	@Deprecated
	public int getK(int milieu) {
		return warnDefault(MNetBuildWsPa.K, milieu) ?
				((Integer) pm.getParam(MNetBuildWsPa.K)).intValue() :
		((Integer) this.get(new Integer(milieu)).get(
						MNetBuildWsPa.K)).intValue();
	}

	/**
	 * @param milieu
	 * @param k
	 *
	 * @deprecated use {@link #setMilieuParam(PmParameterDefinition, int, Object)} instead!
	 */
	@Deprecated
	public void setK(int milieu, int k) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildWsPa.K,
				new Integer(k));
	}

	public double getP_Rewire(int milieu) {
		return warnDefault(MNetBuildWsPa.BETA, milieu) ?
				((Double) pm.getParam(MNetBuildWsPa.BETA)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
						MNetBuildWsPa.BETA)).doubleValue();
	}

	public void setP_Rewire(int milieu, double p) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildWsPa.BETA, new Double(p));
	}

	public double getSearchRadius(int milieu) {
		return warnDefault(MNetBuildBhPa.SEARCH_RADIUS, milieu) ?
				((Double) pm.getParam(MNetBuildBhPa.SEARCH_RADIUS)).doubleValue() :
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
				((Double) pm.getParam(MNetBuildBhPa.X_SEARCH_RADIUS)).doubleValue() :
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
				((Double) pm.getParam(MNetBuildBhPa.MAX_SEARCH_RADIUS)).doubleValue() :
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
				((Double) pm.getParam(MNetBuildBhPa.EXTENDING_SEARCH_FRACTION)).doubleValue() :
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
		return warnDefault(MNetBuildBhPa.DISTANCE_PROBABILITY_EXPONENT, milieu) ?
				((Double) pm.getParam(MNetBuildBhPa.DISTANCE_PROBABILITY_EXPONENT)).doubleValue() :
				((Double) this.get(new Integer(milieu)).get(
						MNetBuildBhPa.DISTANCE_PROBABILITY_EXPONENT))
						.doubleValue();
	}

	public void setDistanceProbExp(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetBuildBhPa.DISTANCE_PROBABILITY_EXPONENT,
				new Double(radius));
	}

	/**
	 * @param milieu
	 * @return
	 *
	 * @deprecated
	 */
	@Deprecated
	public double getDimWeightGeo(int milieu) {
		return warnDefault(MNetBuildBhPa.DIM_WEIGHTS_GEO, milieu) ?
				((Double) pm.getParam(MNetBuildBhPa.DIM_WEIGHTS_GEO)).doubleValue() :
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

	/**
	 * @param milieu
	 * @return
	 *
	 * @deprecated
	 */
	@Deprecated
	public double getDimWeightMilieu(int milieu) {
		return warnDefault(MNetBuildBhPa.DIM_WEIGHTS_MILIEU, milieu) ?
				((Double) pm.getParam(MNetBuildBhPa.DIM_WEIGHTS_MILIEU)).doubleValue() :
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
		return warnDefault(MNetManipulatePa.DYN_PROB_RECIPROCITY, milieu) ?
				((Double) pm.getParam(MNetManipulatePa.DYN_PROB_RECIPROCITY)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
					MNetManipulatePa.DYN_PROB_RECIPROCITY))
				.doubleValue();
	}

	public void setDynProbReciprocity(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_PROB_RECIPROCITY,
				new Double(weight));
	}

	public double getDynProbTransitivity(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_PROB_TRANSITIVITY, milieu) ?
				((Double) pm.getParam(MNetManipulatePa.DYN_PROB_TRANSITIVITY)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
					MNetManipulatePa.DYN_PROB_TRANSITIVITY))
				.doubleValue();
	}

	public void setDynProbTransitivity(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_PROB_TRANSITIVITY,
				new Double(weight));
	}

	public double getDynProbGlobal(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_PROB_GLOBAL, milieu) ?
				((Double) pm.getParam(MNetManipulatePa.DYN_PROB_GLOBAL)).doubleValue() :
			((Double) this.get(new Integer(milieu)).get(
					MNetManipulatePa.DYN_PROB_GLOBAL))
				.doubleValue();
	}

	public void setDynProbGlobal(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_PROB_GLOBAL,
				new Double(weight));
	}

	public double getDynProbLocal(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_PROB_LOCAL, milieu) ?
				((Double) pm.getParam(MNetManipulatePa.DYN_PROB_LOCAL)).doubleValue() :
				((Double) this.get(new Integer(milieu)).get(
						MNetManipulatePa.DYN_PROB_LOCAL))
						.doubleValue();
	}

	public void setDynProbLocal(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(
				MNetManipulatePa.DYN_PROB_LOCAL,
				new Double(weight));
	}

	public double getDynLocalRadius(int milieu) {
		return warnDefault(MNetManipulatePa.DYN_LOCAL_RADIUS, milieu) ?
				((Double) pm.getParam(MNetManipulatePa.DYN_LOCAL_RADIUS)).doubleValue() :
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
	 * @param otherMilieu
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public double getP_Milieu(int ownMilieu, int otherMilieu) {

		return (warnDefault(MNetBuildBhPa.P_MILIEUS, ownMilieu) || !((Map<Integer, Double>) this.get(
				new Integer(ownMilieu)).get(
				MNetBuildBhPa.P_MILIEUS)).containsKey(new Integer(otherMilieu))) ?
				((Double) pm.getParam(MNetBuildBhPa.P_MILIEUS)).doubleValue() :
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
				((Integer) pm.getParam(MNetManipulatePa.DYN_INTERVAL_EDGE_UPDATING)).intValue() :
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
				((Integer) pm.getParam(MNetManipulatePa.DYN_INTERVAL_LINK_MANAGEMENT)).intValue() :
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
				((Double) pm.getParam(MNetManipulatePa.DYN_INCREASE_AMOUNT)).doubleValue() :
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
				((Double) pm.getParam(MNetManipulatePa.DYN_INCREASE_THRESHOLD)).doubleValue() :
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
				((Double) pm.getParam(MNetManipulatePa.DYN_DECREASE_AMOUNT)).doubleValue() :
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
				((Double) pm.getParam(MNetManipulatePa.DYN_DECREASE_THRESHOLD)).doubleValue() :
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
				((Double) pm.getParam(MNetManipulatePa.DYN_FADE_OUT_AMOUNT)).doubleValue() :
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
				((Double) pm.getParam(MNetManipulatePa.DYN_FADE_OUT_INTERVAL)).doubleValue() :
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
				((Double) pm.getParam(MNetManipulatePa.DYN_EDGE_MANAGE_OPTIMUM)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(
							MNetManipulatePa.DYN_EDGE_MANAGE_OPTIMUM))
							.doubleValue();
	}

	public int getNetUpdateInterval(int milieu) {
		return warnDefault(MDonNetworksPa.PERCEIVE_SOCNET_INTERVAL, milieu) ?
				((Integer) pm.getParam(MDonNetworksPa.PERCEIVE_SOCNET_INTERVAL)).intValue() :
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
	 * MGeoRsHomophilyDistanceFfNetworkService
	 **/

	public double getBackwardProb(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.PROB_BACKWARD + " for milieu " + milieu + " requested.");
		}
		// LOGGING ->
		return warnDefault(MNetBuildHdffPa.PROB_BACKWARD, milieu) ?
				((Double) pm.getParam(MNetBuildHdffPa.PROB_BACKWARD)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(MNetBuildHdffPa.PROB_BACKWARD))
							.doubleValue();
	}

	public void setBackwardProb(int milieu, double value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.PROB_BACKWARD,
				new Double(value));
	}

	/**
	 * Forward probability is used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @return
	 */
	public double getForwardProb(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.PROB_FORWARD + " for milieu " + milieu + " requested.");
		}
		// LOGGING ->

		return warnDefault(MNetBuildHdffPa.PROB_FORWARD, milieu) ?
				((Double) pm.getParam(MNetBuildHdffPa.PROB_FORWARD)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(MNetBuildHdffPa.PROB_FORWARD))
							.doubleValue();
	}

	/**
	 * Forward probability is used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @param value
	 */
	public void setForwardProb(int milieu, double value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.PROB_FORWARD,
				new Double(value));
	}

	/**
	 * @param milieu
	 * @return
	 */
	public String getKDistributionClass(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.K_DISTRIBUTION_CLASS + " for milieu " + milieu + " requested.");
		}
		// LOGGING ->

		return warnDefault(MNetBuildHdffPa.K_DISTRIBUTION_CLASS, milieu) ?
				(String) pm.getParam(MNetBuildHdffPa.K_DISTRIBUTION_CLASS) :
					(String) this.get(new Integer(milieu)).get(MNetBuildHdffPa.K_DISTRIBUTION_CLASS);
	}

	/**
	 * Used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @param value
	 */
	public void setKDistributionClass(int milieu, String value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.K_DISTRIBUTION_CLASS, value);
	}

	/**
	 * @param milieu
	 * @return
	 */
	@Deprecated
	public double getKparamA(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.K_PARAM_A + " for milieu " + milieu + " requested.");
		}
		// LOGGING ->

		return warnDefault(MNetBuildHdffPa.K_PARAM_A, milieu) ?
				((Double) pm.getParam(MNetBuildHdffPa.K_PARAM_A)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(MNetBuildHdffPa.K_PARAM_A))
							.doubleValue();
	}

	/**
	 * Used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @param value
	 */
	@Deprecated
	public void setKparamA(int milieu, double value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.K_PARAM_A,
				new Double(value));
	}

	/**
	 * @param milieu
	 * @return
	 */
	@Deprecated
	public double getKparamB(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.K_PARAM_B + " for milieu " + milieu + " requested.");
		}
		// LOGGING ->

		return warnDefault(MNetBuildHdffPa.K_PARAM_B, milieu) ?
				((Double) pm.getParam(MNetBuildHdffPa.K_PARAM_B)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(MNetBuildHdffPa.K_PARAM_B))
							.doubleValue();
	}

	/**
	 * Used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @param value
	 */
	@Deprecated
	public void setKparamB(int milieu, double value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.K_PARAM_B,
				new Double(value));
	}

	/**
	 * @param milieu
	 * @return
	 */
	public String getDistDistributionClass(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.DIST_DISTRIBUTION_CLASS + " for milieu " + milieu
					+ " requested.");
		}
		// LOGGING ->

		return warnDefault(MNetBuildHdffPa.DIST_DISTRIBUTION_CLASS, milieu) ?
				(String) pm.getParam(MNetBuildHdffPa.DIST_DISTRIBUTION_CLASS) :
					(String) this.get(new Integer(milieu)).get(MNetBuildHdffPa.DIST_DISTRIBUTION_CLASS);
	}

	/**
	 * Used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @param value
	 */
	public void setDistDistributionClass(int milieu, String value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.DIST_DISTRIBUTION_CLASS, value);
	}

	/**
	 * @param milieu
	 * @return
	 */
	public double getDistParamA(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.DIST_PARAM_A + " for milieu " + milieu + " requested.");
		}
		// LOGGING ->

		return warnDefault(MNetBuildHdffPa.DIST_PARAM_A, milieu) ?
				((Double) pm.getParam(MNetBuildHdffPa.DIST_PARAM_A)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(MNetBuildHdffPa.DIST_PARAM_A))
							.doubleValue();
	}

	/**
	 * Used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @param value
	 */
	public void setDistParamA(int milieu, double value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.DIST_PARAM_A,
				new Double(value));
	}

	/**
	 * @param milieu
	 * @return
	 */
	public double getDistParamB(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.DIST_PARAM_B + " for milieu " + milieu + " requested.");
		}
		// LOGGING ->

		return warnDefault(MNetBuildHdffPa.DIST_PARAM_B, milieu) ?
				((Double) pm.getParam(MNetBuildHdffPa.DIST_PARAM_B)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(MNetBuildHdffPa.DIST_PARAM_B))
							.doubleValue();
	}

	/**
	 * Used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @param value
	 */
	public void setDistParamB(int milieu, double value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.DIST_PARAM_B,
				new Double(value));
	}

	/**
	 * @param milieu
	 * @return
	 */
	public double getDistParamXMin(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.DIST_PARAM_XMIN + " for milieu " + milieu + " requested.");
		}
		// LOGGING ->

		return warnDefault(MNetBuildHdffPa.DIST_PARAM_XMIN, milieu) ?
				((Double) pm.getParam(MNetBuildHdffPa.DIST_PARAM_XMIN)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(MNetBuildHdffPa.DIST_PARAM_XMIN))
							.doubleValue();
	}

	/**
	 * Used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @param value
	 */
	public void setDistParamXMin(int milieu, double value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.DIST_PARAM_XMIN,
				new Double(value));
	}

	/**
	 * @param milieu
	 * @return
	 */
	public double getDistParamPLocal(int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value of " + MNetBuildHdffPa.DIST_PARAM_PLOCAL + " for milieu " + milieu + " requested.");
		}
		// LOGGING ->

		return warnDefault(MNetBuildHdffPa.DIST_PARAM_PLOCAL, milieu) ?
				((Double) pm.getParam(MNetBuildHdffPa.DIST_PARAM_PLOCAL)).doubleValue() :
					((Double) this.get(new Integer(milieu)).get(MNetBuildHdffPa.DIST_PARAM_PLOCAL))
							.doubleValue();
	}

	/**
	 * Used by {@link MGeoRsHomophilyDistanceFfNetworkService}.
	 *
	 * @param milieu
	 * @param value
	 */
	public void setDistParamPLocal(int milieu, double value) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new LinkedHashMap<PmParameterDefinition, Object>());
		}
		this.get(new Integer(milieu)).put(MNetBuildHdffPa.DIST_PARAM_PLOCAL,
				new Double(value));
	}

	/**
	 * Checks if the requested value is defined in the map and issues a warning otherwise
	 *
	 * @param definition
	 * @param milieu
	 * @return true if no value is defined in the map
	 */
	protected boolean warnDefault(PmParameterDefinition definition, int milieu) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Milieu: " + milieu + " | " + "PmParameterDefinition:" + definition);
		}
		// LOGGING ->

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
				logger.warn("No value for " + definition.toString() + " (milieu: " + milieu
						+ ") defined. Using default (" +
						pm.getParam(definition) + ")!");
			}
			return true;
		}
		return false;
	}
}
