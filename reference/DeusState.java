package deus;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import it.unipr.ce.dsg.deus.core.AutomatorParser;
import it.unipr.ce.dsg.deus.core.Deus;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.SimulationException;
import vesta.mc.NewState;
import vesta.mc.ParametersForState;


public class DeusState  extends NewState {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9125964534739473248L;
	private Engine deusInstance;
	private AutomatorParser deusAutomatorParser;
	private static final String DEUS_LOG_NAME_INIT = "." + File.separator + "temp" + File.separator + "multivestaDEUSLog";
	
	public DeusState(ParametersForState params){
		super(params);	
		
		try {
			this.deusAutomatorParser = new AutomatorParser(getModelName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void setSimulatorForNewSimulation(int randomSeedOfSimulation) {
		
		System.err.println("seed " + randomSeedOfSimulation);

		//no more deus log from multivesta since to get Deus simulator instance is used the AutomatorParser() directly and no more Deus() 
		//String logName = DEUS_LOG_NAME_INIT + "_" + Integer.toString(randomSeedOfSimulation);
		Deus.simulationLogName = DEUS_LOG_NAME_INIT + "_" + Integer.toString(randomSeedOfSimulation);
		//deus = new Deus(getModelName(), logName);
		//deusAutomatorParser = deus.getAutomator();
		//TODO: fix temporaneo in attesa del fix su setNewSeed()
//		try {
//			this.deusAutomatorParser = new AutomatorParser(getModelName());
//		} catch (ClassNotFoundException | IllegalArgumentException
//				| SecurityException | InstantiationException
//				| IllegalAccessException | InvocationTargetException
//				| NoSuchMethodException | JAXBException | SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		deusAutomatorParser.getEngine().setNewSeed(randomSeedOfSimulation);
		
		deusInstance = deusAutomatorParser.getEngine();
		
		System.err.println("getcurrentseed " + deusInstance.getCurrentSeed());
	}
	
	public double getTime() {
		return this.deusInstance.getVirtualTime();
	}

	public void performOneStepOfSimulation(){
		try {
			this.deusInstance.runStep();
		} catch (SimulationException e) {
			System.err.println("MultiVested DEUS: runStep exception...");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void performWholeSimulation(){
		try {
			this.deusInstance.run();
		} catch (SimulationException e) {
			System.err.println("MultiVested DEUS: run exception...");
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public double rval(int which) {
		//System.out.println("VT " + Engine.getDefault().getVirtualTime());
		switch (which) {
		case 0:
			//if ((Engine.getDefault().getVirtualTime() <= Engine.getDefault().getMaxVirtualTime() && Engine.getDefault().getEventsList().size() > 0))
			if ((this.deusInstance.getVirtualTime() <= this.deusInstance.getMaxVirtualTime() && this.deusInstance.getEventsList().size() > 0))
				return 0.0;
			else
				return 1.0;
		case 1:
			return this.getTime();
		case 2:
			return this.getNumberOfSteps();
		case 3:
			return this.getNodes().size();
		case 4:
			return this.getEvents().size();	
		default:
			return this.getStateEvaluator().getVal(which, this);
			//return deusStateEvaluator.getVal(which,getNodes(),getEvents());
		}
	}	
	
	/** From now on we have only DEUS-specific methods **/
	
	public PriorityQueue<Event> getEvents(){
		return this.deusInstance.getEventsList();
	}

	public ArrayList<Node> getNodes(){
		return this.deusInstance.getNodes(); 
	}
	
	public Engine getDeusInstance() {
		return deusInstance;
	}

}