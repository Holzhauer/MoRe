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
import org.junit.Ignore;
import org.junit.Test;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MUndirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.util.MLattice1DGenerator;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.manipulate.edge.MDefaultNetworkEdgeModifier;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;
import edu.uci.ics.jung.algorithms.generators.GraphGenerator;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 19.03.2012 
 *
 */
public class MLattice1DGeneratorTest {

	static final int	NUM_AGENTS	= 21;
	static final int	K_VALUE		= 8;


	@Test
	public void testBuildingDirected() {

		GraphGenerator<MTestNode, MoreEdge<MTestNode>> networkBuilder = new MLattice1DGenerator<MTestNode, MoreEdge<MTestNode>>(
				new Factory<MoreNetwork<MTestNode, MoreEdge<MTestNode>>>() {
					@Override
					public MoreNetwork<MTestNode, MoreEdge<MTestNode>> create() {
						return new MDirectedNetwork<MTestNode, MoreEdge<MTestNode>>();
					}
				},
				new Factory<MTestNode>() {
					@Override
					public MTestNode create() {
						return new MTestNode();
					}

				},
				new MDefaultNetworkEdgeModifier<MTestNode, MoreEdge<MTestNode>>(new MDefaultEdgeFactory<MTestNode>()),
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

		for (MTestNode node : graph.getVertices()) {
			assertEquals(K_VALUE, graph.inDegree(node));
			assertEquals(K_VALUE, graph.outDegree(node));
		}
	}

	@Test
	public void testBuildingUndirected() {

		GraphGenerator<MTestNode, MoreEdge<MTestNode>> networkBuilder = new MLattice1DGenerator<MTestNode, MoreEdge<MTestNode>>(
				new Factory<MoreNetwork<MTestNode, MoreEdge<MTestNode>>>() {
					@Override
					public MoreNetwork<MTestNode, MoreEdge<MTestNode>> create() {
						return new MUndirectedNetwork<MTestNode, MoreEdge<MTestNode>>();
					}
				},
				new Factory<MTestNode>() {
					@Override
					public MTestNode create() {
						return new MTestNode();
					}

				},
				new MDefaultNetworkEdgeModifier<MTestNode, MoreEdge<MTestNode>>(new MDefaultEdgeFactory<MTestNode>()),
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

		for (MTestNode node : graph.getVertices()) {
			assertEquals(K_VALUE, graph.inDegree(node));
		}
	}

	@Test
	public void testBuildingNoneToroidal() {

		GraphGenerator<MTestNode, MoreEdge<MTestNode>> networkBuilder = new MLattice1DGenerator<MTestNode, MoreEdge<MTestNode>>(
				new Factory<MoreNetwork<MTestNode, MoreEdge<MTestNode>>>() {
					@Override
					public MoreNetwork<MTestNode, MoreEdge<MTestNode>> create() {
						return new MUndirectedNetwork<MTestNode, MoreEdge<MTestNode>>();
					}
				},
				new Factory<MTestNode>() {
					@Override
					public MTestNode create() {
						return new MTestNode();
					}

				},
				new MDefaultNetworkEdgeModifier<MTestNode, MoreEdge<MTestNode>>(new MDefaultEdgeFactory<MTestNode>()),
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
	@Ignore
	// not yet implemented - see note in MLattice1DGenerator
	public void testBuildingVariousKNodes() {

		MoreKValueProvider<MTestNode> kProvider = new MoreKValueProvider<MTestNode>() {
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
		};

		GraphGenerator<MTestNode, MoreEdge<MTestNode>> networkBuilder = new MLattice1DGenerator<MTestNode, MoreEdge<MTestNode>>(
				new Factory<MoreNetwork<MTestNode, MoreEdge<MTestNode>>>() {
					@Override
					public MoreNetwork<MTestNode, MoreEdge<MTestNode>> create() {
						return new MDirectedNetwork<MTestNode, MoreEdge<MTestNode>>();
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
				new MDefaultNetworkEdgeModifier<MTestNode, MoreEdge<MTestNode>>(new MDefaultEdgeFactory<MTestNode>()),
				NUM_AGENTS,
				kProvider,
				true,
				false);

		Graph<MTestNode, MoreEdge<MTestNode>> graph = networkBuilder.create();

		assertEquals(NUM_AGENTS, graph.getVertexCount());
		assertEquals(NUM_AGENTS * K_VALUE / 2, graph.getEdgeCount());

		for (MTestNode node : graph.getVertices()) {
			assertEquals(kProvider.getKValue(node), graph.degree(node));
		}
	}
}
