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

$ java -Djava.library.path=. -jar ARGoSMultiVestaTesting.jar

The ARGoS GUI should appear and allow you to run a script. Click on File->Open
and select the file argos3-multivesta/src/testing/gaslike.lua. Run it, and the robots
will perform obstacle avoidance.

Running MultiVeStA
==================

TODO

$ javac -cp ./multivesta/multivesta.jar:./  ./multivesta/ARGoSState.java

$ javac -cp ./multivesta/multivesta.jar:./  ./testing/ARGoSMultiVestaTesting.java

$ java -cp ./multivesta/multivesta.jar:./  multivesta.ARGoSState 

$ java -cp ./multivesta/multivesta.jar:./  testing.ARGoSMultiVestaTesting
