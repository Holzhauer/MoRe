package de.cesr.more.testing;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cesr.more.testing.basic.MAllBasicTests;
import de.cesr.more.testing.building.MAllBuildingTests;
import de.cesr.more.testing.measures.network.MAllMeasuresNetworkTests;
import de.cesr.more.testing.measures.network.supply.algos.MAllNetworkSupplyAlgosTests;
import de.cesr.more.testing.measures.node.MAllMeasuresNodeTests;
import de.cesr.more.testing.networks.MAllNetworkTests;
import de.cesr.more.testing.rs.building.geo.MAllRsBuildingGeoTests;
import de.cesr.more.testing.rs.geo.util.MAllRsGeoUtilTests;
import de.cesr.more.testing.rs.network.MAllRsNetworkTests;
import de.cesr.more.testing.util.MAllUtilitiesTests;



/**
 * run all tests at once
 */
@RunWith(Suite.class)
@SuiteClasses( {MAllBasicTests.class, MAllBuildingTests.class, MAllMeasuresNetworkTests.class,
		MAllNetworkSupplyAlgosTests.class, MAllMeasuresNodeTests.class, MAllNetworkTests.class,
		MAllRsBuildingGeoTests.class, MAllRsNetworkTests.class, MAllRsGeoUtilTests.class,
		MAllUtilitiesTests.class})
public class MAllTests {
}
