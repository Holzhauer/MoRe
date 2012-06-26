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
 * Created by Sascha Holzhauer on 15.12.2011
 */
package de.cesr.more.lara.agent;


import repast.simphony.space.gis.Geography;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.more.basic.agent.MAgentAnalyseNetworkComp;
import de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 15.12.2011 
 *
 */
public abstract class MAbstractLaraAnalyseNetworkAgent<A extends MoreLaraNetworkAgent<A, E, BO> &  MoreNodeMeasureSupport & 
 MoreMilieuAgent, BO extends LaraBehaviouralOption<?, BO>, E extends MoreEdge<? super A>>
		extends MAbstractLaraNetworkAgent<A, BO, E> implements MoreAgentAnalyseNetworkComp<A, E>{

	Geography<Object> geography;
	
	MoreAgentAnalyseNetworkComp<A, E> netComp;

	/**
	 * @param env
	 * @param name
	 */
	public MAbstractLaraAnalyseNetworkAgent(LaraEnvironment env, String name, Geography<Object> geography) {
		super(env, name);
		netComp = new MAgentAnalyseNetworkComp<A, E>(getThis(), geography);
		super.netComp = netComp;
	}
	
	/**
	 * @param env
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public MAbstractLaraAnalyseNetworkAgent(LaraEnvironment env, String name) {
		this(env, name, (Geography<Object>)PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY));
	}
	
	/**
	 * @param env
	 * @param name
	 */
	public MAbstractLaraAnalyseNetworkAgent(LaraEnvironment env) {
		super(env);
		netComp = new MAgentAnalyseNetworkComp<A, E>(getThis());
		super.netComp = netComp;
	}
	
	

	/**
	 * @see de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp#getInDegree()
	 */
	@Override
	public int getInDegree() {
		return this.netComp.getInDegree();
	}


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp#getXtInDegree()
	 */
	@Override
	public int getXtInDegree() {
		return this.netComp.getXtInDegree();
	}


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp#getOutDegree()
	 */
	@Override
	public int getOutDegree() {
		return this.netComp.getOutDegree();
	}


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp#getNbrDispers()
	 */
	@Override
	public float getNbrDispers() {
		return netComp.getNbrDispers();
	}


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp#getNNAvgDeg()
	 */
	@Override
	public float getNNAvgDeg() {
		return netComp.getNNAvgDeg();
	}
 
	/**
	 * @see de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp#getNetPrefDev()
	 */
	@Override
	public double getNetPrefDev() {
		return netComp.getNetPrefDev();
	}

	/**
	 * @see de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp#getNetKDev()
	 */
	@Override
	public int getNetKDev() {
		return netComp.getNetKDev();
	}
}
