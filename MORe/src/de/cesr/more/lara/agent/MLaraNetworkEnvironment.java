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
 * Created by Sascha Holzhauer on 15.01.2010
 */
package de.cesr.more.lara.agent;



import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;



/**
 * @author Sascha Holzhauer
 * @param <A> the common type (of agents) that is contained as nodes in the networks
 * @param <E> edge type
 * @date 15.01.2010
 */
public class MLaraNetworkEnvironment<A extends MoreLaraNetworkAgent<A, E, ?>, E extends MoreEdge<? super A>> extends LEnvironment {

	MoreNetwork<A, E>	network;

	/**
	 * @see de.cesr.lara.components.environment.impl.LEnvironment#getPropertyByName(java.lang.String)
	 */
	@Override
	public LAbstractEnvironmentalProperty<?> getPropertyByName(String name) {
		return super.getPropertyByName(name);
	}

	/**
	 * @see de.cesr.lara.components.environment.impl.LEnvironment#getTypedPropertyByName(java.lang.String)
	 */
	@Override
	public <ValueType> LAbstractEnvironmentalProperty<ValueType> getTypedPropertyByName(String name) {
		return super.getTypedPropertyByName(name);
	}

}
