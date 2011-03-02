/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 27.10.2010
 */
package de.cesr.more.testing;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.impl.LGeneralBehaviouralOption;
import de.cesr.more.lara.AbstractLaraNetworkAgent;
import de.cesr.more.rs.adapter.MRepastEdge;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 27.10.2010 
 *
 */
public class TestUtilsLara {

	/**
	 * test network agent
	 */
	public static class TestNetworkAgent extends AbstractLaraNetworkAgent<TestNetworkAgent, LGeneralBehaviouralOption<TestNetworkAgent>, MRepastEdge<TestNetworkAgent>> {

		/**
		 * constructor
		 * @param env
		 * @param name
		 */
		public TestNetworkAgent(LaraEnvironment env, String name) {
			super(env, name);
		}

		/**
		 * @see de.cesr.lara.components.agents.impl.LAbstractAgent#getThis()
		 */
		@Override
		public TestNetworkAgent getThis() {
			return this;
		}

		/**
		 * @see de.cesr.lara.components.agents.LaraAgent#laraPerceive(de.cesr.lara.components.decision.LaraDecisionConfiguration)
		 */
		@Override
		public void laraPerceive(LaraDecisionConfiguration dBuilder) {
			// nothing to do
		}
	}
}
