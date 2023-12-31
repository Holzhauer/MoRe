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
 * Created by Sascha Holzhauer on 14.11.2011
 */
package de.cesr.more.testing.measures.node;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.measures.node.MVertexSimilarityMeasureCalculator;
import de.cesr.more.testing.testutils.MTestGraphs;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;
import de.cesr.more.util.MSchedule;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 14.11.2011 
 *
 */
public class MVertexSimilarityMeasureCalculatorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MManager.setSchedule(new MSchedule());
	}

	@Test
	public void test() {
		// build up a complete network
		Graph<MTestNode, MoreEdge<MTestNode>> g = MTestGraphs.getCompleteDirectedGraph(5);
		
		Map<MTestNode, Integer> nodes = new LinkedHashMap<MTestNode, Integer>();
		int h = 1;
		for (MTestNode node : g.getVertices()) {
			nodes.put(node, new Integer(h++));
		}

		double[][] similarities = MVertexSimilarityMeasureCalculator.
				getVertexDiceSimilaritiesR(g, "all", nodes);

		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				if (i == j) {
					assertEquals(i + " == " + j + ":", 1.0, similarities[i][j], 0.001);
				} else {
					assertEquals(i + " <> " + j + ":", 6.0 / 8.0, similarities[i][j], 0.001);
				}
			}
		}
		
		similarities = MVertexSimilarityMeasureCalculator.getVertexJaccardSimilaritiesR(g, "all", nodes);
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				if (i == j) {
					assertEquals(i + " == " + j + ":", 1.0, similarities[i][j], 0.001);
				} else {
					assertEquals(i + " <> " + j + ":", 3.0 / 5.0, similarities[i][j], 0.001);
				}
			}
		}
		
		// remove edges between 0 to 1:
		Iterator<MTestNode> iterator = nodes.keySet().iterator();
		MTestNode one = iterator.next();
		MTestNode two = iterator.next();

		g.removeEdge(g.findEdge(one, two));
		g.removeEdge(g.findEdge(two, one));
		
		similarities = MVertexSimilarityMeasureCalculator.getVertexDiceSimilaritiesR(g, "all", nodes);
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = i; j < nodes.size(); j++) {
				if (i == j) {
					assertEquals(i + " == " + j + ":", 1.0, similarities[i][j], 0.001);
				} else if (i == nodes.get(one) - 1) {
					if (j == nodes.get(two) - 1) {
						assertEquals(i + " <> " + j + ":", 6.0 / 6.0, similarities[i][j], 0.001);
					} else {
						// one node is less connected:
						assertEquals(i + " <> " + j + ":", 4.0 / 7.0, similarities[i][j], 0.001);
					}
				} else if (j == (nodes.get(two) - 1) || i == (nodes.get(two) - 1)) {
					// i = 0 covered above...
					assertEquals(i + " <> " + j + ":", 4.0 / 7.0, similarities[i][j], 0.001);
			
				} else {
					// both nodes fully connected:
					assertEquals(i + " <> " + j + ":", 6.0 / 8.0, similarities[i][j], 0.001);
				}
			}
		}
		
		similarities = MVertexSimilarityMeasureCalculator.getVertexJaccardSimilaritiesR(g, "all", nodes);
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = i; j < nodes.size(); j++) {
				if (i == j) {
					assertEquals(i + " == " + j + ":", 1.0, similarities[i][j], 0.001);
				} else if (i == (nodes.get(one) - 1)) {
					if (j == (nodes.get(two) - 1)) {
						assertEquals(3.0 / 3.0, similarities[i][j], 0.001);
					} else {
						assertEquals(2.0 / 5.0, similarities[i][j], 0.001);
					}
				} else if (j == (nodes.get(two) - 1) || i == (nodes.get(two) - 1)) {
					// i = 0 covered above...
					assertEquals(2.0 / 5.0, similarities[i][j], 0.001);
			
				} else {
					assertEquals(3.0 / 5.0, similarities[i][j], 0.001);
				}
			}
		}
	}
}
