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
 * Created by holzhauer on 24.08.2011
 */
package de.cesr.more.building.util;


import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import edu.uci.ics.jung.algorithms.util.Indexer;


/**
 * MORe
 * 
 * Based on repast.simphony.context.space.graph.Lattice2DGenerator<T> Exchanged HashSet by LinkedHashSet that preserves
 * the order of node elements, which is crucial for building up a lattice.
 * 
 * @author holzhauer
 * @date 24.08.2011
 * 
 */
public class MLattice2DGenerator<T, E extends MoreEdge<T>> {

	protected boolean	isToroidal;
	protected int		latticeSize;

	/**
	 * @param @param isToroidal whether lattice wraps or not.
	 */
	public MLattice2DGenerator(boolean isToroidal) {
		this.isToroidal = isToroidal;
	}

	/**
	 * Given an existing network, add edges to create a 2D lattice. The lattice dimension n is the square root of the
	 * number of nodes in the specified network. The resulting lattice will be nxn.
	 * 
	 * @param network
	 *        the network to rewire
	 * @return the created network
	 */
	public MoreNetwork<T, E> createNetwork(MoreNetwork<T, E> network, MoreNetworkEdgeModifier<T, E> edgeModifier) {
		latticeSize = (int) Math.floor(Math.sqrt(network.numNodes()));

		if (network.numNodes() != latticeSize * latticeSize) {
			throw new IllegalArgumentException("Number of nodes must be a square number (but is " + network.numNodes()
					+ ")");
		}

		Set<T> set = new LinkedHashSet<T>();
		for (T node : network.getNodes()) {
			set.add(node);
		}

		int currentLatticeRow = 0, currentLatticeColumn = 0;
		int upIndex = 0, downIndex = 0, leftIndex = 0, rightIndex = 0;

		BidiMap<T, Integer> map = Indexer.create(set);

		int numNodes = network.numNodes();
		boolean isDirected = network.isDirected();

		for (int i = 0; i < numNodes; i++) {
			currentLatticeRow = i / latticeSize;
			currentLatticeColumn = i % latticeSize;

			upIndex = upIndex(currentLatticeRow, currentLatticeColumn);
			leftIndex = leftIndex(currentLatticeRow, currentLatticeColumn);
			downIndex = downIndex(currentLatticeRow, currentLatticeColumn);
			rightIndex = rightIndex(currentLatticeRow, currentLatticeColumn);

			// Add short range connections
			if (currentLatticeRow != 0 || (currentLatticeRow == 0 && isToroidal)) {
				T source = map.getKey(i);
				T target = map.getKey(upIndex);
				if (isDirected)
					edgeModifier.createEdge(network, source, target);
				else if (!network.isAdjacent(source, target))
					edgeModifier.createEdge(network, source, target);
			}

			if (currentLatticeColumn != 0 || (currentLatticeColumn == 0 && isToroidal)) {
				T source = map.getKey(i);
				T target = map.getKey(leftIndex);
				if (isDirected)
					edgeModifier.createEdge(network, source, target);
				else if (!network.isAdjacent(source, target))
					edgeModifier.createEdge(network, source, target);
			}

			if (currentLatticeRow != latticeSize - 1 || (currentLatticeRow == latticeSize - 1 && isToroidal)) {
				T source = map.getKey(i);
				T target = map.getKey(downIndex);
				if (isDirected)
					edgeModifier.createEdge(network, source, target);
				else if (!network.isAdjacent(source, target))
					edgeModifier.createEdge(network, source, target);
			}

			if (currentLatticeColumn != latticeSize - 1 ||
					(currentLatticeColumn == latticeSize - 1 && isToroidal)) {
				T source = map.getKey(i);
				T target = map.getKey(rightIndex);
				if (isDirected)
					edgeModifier.createEdge(network, source, target);
				else if (!network.isAdjacent(source, target))
					edgeModifier.createEdge(network, source, target);
			}
		}

		return network;
	}

	protected int upIndex(int currentLatticeRow, int currentLatticeColumn) {
		if (currentLatticeRow == 0) {
			return latticeSize * (latticeSize - 1) + currentLatticeColumn;
		} else {
			return (currentLatticeRow - 1) * latticeSize
					+ currentLatticeColumn;
		}
	}

	protected int downIndex(int currentLatticeRow, int currentLatticeColumn) {
		if (currentLatticeRow == latticeSize - 1) {
			return currentLatticeColumn;
		} else {
			return (currentLatticeRow + 1) * latticeSize
					+ currentLatticeColumn;
		}
	}

	protected int leftIndex(int currentLatticeRow, int currentLatticeColumn) {
		if (currentLatticeColumn == 0) {
			return currentLatticeRow * latticeSize + latticeSize - 1;
		} else {
			return currentLatticeRow * latticeSize + currentLatticeColumn - 1;
		}
	}

	protected int rightIndex(int currentLatticeRow, int currentLatticeColumn) {
		if (currentLatticeColumn == latticeSize - 1) {
			return currentLatticeRow * latticeSize;
		} else {
			return currentLatticeRow * latticeSize + currentLatticeColumn + 1;
		}
	}
}
