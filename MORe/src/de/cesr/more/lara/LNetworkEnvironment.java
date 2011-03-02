/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 15.01.2010
 */
package de.cesr.more.lara;



import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty;
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
