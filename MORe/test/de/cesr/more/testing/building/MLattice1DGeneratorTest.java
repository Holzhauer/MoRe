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
 * Created by Sascha Holzhauer on 19.03.2012
 */
package de.cesr.more.testing.building;


import static org.junit.Assert.assertEquals;

import org.apache.commons.collections15.Factory;
import org.junit.Test;

import de.cesr.more.basic.edge.MEdge;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MLattice1DGenerator;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;
import edu.uci.ics.jung.algorithms.generators.GraphGenerator;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 19.03.2012 
 *
 */
public class MLattice1DGeneratorTest {

	static final int	NUM_AGENTS	= 10;
	static final int	K_VALUE		= 4;


	@Test
	public void testBuildingDirected() {

		GraphGenerator<MTestNode, MoreEdge<MTestNode>> networkBuilder = new MLattice1DGenerator<MTestNode, MoreEdge<MTestNode>>(
				new Factory<Graph<MTestNode, MoreEdge<MTestNode>>>() {
					@Override
					public Graph<MTestNode, MoreEdge<MTestNode>> create() {
						return new DirectedSparseGraph<MTestNode, MoreEdge<MTestNode>>();
					}
				},
				new Factory<MTestNode>() {
					@Override
					public MTestNode create() {
						return new MTestNode();
					}

				},
				new MoreEdgeFactory<MTestNode, MoreEdge<MTestNode>>() {
					@Override
					public MoreEdge<MTestNode> createEdge(MTestNode source, MTestNode target, boolean directed) {
						return new MEdge<MTestNode>(source, target, directed);
					}
				},
				NUM_AGENTS,
				new MoreKValueProvider<MTestNode>() {
					@Override
					public int getKValue(MTestNode node) {
						return K_VALUE;
					}
				},
				true,
				false);

		Graph<MTestNode, MoreEdge<MTestNode>> graph = networkBuilder.create();

		assertEquals(NUM_AGENTS, graph.getVertexCount());
		assertEquals(NUM_AGENTS * K_VALUE, graph.getEdgeCount());
	}

	@Test
	public void testBuildingUndirected() {

		GraphGenerator<MTestNode, MoreEdge<MTestNode>> networkBuilder = new MLattice1DGenerator<MTestNode, MoreEdge<MTestNode>>(
				new Factory<Graph<MTestNode, MoreEdge<MTestNode>>>() {
					@Override
					public Graph<MTestNode, MoreEdge<MTestNode>> create() {
						return new UndirectedSparseGraph<MTestNode, MoreEdge<MTestNode>>();
					}
				},
				new Factory<MTestNode>() {
					@Override
					public MTestNode create() {
						return new MTestNode();
					}

				},
				new MoreEdgeFactory<MTestNode, MoreEdge<MTestNode>>() {
					@Override
					public MoreEdge<MTestNode> createEdge(MTestNode source, MTestNode target, boolean directed) {
						return new MEdge<MTestNode>(source, target, directed);
					}
				},
				NUM_AGENTS,
				new MoreKValueProvider<MTestNode>() {
					@Override
					public int getKValue(MTestNode node) {
						return K_VALUE;
					}
				},
				true,
				false);

		Graph<MTestNode, MoreEdge<MTestNode>> graph = networkBuilder.create();

		assertEquals(NUM_AGENTS, graph.getVertexCount());
		assertEquals(NUM_AGENTS * K_VALUE / 2, graph.getEdgeCount());
	}

	@Test
	public void testBuildingNoneToroidal() {

		GraphGenerator<MTestNode, MoreEdge<MTestNode>> networkBuilder = new MLattice1DGenerator<MTestNode, MoreEdge<MTestNode>>(
				new Factory<Graph<MTestNode, MoreEdge<MTestNode>>>() {
					@Override
					public Graph<MTestNode, MoreEdge<MTestNode>> create() {
						return new UndirectedSparseGraph<MTestNode, MoreEdge<MTestNode>>();
					}
				},
				new Factory<MTestNode>() {
					@Override
					public MTestNode create() {
						return new MTestNode();
					}

				},
				new MoreEdgeFactory<MTestNode, MoreEdge<MTestNode>>() {
					@Override
					public MoreEdge<MTestNode> createEdge(MTestNode source, MTestNode target, boolean directed) {
						return new MEdge<MTestNode>(source, target, directed);
					}
				},
				NUM_AGENTS,
				new MoreKValueProvider<MTestNode>() {
					@Override
					public int getKValue(MTestNode node) {
						return K_VALUE;
					}
				},
				false,
				false);

		Graph<MTestNode, MoreEdge<MTestNode>> graph = networkBuilder.create();

		assertEquals(NUM_AGENTS, graph.getVertexCount());
		assertEquals(NUM_AGENTS * K_VALUE / 2 - ((K_VALUE / 2 + 1) * K_VALUE / 2) / 2, graph.getEdgeCount());
	}

	@Test
	public void testBuildingVariousKNodes() {
		GraphGenerator<MTestNode, MoreEdge<MTestNode>> networkBuilder = new MLattice1DGenerator<MTestNode, MoreEdge<MTestNode>>(
				new Factory<Graph<MTestNode, MoreEdge<MTestNode>>>() {
					@Override
					public Graph<MTestNode, MoreEdge<MTestNode>> create() {
						return new UndirectedSparseGraph<MTestNode, MoreEdge<MTestNode>>();
					}
				},
				new Factory<MTestNode>() {
					int	counter	= 0;

					@Override
					public MTestNode create() {
						counter++;
						if (counter % 2 == 0) {
							return new MTestNode(2);
						} else {
							return new MTestNode(1);
						}
					}

				},
				new MoreEdgeFactory<MTestNode, MoreEdge<MTestNode>>() {
					@Override
					public MoreEdge<MTestNode> createEdge(MTestNode source, MTestNode target, boolean directed) {
						return new MEdge<MTestNode>(source, target, directed);
					}
				},
				NUM_AGENTS,
				new MoreKValueProvider<MTestNode>() {
					@Override
					public int getKValue(MTestNode node) {
						switch (node.getMilieuGroup()) {
							case 1:
								return K_VALUE - 2;
							case 2:
								return K_VALUE + 2;
							default:
								return K_VALUE;
						}
					}
				},
				true,
				false);

		Graph<MTestNode, MoreEdge<MTestNode>> graph = networkBuilder.create();

		assertEquals(NUM_AGENTS, graph.getVertexCount());
		assertEquals(NUM_AGENTS * K_VALUE / 2, graph.getEdgeCount());
	}
}
