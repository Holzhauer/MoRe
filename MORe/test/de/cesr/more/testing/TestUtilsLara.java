/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 27.10.2010
 */
package de.cesr.more.testing;

import de.cesr.lara.components.LGeneralBehaviouralOption;
import de.cesr.lara.components.LaraDecisionBuilder;
import de.cesr.lara.components.LaraEnvironment;
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
		 * @see de.cesr.lara.components.impl.AbstractLaraAgent#getThis()
		 */
		@Override
		public TestNetworkAgent getThis() {
			return this;
		}

		/**
		 * @see de.cesr.lara.components.LaraAgent#laraPerceive(de.cesr.lara.components.LaraDecisionBuilder)
		 */
		@Override
		public void laraPerceive(LaraDecisionBuilder dBuilder) {
			// nothing to do
		}
	}
}
