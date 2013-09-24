package testing;

import multivesta.ARGoSState;
import vesta.mc.ParametersForState;

public class ARGoSMultiVestaTesting {
   public static void main(String[] args) {
      String configFile = "../src/testing/diffusion_10.argos";
      ParametersForState params = new ParametersForState(configFile, "");
      ARGoSState a = new ARGoSState(params);
      a.performWholeSimulation();
   }
}
