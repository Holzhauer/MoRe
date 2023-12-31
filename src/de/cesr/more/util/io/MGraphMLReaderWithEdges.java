/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.10.2010
 */
package de.cesr.more.util.io;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.Factory;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;

import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.util.Log4jLogger;

import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.io.GraphMLReader;

/**
 * MORe
 * Creates a new MoreNetwork using the factories. For edges, source and target informations is
 * derived from the GraphML file.
 * 
 * @author Sascha Holzhauer
 * @date 06.10.2010 
 *
 * @param <G> Graph Type
 * @param <V> Vertex Type
 * @param <E> Edge Type
 */
public class MGraphMLReaderWithEdges<G extends Hypergraph<V, E>, V, E> extends GraphMLReader<G, V, E> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MGraphMLReader2NodeMap.class);
	
	/**
	 * An {@link MoreEdgeFactory} that is used to create edge objects
	 */
	protected MoreEdgeFactory<V, E> edgeFactory;
	
	/**
	 * @param vertex_factory
	 * @param edge_factory
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public MGraphMLReaderWithEdges(Factory<V> vertex_factory,
			MoreEdgeFactory<V, E> edge_factory)
        throws ParserConfigurationException, SAXException {
		super(vertex_factory, null);
		this.edgeFactory = edge_factory;
    }

	/**
	 * Passes contents of {@link this#nodeMap} to {@link super#vertex_ids}.
	 * super{@link #clearData()} is called after super{@link #initializeData()}...
	 * @see edu.uci.ics.jung.io.GraphMLReader#clearData()
	 */
	@Override
	protected void clearData() {
		super.clearData();
	}
	
	/**
	 * Fetches information about source, target and direction from attributes to enable initialisation of
	 * and extended edge object.
	 * @see edu.uci.ics.jung.io.GraphMLReader#createEdge(org.xml.sax.Attributes, edu.uci.ics.jung.io.GraphMLReader.TagState)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void createEdge(Attributes atts, TagState state)
		throws SAXNotSupportedException
	    {
	        Map<String,String> edge_atts = getAttributeMap(atts);

	        String id = edge_atts.get("id");
	        E e;
	        
	        String source_id = edge_atts.get("source");
	        if (source_id == null)
	            throw new SAXNotSupportedException("edge attribute list missing " +
	            		"'source': " + atts.toString());
	        V source = vertex_ids.getKey(source_id);
	        if (source == null)
	            throw new SAXNotSupportedException("specified 'source' attribute " +
	            		"\"" + source_id + "\" does not match any node ID");

	        String target_id = edge_atts.get("target");
	        if (target_id == null)
	            throw new SAXNotSupportedException("edge attribute list missing " +
	            		"'target': " + atts.toString());
	        V target = vertex_ids.getKey(target_id);
	        if (target == null)
	            throw new SAXNotSupportedException("specified 'target' attribute " +
	            		"\"" + target_id + "\" does not match any node ID");

	        // directedness:
	        // TODO check if working with EdgeType is more appropriate!
	        boolean isDirected;
	        String directed = edge_atts.remove("directed");
	        if (directed == null)
	        	isDirected = false;
	        else if (directed.equals("true"))
	        	isDirected = true;
	        else if (directed.equals("false"))
	        	isDirected = false;
	        else
	            throw new SAXNotSupportedException("Unrecognized edge direction specifier 'direction=\"" +
	            		directed + "\"': " + "source: " + source_id + ", target: " + target_id);
	        
	        if (edgeFactory != null)
	        	e = edgeFactory.createEdge(source, target, isDirected);
	        else
	            if (id != null)
	                e = (E)id;
	            else
	                throw new IllegalArgumentException("If no edge factory is supplied, " +
	                		"edge id may not be null: " + edge_atts);

	        if (id != null)
	        {
	            if (edge_ids.containsKey(e))
	                throw new SAXNotSupportedException("Edge id \"" + id +
	                		"\" is a duplicate of an existing edge ID");
	            edge_ids.put(e, id);
	        }
	        
	        // <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Created Edge: " + e + " between " + source + " and " + target);
			}
			// LOGGING ->

	        if (state == TagState.EDGE)
	        	assignEdgeSourceTarget(e, atts, edge_atts); //, id);

	        // put remaining attribute/value pairs in edge_data
	        addExtraData(edge_atts, edge_metadata, e);

	        this.current_edge = e;
	}
	
    /**
     * Adapted to this application when vertex_ids is provided beforehand.
     * @see edu.uci.ics.jung.io.GraphMLReader#createVertex(org.xml.sax.Attributes)
     */
    @Override
	@SuppressWarnings("unchecked")
	protected void createVertex(Attributes atts) throws SAXNotSupportedException
    {
        Map<String, String> vertex_atts = getAttributeMap(atts);
        String id = vertex_atts.remove("id");
        if (id == null)
            throw new SAXNotSupportedException("node attribute list missing " +
            		"'id': " + atts.toString());
        V v = vertex_ids.getKey(id);
        
        // <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("ID: " + id);
		}
		// LOGGING ->


        if (v == null)
        {
        	if (vertex_factory != null)
        		v = vertex_factory.create();
        	else
        		v = (V)id;
            vertex_ids.put(v, id);
            this.current_graph.addVertex(v);

            // put remaining attribute/value pairs in vertex_data
           	addExtraData(vertex_atts, vertex_metadata, v);
        }
        // error message (else case: duplicate node id) deleted

        this.current_vertex = v;
    }
}
