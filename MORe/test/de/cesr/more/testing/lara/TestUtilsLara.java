/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 27.10.2010
 */
package de.cesr.more.testing.lara;

import de.cesr.lara.components.LaraDecisionBuilder;
import de.cesr.lara.components.LaraEnvironment;
import de.cesr.more.lara.AbstractLaraNetworkAgent;

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
	public static class TestNetworkAgent extends AbstractLaraNetworkAgent {

		/**
		 * constructor
		 * @param env
		 * @param name
		 */
		public TestNetworkAgent(LaraEnvironment env, String name) {
			super(env, name);
		}

		@Override
		public void perceive(LaraDecisionBuilder dBuilder) {
		}
	}
}
