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
   private native boolean isExperimentFinishedInARGoS();
	
   private static final String ARGOS_LIBRARY = "argos3_multivesta";

   public static final int OBSERVE_TIME = 0;
   public static final int OBSERVE_STEP = 1;
   public static final int OBSERVE_DONE = 2;

   public ARGoSState(ParametersForState params) {
     /* Initialize MultiVeStA */
	   super(params);
	   /* Load the wrapping library */
	   System.loadLibrary(ARGOS_LIBRARY);
	   /* Initialize ARGoS */
	   initARGoS(params.getModel());
   }

   /**
    * Reset ARGoS with the wanted random seed.
    * @param randomSeed The random seed generated by the client of multivesta
    */
   @Override
   public void setSimulatorForNewSimulation(int randomSeed) {
      resetARGoS(randomSeed);
   }

   /**
    * Return the current simulation time step.
    * @return the current simulation time step.
    * @todo It could return time in seconds, instead of steps
    */
   @Override
   public double getTime() {
      return getTimeFromARGoS();
   }

   /**
    * Performs one step of simulation.
    */
   @Override
   public void performOneStepOfSimulation() {
      stepARGoS();
   }

   /**
    * Runs an experiment up to completion.
    */
   @Override
   public void performWholeSimulation() {
      runARGoS();
   }

   /**
    * Returns <tt>true</tt> if the current experiment is finished, <tt>false</tt> otherwise.
    * @return <tt>true</tt> if the current experiment is finished, <tt>false</tt> otherwise.
    */
   @Override
   public boolean getIsSimulationFinished() {
      return isExperimentFinishedInARGoS();
   }

   /**
    * Performs an observation on the current experiment.
    * The current values for <tt>observation</tt> are hardcoded:
    * <table>
    * <tr>
    * <th>Constant<th>
    * <th>Value</th>
    * <th>Meaning</th>
    * </tr>
    * <tr>
    * <td><tt>OBSERVE_TIME</tt></td>
    * <td>0</td>
    * <td>The output of <tt>getTime()</tt></td>
    * </tr>
    * <tr>
    * <td><tt>OBSERVE_STEPS</tt></td>
    * <td>1</td>
    * <td>The output of <tt>getNumberOfSteps()</tt></td>
    * </tr>
    * <tr>
    * <td><tt>OBSERVE_DONE</tt></td>
    * <td>2</td>
    * <td>The output of <tt>getIsSimulationFinished()</tt></td>
    * </tr>
    * </table>
    * Any value above 2 is passed to the analogous method in the ARGoS
    * loop functions that returns user-defined observations.
    */
   @Override
   public double rval(int observation) {
      switch(observation) {
         case OBSERVE_TIME: return getTime();
         case OBSERVE_STEP: return getNumberOfSteps();
         case OBSERVE_DONE: return getIsSimulationFinished() ? 1.0 : 0.0;
         default:           return observeARGoS(observation);
      }
   }

   /**
    * Releases the resources allocated by ARGoS.
    * This method must be called when ARGoS is not necessary anymore.
    */
   @Override
   public void destroyState() {
      System.out.println("ARGoSState.destroyState");
      destroyARGoS();
   }

}
