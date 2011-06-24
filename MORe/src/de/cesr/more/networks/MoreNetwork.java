/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 07.01.2010
 */
package de.cesr.more.networks;

import java.util.Collection;
import java.util.NoSuchElementException;

import de.cesr.more.basic.MoreEdge;

import edu.uci.ics.jung.graph.Graph;


/**
 * Specifies demands on network implementations that shall integrate into LARA's decision making modelling.
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
	 * Created by Sascha Holzhauer on 30.03.2011
	 */
	public void addNode(AgentType node);

	/**
	 * Removes a node from the network.
	 * @param node
	 * Created by Sascha Holzhauer on 30.03.2011
	 */
	public void removeNode(AgentType node);
	
	/**
	 * The method is used to obtain a new Sub-MoreNetwork. The parameter is a {@link Graph} instance that contains subsets of
	 * vertices and edges in the original MoreNetwork this method is applied to. For instance, such graphs may be a result
	 * of a {@link VertexPredicateFilter}. The given graph needs to be of corresponding directedness! This (the original)
	 * instance of {@link MoreNetwork} is not altered!
	 * 
	 * @throws NoSuchElementException when the given graph contains a vertex or an edge this network does not contain.
	 * @return A new {@link MoreNetwork} containing only vertices and edges contained in given graph object
	 * 
	 * Created by Sascha Holzhauer on 16.11.2010
	 */
	public MoreNetwork<AgentType, EdgeType> getGraphFilteredInstance(Graph<AgentType, EdgeType> graph, String new_name);
	
	/**
	 * If there is already a connection object between these nodes it is removed and the given one added.
	 * @param source
	 * @param target
	 * Created by Sascha Holzhauer on 15.01.2010
	 * @return the new edge
	 */
	public EdgeType connect(AgentType source, AgentType target);
	
	/**
	 * Adds an edge to this network.
	 * 
	 * @param edge
	 * Created by Sascha Holzhauer on 30.03.2011
	 */
	public void addEdge(EdgeType edge);

	/**
	 * @param source
	 * @param target
	 * Created by Sascha Holzhauer on 15.01.2010
	 */
	public void disconnect(AgentType source, AgentType target);

	/**
	 * @param source
	 * @param target
	 * @param weight
	 *  Created by Sascha Holzhauer on 15.01.2010
	 */
	public void setWeight(AgentType source, AgentType target, double weight);

	/**
	 * Normalises the edges' weights by dividing by the largest weight. 
	 * Created by Sascha Holzhauer on 18.01.2010
	 */
	public void normalizeWeights();
	
	/**
	 * @param source
	 * @param target
	 * @return
	 * Created by Sascha Holzhauer on 18.01.2010
	 */
	public double getWeight(AgentType source, AgentType target);
	
	/**
	 * @param ego
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public Iterable<AgentType> getAdjacent(AgentType ego);

	/**
	 * Return a random neighbour. For directed networks,
	 * this is a successor.
	 * @param ego
	 * @return alter the randomly chosen neighbour
	 */
	public AgentType getRandomSuccessor(AgentType ego);
	
	/**
	 * @param ego
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public Iterable<AgentType> getPredecessors(AgentType ego);

	/**
	 * @param ego
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public Iterable<AgentType> getSuccessors(AgentType ego);

	/**
	 * @param ego
	 * @param alter
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public boolean isAdjacent(AgentType ego, AgentType alter);
	
	/**
	 * Checks whether there is a links from ego to alter.
	 * @param ego
	 * @param alter
	 * @return true, if alter is a successor of ego
	 */
	public boolean isSuccessor(AgentType ego, AgentType alter);

	/**
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public boolean isDirected();

	/**
	 * @param ego
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public int getDegree(AgentType ego);

	/**
	 * @param ego
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public int getInDegree(AgentType ego);

	/**
	 * @param ego
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public int getOutDegree(AgentType ego);

	/**
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public String getName();

	/**
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public Iterable<AgentType> getNodes();

	/**
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public int numEdges();
	
	/**
	 * @param source
	 * @param target
	 * @return
	 * Created by Sascha Holzhauer on 07.10.2010
	 */
	public EdgeType getEdge(AgentType source, AgentType target);

	
	/**
	 * @return collection of all edges
	 * 
	 * Created by Sascha Holzhauer on 07.10.2010
	 */
	public Collection<EdgeType> getEdgesCollection();
	
	/**
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public int numNodes();
	
	/**
	 * Return a JUNG Graph object of this network.
	 * @return a JUNG Graph object of this network
	 * Created by Sascha Holzhauer on 05.10.2010
	 */
	public Graph<AgentType, EdgeType> getJungGraph();
	
	/**
	 * Returns a reversed network, i.e. for all edges source and target vertices are excahnged.
	 * @return reversed network
	 * 
	 * Created by Sascha Holzhauer on 25.01.2011
	 */
	public void reverseNetwork();
}