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
 * Created by holzhauer on 31.10.2011
 */
package de.cesr.more.param;


import de.cesr.more.rs.building.MGeoRsHomophilyDistanceFfNetworkService;
import de.cesr.parma.core.PmParameterDefinition;


/**
 * Parameter definitions for the Homophily Distance Fire Forest Network Service. See
 * {@link MGeoRsHomophilyDistanceFfNetworkService}.
 * 
 * Default parameter values are based on [1]
 * 
 * [1] Holzhauer, Sascha. Dynamic Social Networks in Agent-based Modelling. Unpublished dissertation.
 * 
 * @author holzhauer
 * @date 21.06.2013
 * 
 */
public enum MNetBuildHdffPa implements PmParameterDefinition {

	/**
	 * Class that provides a degree distribution. Default: <code>NegativeBinomial.class</code>. Values are normally
	 * taken from {@link MMilieuNetworkParameterMap}. However, this parameter definition is required to access values in
	 * the map.
	 */
	K_DISTRIBUTION_CLASS(String.class, "de.cesr.more.util.distributions.MPascalDistribution"),

	/**
	 * First (A) parameter of distribution for degree. Default: <code>3.11</code> (empirical value of r of the total
	 * network, see [1]). Values are normally taken from {@link MMilieuNetworkParameterMap}. However, this parameter
	 * definition is required to access values in the map.
	 */
	K_PARAM_A(Double.class, new Double(3.11)),

	/**
	 * Second (B) parameter of distribution for degree. Default: <code>0.17</code> (empirical value of r of the total
	 * network, see [1]). Values are normally taken from {@link MMilieuNetworkParameterMap}, However, this parameter
	 * definition is required to access values in the map.
	 */
	K_PARAM_B(Double.class, new Double(0.17)),

	/**
	 * Class that provides a distance distribution. Default:
	 * <code>org.apache.commons.math3.distribution.WeibullDistribution</code>. Values are normally taken from
	 * {@link MMilieuNetworkParameterMap}. However, this parameter definition is required to access values in the map.
	 */
	DIST_DISTRIBUTION_CLASS(String.class, "de.cesr.more.util.distributions.MWeibullDistanceDistribution"),

	/**
	 * First (A) parameter of distribution for distances. Default: <code>1.02</code> (empirical value of k of the total
	 * network, see [1]). Values are normally taken from {@link MMilieuNetworkParameterMap}. However, this parameter
	 * definition is required to access values in the map.
	 */
	DIST_PARAM_A(Double.class, new Double(1.02)),

	/**
	 * Second (B) parameter of distribution for distance. Default: <code>174.24</code> (empirical value of r of the
	 * total network, see [1]). Values are normally taken from {@link MMilieuNetworkParameterMap}, However, this
	 * parameter definition is required to access values in the map.
	 */
	DIST_PARAM_B(Double.class, new Double(174.24)),

	/**
	 * XMin parameter of distribution for distance in KM. Default: <code>20.00</code> (empirical value of r of the total
	 * network, see [1]). Values are normally taken from {@link MMilieuNetworkParameterMap}, However, this parameter
	 * definition is required to access values in the map.
	 */
	DIST_PARAM_XMIN(Double.class, new Double(20.00)),

	/**
	 * PLocal parameter of distribution for distance. Default: <code>0.70</code> (empirical value of r of the total
	 * network, see [1]). Values are normally taken from {@link MMilieuNetworkParameterMap}, However, this parameter
	 * definition is required to access values in the map.
	 */
	DIST_PARAM_PLOCAL(Double.class, new Double(0.70)),

	/**
	 * Max. radius to potential partner agents that are considered as ambassadors in meters. This is sometimes useful to
	 * reduce computational effort since it reduces the collections that need to be initialised. Default:
	 * <code>{@link Double#MAX_VALUE}</code>.
	 * 
	 * Values are normally taken from {@link MMilieuNetworkParameterMap} but this parameter definition is required to
	 * access values in the map.
	 */
	MAX_SEARCH_RADIUS(Double.class, new Double(Double.MAX_VALUE)),

	/**
	 * Determines the probability of establishing links from partners of the focal agent (e.g. ambassador) (and
	 * recursively). Default: <code>0.2</code>. The values is multiplied by distance and milieu related probabilities
	 * (see {@link MNetBuildHdffPa#DIM_WEIGHTS_GEO} and {@link MNetBuildHdffPa#DIM_WEIGHTS_MILIEU}).
	 * 
	 * For social influence, this is more important than {@link this#PROB_FORWARD} because it is regarded for incoming
	 * links of the focal agent (i.e. the focal agents decides about its social influence).
	 */
	PROB_BACKWARD(Double.class, new Double(0.2)),

	/**
	 * Determines the probability of establishing links to partners of the ambassador (and recursively). Default:
	 * <code>0.0</code>. The values is multiplied by distance and milieu related probabilities (see
	 * {@link MNetBuildHdffPa#DIM_WEIGHTS_GEO} and {@link MNetBuildHdffPa#DIM_WEIGHTS_MILIEU}).
	 */
	PROB_FORWARD(Double.class, new Double(0.0)),

	/**
	 * Weight for geographical proximity regarding partner homophily. Default: <code>0.5</code>. Values are normally
	 * taken from {@link MMilieuNetworkParameterMap} but this parameter definition is required to access values in the
	 * map. See also {@link MNetBuildHdffPa#PROB_FORWARD}).
	 */
	DIM_WEIGHTS_GEO(Double.class, new Double(0.5)),

	/**
	 * Weight for milieu regarding partner homophily. Default: <code>0.5</code>. Values are normally taken from
	 * {@link MMilieuNetworkParameterMap} but this parameter definition is required to access values in the map. See
	 * also {@link MNetBuildHdffPa#PROB_FORWARD}).
	 */
	DIM_WEIGHTS_MILIEU(Double.class, new Double(0.5)),

	/**
	 * Shapefile for hexagons that are used to groups agents in to clusters of near-by instances to ease computation of
	 * distances between agents. The bounding box of the hexagons should not exceed the area too much since it is used
	 * to computer the maximum extend of the agents' locations (otherwise, set {@link this#MAX_SEARCH_RADIUS}
	 * appropriately).
	 */
	HEXAGON_SHAPEFILE(String.class, "./data/shapefiles/hexagon/hexagon.shp"),

	/**
	 * The order of agent selection during forest fire linking is relevant since the later agents have more links to
	 * follow. In order to mitigate this effect the network service allows to shuffle the ordering after the number of
	 * turns specified here. Default: <code>Integer.MAX_VALUE</code>
	 */
	AGENT_SHUFFLE_INTERVAL(Integer.class, new Integer(Integer.MAX_VALUE));
	

	private Class<?> type;
	private Object defaultValue;
	
	/**
	 * @param type
	 */
	MNetBuildHdffPa(Class<?> type) {
		this(type, null);
	}

	/**
	 * @param type
	 * @param defaultValue
	 */
	MNetBuildHdffPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	/**
	 * @see de.cesr.parma.core.PmParameterDefinition#getType()
	 */
	@Override
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * @see de.cesr.parma.core.PmParameterDefinition#getDefaultValue()
	 */
	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
}
