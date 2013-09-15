package multivesta;

import vesta.mc.NewState;
import vesta.mc.ParametersForState;

public class ARGoSState extends NewState {

   public ARGoSState(ParametersForState params){
      super(params);//this allows to store all the "standard" parameters contained in params. Further simulator-specific parameters can be provided as a string by the user via the -o command. Such String can be accessed as params.getOtherParameters()
      //any further initialization required by argos that has to be done once, and not at the beginning of each simulation (e.g. loading of xml file...)
   }
	
   @Override
   public void setSimulatorForNewSimulation(int randomSeed) {
      // this method is invoked by multivesta before performing a new simulation. It has to "(re)set" the simulator to perform a new simulation, and the provided parameter has to be used to set the random seed of ARGoS.		
   }
	
   @Override
   public double getTime() {
      // return the current simulated time.
      return 0;
   }

   @Override
   public void performOneStepOfSimulation() {
      // this method is invoked by multivesta to order the simulator to perform a step of simulation	
   }
   
   @Override
   public double rval(int observation) {
      //this method is used to perform observations on the current state of the simulation. "Model-independent observations" have to be defined here. The model-specific ones have to be dealt by a provided state evaluator. 
      switch (observation) {
         case 0:
            return this.getTime();
         case 1:
            return this.getNumberOfSteps();	
         default:
            return this.getStateEvaluator().getVal(observation, this);
            //otherwise, an alternative is default: return 0; //invoke the ARGoS side counterpart of rval
      }
   }
   
   @Override
   public void performWholeSimulation() {
      // We can ignore this method for now
   }
   
}
