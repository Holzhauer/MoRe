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
 * Created by Sascha Holzhauer on 28.10.2010
 */
package de.cesr.more.measures;

import de.cesr.more.measures.measures.MAbstractNetworkMeasure;
import de.cesr.more.measures.measures.MoreMeasure;


/**
 * MORe
 *
 * A marker Interface for {@link MAbstractNetworkMeasure} categories
 * 
 * <code>MoreMeasureCategory</code>s categorize {@link MoreMeasure}s and are provided by
 * the {@link MoreMeasureSupplier}s when these support that category. The
 * <code>MoreNodeMeasureManager</code> and <code>MoreNetworkMeasureManager</code> provide common
 * categories as constants which should be used by custom implementations.
 * 
 * @author Sascha Holzhauer
 * @date 28.10.2010 
 *
 */
public interface MoreMeasureCategory {

}
