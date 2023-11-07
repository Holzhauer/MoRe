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
 * Created by Sascha Holzhauer on 14.05.2012
 */
package de.cesr.more.testing.rs.building;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cesr.more.testing.rs.building.geo.MGeoDistanceQueryTest;
import de.cesr.more.testing.rs.building.geo.MGeoRsCompleteNetworkBuilderTest;
import de.cesr.more.testing.rs.building.geo.MGeoRsWattsBetaSwBuilderTest;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 14.05.2012 
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ MRsCompleteNetworkBuilderTest.class, MRsLattice2DNetworkBuilderTest.class })
public class MAllRsNetworkBuilderTests {
}
