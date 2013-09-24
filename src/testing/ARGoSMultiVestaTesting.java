package testing;

import multivesta.ARGoSState;
import vesta.mc.ParametersForState;

public class ARGoSMultiVestaTesting {
	public static void main(String[] args) {
		String fileWithModelSpecification = "../test_footbot_lua.xml";
		//String otherParameters = "libraryPath=argos3_multivesta";
    //String otherParameters = "libraryPath=libargos3_multivesta";
    //String otherParameters = "libraryPath=/home/andrea/Dropbox/prova/argos3-multivesta/build/libargos3_multivesta";
    String otherParameters = "libraryPath=";
    if(args.length != 0)
      otherParameters += args[0];
    else  
      otherParameters += "libargos3_multivesta";
		ParametersForState params = new ParametersForState(
				fileWithModelSpecification, otherParameters);
		ARGoSState a = new ARGoSState(params);
		a.performWholeSimulation();	
	}
}
