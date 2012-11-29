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
 * Created by Sascha Holzhauer on 06.02.2012
 */
package de.cesr.more.building.util;


import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.log4j.Logger;

import repast.simphony.space.graph.Network;
import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.param.MRandomPa;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.algorithms.util.Indexer;


/**
 * MORe
 * 
 * Based on repast.simphony.context.space.graph.RandomDensityGenerator:
 * 
 * "Generates a random network with a specified approximate density. The network is created by looping over all i, j
 * node pairs and deciding on the existence of a link between the nodes by comparing the value of a probability to a
 * uniform random number. If the boolean allowLoops is false, no self loops (links from i to itself) will be permitted.
 * If the boolean isSymmetric is true, all ties will be bidirectional (i -> j = j -> i). This is what is generally
 * referred to in the network literature as "random" network - a class of networks which have been well studied
 * analytically, but which are structurally quite unlike most empirically observed "social" networks."
 * 
 * Uses {@link MRandomPa.RND_STREAM_RANDOM_NETWORK_BUILDING} instead of RS's RandomHelper.
 * 
 * Uses the {@link MoreLinkProbProvider} if not null. Uses the given density parameter otherwise.
 * 
 * @author Nick Collier
 * @author Sascha Holzhauer
 * @date 06.02.2012
 */
public class MRandomNetworkGenerator<AgentType> {

	/**
	 * Logger
	 */
	static private Logger				logger	= Logger.getLogger(MRandomNetworkGenerator.class);

	private final double				density;
	private final boolean				loops, isSymmetric;
	private BidiMap<AgentType, Integer>	map;

	Uniform								uniform;

	MoreLinkProbProvider<AgentType>		linkProbProvider	= null;

	/**
	 * Creates a random network.
	 * 
	 * @param density
	 *        the approximate density of the network
	 * @param allowSelfLoops
	 *        whether or not self loops are allowed in the created network
	 * @param symmetric
	 *        whether or not ties will be bidirectional in the created network
	 * @param linkProbProvider
	 *        Provider of vertex specific link probabilities
	 */
	public MRandomNetworkGenerator(double density, boolean allowSelfLoops, boolean symmetric,
			MoreLinkProbProvider<AgentType> linkProbProvider) {
		this.loops = allowSelfLoops;
		this.isSymmetric = symmetric;
		this.density = density;
		if (density > 1.0 || density < 0.0) {
			logger.error("Error creating RandomDensityNetworkGenerator",
					new IllegalArgumentException("Density must be between 0 and 1."));
		}

		this.uniform = MManager.getURandomService().getNewUniformDistribution(
				MManager.getURandomService().getGenerator(
						((String) PmParameterManager.getParameter(MRandomPa.RND_STREAM_RANDOM_NETWORK_BUILDING))));
		this.linkProbProvider = linkProbProvider;
	}

	/**
	 * Creates a random network.
	 * 
	 * @param density
	 *        the approximate density of the network
	 * @param allowSelfLoops
	 *        whether or not self loops are allowed in the created network
	 * @param symmetric
	 *        whether or not ties will be bidirectional in the created network
	 */
	public MRandomNetworkGenerator(double density, boolean allowSelfLoops, boolean symmetric) {
		this(density, allowSelfLoops, symmetric, null);
	}

	/**
	 * Add edges to the existing network to create a random density network.
	 * 
	 * @param network
	 *        the network to add edges to
	 * @return the random network
	 */
	public Network<AgentType> createNetwork(Network<AgentType> network) {
		boolean isDirected = network.isDirected();
		init(network);
		if (isDirected) {
			if (loops && isSymmetric) {
				return symmetricLoops(network);
			} else if (!loops && !isSymmetric) {
				return nonSymmetricNoLoops(network);
			} else if (loops) {
				return nonSymmetricLoops(network);
			} else {
				return symmetricNoLoops(network);
			}
		} else {
			if (loops) {
				return nonSymmetricLoops(network);
			} else {
				return nonSymmetricNoLoops(network);
			}
		}
	}

	private Network<AgentType> symmetricLoops(Network<AgentType> network) {
		double nodeDensity;
		for (int i = 0, n = network.size(); i < n; i++) {
			AgentType source = map.getKey(i);
			nodeDensity = linkProbProvider != null ? linkProbProvider.getLinkProb(source) : density;
			for (int j = i; j < n; j++) {
				if (uniform.nextDouble() < nodeDensity) {
					AgentType target = map.getKey(j);
					network.addEdge(source, target);
					network.addEdge(target, source);
				}
			}
		}

		return network;
	}

	private void init(Network<AgentType> network) {
		Set<AgentType> set = new HashSet<AgentType>();
		for (AgentType node : network.getNodes()) {
			set.add(node);
		}
		map = Indexer.create(set);
	}

	private Network<AgentType> nonSymmetricNoLoops(Network<AgentType> network) {
		double nodeDensity;
		for (int i = 0, n = network.size(); i < n; i++) {
			AgentType source = map.getKey(i);
			nodeDensity = linkProbProvider != null ? linkProbProvider.getLinkProb(source) : density;
			for (int j = i + 1; j < n; j++) {
				if (uniform.nextDouble() < nodeDensity) {
					AgentType target = map.getKey(j);
					network.addEdge(source, target);
				}
			}
		}
		return network;
	}

	private Network<AgentType> nonSymmetricLoops(Network<AgentType> network) {
		double nodeDensity;
		for (int i = 0, n = network.size(); i < n; i++) {
			AgentType source = map.getKey(i);
			nodeDensity = linkProbProvider != null ? linkProbProvider.getLinkProb(source) : density;
			for (int j = i; j < n; j++) {
				if (uniform.nextDouble() < nodeDensity) {
					AgentType target = map.getKey(j);
					network.addEdge(source, target);
				}
			}
		}
		return network;
	}

	private Network<AgentType> symmetricNoLoops(Network<AgentType> network) {
		double nodeDensity;
		for (int i = 0, n = network.size(); i < n; i++) {
			AgentType source = map.getKey(i);
			nodeDensity = linkProbProvider != null ? linkProbProvider.getLinkProb(source) : density;
			for (int j = i + 1; j < n; j++) {
				if (uniform.nextDouble() < nodeDensity) {
					AgentType target = map.getKey(j);
					network.addEdge(source, target);
					network.addEdge(target, source);
				}
			}
		}
		return network;
	}
}
