package testing;

import multivesta.ARGoSState;
import vesta.mc.ParametersForState;

public class ARGoSMultiVestaTesting {
   public static void main(String[] args) {
      ARGoSState a = new ARGoSState(new ParametersForState("", ""));
      a.performWholeSimulation();
   }
}
