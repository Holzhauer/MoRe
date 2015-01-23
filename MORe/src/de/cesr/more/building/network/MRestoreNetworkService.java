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
 * Created by Sascha Holzhauer on 27.03.2014
 */
package de.cesr.more.building.network;


import java.util.Collection;

import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.geo.building.edge.MDefaultGeoEdgeFactory;
import de.cesr.more.geo.building.network.MoreGeoNetworkService;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.util.MNetworkBuilderRegistry;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 27.03.2014 
 *
 */
public class MRestoreNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MoreEdge<AgentType>>
		extends MNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger								logger						= Logger.getLogger(MRestoreNetworkService.class);

	protected MoreNetworkService<AgentType, EdgeType>	maintainingNetworkService	= null;

	public MRestoreNetworkService(
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this(edgeFac, name, PmParameterManager.getInstance(null));
	}

	/**
	 * @param areasGeography
	 */
	public MRestoreNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name, PmParameterManager pm) {
		super(edgeFac);

		this.name = name;
		this.pm = pm;

		if (pm.getParam(MNetworkBuildingPa.MAINTAINING_NETWORK_SERVICE) != null) {

			@SuppressWarnings("unchecked")
			// user's duty to set parameter right
			Class<? extends MoreNetworkService<AgentType, EdgeType>> serviceClass =
					(Class<? extends MoreNetworkService<AgentType, EdgeType>>) pm
							.getParam(MNetworkBuildingPa.MAINTAINING_NETWORK_SERVICE);
			// <- LOGGING
			logger.info("Maintaining network service is: " + serviceClass);
			// LOGGING ->

			try {
				if (MoreGeoNetworkService.class.isAssignableFrom(serviceClass)) {
					this.maintainingNetworkService = serviceClass
							.getConstructor(MoreEdgeFactory.class, String.class, PmParameterManager.class)
							.newInstance(new MDefaultGeoEdgeFactory<AgentType>(), this.name, this.pm);
				} else {
					this.maintainingNetworkService = serviceClass
							.getConstructor(MoreEdgeFactory.class, String.class, PmParameterManager.class)
							.newInstance(new MDefaultEdgeFactory<AgentType>(), this.name, this.pm);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {
		MRestoreNetworkBuilder<AgentType, EdgeType> restoreBuilder = new MRestoreNetworkBuilder<AgentType, EdgeType>(
				this.edgeFac, this.name, pm);

		MoreNetwork<AgentType, EdgeType> network = restoreBuilder.buildNetwork(agents);
		MNetworkBuilderRegistry.registerNetworkBuiler(network, this);

		return network;
	}

	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		if (this.maintainingNetworkService != null) {
			this.maintainingNetworkService.addAndLinkNode(network, node);
			return true;
		}
		logger.warn("AddAndLinkNode: No maintaining network service assigned!");
		return false;
	}

}
