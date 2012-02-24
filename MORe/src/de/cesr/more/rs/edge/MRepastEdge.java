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
import de.cesr.more.basic.edge.MoreTraceableEdge;
import de.cesr.more.geo.MoreGeoEdge;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.util.exception.MIdentifyCallerException;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date Jan 3, 2011 
 *
 */
public class MRepastEdge<AgentT> extends RepastEdge<AgentT> implements MoreGeoEdge<AgentT>, MoreTraceableEdge<AgentT> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MRepastEdge.class);

	protected double length = 0.0;
	
	protected boolean active;

	/**
	 * @param source
	 * @param target
	 * @param directed
	 * @throws MIdentifyCallerException
	 */
	public MRepastEdge(AgentT source, AgentT target, boolean directed) {
		super(source, target, directed);
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

	public MRepastEdge(AgentT source, AgentT target, boolean directed, double weight) {
		super(source, target, directed, weight);
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
		int result = 17;
		result= 31 * result + getStart().hashCode(); 
		result= 31 * result + getEnd().hashCode();
		return result;
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
}
