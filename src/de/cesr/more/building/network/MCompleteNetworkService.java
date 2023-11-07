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
 * Created by holzhauer on 21.11.2011
 */
package de.cesr.more.building.network;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MUndirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 * 
 * @formatter:off
 * <table>
 * <th>Parameter</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents)</td></tr>
 * <th>Property</th><th>Value</th>
 * <tr><td>#Edges:</td><td>N*(N-1)</td></tr>
 * <tr><td></td><td></td></tr>
 * </table>
 * <br>
 * 
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * </ul>
 *
 * @author holzhauer
 * @date 21.11.2011 
 *
 */
public class MCompleteNetworkService<AgentType, EdgeType extends MoreEdge<AgentType>> 
	extends MGCompleteNetworkService<AgentType, EdgeType, MoreNetwork<AgentType, EdgeType>> {
	
	/**
	 * @param eFac
	 */
	public MCompleteNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac, String name, PmParameterManager pm) {
		super(((Boolean) pm.getParam(MNetworkBuildingPa.BUILD_DIRECTED))?
				new MDirectedNetwork<AgentType, EdgeType >(eFac, name) :
					new MUndirectedNetwork<AgentType, EdgeType >(eFac, name), eFac, name);
		this.pm = pm;
	}
	
	/**
	 * @param eFac
	 */
	public MCompleteNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED))?
				new MDirectedNetwork<AgentType, EdgeType >(eFac, name) :
					new MUndirectedNetwork<AgentType, EdgeType >(eFac, name), eFac, name);
	}
	
	/**
	 * @param eFac
	 */
	public MCompleteNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this(eFac, "Network");
	}
}
