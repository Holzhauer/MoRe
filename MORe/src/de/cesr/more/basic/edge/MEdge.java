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
 * Created by Sascha Holzhauer on 03.12.2010
 */
package de.cesr.more.basic.edge;

import de.cesr.more.basic.MManager;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;


/**
 * Default MORe edge
 *
 * @author Sascha Holzhauer
 * @date 03.12.2010 
 *
 */
public class MEdge<V> implements MoreEdge<V>, MoreTraceableEdge<V> {
	
	private final static double DEFAULT_EDGE_WEIGHT = 1.0;
	
	protected V start, end;
	protected double weight;
	protected boolean directed;
	
	protected boolean active;
	
	/**
	 * Creates an undirected edge.
	 * @param start
	 * @param end
	 */
	public MEdge(V start, V end) {
		this(start, end, false);
	}

	/**
	 * @param start
	 * @param end
	 * @param directed 
	 */
	public MEdge(V start, V end, boolean directed) {
		this(start, end, directed, DEFAULT_EDGE_WEIGHT);
	}
	
	/**
	 * @param start
	 * @param end
	 * @param directed
	 * @param weight
	 */
	public MEdge(V start, V end, boolean directed, double weight) {
		this.start = start;
		this.end = end;
		this.directed = directed;
		this.weight = weight;
	}
	
	/**
	 * @see de.cesr.more.basic.edge.MoreEdge#getEnd()
	 */
	@Override
	public V getEnd() {
		return this.end;
	}

	/**
	 * @see de.cesr.more.basic.edge.MoreEdge#getStart()
	 */
	@Override
	public V getStart() {
		return this.start;
	}

	/**
	 * @see de.cesr.more.basic.edge.MoreEdge#getWeight()
	 */
	@Override
	public double getWeight() {
		return this.weight;
	}

	/**
	 * @see de.cesr.more.basic.edge.MoreEdge#setWeight(double)
	 */
	@Override
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	@Override
	public String toString() {
		return "[" + getStart() + " > " + getEnd() + "]";
	}

	/**
	 * @see de.cesr.more.basic.edge.MoreEdge#isDirected()
	 */
	@Override
	public boolean isDirected() {
		return directed;
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
