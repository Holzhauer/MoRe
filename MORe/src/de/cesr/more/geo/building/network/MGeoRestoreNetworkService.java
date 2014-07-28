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
package de.cesr.more.geo.building.network;


import java.util.Collection;

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MRestoreNetworkBuilder;
import de.cesr.more.geo.MoreGeoEdge;
import de.cesr.more.geo.building.edge.MDefaultGeoEdgeFactory;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 27.03.2014 
 *
 */
public class MGeoRestoreNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MoreGeoEdge<AgentType>>
		extends MGeoNetworkService<AgentType, EdgeType> {

	protected MoreGeoNetworkService<AgentType, EdgeType>	maintainingNetworkService	= null;

	/**
	 * Takes the geography from {@link MBasicPa#ROOT_GEOGRAPHY}.
	 * 
	 * @param edgeFac
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public MGeoRestoreNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac, name);
	}

	/**
	 * Uses main instance of {@link PmParameterManager}.
	 * 
	 * @param areasGeography
	 */
	public MGeoRestoreNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this(geography, edgeFac, name, PmParameterManager.getInstance(null));
	}

	public MGeoRestoreNetworkService(
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name, PmParameterManager pm) {
		this(null, edgeFac, name, pm);
	}

	/**
	 * @param areasGeography
	 */
	public MGeoRestoreNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name, PmParameterManager pm) {
		super(geography, edgeFac, pm);

		this.name = name;
		this.pm = pm;

		if (pm.getParam(MNetworkBuildingPa.MAINTAINING_NETWORK_SERVICE) != null) {
			Class serviceClass = (Class) pm.getParam(MNetworkBuildingPa.MAINTAINING_NETWORK_SERVICE);
			if (MoreGeoNetworkService.class.isAssignableFrom(serviceClass)) {
				try {
					this.maintainingNetworkService = (MoreGeoNetworkService<AgentType, EdgeType>) (serviceClass)
							.getConstructor(
									MoreEdgeFactory.class, String.class, PmParameterManager.class)
							.newInstance(new MDefaultGeoEdgeFactory<AgentType>(), this.name, this.pm);

					this.maintainingNetworkService.setGeography(this.geography);
					this.maintainingNetworkService.setGeoRequestClass(this.geoRequestClass);

				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
			// TODO error handling
		}
	}

	/**
	 * @see de.cesr.more.geo.building.network.MGeoNetworkService#setGeography(repast.simphony.space.gis.Geography)
	 */
	public void setGeography(Geography<Object> geography) {
		super.setGeography(geography);
		if (this.maintainingNetworkService != null) {
			this.maintainingNetworkService.setGeography(geography);
		}
	}

	/**
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {
		MRestoreNetworkBuilder<AgentType, EdgeType> restoreBuilder = new MRestoreNetworkBuilder<AgentType, EdgeType>(
				this.edgeFac, this.name, pm);
		return restoreBuilder.buildNetwork(agents);
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
		return false;
	}

	/**
	 * @see de.cesr.more.building.network.MNetworkService#removeNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	public boolean removeNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		if (this.maintainingNetworkService != null) {
			return this.maintainingNetworkService.removeNode(network, node);
		}
		return super.removeNode(network, node);
	}
}
