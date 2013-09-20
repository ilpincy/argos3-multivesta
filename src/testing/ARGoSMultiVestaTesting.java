package testing;

import multivesta.ARGoSState;
import vesta.mc.ParametersForState;

public class ARGoSMultiVestaTesting {
	public static void main(String[] args) {
		String fileWithModelSpecification = "../test_footbot_lua.xml";
		String otherParameters = "libraryPath=argos3_multivesta";
		ParametersForState params = new ParametersForState(
				fileWithModelSpecification, otherParameters);
		ARGoSState a = new ARGoSState(params);
		a.performWholeSimulation();
	}
}
