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
 * Created by holzhauer on 07.10.2011
 */
package de.cesr.more.rs.geo.util;


import repast.simphony.query.Query;
import repast.simphony.space.gis.Geography;
import de.cesr.more.geo.MTorusCoordinate;
import de.cesr.more.rs.geo.util.MGeographyWrapper.MGeoDistanceFactory;


/**
 * MORe
 * 
 * Introduced in order to allow more flexibility for geographical queries. E.g., {@link MGeoDistanceFactory} uses a
 * {@link MGeoTorusDistanceQuery} in case {@link MTorusCoordinate} is part of the geography.
 * 
 * @author holzhauer
 * @date 07.10.2011
 * 
 */
public interface MoreWithinQueryFactory<AgentType> {
	
	/**
	 * 
	 * @param geography
	 * @param distance
	 * @param sourceObject
	 * @return
	 */
	public Query<AgentType> initQuery(Geography<AgentType> geography, double distance,
			AgentType sourceObject);

}
