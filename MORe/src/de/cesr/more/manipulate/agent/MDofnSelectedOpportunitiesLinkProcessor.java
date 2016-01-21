/**
 * 
 */
package de.cesr.more.manipulate.agent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections15.bag.HashBag;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import repast.simphony.space.gis.Geography;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;

import com.csvreader.CsvWriter;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.agent.MoreEgoNetworkManagingAgent;
import de.cesr.more.basic.edge.MoreAgingEdge;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.manipulate.network.MDofnParameters;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.building.MDefaultPartnerFinder;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.rs.building.MorePartnerFinder;
import de.cesr.more.util.Log4jLogger;
import de.cesr.more.util.distributions.MGeneralDistributionParameter;
import de.cesr.more.util.distributions.MRandomEngineGenerator;
import de.cesr.more.util.distributions.MRealDistribution;
import de.cesr.parma.core.PmParameterManager;

/**
 * @author Sascha Holzhauer
 *
 */
public class MDofnSelectedOpportunitiesLinkProcessor<A extends MoreLinkManipulatableAgent<A> & 
	MoreMilieuAgent & MoreEgoNetworkManagingAgent<A, E>, E extends MoreEdge<? super A>> extends
		MThresholdLinkProcessor<A, E> {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger
			.getLogger(MDofnSelectedOpportunitiesLinkProcessor.class);
	
	protected Map<Integer, MRealDistribution>			distanceDistributions;
	protected MMilieuNetworkParameterMap				paraMap;

	protected double areaDiameter;

	protected Uniform rand;
	
	protected Geography geography;
	
	protected MorePartnerFinder<A, E> partnerFinder = new MDefaultPartnerFinder<A, E>();
	
	protected HashBag<Integer>					edgeDurations				= new HashBag<>();

	protected String							edgeDurationCsvTargetFile	= null;

	protected boolean							initialised					= false;

	public MDofnSelectedOpportunitiesLinkProcessor(
			MoreNetworkEdgeModifier<A, E> edgeMan, Geography geography) {
		super(edgeMan);
	
		// <- LOGGING
		logger.info("Instantiate Selected opportuniteis network processor...");
		// LOGGING ->
		
		this.geography = geography;
		AbstractDistribution abstractDis = MManager
				.getURandomService()
				.getDistribution(
						(String) PmParameterManager
								.getParameter(MRandomPa.RND_UNIFORM_DIST_NETWORK_BUILDING));
		
		if (abstractDis instanceof Uniform) {
			this.rand = (Uniform) abstractDis;
		} else {
			this.rand = MManager.getURandomService().getUniform();
			logger.warn("Use default uniform distribution");
		}
		
		assignMilieuParamMap();
		initDistanceDistributions();
	}

	/**
	 * @see de.cesr.more.manipulate.agent.MoreEgoNetworkProcessor#process(java.lang.Object,
	 *      de.cesr.more.basic.network.MoreNetwork)
	 */
	@Override
	public void process(A agent, MoreNetwork<A, E> net) {
		// <- LOGGING
		logger.info("Process network: remove edges with negative weight...");
		// LOGGING ->

		if (!initialised) {
			MManager.getSchedule().schedule(MScheduleParameters.getScheduleParameter(
					MScheduleParameters.END_TICK, 1,
					MScheduleParameters.END_TICK, MScheduleParameters.LAST_PRIORITY), new MoreAction() {
				@Override
				public void execute() {
					MDofnSelectedOpportunitiesLinkProcessor.this.outputEdgeDurations();
				}
			});
			this.initialised = true;
		}

		int counter = 0;
		for (A neighbour : net.getPredecessors(agent)) {
			E edge = net.getEdge(neighbour, agent);
			if (net.getEdge(neighbour, agent).getWeight() <= 0.0) {

				if (edge instanceof MoreAgingEdge) {
					this.edgeDurations.add(((MoreAgingEdge) edge).getAge());
				}

				edgeMan.removeEdge(net, neighbour, agent);
				counter++;

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge (" + edge.getWeight() + ")removed: " + edge);
				}
				// LOGGING ->

			}
		}
		makeNewConnections(counter, agent, net);
	}

	/**
	 * @param numNewConnections
	 */
	@Override
	public void makeNewConnections(int numNewConnections, A agent,
			MoreNetwork<A, E> net) {
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> Make new connections...");
		}
		// LOGGING ->

		List<A> potPartners = new ArrayList<A>();

		int transitiveTiesPool = 0;
		int reciprocalTiesPool = 0;
		int commonOutTiesPool = 0;
		int randomTiesPool = 0;
		
		double distanceProbDivisor = Math.max(
				this.distanceDistributions.get(agent.getMilieuGroup()).density(
						paraMap.getDistParamXMin(agent.getMilieuGroup())),
				this.distanceDistributions.get(agent.getMilieuGroup()).density(
						this.distanceDistributions.get(agent.getMilieuGroup()).getSupportLowerBound()));
		
		// add transitive candidates with probability related to confidence
		if (MDofnParameters.PROP_TRANSITIVE_CONFIDENCE > 0.0) {
			for (A neighbour : net.getPredecessors(agent)) {
				for (A third : net.getPredecessors(neighbour)) {
					if (third != agent && !potPartners.contains(third)) {
						if (rand.nextDouble() <= MDofnParameters.PROP_TRANSITIVE_CONFIDENCE) {
							potPartners.add(third);
							transitiveTiesPool++;
						}
					}
				}
			}
		}
		
		// add all reciprocal candidates with probability related to confidence
		for (A successor : net.getSuccessors(agent)) {
			// check if a links does not already exist:
			if ((!net.isSuccessor(agent, successor)) && !potPartners.contains(successor) && 
					rand.nextDouble() <= MDofnParameters.PROP_RECIPROCAL_CONFIDENCE) {
				potPartners.add(successor);
				reciprocalTiesPool++;
			}
		}
		
		// add common out-neighbours with probability related to confidence
		if (MDofnParameters.PROP_COMMON_OUTNEIGHBOUR_CONFIDENCE > 0.0) {
			for (A neighbour : net.getSuccessors(agent)) {
				for (A third : net.getPredecessors(neighbour)) {
					if (third != agent && !potPartners.contains(third)) {
						if (rand.nextDouble() <= MDofnParameters.PROP_COMMON_OUTNEIGHBOUR_CONFIDENCE) {
							potPartners.add(third);
							commonOutTiesPool++;
						}
					}
				}
			}
		}
		
		// add random nodes (select $p_{rand}|\{N_{trans},N_{reci}\}|$ random links 
		// based on the distance distribution:
		for (int i = 0; i < (transitiveTiesPool + reciprocalTiesPool + commonOutTiesPool); i++) {
			A global = partnerFinder.findPartner(
					net.getJungGraph(), agent, true);

			if (global != null && global != agent) {
				potPartners.add(global);
				randomTiesPool++;
			}
		}
		
		// shuffle candidates
		Collections.shuffle(potPartners, new Random(((Integer)PmParameterManager.getParameter(
				MRandomPa.RANDOM_SEED_NETWORK_DYNAMICS)).intValue()));
		logger.trace("Shuffle order: " + potPartners);
		
		// loop opportunity set:
		// calculate probability
		Map<A, Double> popPartnerProbs = new HashMap<A, Double>();
		double probSum = 0.0;
		
		logger.debug("Composition of opportunity set: transitive (" + transitiveTiesPool + ")" +
				"reciprocal (" + reciprocalTiesPool + ")" +
				"common-out (" + commonOutTiesPool + ")" +
				"random (" + randomTiesPool + ")");
		
		for (A potPartner : potPartners) {
			double probDistance = this.distanceDistributions.get(agent.getMilieuGroup()).
					density(this.geography.getGeometry(agent).distance(geography.getGeometry(potPartner)) /
							((Double) pm.getParam(MNetBuildHdffPa.DISTANCE_FACTOR_FOR_DISTRIBUTION)).doubleValue() *
							distanceProbDivisor);
			
			double probPreference = this.paraMap.getP_Milieu(agent.getMilieuGroup(),
					potPartner.getMilieuGroup())
					* (Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_MILIEU,
							agent.getMilieuGroup());
			
			int commonPartners = 0;
			for (A partnerEgo : net.getAdjacent(agent)) {
				for (A partnerPotPartner : net.getAdjacent(potPartner)) {
					if (partnerEgo == partnerPotPartner) {
						commonPartners++;
					}
				}
			}
			double probCommonPartners = MDofnParameters.getProbSharedAcquaintances(commonPartners/2);
			
			// decide
			popPartnerProbs.put(potPartner, (probDistance + probPreference + probCommonPartners)/3.0);
			probSum += (probDistance + probPreference + probCommonPartners)/3.0;
		}
				
		for (int i = net.getInDegree(agent); i < agent.getDegreeTarget(); i++) {
			double randNum = this.rand.nextDouble();
			double checkedProbSum = 0.0;
			int counter = 0;
			while (randNum >= checkedProbSum) {
				checkedProbSum += popPartnerProbs.get(potPartners.get(counter))/probSum;
				probSum -= popPartnerProbs.get(potPartners.get(counter));
			}
			edgeMan.createEdge(net, agent, potPartners.get(counter));
		}
	}
	
	/**
	 *
	 */
	protected void assignMilieuParamMap() {
		if (((MMilieuNetworkParameterMap) pm
				.getParam(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS)) == null) {
			new MMilieuNetDataReader().initParameters();

			// <- LOGGING
			logger.warn("Parameter MNetworkBuildingPa.MILIEU_NETWORK_PARAMS has not been set! (Re-)Initialise it.");
			// LOGGING ->
		}

		this.paraMap = (MMilieuNetworkParameterMap) pm
				.getParam(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);
	}
	
	private void initDistanceDistributions() {
		this.distanceDistributions = new HashMap<Integer, MRealDistribution>();

		for (int i = (Integer) pm.getParam(MBasicPa.MILIEU_START_ID); i < paraMap.size()
				+ (Integer) pm.getParam(MBasicPa.MILIEU_START_ID); i++) {
			MRealDistribution dist = null;

			
			try {
				dist = (MRealDistribution) Class.forName(paraMap.getDistDistributionClass(i)).
						getConstructor(RandomGenerator.class).newInstance(
								new MRandomEngineGenerator(MManager.getURandomService().getGenerator(
										(String) pm.getParam(MRandomPa.RND_STREAM_NETWORK_BUILDING))));
				dist.setParameter(MGeneralDistributionParameter.PARAM_A, paraMap.getDistParamA(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_B, paraMap.getDistParamB(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_C, paraMap.getDistParamXMin(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_D, this.areaDiameter
						/ ((Double) pm.getParam(MNetBuildHdffPa.DISTANCE_FACTOR_FOR_DISTRIBUTION)).doubleValue());
				dist.setParameter(MGeneralDistributionParameter.PARAM_E, paraMap.getDistParamPLocal(i));
				dist.init();

				this.distanceDistributions.put(new Integer(i), dist);
			} catch (IllegalArgumentException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (SecurityException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (InstantiationException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (IllegalAccessException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (InvocationTargetException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (NoSuchMethodException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (ClassNotFoundException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			}
		}
	}
	
	/**
	 * @param areaDiameter the areaDiameter to set
	 */
	public void setAreaDiameter(double areaDiameter) {
		this.areaDiameter = areaDiameter;
	}

	public void outputEdgeDurations() {
		if (edgeDurationCsvTargetFile != null) {
			try {
				File outfile = new File(this.edgeDurationCsvTargetFile);
				if (outfile.exists())
					outfile.delete();

				// use FileWriter constructor that specifies open for appending
				if (!outfile.getParentFile().exists()) {
					outfile.getParentFile().mkdirs();
				}
				CsvWriter csvOutput = new CsvWriter(new FileWriter(outfile, true), ',');

				// write out the header line
				csvOutput.write("Duration");
				csvOutput.write("Frequency");
				csvOutput.endRecord();

				// else assume that the file already has the correct header line

				for (Integer duration : this.edgeDurations.uniqueSet()) {
					csvOutput.write(duration + "");
					csvOutput.write(this.edgeDurations.getCount(duration) + "");
					csvOutput.endRecord();
				}
				csvOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setEdgeDurationCsvTargetFile(String edgeDurationCsvTargetFile) {
		this.edgeDurationCsvTargetFile = edgeDurationCsvTargetFile;
	}
}
