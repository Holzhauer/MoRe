package de.cesr.more.manipulate.network;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;

public class MDofnParameters {
	
	protected static Map<Integer, AbstractRealDistribution> waitingTimeDists = new HashMap<Integer, AbstractRealDistribution>();
	
	static {
		// HAPPenInGS-S
		waitingTimeDists.put(1, new LogNormalDistribution(2.54, 1.32));
		waitingTimeDists.put(2, new LogNormalDistribution(2.53, 1.18));
		waitingTimeDists.put(3, new LogNormalDistribution(2.61, 1.4));
		waitingTimeDists.put(4, new LogNormalDistribution(2.22, 1.74));

		// SoNoMoDe
		waitingTimeDists.put(11, new LogNormalDistribution(2.53, 1.18));
		waitingTimeDists.put(12, new LogNormalDistribution(2.61, 1.4));
		waitingTimeDists.put(13, new LogNormalDistribution(2.22, 1.74));
	}
	
	public static final double PROP_TRANSITIVE_CONFIDENCE = 0.039;
	public static final double PROP_COMMON_OUTNEIGHBOUR_CONFIDENCE = 0.170;
	public static final double PROP_RECIPROCAL_CONFIDENCE = 0.166;
	
	public static final double PROP_TRANSITIVE_USAGE = 0.264;
	public static final double PROP_COMMON_OUTNEIGHBOUR_USAGE = 0.270;
	public static final double PROP_RECIPROCAL_USAGE = 0.012;
	
	public static int getWaitingTime(int milieu) {
		return (int) Math.ceil(waitingTimeDists.get(milieu).sample());
	}
	
	/**
	 * According to Kossinets2006b
	 * 
	 * @param sharedAcquaintances
	 * @return probability
	 */
	public static double getProbSharedAcquaintances(int sharedAcquaintances) {
		return  -170 + 13*Math.pow(sharedAcquaintances,2) + 234*sharedAcquaintances;
	}
	
	/**
	 * According to Burt200 and table socnetsabm.sna.processes.dofn.decay
	 * 
	 * @param duration
	 * @return probability
	 */
	public static double getProbDecay(int duration) {
		return Math.pow(duration+1, -1.27);
	}
	
	/**
	 * According to Kossinets2006b
	 * @return probability
	 */
	public static double getProbRandom() {
		return 0.000001;
	}
}
