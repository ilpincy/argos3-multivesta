ARGoS3-MultiVeStA
=================

Integration between the ARGoS multi-robot simulator and the MultiVeStA distributed statistical analyzer.

Requirements
============

ARGoS >= 3.0.0-beta17
openjdk

Compiling the code
==================

$ mkdir build
$ cd build
$ cmake ../src
$ make

Running the test
================

While in the directory build/, type:

$ java -Djava.library.path=. -cp ARGoSMultiVestaTesting.jar testing.ARGoSMultiVestaTesting

The ARGoS GUI should appear and allow you to run a script. Click on File->Open
and select the file argos3-multivesta/src/testing/gaslike.lua. Run it, and the robots
will perform obstacle avoidance.

Running MultiVeStA
==================

***Running a MultiVeStA server
$ java -Djava.library.path=. -cp ARGoSMultiVestaTesting.jar vesta.mc.NewVestaServer PORTNUMBER
or
$ java -Djava.library.path=. -cp ARGoSMultiVestaTesting.jar entryPointMultivesta.UniqueEntryPoint server PORTNUMBER

where 	1) PORTNUMBER is the port where the server will receive the information from the client (at address ipoftheserver:PORTNUMBER)

e.g.:
$ java -Djava.library.path=. -cp ARGoSMultiVestaTesting.jar vesta.mc.NewVestaServer 49141



***Running the MultiVeStA clinet
$ java  -cp ARGoSMultiVestaTesting.jar vesta.NewVesta -sd multivesta.ARGoSState -m MODEL.argos -f QUERY.quatex -l SERVERSLIST -vp VISUALIZEPLOT -bs BLOCK_SIZE -a ALPHA -d1 DELTA -sots SEEDOFTHESEEDS -osws ONESTEPORWHOLESIMULATION -ds [d1,d2,d3...] -ms MAXNUMBEROFSIMULATIONS

$ java  -cp ARGoSMultiVestaTesting.jar entryPointMultivesta.UniqueEntryPoint client -sd multivesta.ARGoSState -m MODEL.argos -f QUERY.quatex -l SERVERSLIST -vp VISUALIZEPLOT -bs BLOCK_SIZE -a ALPHA -d1 DELTA -sots SEEDOFTHESEEDS -osws ONESTEPORWHOLESIMULATION -ds [d1,d2,d3...] -ms MAXNUMBEROFSIMULATIONS

where	1)MODEL.argos is the name of the file containing the model specification
	2)QUERY.quatex is the name of the file containing the specification of the property of interest
	3)SERVERSLIST is the name of the file containing the list of addresses of the MultiVeStA servers (one per line)
	4)VISUALIZEPLOT is true if we want to visualize the result of a parametric query in an interactive plot, or false if we just want the results to be provided in a gnuplot input file
	5)BLOCK_SIZE: how many simulations have to be perfomed before checking the confidence of interval (the analysis proceeds by iterations. At every iterations are performed BLOCK_SIZE simulations)
	6)ALPHA and DELTA: the alpha and delta specifying the required confidence interval (actually a list of deltas can be provided, if the parametric multi-expression regards properties with different orders of magnitude). With probability 1 - alpha, the actual expected value of the estimated property belongs to the interval [estimatedValue - delta/2, estimatedValue + delta/2]
	7)SEEDOFTHESEEDS: the seed used by the MultiVeStA client to generate the seeds of the necessary simulations

	SOME OPTIONAL PARAMETERS
	8)ONESTEPORWHOLESIMULATION: is ONESTEP if the simulation should be performed step-wise (i.e. MultiVeStA requires to perform single steps of simulation), or WHOLESIMULATION if MultiVeStA simply asks the simulator to perform a simulation
	9)di: each di is a delta used for one of the  properties. If this list is provided, then 1 di must be defined for each eval clause of the query, or for each "E[...]" in the keyword "parametric" of the query
	10)MAXNUMBEROFSIMULATIONS: the maximum number of simulations. In case the analysis terminates due to this constraint, the current confidence interval of each proeprty is also returned.

e.g.:
$ java -Djava.library.path=. -cp ARGoSMultiVestaTesting.jar vesta.NewVesta -sd multivesta.ARGoSState -m ../src/testing/diffusion_10B.argos -f ../quatex/expr1.quatex  -l ../serversLists/oneLocalServer -vp TRUE -bs 30 -a 0.1 -d1 2.0 -sots 0 -osws ONESTEP

or
$ java -Djava.library.path=. -cp ARGoSMultiVestaTesting.jar vesta.NewVesta -sd multivesta.ARGoSState -m ../src/testing/diffusion_10B.argos -f ../quatex/expr3.quatex  -l ../serversLists/oneLocalServer -vp TRUE -bs 3 -a 0.1 -d1 1.0 -sots 0 -osws ONESTEP  

or
$ java -cp ARGoSMultiVestaTesting.jar vesta.NewVesta -sd multivesta.ARGoSState -m ../src/testing/diffusion_10B.argos -f ../quatex/expr6.quatex  -l ../serversLists/oneLocalServer -vp TRUE -bs 30 -a 0.1 -d1 5.0 -sots 0 -osws ONESTEP

or
$ java -cp ARGoSMultiVestaTesting.jar vesta.NewVesta -sd multivesta.ARGoSState -m ../src/testing/diffusion_10B.argos -f ../quatex/expr6.quatex  -l ../serversLists/twoLocalServers -vp TRUE -bs 30 -a 0.1 -d1 5.0 -sots 0 -osws ONESTEP

or
$ java -cp ARGoSMultiVestaTesting.jar vesta.NewVesta -sd multivesta.ARGoSState -m ../src/testing/diffusion_10B.argos -f ../quatex/expr6.quatex  -l ../serversLists/oneLocalServer -vp TRUE -bs 30 -a 0.1 -d1 5.0 -sots 0 -osws WHOLESIMULATION