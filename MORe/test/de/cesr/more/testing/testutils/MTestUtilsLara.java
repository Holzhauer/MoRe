/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 27.10.2010
 */
package de.cesr.more.testing.testutils;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.more.lara.agent.MAbstractLaraNetworkAgent;
import de.cesr.more.rs.edge.MRepastEdge;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 27.10.2010 
 *
 */
public class MTestUtilsLara {

	/**
	 * test network agent
	 */
	public static class MTestNetworkAgent<A extends MTestNetworkAgent<A>> extends MAbstractLaraNetworkAgent<A, LaraBehaviouralOption<?,?>, MRepastEdge<A>> {

		/**
		 * constructor
		 * @param env
		 * @param name
		 */
		public MTestNetworkAgent(LaraEnvironment env, String name) {
			super(env, name);
		}


		/**
		 * @see de.cesr.lara.components.agents.LaraAgent#laraPerceive(de.cesr.lara.components.decision.LaraDecisionConfiguration)
		 */
		@Override
		public void laraPerceive(LaraDecisionConfiguration dBuilder) {
			// nothing to do
		}



		/**
		 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#perceiveNetworks()
		 */
		@Override
		public void perceiveNetworks() {
			// nothing to do
		}


		/**
		 * @see de.cesr.lara.components.agents.impl.LAbstractAgent#getThis()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public A getThis() {
			return (A) this;
		}
	}
}
