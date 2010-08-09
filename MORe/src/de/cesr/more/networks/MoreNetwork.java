/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 07.01.2010
 */
package de.cesr.more.networks;


/**
 * Specifies demands on network implementations that shall integrate into LARA's decision making modelling.
 * 
 * @author Sascha Holzhauer
 * @param <AgentType> the common type (of agents) that is contained as nodes in the network
 * @date 07.01.2010
 */
public interface MoreNetwork<AgentType> {

	/**
	 * If there is already a connection object between these nodes it is removed and the given one added.
	 * @param source
	 * @param target
	 * Created by Sascha Holzhauer on 15.01.2010
	 */
	public void connect(AgentType source, AgentType target);

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
	 * @return Created by Sascha Holzhauer on 15.01.2010
	 */
	public int numNodes();
}