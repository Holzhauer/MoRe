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
 * Created by holzhauer on 27.07.2011
 */
package de.cesr.more.util;

import java.util.HashMap;
import java.util.Map;


/**
 * MoRe
 * 
 * TODO provide default values! TODO exception management
 * 
 * @author Sascha Holzhauer
 * @date 27.07.2010
 * 
 */
public class MMilieuNetworkParameterMap extends HashMap < Integer , Map < String , Object >> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5395092541374359151L;

	public enum MilieuNetworkParameterKeys {
		K, P_REWIRE, P_MILIEUS, SEARCH_RADIUS, X_SEARCH_RADIUS, MAX_SEARCH_RADIUS, DIM_WEIGHTS_GEO, DIM_WEIGHTS_MILIEU;
	}

	public int getK(int milieu) {
		return ((Integer) this.get(new Integer(milieu)).get(MilieuNetworkParameterKeys.K.name())).intValue();
	}

	public void setK(int milieu, int k) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap < String , Object >());
		}
		this.get(new Integer(milieu)).put(MilieuNetworkParameterKeys.K.name(), new Integer(k));
	}

	public double getP_Rewire(int milieu) {
		return ((Double) this.get(new Integer(milieu)).get(MilieuNetworkParameterKeys.P_REWIRE.name())).doubleValue();
	}

	public void setP_Rewire(int milieu, double p) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap < String , Object >());
		}
		this.get(new Integer(milieu)).put(MilieuNetworkParameterKeys.P_REWIRE.name(), new Double(p));
	}

	public double getSearchRadius(int milieu) {
		return ((Double) this.get(new Integer(milieu)).get(MilieuNetworkParameterKeys.SEARCH_RADIUS.name()))
				.doubleValue();
	}

	public void setSearchRadius(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap < String , Object >());
		}
		this.get(new Integer(milieu)).put(MilieuNetworkParameterKeys.SEARCH_RADIUS.name(), new Double(radius));
	}

	public double getXSearchRadius(int milieu) {
		return ((Double) this.get(new Integer(milieu)).get(MilieuNetworkParameterKeys.X_SEARCH_RADIUS.name()))
				.doubleValue();
	}

	public void setXSearchRadius(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap < String , Object >());
		}
		this.get(new Integer(milieu)).put(MilieuNetworkParameterKeys.X_SEARCH_RADIUS.name(), new Double(radius));
	}

	public double getMaxSearchRadius(int milieu) {
		return ((Double) this.get(new Integer(milieu)).get(MilieuNetworkParameterKeys.MAX_SEARCH_RADIUS.name()))
				.doubleValue();
	}

	public void setMaxSearchRadius(int milieu, double radius) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap < String , Object >());
		}
		this.get(new Integer(milieu)).put(MilieuNetworkParameterKeys.MAX_SEARCH_RADIUS.name(), new Double(radius));
	}

	public double getDimWeightGeo(int milieu) {
		return ((Double) this.get(new Integer(milieu)).get(MilieuNetworkParameterKeys.DIM_WEIGHTS_GEO.name()))
				.doubleValue();
	}

	public void setDimWeightGeo(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap < String , Object >());
		}
		this.get(new Integer(milieu)).put(MilieuNetworkParameterKeys.DIM_WEIGHTS_GEO.name(), new Double(weight));
	}
	
	public double getDimWeightMilieu(int milieu) {
		return ((Double) this.get(new Integer(milieu)).get(MilieuNetworkParameterKeys.DIM_WEIGHTS_MILIEU.name()))
				.doubleValue();
	}

	public void setDimWeightMilieu(int milieu, double weight) {
		if (!this.containsKey(new Integer(milieu))) {
			this.put(new Integer(milieu), new HashMap < String , Object >());
		}
		this.get(new Integer(milieu)).put(MilieuNetworkParameterKeys.DIM_WEIGHTS_MILIEU.name(), new Double(weight));
	}
	
	/**
	 * @param ownMilieu milieu id (starting with 1!)
	 * @param otherMilieu milieu id (starting with 1!)
	 * @return Created by Sascha Holzhauer on 16.08.2010
	 */
	public double getP_Milieu(int ownMilieu, int otherMilieu) {
		//
		return ((Map < Integer , Double >) this.get(new Integer(ownMilieu)).get(
				MilieuNetworkParameterKeys.P_MILIEUS.name())).get(new Integer(otherMilieu)).doubleValue();
	}

	public void setP_Milieu(int ownMilieu, int otherMilieu, double p) {
		if (!this.containsKey(new Integer(ownMilieu))) {
			this.put(new Integer(ownMilieu), new HashMap < String , Object >());
		}
		if (!this.get(new Integer(ownMilieu)).containsKey(MilieuNetworkParameterKeys.P_MILIEUS.name())) {
			this.get(new Integer(ownMilieu)).put(MilieuNetworkParameterKeys.P_MILIEUS.name(),
					new HashMap < Integer , Double >());
		}
		((HashMap < Integer , Double >) this.get(new Integer(ownMilieu)).get(
				MilieuNetworkParameterKeys.P_MILIEUS.name())).put(new Integer(otherMilieu), new Double(p));
	}
}
