package vesta.misscel;

import it.imtlucca.util.RandomEngineFacilities;
import it.unipr.ce.dsg.deus.core.SimulationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.math.plot.components.SetScalesFrame;

import cern.jet.random.engine.MersenneTwister;
import expectj.ExpectJ;
import expectj.ExpectJException;
import expectj.SpawnedProcess;
import vesta.mc.AnyException;
import vesta.mc.NewState;
import vesta.mc.ParametersForState;

public class MISSCELState extends NewState
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7214766724999191754L;
	/**
	 * 
	 */
	//private double t;
	//private int stepOfStoredTime = -1;
	private SpawnedProcess maude;
	private String term;

	//new
	private String command;
//	private String model;
	
	private HowNonDeterminismIsSolved howNondeterminismIsSolved;
	private String rootModule;
	
	private String apmaude;
	private String utilities;
	private String MISSCEL_TS;
	private final ArrayList<String> rules;
	private RandomEngineFacilities randomGenerator;
	private ArrayList<String> probabilisticallySortedSCIds;
	private static String noMoreStates="NOMORESTATES";
	private ExpectJ p = new ExpectJ(null, -1L);
	
	
	private static final String DEFAULT_ROOTMODULE = "NO-ROOT-MODULE-PROVIDED";

	private static final String DEFAULT_HOWNONDETERMINISMISSOLVED = "NDRNDA";
	
	public MISSCELState(ParametersForState params){
		super(params);
		
		File jf = new File(params.getModel());
		String mfname = jf.getName();

		jf = new File(mfname);
		if (!jf.exists()) {
			throw new AnyException("Make sure " + mfname + " exists in the working directory " + System.getProperty("user.dir"));
		}

		this.rootModule = DEFAULT_ROOTMODULE;
		String howSolveNonDeterminism = DEFAULT_HOWNONDETERMINISMISSOLVED;
		//str = "Hello I'm your String";
		String[] splitted = params.getOtherParameters().split("\\s+");
		for (int i=0; i<splitted.length; i++){
			String s = splitted[i];
			if (s.equals("-rm")){
				this.rootModule = splitted[i+1];
			}
			if (s.equals("-nd")){
				howSolveNonDeterminism = splitted[i+1];
			}
		}
		
		this.command = "maude";
		
		if((howSolveNonDeterminism == null) || (howSolveNonDeterminism.equals(""))){
			System.out.println("No information about how to solve nondeterminism has been given. We fix the default value 'ndRndA' (i.e. we generate all the one-step next states, and we choose one of them with uniform probability)");
			this.howNondeterminismIsSolved = HowNonDeterminismIsSolved.NDRNDA;
		} else {
			if((!howSolveNonDeterminism.equalsIgnoreCase("ndRndA")) && (!howSolveNonDeterminism.equalsIgnoreCase("cpRndA"))  && (!howSolveNonDeterminism.equalsIgnoreCase("ndRcpA"))  && (!howSolveNonDeterminism.equalsIgnoreCase("cpRcpA"))){
				System.out.println("A wrong information has been provided about how to solve nondeterminism("+ howSolveNonDeterminism +"). Please, choose a value among {ndRndA,cpRndA,ndRcpA,cpRcpA}");
				System.exit(-1);
			} else {
				this.howNondeterminismIsSolved = HowNonDeterminismIsSolved.valueOf(howSolveNonDeterminism.toUpperCase());
			}
		}		
		rules = generateListOfRuleNames();		
	}
	
	@Override
	public void setSimulatorForNewSimulation(int randomSeedOfSimulation) {
		createNewMaudeInstanceLoadMISSCELLoadSystemSpecification();
	}
	
	public void performOneStepOfSimulation(){
		String generatedState;
		ArrayList<String> sortedRules;
		try {
			switch (howNondeterminismIsSolved) {
			case NDRNDA:
				//generatedState = getNextState(i, rootModule, nextState,randomGenerator, "EXECUTIONTOKEN");
				generatedState = getNextState("EXECUTIONTOKEN");
				if(!generatedState.equals(noMoreStates)){
					term = generatedState;
					//if(drawImagesBool){drawStateUsingDOT(i, generatedState);}
				} else{
					//System.out.println("I am not been able to generate a new state with token EXECUTIONTOKEN. Hence the simulation is terminated. I should manage this fact");
				}
				break;
				
			case NDRCPA:
				if(probabilisticallySortedSCIds == null || probabilisticallySortedSCIds.isEmpty()){
					probabilisticallySortedSCIds = obtainAndProbabilisticallySortSCIdsViaJava(randomGenerator);
				}
				generatedState = noMoreStates;
				while(probabilisticallySortedSCIds.size() > 0 && generatedState.equals(noMoreStates)){
					String fixedComponent = probabilisticallySortedSCIds.remove(0);
					//System.out.println("   fixed componentp "+fixedComponent);
					//generatedState = getNextState(i,rootModule,nextState,randomGenerator,"EXECUTIONTOKEN(id('" + fixedComponent + "))");
					generatedState = getNextState("EXECUTIONTOKEN(id('" + fixedComponent + "))");
					if (!(generatedState.equals(noMoreStates))){
						term = generatedState;
						//break;
						//if(drawImagesBool){drawStateUsingDOT(stateCounter, generatedState);}
					}
				}
				break;
				
			case CPRNDA:
				sortedRules = probabilisticallySortRules(randomGenerator);
				generatedState = noMoreStates;
				while((generatedState.equals(noMoreStates)) && sortedRules.size() > 0){
					String fixedRule = sortedRules.remove(0);
					//generatedState = getNextState(i,rootModule,nextState,randomGenerator,"EXECUTIONTOKEN("+ fixedRule +")");
					generatedState = getNextState("EXECUTIONTOKEN("+ fixedRule +")");
				}
				//System.out.println("I am not been able to generate a new state with token EXECUTIONTOKEN. Hence the simulation is terminated. I should manage this fact");
				if(generatedState.equals(noMoreStates)){
					//System.out.println("I am not been able to generate a new state with token EXECUTIONTOKEN(ruleName) for any ruleName. Hence the simulation is terminated. I should manage this fact");
					break;
				}
				else{
					term = generatedState;
					//if(drawImagesBool){drawStateUsingDOT(i, generatedState);}
				}

				break;
				
			case CPRCPA:
				if(probabilisticallySortedSCIds == null || probabilisticallySortedSCIds.isEmpty()){
					probabilisticallySortedSCIds = obtainAndProbabilisticallySortSCIdsViaJava(randomGenerator);
				}
				//qui c'è il rischio che ad uno stp non faccio nulla anche se potrei: per esempio potrei non essere in grado di fare nulla con la lista di SCID attuali perchè manca proprio quello che potrebbe fare qualcosa. 
				//Pazienza, non è un grosso problema. Basta ricordarselo.
				generatedState = noMoreStates;
				while(probabilisticallySortedSCIds.size() > 0 && generatedState.equals(noMoreStates)){
					String fixedComponent = probabilisticallySortedSCIds.remove(0);
					//System.out.println("   fixed componentp "+fixedComponent);
					sortedRules = probabilisticallySortRules(randomGenerator);
					while((generatedState.equals(noMoreStates)) && sortedRules.size() > 0){
						String fixedRule = sortedRules.remove(0);
						//System.out.println("      trying rule "+fixedRule);
						//generatedState = getNextState(i,rootModule,nextState,randomGenerator,"EXECUTIONTOKEN("+ fixedRule + ", id('" + fixedComponent + "))");
						generatedState = getNextState("EXECUTIONTOKEN("+ fixedRule + ", id('" + fixedComponent + "))");
						if (!(generatedState.equals(noMoreStates))){
							term = generatedState;
							//break;
							//if(drawImagesBool){drawStateUsingDOT(stateCounter, generatedState);}
						}
					}	
				}
				break;	

			default:
				break;
			}
					
		} catch (ExpectJException e) {
			System.out.println("\n\nHHH\n\n");
			throw new AnyException("ExpectJException while generating the next step of the simulation in maude: \n", e);
		}
	}
	
	public void performWholeSimulation(){
		System.out.println("last is not currently implemented for MISSCEL");
		System.exit(-1);
	}
	
	public double rval(int r) {
		try {
			//current virtual time
			/*if(r == 0)
				return this.getTime();*/ 
			//current number of steps
			if(r == 1)
				return (double)this.getNumberOfSteps();
			//System.out.println("beginning of rval.\n");
			//System.out.println("Before\n"+"red val(" + r + "," + this.term + ") .\n");
			
			String maudeCommand = "red in "+ rootModule + " : val("+ r + ", { " + term + " }) ." ;
			this.maude.send(maudeCommand + "\n");
			
			String ret = this.maude.maudeStepReduceOrRewrite();
			//System.out.println("After\n"+"red val(" + r + "," + this.term + ") .\n");
			//System.out.println("ResultString:\n"+ret);
			
			//new
			String valString;
			if (ret.startsWith("result FiniteFloat:")) {
				valString = ret.replaceFirst("result FiniteFloat:", "").trim();
				return Double.parseDouble(valString);
			} else if(ret.startsWith("result Float:")) {
				valString = ret.replaceFirst("result Float:", "").trim();
				return Double.parseDouble(valString);
			} else{
				System.out.println("\n\nIII\n\n");
				throw new AnyException("Error in invoking and executing maude: \n" + ret);
			}	
			
			
			
			/*
			if ((!ret.startsWith("result FiniteFloat:")) && (!ret.startsWith("result Float:"))) {
				throw new AnyException("Error in invoking and executing maude: \n" + ret);
			}
			if (ret.startsWith("result FiniteFloat:")) {
				String time = ret.replaceFirst("result FiniteFloat:", "").trim();
				return Double.parseDouble(time);
			}
			String time = ret.replaceFirst("result Float:", "").trim();
			return Double.parseDouble(time);*/
		}
		catch (Exception e) {
			System.out.println("\n\nLLL\n\n");
			throw new AnyException("Error in querying val in maude: \n", e);
		}
	}
	
	public double getTime() {
		System.out.println("getTime does not make sense for misscel. I return 0");
		//System.exit(-1);
		return 0;
		/*if(needToReadTime()){
			readAndStoreTime();
		} 
		
		return this.t;*/
	}

	
	
	/** FROM NOW ON WE HAVE ONLY MiSSCEL-SPECIFIC CODE **/
	
	
	private void createNewMaudeInstanceLoadMISSCELLoadSystemSpecification(){
		
		//setNumberOfSteps(0);
		//stepOfStoredTime = -1;
		//howNondeterminismIsSolved = null;
		probabilisticallySortedSCIds = null;
		
		//ExpectJ p = new ExpectJ(null, -1L);
//		if(this.getSeeds() == null || this.getSeeds().size() == 0){
//			System.out.println("Not enough seeds");
//			throw new AnyException("Not enough seeds: \n", new Exception("Not enough seeds: \n"));
//		}
//
//		int seed = this.getSeeds().remove(this.getSeeds().size() - 1);

		//randomGenerator = new Random(this.getCurrentSeed());
		randomGenerator = new RandomEngineFacilities(new MersenneTwister(this.getCurrentSeed()));
		
		try {
			if(this.maude != null) 
				this.maude.stop();
			this.maude = p.spawn(command + " -no-wrap -no-banner -random-seed=" + this.getCurrentSeed());
		} catch (Exception e) {
			System.out.println("\n\nEXCEPTION IN this.maude.stop(); " + e.getMessage()+"\n\n\n\n");
			throw new AnyException("Error in invoking maude (while creating the Maude process): \n", e);
		}

		try {
			
			this.maude.send("set print format off .\n");	
			
			//this.maude.send("red s(0) ." + "\n");
			//String uno = this.maude.maudeStepReduce();
			
			loadNecessaryStoredMaudeFiles();
			//System.out.println("After loadNecessaryStoredMaudeFiles()");
			
			//this.maude.send("red s(0) ." + "\n");
			//uno = this.maude.maudeStepReduce();
			
			//String modelString = readFile(model);
			String modelString = readFile(this.getModelName());
			
			this.maude.send(modelString);
			//this.maude.send("in " + model + "\n");
			//System.out.println("after this.maude.send(modelString);");
			//this.maude.send("red in "+ rootModule + " : s(0) ." + "\n");
			//uno = this.maude.maudeStepReduce();

			String maudeCommand = "red in "+ rootModule + " : initState ." ;
			this.maude.send(maudeCommand + "\n");
			
			String ret = this.maude.maudeStepReduceOrRewrite();
			
			//this.maude.send("red s(0) ." + "\n");
			//uno = this.maude.maudeStepReduce();
			
			
			
			
			if(ret.startsWith("result ClosedSystem:")){
				term = ret.replaceFirst("result ClosedSystem:", "").trim();
				//System.out.println("ClosedSystem=["+term+"]");
				term = term.substring(1, term.length() -1);//from ClosedSystem to System .
				//System.out.println("System=["+term+"]");
			}
			else {
				System.out.println("Error in reducing the initial state of the system. Please, make sure that the system specification has an equation 'eq initState = the closedSystem representing the initial state':\n" + ret);
				throw new AnyException("Error in reducing the initial state of the system. Please, make sure that the system specification has an equation 'eq initState = the closedSystem representing the initial state':\n" + ret);
			}	
			//this.readAndStoreTime();
		}
		catch (ExpectJException e) {
			System.out.println("\n\n ExpectJException EXCEPTION " + e.getMessage()+"\n\n\n\n");
			throw new AnyException("Error in invoking and executing maude: \n", e);
		} catch (IOException ioe) {
			System.out.println("\n\n IOException EXCEPTION " + ioe.getMessage()+"\n\n\n\n");
			throw new AnyException("Error in loading the Maude stored necessary maude files: \n", ioe);
		}

	}
	
	private void loadNecessaryStoredMaudeFiles() throws IOException, ExpectJException{
		if(utilities == null){
			utilities = readStoredMaudeFile("misscel/utilities.maude");
		}
		if(apmaude == null){
			apmaude = readStoredMaudeFile("misscel/apmaude.maude");
		}
		if(MISSCEL_TS == null){
			MISSCEL_TS = readStoredMaudeFile("misscel/MISSCEL_TS.maude");
		}
		//System.out.println("before this.maude.send(utilities);");
		this.maude.send(utilities);
		//System.out.println("before this.maude.send(apmaude);");
		this.maude.send(apmaude);
		//System.out.println("before this.maude.send(MISSCEL_TS);");
		this.maude.send(MISSCEL_TS);
		//System.out.println("after this.maude.send(MISSCEL_TS);");
		//this.maude.send("red s(0) ." + "\n");
		//String uno = this.maude.maudeStepReduce();
	}
	
	private String readStoredMaudeFile(String resource) throws IOException, ExpectJException {
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resource)));
		String line;
		StringBuffer readFile = new StringBuffer("");
		while (true) {
			line = br.readLine();
			if (line == null) {
				break;
			} else {
				//System.out.println(line);
				readFile.append(line);
				readFile.append("\n");
				//this.maude.send(line + "\n");
			}
		}
		br.close();
		readFile.append("\n");
		return readFile.toString();
	}
	
	private String readFile(String filePath) throws IOException, ExpectJException {
		File f = new File(filePath);
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String line;
		StringBuffer readFile = new StringBuffer("");
		while (true) {
			line = br.readLine();
			if (line == null) {
				break;
			} else {
				//System.out.println(line);
				readFile.append(line);
				readFile.append("\n");
				//this.maude.send(line + "\n");
			}
		}
		br.close();
		readFile.append("\n");
		return readFile.toString();
	}

	//public NewAPMaudeState(String command, String model, long seed)
	/*public MISSCELState(String command, String model, ArrayList<Integer> seeds)
	{
		this(command,model);
		this.setSeeds(seeds);
	}*/
		
	//new
	/*public MISSCELState(String command, String model, String rootModule, String howNonDeterminismIsSolved)
	{
		this.command = command;
		this.model = model;
		this.rootModule = rootModule;
		this.setNumberOfSteps(0);
		//this.stepOfStoredTime = -1;
		this.term=null;
		
		if((howNonDeterminismIsSolved == null) || (howNonDeterminismIsSolved.equals(""))){
			System.out.println("No information about how to solve nondeterminism has been given. We fix the default value 'ndRndA' (i.e. we generate all the one-step next states, and we choose one of them with uniform probability)");
			this.howNondeterminismIsSolved = HowNonDeterminismIsSolved.NDRNDA;
		} else {
			if((!howNonDeterminismIsSolved.equalsIgnoreCase("ndRndA")) && (!howNonDeterminismIsSolved.equalsIgnoreCase("cpRndA"))  && (!howNonDeterminismIsSolved.equalsIgnoreCase("ndRcpA"))  && (!howNonDeterminismIsSolved.equalsIgnoreCase("cpRcpA"))){
				System.out.println("A wrong information has been provided about how to solve nondeterminism("+ howNonDeterminismIsSolved +"). Please, choose a value among {ndRndA,cpRndA,ndRcpA,cpRcpA}");
				System.exit(-1);
			} else {
				this.howNondeterminismIsSolved = HowNonDeterminismIsSolved.valueOf(howNonDeterminismIsSolved.toUpperCase());
			}
		}		
		rules = generateListOfRuleNames();
	}*/
	
	private ArrayList<String> generateListOfRuleNames(){
		ArrayList<String> rulesTMP = new ArrayList<String>();
		rulesTMP.add("newc");
		rulesTMP.add("freshn");
		rulesTMP.add("lgetANDlqry");
		rulesTMP.add("lput");
		rulesTMP.add("ptpgetANDptpqry");
		rulesTMP.add("ptpput");
		rulesTMP.add("grgetANDgrqry");
		rulesTMP.add("grput");
		return rulesTMP;
	}

	/*	public NewAPMaudeState(double t, SpawnedProcess maude, String term) {
		this.t = t;
		this.maude = maude;
		this.term = term;
	}
	 */	

	private String getNextState(String executionToken) throws ExpectJException{
		int beginNumberStates;
		int endNumberStates;
		int beginChosenState;
		int endChosenState;
		int numberOfStates;
		int numberChosenState;
		String labelChosenState;
		int posLabelState;
		
		//compute all the states reachable in one step, given the exectuion token
		String maudeCommand = "search in "+ rootModule + " : { "+ executionToken + " || " + term + " } =>1 X:ClosedSystem .";
		this.maude.send(maudeCommand + "\n");
		String oneStepNextStates = this.maude.maudeStepSearch();
		//String oneStepNextStates = this.maude.maudeStepSearchOld();
		if (oneStepNextStates == null || oneStepNextStates.equals("")){
			System.out.println("Error in computing the one-step next states. This is the obtained output:\n" + oneStepNextStates);
			throw new AnyException("Error in computing the one-step next states. This is the obtained output:\n" + oneStepNextStates);
		}
		
		//uniformly select one of the generated states 
		beginNumberStates = oneStepNextStates.lastIndexOf("Solution ");
		if(beginNumberStates == -1){
			//then I either have 1 solution, or 0.
			//in case I have one
			beginChosenState = oneStepNextStates.indexOf("--> ");

			if(beginChosenState == -1){
				//no more results found. I can stop the simulation and return the current state
				//System.out.println("It has not been possible to generate any new state with the token "+executionToken+". The next state is hence the current one. We can however have further states with different executionTokens");
				return noMoreStates;
			}

			beginChosenState = beginChosenState + 4;
			endChosenState = oneStepNextStates.indexOf("}\n", beginChosenState);
			endChosenState = endChosenState + 1;
			
			//term = oneStepNextStates.substring(beginChosenState + 1,endChosenState - 1);
			return oneStepNextStates.substring(beginChosenState + 1,endChosenState - 1);
		}
		else{
			//then I have more than 1 solution
			//System.out.println("beginNumberStates="+oneStepNextStates.substring(beginNumberStates + 9));
			endNumberStates = oneStepNextStates.indexOf(" ", beginNumberStates + 9);
			//System.out.println("endNumberStates="+oneStepNextStates.substring(endNumberStates - 1, endNumberStates));
			//System.out.println("numberOfStates="+oneStepNextStates.substring(beginNumberStates + 9, endNumberStates));
			numberOfStates = Integer.valueOf(oneStepNextStates.substring(beginNumberStates + 9, endNumberStates));
			//System.out.println("Number of states = "+numberOfStates);
			numberChosenState = (randomGenerator.nextInt(numberOfStates) + 1);
			labelChosenState = "Solution " + numberChosenState;
			if(labelChosenState.equals("Solution 1")){
				posLabelState = -1;
			}
			else{
				posLabelState = oneStepNextStates.indexOf(labelChosenState);
			}

			beginChosenState = oneStepNextStates.indexOf("--> ",posLabelState);
			beginChosenState = beginChosenState + 4;
			endChosenState = oneStepNextStates.indexOf("}\n", beginChosenState);
			endChosenState = endChosenState + 1;

			//term = oneStepNextStates.substring(beginChosenState + 1,endChosenState - 1);
			return oneStepNextStates.substring(beginChosenState + 1,endChosenState - 1);
		}
	}
	
	private ArrayList<String> probabilisticallySortRules(RandomEngineFacilities randomGenerator) {
		@SuppressWarnings("unchecked")
		ArrayList<String> tmpRules = (ArrayList<String>)rules.clone();
		int tmpRulesSize = tmpRules.size();
		ArrayList<String> probabilisticallySortedRules = new ArrayList<String>(tmpRulesSize);
		int chosenPos;
		for(int i = 0; i < tmpRulesSize; i++){
			chosenPos = randomGenerator.nextInt(tmpRules.size());
			probabilisticallySortedRules.add(tmpRules.remove(chosenPos));
		}

		return probabilisticallySortedRules;
	}
	
	private ArrayList<String> obtainAndProbabilisticallySortSCIdsViaJava(RandomEngineFacilities randomGenerator) {

		//< tId('SCId) ; av(id('
		ArrayList<String> probabilisticallySortedSCIds;
		ArrayList<String> scidsTmp = new ArrayList<String>();
        String currentState = term;
        
		int posOfNextId = currentState.indexOf("< tId('SCId) ; av(id('");
		int endOfNextId;
		while(!(posOfNextId == -1)){
			currentState = currentState.substring(posOfNextId + 22);
			endOfNextId = currentState.indexOf(")");
			scidsTmp.add(currentState.substring(0, endOfNextId));
			posOfNextId = currentState.indexOf("< tId('SCId) ; av(id('");
		}

		probabilisticallySortedSCIds = new ArrayList<String>(scidsTmp.size());

		int scidsTmpSize = scidsTmp.size();
		int chosenPos;
		for(int i = 0; i < scidsTmpSize; i++){
			chosenPos = randomGenerator.nextInt(scidsTmp.size());
			probabilisticallySortedSCIds.add(scidsTmp.remove(chosenPos));
		}
		return probabilisticallySortedSCIds;
	}
	
	/*private void readAndStoreTime(){
		String ret = null;
		try {
			this.maude.send("red getTime(" + this.term + ") .\n");
			ret = this.maude.maudeStep();
		} catch (ExpectJException e) {
			System.out.println("\n\nZZZ\n\n");
			throw new AnyException("Error in getting the current virtual time of the simulation: \n", e);
		}
		
		if (ret == null) 
			throw new AnyException("Error in getting the current virtual time of the simulation: \n", new Exception("Error in getting the current virtual time of the simulation: ret is null"));
		
		//new
		if (ret.startsWith("result FiniteFloat:")) {
			String time = ret.replaceFirst("result FiniteFloat:", "").trim();
			this.t = Double.parseDouble(time);
			stepOfStoredTime = getNumberOfSteps();
		} else if(ret.startsWith("result Float:")) {
			String time = ret.replaceFirst("result Float:", "").trim();
			this.t = Double.parseDouble(time);
			stepOfStoredTime = getNumberOfSteps();
		} else{
			System.out.println("\n\nGGG\n\n");
			throw new AnyException("Error in invoking and executing maude: \n" + ret);
		}
	   	
	}*/
	
	public String getTerm(){
		return this.term;
	}

	public void stop() {
		this.maude.stop();
	}

	@Override
	public String toString() {
		//return "Time: "+getTime() + ", Steps: "+getNumberOfSteps()  + ", Final state: "+getTerm();
		return "Steps: "+getNumberOfSteps()  + ", current state: "+getTerm();
	}

	/*public static void main(String[] args) {
		NewAPMaudeState s = new NewAPMaudeState("maude", args[0], (int)System.currentTimeMillis());
		int n = Integer.parseInt(args[1]);
		for (int i = 0; i < n; i++) {
			s.next();
		}
		s.stop();
	}*/
}