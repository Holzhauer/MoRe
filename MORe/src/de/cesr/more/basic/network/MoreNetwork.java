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
 */
package de.cesr.more.basic.network;

import java.util.Collection;
import java.util.NoSuchElementException;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MoreNetworkBuilder;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.graph.Graph;


/**
 * Specifies demands on network implementations.
 * 
 * @author Sascha Holzhauer
 * @param <AgentType> the common type (of agents) that is contained as nodes in the network
 * @param <EdgeType> the edge type
 * @date 07.01.2010
 */
public interface MoreNetwork<AgentType, EdgeType extends MoreEdge<? super AgentType>> {
	

	/**
	 * Add a node to the network.
	 * @param node
	 */
	public void addNode(AgentType node);

	/**
	 * Removes a node and all its edges from the network.
	 * @param node
	 */
	public void removeNode(AgentType node);
	
	/**
	 * Return true if the given node is contained within this network
	 * @param node
	 * @return true if the network contains the given node
	 */
	public boolean containsNode(AgentType node);
	
	/**
	 * The method is used to obtain a new Sub-MoreNetwork. The parameter is a {@link Graph} instance that contains subsets of
	 * vertices and edges in the original MoreNetwork this method is applied to. For instance, such graphs may be a result
	 * of a {@link VertexPredicateFilter}. The given graph needs to be of corresponding directedness! This (the original)
	 * instance of {@link MoreNetwork} is not altered!
	 * 
	 * @throws NoSuchElementException when the given graph contains a vertex or an edge this network does not contain.
	 * @return A new {@link MoreNetwork} containing only vertices and edges contained in given graph object
	 */
	public MoreNetwork<AgentType, EdgeType> getGraphFilteredInstance(Graph<AgentType, EdgeType> graph, String new_name);
	
	/**
	 * If there is already a connection object between these nodes it is removed and the given one added.
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	public EdgeType connect(AgentType source, AgentType target);
	
	/**
	 * Adds an edge to this network.
	 * Implementing classes are required to use the edge factory.
	 * 
	 * @param edge to add to the network
	 */
	public void connect(EdgeType edge);

	/**
	 * @param source
	 * @param target
	 */
	public EdgeType disconnect(AgentType source, AgentType target);

	/**
	 * @param source
	 * @param target
	 * @param weight
	 */
	public void setWeight(AgentType source, AgentType target, double weight);

	/**
	 * Normalises the edges' weights by dividing by the largest weight. 
	 */
	public void normalizeWeights();
	
	/**
	 * @param source
	 * @param target
	 * @return the weight of the edge between source and target node
	 */
	public double getWeight(AgentType source, AgentType target);
	
	/**
	 * @param ego
	 * @return iterable collection of all adjacent nodes (predecessors and successors) to the given node
	 */
	public Iterable<AgentType> getAdjacent(AgentType ego);

	/**
	 * Return a random neighbour. For directed networks,
	 * this is a successor.
	 * @param ego
	 * @return alter the randomly chosen successor
	 */
	public AgentType getRandomSuccessor(AgentType ego);
	
	/**
	 * Return a random neighbour. For directed networks,
	 * this is a predecessor.
	 * @param ego
	 * @return alter the randomly chosen predecessor
	 */
	public AgentType getRandomPredecessor(AgentType ego);
	
	/**
	 * @param ego
	 * @return iterable collection of all predecessors of the given node
	 */
	public Iterable<AgentType> getPredecessors(AgentType ego);

	/**
	 * @param ego
	 * @return iterable collection of all successors from the given node
	 */
	public Iterable<AgentType> getSuccessors(AgentType ego);

	/**
	 * @param ego
	 * @param alter
	 * @return true if the given nodes are adjacent to each other
	 */
	public boolean isAdjacent(AgentType ego, AgentType alter);
	
	/**
	 * 
	 * Checks whether there is a link from ego to alter.
	 * @param ego
	 * @param alter
	 * @return true, if alter (1st node) is a successor of ego (2nd node)
	 */
	public boolean isSuccessor(AgentType alter, AgentType ego);

	/**
	 * @return true if this network is directed
	 */
	public boolean isDirected();

	/**
	 * @param ego
	 * @return the number of in- and outgoing edges (degree)
	 */
	public int getDegree(AgentType ego);

	/**
	 * @param ego
	 * @return the number of incoming edges (in-degree)
	 */
	public int getInDegree(AgentType ego);

	/**
	 * @param ego
	 * @return the number of outgoing edges (out-degree)
	 */
	public int getOutDegree(AgentType ego);

	/**
	 * @return the network's name
	 */
	public String getName();

	/**
	 * @return the collection of all nodes
	 */
	public Iterable<AgentType> getNodes();

	/**
	 * @return the number of edges in this network
	 */
	public int numEdges();
	
	/**
	 * @param source
	 * @param target
	 * @return
	 */
	public EdgeType getEdge(AgentType source, AgentType target);

	
	/**
	 * @return collection of all edges
	 */
	public Collection<EdgeType> getEdgesCollection();
	
	/**
	 * @return the number of nodes in this network
	 */
	public int numNodes();
	
	/**
	 * Return a JUNG Graph object of this network.
	 * @return a JUNG Graph object of this network
	 */
	public Graph<AgentType, EdgeType> getJungGraph();
	
	/**
	 * Returns a reversed network, i.e. for all edges source and target vertices are exchanged.
	 * @return reversed network
	 */
	public void reverseNetwork();
	
	public Class<? extends MoreNetworkBuilder<?, ?>> getNetworkBuilderClass();

	public void setNetworkBuilderClass(Class<? extends MoreNetworkBuilder<?, ?>> builderClass);

	/**
	 * Sets the {@link MoreEdgeFactory} that is used to connect agents.
	 * @param edgeFac
	 */
	public void setEdgeFactory(MoreEdgeFactory<AgentType, EdgeType> edgeFac);
}