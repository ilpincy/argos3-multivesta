package multivesta;

import vesta.mc.NewState;
import vesta.mc.ParametersForState;

import java.util.StringTokenizer;

public class ARGoSState extends NewState {

	private native void initARGoS(String config_file);

	private native void destroyARGoS();

	private native double getTimeFromARGoS();

	private native void resetARGoS(int randomSeed);

	private native void stepARGoS();

	private native void runARGoS();

	private native double observeARGoS(int observation);
	
	private native boolean getIsExperimentFinishedFromARGoS();
	
	private static String ARGOSLIBRARYPATH_PARAM = "libraryPath=";
	
	private static String ARGOSLIBRARYPATH = "argos3_multivesta";

	public ARGoSState(ParametersForState params) {
		// Initialize MultiVeStA
		super(params);
		
		/*
		 * Any ARGOS-specific parameter (i.e. not provided by
		 * ParametersForState) is provided by the user as a unique String when
		 * launching the multivesta client with: -o "param1 param2 param3". Thus
		 * we need a StringTokenizer to obtain each parameter.
		 */
		
		// Load the wrapper library
		String argosLibraryPath=null;;
		final StringTokenizer otherparams = new StringTokenizer(params.getOtherParameters());
		while (otherparams.hasMoreElements()) {
            final String param = otherparams.nextToken().trim();
            if (param.startsWith(ARGOSLIBRARYPATH_PARAM)) {
            	argosLibraryPath = param.replace(ARGOSLIBRARYPATH_PARAM, "");
            } else {
                 System.out.println("Ignored parameter \"" + param + "\": it's not among the supported parameters.");
            }
		}
		
		if(argosLibraryPath == null){
			System.out.println("The library has not been provided. I set the default one:"+ARGOSLIBRARYPATH);
			argosLibraryPath = ARGOSLIBRARYPATH;
		}
		
		System.loadLibrary(argosLibraryPath);

		// Initialize ARGoS
		//the path of the model has to be provided by the user when launching the client of multivesta.
		initARGoS(params.getModel());

		/*
		 * TODO ANDREA: TOBECHECKED: Here we do not need yet to "launch" ARGoS. It has to be
		 * launched only in setSimulatorForNewSimulation.
		 */
	}

	@Override
	public void setSimulatorForNewSimulation(int randomSeed) {
		// Reset ARGoS with the wanted random seed
		resetARGoS(randomSeed);
		/*
		 * TODO ANDREA: TOBECHECKED; at the end of this method, ARGoS has to be ready to perform a
		 * new simulation: we have to set a new seed, we have to reset the
		 * intial state, and we have to clean the datastructures.
		 */
	}

	@Override
	public double getTime() {
		// Return current time step in ARGoS
		/*
		 * MultiVeStA does care about the granularity of time. It is up
		 * to us, as this is a value that we can use when doing queries.
		 */

		return getTimeFromARGoS();

	}

	@Override
	public void performOneStepOfSimulation() {
		/*
		 * Perform one simulation step for ARGoS. If the simulation finishes in
		 * this step, this method will not be invoked anymore for this
		 * simulation.
		 */
		stepARGoS();
	}

	@Override
	public void performWholeSimulation() {
		// Run a full experiment with ARGoS
		runARGoS();
	}

	@Override
	public boolean getIsSimulationFinished() {
		// tell to multivesta if the experiment has been completed
		return getIsExperimentFinishedFromARGoS();
	}

	@Override
	public double rval(int observation) {
		// Perform an observation on ARGoS

		//model-independent observations
		if (observation == 0) {
			return getTime();
		}

		if (observation == 1) {
			return getNumberOfSteps();
		}

		if (observation == 2) {
			if (getIsExperimentFinishedFromARGoS())
				return 1.0;
			else
				return 0.0;
		}

		//model-specific observations
		// This interfaces directly with the user-defined loop functions
		return observeARGoS(observation);
	}

	@Override
	public void destroyState() {
		/*
		 * This method is invoked by a MultiVeSta server when argos is not
		 * necessary anymore: i.e. when the analysis has been completed.
		 */
		destroyARGoS();
	}

}

//javac -cp ./multivesta/multivesta.jar:./  ./multivesta/ARGoSState.java
//javac -cp ./multivesta/multivesta.jar:./  ./testing/ARGoSMultiVestaTesting.java
//java -cp ./multivesta/multivesta.jar:./  multivesta.ARGoSState 
//java -cp ./multivesta/multivesta.jar:./  testing.ARGoSMultiVestaTesting