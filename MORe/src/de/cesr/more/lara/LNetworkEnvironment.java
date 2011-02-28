/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 15.01.2010
 */
package de.cesr.more.lara;



import de.cesr.lara.components.LaraAgent;
import de.cesr.lara.components.impl.environment.AbstractEnvironmentalProperty;
import de.cesr.lara.components.impl.environment.LEnvironment;
import de.cesr.more.lara.util.LNetworkAnalysis;
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
	 * @see de.cesr.lara.components.impl.environment.LEnvironment#getPropertyByName(java.lang.String,
	 *      de.cesr.lara.components.LaraAgent)
	 */
	@Override
	public AbstractEnvironmentalProperty<?> getPropertyByName(String name, LaraAgent agent) {
		// TODO Auto-generated method stub
		return super.getPropertyByName(name, agent);
	}

	/**
	 * @see de.cesr.lara.components.impl.environment.LEnvironment#getPropertyByName(java.lang.String)
	 */
	@Override
	public AbstractEnvironmentalProperty<?> getPropertyByName(String name) {
		// TODO Auto-generated method stub
		return super.getPropertyByName(name);
	}

	/**
	 * @see de.cesr.lara.components.impl.environment.LEnvironment#getTypedPropertyByName(java.lang.String,
	 *      de.cesr.lara.components.LaraAgent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <ValueType> AbstractEnvironmentalProperty<ValueType> getTypedPropertyByName(String name,
			LaraAgent agent) {
		// TODO check for LaraSimpleNetworkAgent!
		AbstractEnvironmentalProperty<ComboundNetworkInfo> property = super.getTypedPropertyByName(name, agent);
		return (AbstractEnvironmentalProperty<ValueType>) LNetworkAnalysis.<A, E> getCompoundValue(network,
				(A) agent, property.getValue());
	}

	/**
	 * @see de.cesr.lara.components.impl.environment.LEnvironment#getTypedPropertyByName(java.lang.String)
	 */
	@Override
	public <ValueType> AbstractEnvironmentalProperty<ValueType> getTypedPropertyByName(String name) {
		// TODO Auto-generated method stub
		return super.getTypedPropertyByName(name);
	}

}
