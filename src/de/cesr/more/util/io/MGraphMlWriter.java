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
 * Created by Sascha Holzhauer on 25.10.2011
 */
package de.cesr.more.util.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.collections15.Transformer;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.util.MVersionInfo;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLWriter;

/**
 * MORe
 *
 * Added "attr.name" to key definitions (igraph'S read.graphy breaks without > bug reported)
 * 
 * @author Sascha Holzhauer
 * @date 25.10.2011 
 *
 */
public class MGraphMlWriter<AgentType, E extends MoreEdge<? super AgentType>> extends GraphMLWriter<AgentType, E> {
	
	protected String getType(String description) {
		// parse description
		if (description.indexOf("[") >= 0 && (description.indexOf("]") > description.indexOf("["))) {
			return description.substring(description.indexOf("[") + 1, description.indexOf("]"));
		}
		return "string"; 
	}
	
	@Override
	protected void writeKeySpecification(String key, String type, 
			GraphMLMetadata<?> ds, BufferedWriter bw) throws IOException
	{
		// SH: changed the following line:
		bw.write("<key id=\"" + key + "\" for=\"" + type + "\" attr.name=\"" + key + "\" attr.type=\"" + getType(ds.description) + "\"");
		boolean closed = false;
		// write out description if any
		String desc = ds.description.indexOf("]") >= 0 ? ds.description.substring(0, ds.description.indexOf("[")) : ds.description;
		if (desc != null)
		{
			if (!closed)
			{
				bw.write(">\n");
				closed = true;
			}
			bw.write("<desc>" + desc + "</desc>\n");
		}
		// write out default if any
		Object def = ds.default_value;
		if (def != null)
		{
			if (!closed)
			{
				bw.write(">\n");
				closed = true;
			}
			bw.write("<default>" + def.toString() + "</default>\n");
		}
		if (!closed) {
			bw.write("/>\n");
		} else {
			bw.write("</key>\n");
		}
	}
	
	/**
	 * This method is also overridden since it occurs that RS's older JUNG version is loaded
	 * before the referenced Jung-IO library and these versions have an erroneous save-method. 
	 * 
	 * @param graph
	 * @param w
	 * @throws IOException 
	 */
	@Override
	public void save(Hypergraph<AgentType,E> graph, Writer w) throws IOException
	{
		this.save(null, graph, w);
	}

	/**
	 * @param graph
	 * @param w
	 * @throws IOException
	 */
	public void save(MoreNetwork<AgentType, E> network, Writer w) throws IOException {
		this.save(network, network.getJungGraph(), w);
	}

	/**
	 * @param graph
	 * @param w
	 * @throws IOException
	 */
	public void save(MoreNetwork<AgentType, E> network, Hypergraph<AgentType, E> graph, Writer w) throws IOException {
		BufferedWriter bw = new BufferedWriter(w);

		// write out boilerplate header
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		bw.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"\n" +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  \n");
		bw.write("xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml\">\n");
		
		writeNetworkInformation(network, bw);

		// write out data specifiers, including defaults
		for (String key : graph_data.keySet()) {
			writeKeySpecification(key, "graph", graph_data.get(key), bw);
		}
		for (String key : vertex_data.keySet()) {
			writeKeySpecification(key, "node", vertex_data.get(key), bw);
		}
		for (String key : edge_data.keySet()) {
			writeKeySpecification(key, "edge", edge_data.get(key), bw);
		}

		// write out graph-level information
		// set edge default direction
		bw.write("<graph edgedefault=\"");
		directed = !(graph instanceof UndirectedGraph);
        if (directed) {
			bw.write("directed\">\n");
		} else {
			bw.write("undirected\">\n");
		}

        // write graph description, if any
		String desc = graph_desc.transform(graph);
		if (desc != null) {
			bw.write("<desc>" + desc + "</desc>\n");
		}
		
		// write graph data out if any
		for (String key : graph_data.keySet())
		{
			Transformer<Hypergraph<AgentType,E>, ?> t = graph_data.get(key).transformer;
			Object value = t.transform(graph);
			if (value != null) {
				bw.write(format("data", "key", key, value.toString()) + "\n");
			}
		}
        
		// write vertex information
        writeVertexData(graph, bw);
		
		// write edge information
        writeEdgeData(graph, bw);

        // close graph
        bw.write("</graph>\n");
        bw.write("</graphml>\n");
        bw.flush();
        
        bw.close();
	}

	protected void writeNetworkInformation(MoreNetwork<AgentType, E> network, BufferedWriter bw) throws IOException {
		if (network != null) {
			bw.write("<!-- Network Builder: " + network.getNetworkBuilderClass().getName() + " -->\n");
			bw.write("<!-- MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID: "
					+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID) + " -->\n");
			bw.write("<!-- MRandomPa.RANDOM_SEED_NETWORK_BUILDING: "
					+ PmParameterManager.getParameter(MRandomPa.RANDOM_SEED_NETWORK_BUILDING) + " -->\n");
			bw.write("<!-- More Revision Number: " + MVersionInfo.revisionNumber + " -->\n");
			bw.write("<!-- Network Size (Vertices): " + network.numNodes() + " -->\n");
			bw.write("<!-- Network Size (Edges): " + network.numEdges() + " -->\n");
		}
	}

}
