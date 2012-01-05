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
 * Created by Sascha Holzhauer on 29.11.2011
 */
package de.cesr.more.rs.building;

import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;

import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MNetBuildLattice2DPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.more.util.io.MGraphMLReader2NodeMap;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 * 
 * TODO test
 * TODO make description
 * 
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>...</li>
 * </ul>
 *
 * @author Sascha Holzhauer
 * @date 29.11.2011 
 *
 */
public class MGeoRsRestoreNetworkBuilder<AgentType, EdgeType extends MRepastEdge<AgentType>> 
		extends  MAbstractGeoRsNetworkBuilder<AgentType, EdgeType> {
	
	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MGeoRsRestoreNetworkBuilder.class);

	protected String name;
	
	/**
	 * @param areasGeography
	 */
	public MGeoRsRestoreNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		this.edgeFac = eFac;
		this.name = name;
	}

	@Override
	public MoreRsNetwork < AgentType , EdgeType > buildNetwork(Collection < AgentType > agents) {

		MRsContextJungNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType >(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
						new DirectedJungNetwork<AgentType>(name) :
						new UndirectedJungNetwork<AgentType>(name), context);

		// put agents into map indexed by their indices:
		BidiMap < AgentType , String > households = new DualHashBidiMap < AgentType , String >();

		// <- LOGGING
		logger.info("Add agents");
		// LOGGING ->

		MGraphMLReader2NodeMap < Graph < AgentType , EdgeType > , AgentType , EdgeType > graphReader;
		try {
			graphReader = new MGraphMLReader2NodeMap < Graph < AgentType , EdgeType > , AgentType , EdgeType >(
						edgeFac, households);
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Load network from file " + (String) 
						PmParameterManager.getParameter(MNetworkBuildingPa.RESTORE_NETWORK_SOURCE_FILE));
			}
			// LOGGING ->

			graphReader.load(((String) PmParameterManager.getParameter(MNetworkBuildingPa.RESTORE_NETWORK_SOURCE_FILE)),
					network.getJungGraph());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return network;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "MGeoRsRestoreNetworkBuilder";
	}
}