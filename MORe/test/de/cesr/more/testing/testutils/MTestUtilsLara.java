/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 27.10.2010
 */
package de.cesr.more.testing.testutils;


import java.util.HashMap;
import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.impl.LPrefEntry;
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
	public static class MTestNetworkAgent<A extends MTestNetworkAgent<A>> extends
			MAbstractLaraNetworkAgent<A, MTestBo<A>, MRepastEdge<A>> {

		/**
		 * constructor
		 * @param env
		 * @param name
		 */
		public MTestNetworkAgent(LaraEnvironment env, String name) {
			super(env, name);
		}

		/**
		 * @see de.cesr.lara.components.agents.impl.LAbstractAgent#getThis()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public A getThis() {
			return (A) this;
		}

		/**
		 * @see de.cesr.lara.components.eventbus.LaraEventSubscriber#onEvent(de.cesr.lara.components.eventbus.events.LaraEvent)
		 */
		@Override
		public <T extends LaraEvent> void onEvent(T event) {
		}
	}

	/**
	 * Test behavioural option for MTestNetworkAgent
	 * 
	 * @author Sascha Holzhauer
	 * 
	 */
	public static class MTestBo<A extends MTestNetworkAgent<A>> extends LaraBehaviouralOption<A, MTestBo<A>> {

		static int	counter	= 0;

		public MTestBo(A agent) {
			super("LTestBo"
					+ LModel.getModel().getIntegerFormat().format(counter++),
					agent);
		}

		public MTestBo(A agent,
				Map<Class<? extends LaraPreference>, Double> utilities) {
			super("LTestBo"
					+ LModel.getModel().getIntegerFormat().format(counter++),
					agent, utilities);
		}

		public MTestBo(String key, A agent,
				Map<Class<? extends LaraPreference>, Double> utilities) {
			super(key, agent, utilities);
		}

		/**
		 * @param key
		 * @param agent
		 * @param prefEntry
		 */
		public MTestBo(String key, A agent, LPrefEntry... prefEntry) {
			super(key, agent, prefEntry);
		}

		@Override
		public Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(
				LaraDecisionConfiguration dBuilder) {
			return new HashMap<Class<? extends LaraPreference>, Double>();
		}

		@Override
		public MTestBo<A> getModifiedBO(A agent,
				Map<Class<? extends LaraPreference>, Double> preferenceUtilities) {
			return new MTestBo<A>(this.getKey(), agent, preferenceUtilities);
		}
	}
}
