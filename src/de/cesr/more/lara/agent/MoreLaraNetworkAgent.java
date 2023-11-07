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
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara.agent;


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.more.basic.agent.MoreAgentNetworkComp;
import de.cesr.more.basic.agent.MoreNetworkAgent;
import de.cesr.more.basic.edge.MoreEdge;



/**
 * Interface for agents with network support that allows to directly call the {@link LaraAgentComponent} and
 * {@link MoreAgentNetworkComp} methods at the agent.
 * 
 * @author Sascha Holzhauer
 * @param <A> the common type (of agents) that is contained as nodes in the networks this agent refers to
 * @param <E> edge type
 * @param <BO> behavioural option type
 * @date 19.01.2010
 * 
 * TODO rename (/Lara)
 */
public interface MoreLaraNetworkAgent<A extends LaraAgent<A, BO>, E extends MoreEdge<? super A>, BO extends 
	LaraBehaviouralOption<?, ? extends BO>> extends LaraAgent<A, BO>, MoreAgentNetworkComp<A, E>,
	 MoreNetworkAgent<A, E>{
}
