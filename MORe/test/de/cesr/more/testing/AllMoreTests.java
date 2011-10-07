package de.cesr.more.testing;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cesr.more.testing.adapter.AlLAdapterTests;
import de.cesr.more.testing.building.AllBuildingTests;
import de.cesr.more.testing.measures.network.AllMeasuresNetworksTests;
import de.cesr.more.testing.measures.network.supply.algos.AllNetworkSupplyAlgosTests;
import de.cesr.more.testing.standalone.AllStandaloneTests;
import de.cesr.more.testing.util.AllUtilitiesTests;



/**
 * run all tests at once
 */
@RunWith(Suite.class)
@SuiteClasses( { AllUtilitiesTests.class, AllNetworkSupplyAlgosTests.class, AllMeasuresNetworksTests.class,
		AlLAdapterTests.class, AllStandaloneTests.class,AllBuildingTests.class })
public class AllMoreTests {
}
