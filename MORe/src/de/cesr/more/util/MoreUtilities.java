/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.10.2010
 */
package de.cesr.more.util;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import de.cesr.more.networks.MoreNetwork;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLWriter;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 05.10.2010
 * 
 */
public class MoreUtilities {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(MoreUtilities.class);

	/**
	 * TODO handle exception Created by Sascha Holzhauer on 06.05.2010
	 */
	public static <V, E> void outputGraph(final MoreNetwork<V, E> network, File outputFile) {
		long startTimeMillis = 0;
		if (logger.isDebugEnabled()) {
			startTimeMillis = System.currentTimeMillis();
			logger.debug(network.getName() + "> generate output");
		}
		try {
			if (!outputFile.getParentFile().exists()) {
				outputFile.getParentFile().mkdirs();
			}
			FileWriter fileWriter = new FileWriter(outputFile);
			GraphMLWriter<V, E> graphWriter = new GraphMLWriter<V, E>();

			if (logger.isDebugEnabled()) {
				logger.debug(network.getName() + "> save to " + outputFile.getName());
			}

			// add attractiveness data:
			// Map<String, GraphMLMetadata<V>> attractivenessMap = new HashMap<String, GraphMLMetadata<V>>();
			// Transformer<V, String> transformer = new Transformer<V, String>() {
			// public String transform(V agent) {
			// return new Double(((V)agent).getAttractiveness()).toString();
			// }
			// };
			// attractivenessMap.put("attractiveness", new GraphMLMetadata<V>("attractiveness", "-1", transformer));
			// graphWriter.setVertexData(attractivenessMap);

			// add graph id:
			Map<String, GraphMLMetadata<Hypergraph<V, E>>> graphMap = new HashMap<String, GraphMLMetadata<Hypergraph<V, E>>>();
			Transformer<Hypergraph<V, E>, String> gTransformer = new Transformer<Hypergraph<V, E>, String>() {
				public String transform(Hypergraph<V, E> graph) {
					return network.getName();
				}

			};
			graphMap.put("id", new GraphMLMetadata<Hypergraph<V, E>>("id", "-1", gTransformer));
			graphWriter.setGraphData(graphMap);

			graphWriter.save(network.getGraph(), fileWriter);

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(network.getName() + "> generation of output failed (" + outputFile.getName() + ")");
		}
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(network.getName() + "> output graph (taking "
						+ DatatypeFactory.newInstance().newDuration((System.currentTimeMillis() - startTimeMillis))
						+ ")");
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

}
