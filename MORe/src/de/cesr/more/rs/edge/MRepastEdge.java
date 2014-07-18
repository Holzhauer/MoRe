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
 * Created by Sascha Holzhauer on Jan 3, 2011
 */
package de.cesr.more.rs.edge;


import org.apache.log4j.Logger;

import repast.simphony.space.graph.RepastEdge;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreFadingWeightEdge;
import de.cesr.more.basic.edge.MoreTraceableEdge;
import de.cesr.more.geo.MoreGeoEdge;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetManipulatePa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.util.exception.MIdentifyCallerException;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date Jan 3, 2011 
 *
 */
public class MRepastEdge<AgentT> extends RepastEdge<AgentT> implements MoreGeoEdge<AgentT>, MoreTraceableEdge<AgentT>,
		MoreFadingWeightEdge {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MRepastEdge.class);

	protected double length = 0.0;
	
	protected double		fadeAmount	= 0.0;

	protected boolean active;
	
	protected int hashcode = 0;

	/**
	 * @param source
	 * @param target
	 * @param directed
	 * @throws MIdentifyCallerException
	 */
	public MRepastEdge(AgentT source, AgentT target, boolean directed) {
		this(source, target, directed, 1.0);
		// <- LOGGING
	}

	public MRepastEdge(AgentT source, AgentT target, boolean directed, double weight) {
		super(source, target, directed, weight);

		// schedule fading out:
		Object agent = (Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_CONSIDER_SOURCES) ? 
				this.getStart() : this.getEnd(); 
		this.fadeAmount = (agent instanceof MoreMilieuAgent && PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS) != null) ? 
				((MMilieuNetworkParameterMap)PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS)).
				getDynFadeOutAmount(((MoreMilieuAgent) agent).getMilieuGroup()) :
				((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_FADE_OUT_AMOUNT))
				.doubleValue();
				
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> Fading amount: " + fadeAmount + " Net params: " + PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS));
		}
		// LOGGING ->
				
		if (fadeAmount > 0.0) {
			MManager.getSchedule().schedule(MScheduleParameters.getScheduleParameter(1.0,
					((Double) PmParameterManager.getParameter(MNetManipulatePa.DYN_FADE_OUT_INTERVAL)).doubleValue(),
					Double.POSITIVE_INFINITY, MScheduleParameters.LAST_PRIORITY), new MoreAction() {
				@Override
				public void execute() {
					MRepastEdge.this.fadeWeight();
				}
			});
		}
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Edge created");
			try {
				throw new MIdentifyCallerException();
			} catch (MIdentifyCallerException e) {
				e.printStackTrace();
			}
		}
		// LOGGING ->
	}
	
	/**
	 * @see de.cesr.more.basic.edge.MoreEdge#getEnd()
	 */
	@Override
	public AgentT getEnd() {
		return this.getTarget();
	}

	/**
	 * @see de.cesr.more.basic.edge.MoreEdge#getStart()
	 */
	@Override
	public AgentT getStart() {
		return this.getSource();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + getStart() + " > " + getEnd() + "]";
	}
	
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (hashcode == 0) {
			hashcode = 17;
			hashcode = 31 * hashcode + getStart().hashCode(); 
			hashcode = 31 * hashcode + getEnd().hashCode();
		}
		return hashcode;
	}

	/**
	 * @see de.cesr.more.geo.MoreGeoEdge#setLength(double)
	 */
	@Override
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * @see de.cesr.more.geo.MoreGeoEdge#getLength()
	 */
	@Override
	public double getLength() {
		return this.length;
	}

	/**
	 * @see de.cesr.more.basic.edge.MoreTraceableEdge#activate()
	 */
	@Override
	public void activate() {
		this.active = true;
		MManager.getSchedule().schedule(MScheduleParameters.getScheduleParameter(MManager.getSchedule().getCurrentTick() + 1, 
				MScheduleParameters.END_TICK, 
				MManager.getSchedule().getCurrentTick() + 1, 
				MScheduleParameters.FIRST_PRIORITY), new MoreAction() {
					@Override
					public void execute() {
						active = false;
					}
				});
	}

	/**
	 * @see de.cesr.more.basic.edge.MoreTraceableEdge#isActive()
	 */
	@Override
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @see de.cesr.more.basic.edge.MoreFadingWeightEdge#fadeWeight()
	 */
	@Override
	public void fadeWeight() {
		this.setWeight(this.getWeight() - fadeAmount);
	}
}
