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
 * Created by Sascha Holzhauer on 04.11.2011
 */
package de.cesr.more.testing.basic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MEdge;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.edge.MoreTraceableEdge;
import de.cesr.more.param.MNetManipulatePa;
import de.cesr.more.util.MSchedule;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 04.11.2011 
 *
 */
public class MEdgeTest {

	ArrayList<MoreTraceableEdge<Object>> edges = new ArrayList<MoreTraceableEdge<Object>>();
	MSchedule	schedule;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		schedule = new MSchedule();
		MManager.setSchedule(schedule);
		edges.add(new MEdge<Object>(new Object(), new Object()));
		edges.add(new MEdge<Object>(new Object(), new Object()));
		edges.add(new MEdge<Object>(new Object(), new Object()));
	}

	/**
	 * Test method for {@link de.cesr.more.basic.edge.MEdge#activate()}.
	 */
	@Test
	public void testActivate() {
		assertEquals(0, checkActivatedEdges());
		schedule.step(1);
		assertEquals(0, checkActivatedEdges());
		edges.get(0).activate();
		assertEquals(1, checkActivatedEdges());
		edges.get(1).activate();
		assertEquals(2, checkActivatedEdges());
		edges.get(0).activate();
		assertEquals(2, checkActivatedEdges());
		edges.get(2).activate();
		assertEquals(3, checkActivatedEdges());
		schedule.step(2);
		assertEquals(0, checkActivatedEdges());
	}
	
	/**
	 * Test method for {@link de.cesr.more.basic.edge.MEdge#fadeWeight()}.
	 */
	@Test
	public void testfadingWeight() {
		PmParameterManager.setParameter(MNetManipulatePa.DYN_FADE_OUT_AMOUNT, 0.1);
		MoreEdge<Object> edge = new MEdge<Object>(new Object(), new Object());
		edge.setWeight(1.0);
		assertEquals(1.0, edge.getWeight(), 0.001);
		schedule.step(1);
		assertEquals(0.9, edge.getWeight(), 0.001);
		schedule.step(1);
		assertEquals(0.8, edge.getWeight(), 0.001);
		schedule.step(1);
		assertEquals(0.7, edge.getWeight(), 0.001);
	}

	public int checkActivatedEdges() {
		int counter = 0;
		for (MoreTraceableEdge<Object> edge : edges) {
			if (edge.isActive()) {
				counter++;
			}
		}
		return counter;
	}
}
