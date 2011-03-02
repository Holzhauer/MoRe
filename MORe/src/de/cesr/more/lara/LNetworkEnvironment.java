/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 15.01.2010
 */
package de.cesr.more.lara;



import de.cesr.lara.components.impl.environment.LEnvironment;
import de.cesr.lara.components.impl.environment.LaraEnvironmentalProperty;
import de.cesr.more.networks.MoreNetwork;



/**
 * @author Sascha Holzhauer
 * @param <A> the common type (of agents) that is contained as nodes in the networks
 * @param <E> edge type
 * @date 15.01.2010
 */
public class LNetworkEnvironment<A extends LaraSimpleNetworkAgent<A, ?, E>, E> extends LEnvironment {

	MoreNetwork<A, E>	network;

	/**
	 * @see de.cesr.lara.components.impl.environment.LEnvironment#getPropertyByName(java.lang.String)
	 */
	@Override
	public LaraEnvironmentalProperty<?> getPropertyByName(String name) {
		return super.getPropertyByName(name);
	}

	/**
	 * @see de.cesr.lara.components.impl.environment.LEnvironment#getTypedPropertyByName(java.lang.String)
	 */
	@Override
	public <ValueType> LaraEnvironmentalProperty<ValueType> getTypedPropertyByName(String name) {
		return super.getTypedPropertyByName(name);
	}

}
