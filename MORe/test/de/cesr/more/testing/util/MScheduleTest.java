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
 * Created by Sascha Holzhauer on 10.12.2010
 */
package de.cesr.more.testing.util;



import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.util.MSchedule;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 10.12.2010
 * 
 * TODO test end parameter
 * 
 */
public class MScheduleTest {

	MSchedule			schedule;
	MoreAction			a1, a2;
	MScheduleParameters	p1, p2;

	int					effect	= 0;

	/**
	 * 
	 * Created by Sascha Holzhauer on 10.12.2010
	 */
	public void increaseEffect() {
		effect++;
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 10.12.2010
	 */
	public void decreaseEffect() {
		effect--;
	}

	/**
	 * @throws java.lang.Exception Created by Sascha Holzhauer on 10.12.2010
	 */
	@Before
	public void setUp() throws Exception {
		schedule = new MSchedule();
		a1 = new MoreAction() {
			@Override
			public void execute() {
				increaseEffect();
			}
		};

		a2 = new MoreAction() {
			@Override
			public void execute() {
				decreaseEffect();
				decreaseEffect();
			}
		};

		p1 = MScheduleParameters.getScheduleParameter(1, 2, MScheduleParameters.END_TICK, MScheduleParameters.RANDOM_PRIORITY);
		p2 = MScheduleParameters.getScheduleParameter(3, 1, MScheduleParameters.END_TICK, MScheduleParameters.RANDOM_PRIORITY);
	}

	/**
	 * Test method for {@link de.cesr.more.util.MSchedule#step(int)}.
	 */
	@Test
	public final void testStep() {
		schedule.schedule(p1, a1);
		schedule.schedule(p2, a2);

		schedule.step(1);
		assertEquals( "only a1: 1", 1, effect);

		schedule.step(2);
		assertEquals("no action: 1", 1, effect);

		schedule.step(3);
		assertEquals("a1 & a2: 0", 0, effect);

		schedule.step(4);
		assertEquals("only a2: -2", -2, effect);

		schedule.step(5);
		assertEquals("both: -3", -3, effect);

		schedule.removeAction(a2);

		schedule.step(6);
		assertEquals("none since a2 removed: -3", -3, effect);
	}
	
	/**
	 * Test method for {@link de.cesr.more.util.MSchedule#isScheduled(MoreAction, int)}.
	 * Created by Sascha Holzhauer on 22.12.2010
	 */
	@Test
	public final void testIsScheduled() {
		schedule.schedule(p1, a1);
		schedule.schedule(p2, a2);
		
		assertTrue("Action 1 is scheduled at timestep 1", schedule.isScheduled(a1, 1));
		assertFalse("Action 1 is not scheduled at timestep 2", schedule.isScheduled(a1, 2));
		assertTrue("Action 1 is scheduled at timestep 3", schedule.isScheduled(a1, 3));
		
		assertFalse("Action 2 is not scheduled at timestep 1", schedule.isScheduled(a2, 1));
		assertFalse("Action 2 is not scheduled at timestep 2", schedule.isScheduled(a2, 2));
		assertTrue("Action 2 is scheduled at timestep 3", schedule.isScheduled(a2, 3));
		assertTrue("Action 2 is scheduled at timestep 4", schedule.isScheduled(a2, 4));
		assertTrue("Action 2 is scheduled at timestep 5", schedule.isScheduled(a2, 5));
		
		schedule.removeAction(a1);
		assertFalse("Action 1 is no longer scheduled at timestep 1", schedule.isScheduled(a1, 1));
		assertFalse("Action 1 is not scheduled at timestep 2", schedule.isScheduled(a1, 2));
		assertFalse("Action 1 is no logner scheduled at timestep 3", schedule.isScheduled(a1, 3));
	}
}
