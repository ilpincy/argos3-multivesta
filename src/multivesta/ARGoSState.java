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

   public ARGoSState(ParametersForState params){
      // Initialize MultiVeStA
      super(params);
      // Load the wrapper library
      // TODO: the path to the library should be a parameter
      System.loadLibrary("argos3_multivesta");
      // Initialize ARGoS
      // TODO: the path to the config file should be a parameter
      initARGoS("../test_footbot_lua.xml");
   }
   
   @Override
   public void setSimulatorForNewSimulation(int randomSeed) {
      // Reset ARGoS with the wanted random seed
      resetARGoS(randomSeed);
   }
	
   @Override
   public double getTime() {
      // Return current time step in ARGoS
      // TODO: could return the current time in seconds instead
      return getTimeFromARGoS();
   }

   @Override
   public void performOneStepOfSimulation() {
      // Perform one simulation step for ARGoS
      // TODO: how do you know when the simulation is finished?
      stepARGoS();
   }
   
   @Override
   public void performWholeSimulation() {
      // Run a full experiment with ARGoS
      runARGoS();
   }
   
   @Override
   public double rval(int observation) {
      // Perform an observation on ARGoS
      // This interfaces directly with the user-defined loop functions
      return observeARGoS(observation);
   }
   
}
