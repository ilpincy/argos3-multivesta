package multivesta;

import vesta.mc.NewState;
import vesta.mc.ParametersForState;

public class ARGoSState extends NewState {

   private native void initARGoS(String config_file);
   private native void destroyARGoS();
   private native double getTimeFromARGoS();
   private native void resetARGoS(int randomSeed);
   private native void stepARGoS();
   private native void runARGoS();
   private native double observeARGoS(int observation);
   private native boolean getIsExperimentFinishedFromARGoS();

   
   public ARGoSState(ParametersForState params){
      // Initialize MultiVeStA
      super(params);
      // Load the wrapper library
      // TODO: the path to the library should be a parameter
      System.loadLibrary("argos3_multivesta");
      //ANDREA: any ARGOS-specific parameter (i.e. not provided y ParametersForState) is provided by the user as a unique String when launching the multivesta client with: -o "param1 param2 param3". Thus we need a StringTokenizer to obtain each parameter.
      /*final StringTokenizer otherparams = new StringTokenizer(params.getOtherParameters());
		  String libraryPath = otherparams.nextToken();
      System.loadLibrary(libraryPath);*/
      
      // Initialize ARGoS
      // TODO: the path to the config file should be a parameter
      initARGoS("../test_footbot_lua.xml");
      //ANDREA: the path of the model has to be provided by the user when launching the client of multivesta. Such path is obtained with params.getModel();
      /*initARGoS(params.getModel());*/
      //ANDREA: Here we do not need yet to "launch" ARGoS. It has to be launched only in setSimulatorForNewSimulation.
   }
   
   @Override
   public void setSimulatorForNewSimulation(int randomSeed) {
      // Reset ARGoS with the wanted random seed
      resetARGoS(randomSeed);
      //ANDREA: at the end of this method, ARGoS has to be ready to perform a new simulation: we have to set a new seed, we have to reset the intial state, and we have to clean the datastructures.
   }
	
   @Override
   public double getTime() {
      // Return current time step in ARGoS
      // TODO: could return the current time in seconds instead
      return getTimeFromARGoS();
      //ANDREA: MultiVeStA does care about the granularity of time. It is up to us. This is a value that we can use when doing queries. 
   }

   @Override
   public void performOneStepOfSimulation() {
      // Perform one simulation step for ARGoS
      // TODO: how do you know when the simulation is finished? 
      //ANDREA: DONE, because MultiVeStA invokes performOneStepOfSimulation() only if getIsSimulationFinished() gives false. If the simulation has been completed in this step, this method will not be invoked anymore for this simulation.
      stepARGoS();
   }
   
   @Override
   public void performWholeSimulation() {
      // Run a full experiment with ARGoS
      runARGoS();
   }
 
   @Override
	 public boolean getIsSimulationFinished(){
      // tell to multivesta if the experiment has been completed
		  return getIsExperimentFinishedFromARGoS();
	 }
   
   @Override
   public double rval(int observation) {
      // Perform an observation on ARGoS
      
      if(observation == 0) {
        return getTime();
      }
   
      if(observation == 1) {
        return getNumberOfSteps();
      }
      
      if(observation == 2) {
        if(getIsExperimentFinishedFromARGoS()) 
          return 1.0;
        else 
          return 0.0;
      }
      
      // This interfaces directly with the user-defined loop functions
      return observeARGoS(observation);
   }
   
  @Override
	public void destroyState(){
		//ANDREA: this method is invoked when argos is not necessary anymore: i.e. when the analysis has been completed.
    destroyARGoS();
	}
   
}

//javac -cp ./multivesta/multivesta.jar:./  ./multivesta/ARGoSState.java
//javac -cp ./multivesta/multivesta.jar:./  ./testing/ARGoSMultiVestaTesting.java
//java -cp ./multivesta/multivesta.jar:./  multivesta.ARGoSState 
//java -cp ./multivesta/multivesta.jar:./  testing.ARGoSMultiVestaTesting