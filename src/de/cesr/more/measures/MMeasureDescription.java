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

import de.cesr.more.measures.node.MNodeMeasureCategory;
import de.cesr.more.measures.node.MNodeMeasureManager;

/**
 * MORe
 * 
 * <code>MeasureDescription</code>s provide informations about {@link MoreMeasure} objects
 * network measure supplier provide ({@link MoreMeasureCategory}, short and long description).
 * They are identified by their short description.
 * 
 * 
 * @author Sascha Holzhauer
 * @date 28.10.2010 
 *
 */
public class MMeasureDescription implements Comparable<MMeasureDescription> {
		String longname;
		String shortname;
		MoreMeasureCategory cat;
		
		/**
		 * Instantiates a <code>MeasureDescription</code> solely by its short description.
		 * The category is set to {@link MNodeMeasureManager#NOT_DEFINED}.
		 * @param shortDescription The short description for the new <code>MeasureDescription</code>
		 */
		public MMeasureDescription(String shortDescription) {
			this(MNodeMeasureCategory.NOT_DEFINED, shortDescription, shortDescription);
		}
		
		/**
		 * Constructs a new <code>MeasureDescription</code> by a short description and
		 * {@link MoreMeasureCategory}.
		 * 
		 * @param cat The {@link MoreMeasureCategory}
		 * @param shortDescription The short description for the new <code>MeasureDescription</code>
		 */
		public MMeasureDescription(MoreMeasureCategory cat, String shortDescription) {
			this(cat, shortDescription, shortDescription);
		}
		
		/**
		 * @see MeasureDescription#MeasureDescription(de.cesr.more.measures.node.MNodeMeasureManager.sh.soneta.measures.NetworkMeasureUtilities.MeasureCategory, String)
		 * 
		 * @param cat The {@link MoreMeasureCategory}
		 * @param shortDescription The short description for the new <code>MeasureDescription</code>
		 * @param longDescription The long description for the new <code>MeasureDescription</code>
		 */
		public MMeasureDescription(MoreMeasureCategory cat, String shortDescription, String longDescription) {
			shortname = shortDescription;
			longname = longDescription;
			this.cat = cat;
		}
		
		/**
		 * @date 15.08.2008
		 *
		 * @return Short description key
		 */
		public String getShort() {
			return shortname;
		}
		
		/**
		 * @see java.lang.Object#toString()
		 * Returns the long description
		 */
		@Override
		public String toString() {
			return longname;
		}
		
		/**
		 * @date 15.08.2008
		 *
		 * @return The {@link MeasureCategory} of this measure description
		 */
		public MoreMeasureCategory getCategory() {
			return cat;
		}
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 * Uses the short description to identify a <code>MeasureDescription</code>
		 */
		@Override
		public boolean equals(Object description) {
			if (description instanceof MMeasureDescription) {
				return shortname.equals(((MMeasureDescription)description).getShort());
			}
			else {
				return super.equals(description);
			}
		}
		
		/**
		 * @see java.lang.Object#hashCode()
		 * Uses the short description's hash code
		 */
		@Override
		public int hashCode() {
			return shortname.hashCode();
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(MMeasureDescription o) {
			return this.shortname.compareTo(o.shortname);
		}
	}

