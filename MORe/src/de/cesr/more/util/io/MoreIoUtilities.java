/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.10.2010
 */
package de.cesr.more.util.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.util.Log4jLogger;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLWriter;

/**
 * MORe
 * 
 * Utility methods for writing and reading networks
 * 
 * @author Sascha Holzhauer
 * @date 05.10.2010
 * 
 */
public class MoreIoUtilities {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MoreIoUtilities.class);


	/**
	 * Write the given network to the given file in GraphML format.
	 * @param <V> vertex type parameter
	 * @param <E> edge type parameter
	 * @param network the network to write
	 * @param outputFile the file to write the network to
	 */
	public static <V, E extends MoreEdge<? super V>> void outputGraph( final MoreNetwork<V, E> network, File outputFile) {
		outputGraph(network, outputFile, null, null);
	}


	/**
	 * Allowed values for type in meta data: boolean, int, long, float, double, string
	 * 
	 * Write the given network to the given file in GraphML format.
	 * @param <V> vertex type parameter
	 * @param <E> edge type parameter
	 * @param network the network to write
	 * @param outputFile the file to write the network to 
	 * @param vertexMetadata definition of meta-data for vertices
	 * @param edgeMetadata definition of meta-data for edges
	 */
	public static <V, E extends MoreEdge<? super V>> void outputGraph(final MoreNetwork<V, E> network, File outputFile,
			Map<String, GraphMLMetadata<V>> vertexMetadata, Map<String, GraphMLMetadata<E>> edgeMetadata) {
		
		// <- start logging
		long startTimeMillis = 0;
		if (logger.isDebugEnabled()) {
			startTimeMillis = System.currentTimeMillis();
			logger.debug(network.getName() + "> generate output");
		}
		// end logging ->
		
		try {
			if (!outputFile.getParentFile().exists()) {
				outputFile.getParentFile().mkdirs();
			}
			FileWriter fileWriter = new FileWriter(outputFile);
			GraphMLWriter<V, E> graphWriter = new MGraphMlWriter<V, E>();

			// <- start logging
			if (logger.isDebugEnabled()) {
				logger.debug(network.getName() + "> save to "
						+ outputFile.getName());
			}
			// end logging ->

			// add graph id:
			Map<String, GraphMLMetadata<Hypergraph<V, E>>> graphMap = new HashMap<String, GraphMLMetadata<Hypergraph<V, E>>>();
			Transformer<Hypergraph<V, E>, String> gTransformer = new Transformer<Hypergraph<V, E>, String>() {
				@Override
				public String transform(Hypergraph<V, E> graph) {
					return network.getName();
				}

			};
			graphMap.put("id", new GraphMLMetadata<Hypergraph<V, E>>("id",
					"-1", gTransformer));
			graphWriter.setGraphData(graphMap);

			if (vertexMetadata != null) {
				graphWriter.setVertexData(vertexMetadata);
			}
			if (edgeMetadata != null) {
				graphWriter.setEdgeData(edgeMetadata);
			}
			
			graphWriter.save(network.getJungGraph(), fileWriter);

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(network.getName() + "> generation of output failed ("
					+ outputFile.getName() + ")");
		}
		// <- start logging
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(network.getName()
						+ "> output graph (taking "
						+ DatatypeFactory.newInstance().newDuration(
								(System.currentTimeMillis() - startTimeMillis))
						+ ")");
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		}
		// end logging ->
	}

	/**
	 * Reads a GraphML network from the given file into an directed MoreNetwork.
	 * @param <V> vertex type parameter
	 * @param inputfile the file to read the network from
	 * @param nodeFactory the factory used to create new vertex objects
	 * @param name the new name of the network
	 * @return a directed MoreNetwork
	 */
	public static <V> MoreNetwork<V, MoreEdge<V>> inputNetwork(File inputfile,
			Factory<V> nodeFactory, String name) {
		return inputNetwork(inputfile, nodeFactory, new MDefaultEdgeFactory<V>(), name);
	}
	/**
	 * Reads a GraphML network from the given file into an directed MoreNetwork.
	 * @param <V> vertex type parameter
	 * @param <E> edge type parameter
	 * @param inputfile the file to read the network from
	 * @param nodeFactory the factory used to create new vertex objects
	 * @param edgeFactory the factory used to create new edge objects
	 * @param name the new name of the network
	 * @return a directed MoreNetwork
	 */
	public static <V, E extends MoreEdge<V>> MoreNetwork<V, E> inputNetwork(File inputfile,
			Factory<V> nodeFactory, MoreEdgeFactory<V, E> edgeFactory, String name) {

		MoreNetwork<V, E> network = new MDirectedNetwork<V, E>(
				edgeFactory, name);
		MGraphMLReaderWithEdges<Graph<V, E>, V, E> graphReader;
		try {
			graphReader = new MGraphMLReaderWithEdges<Graph<V, E>, V, E>(
					nodeFactory, edgeFactory);
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Load network from file " + inputfile.toString());
			}
			// LOGGING ->

			graphReader.load(new FileReader(inputfile), network.getJungGraph());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return network;
	}
}
