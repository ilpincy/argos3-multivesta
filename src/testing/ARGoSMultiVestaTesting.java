package testing;

import multivesta.ARGoSState;
import vesta.mc.ParametersForState;

public class ARGoSMultiVestaTesting {

	public static void main(String[] args) {
		String configFile = "../src/testing/diffusion_10.argos";
		//String otherParameters = ARGoSState.ARGOSLIBRARYPATH_PARAM +"argos3_multivesta";
		//String otherParameters = ARGoSState.ARGOSLIBRARYPATH_PARAM +"+"libargos3_multivesta";
		String otherParameters;
		if(args.length != 0)
			otherParameters = ARGoSState.ARGOSLIBRARYPATH_PARAM + args[0];
		else  
			otherParameters = ARGoSState.ARGOSLIBRARYPATH_PARAM + "libargos3_multivesta";
		ParametersForState params = new ParametersForState(configFile, otherParameters);
		ARGoSState a = new ARGoSState(params);
		a.performWholeSimulation();	
	}
}	

