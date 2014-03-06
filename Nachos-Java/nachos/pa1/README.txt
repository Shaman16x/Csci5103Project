Jeremiah Thomas:4145047
Kyle Michaels:3914066

How To Compile
----------------------------------------------------------------------------
	In the Nachos-Java directory type "make".


How To Run Schedulers
----------------------------------------------------------------------------
To use these Schedulers in Nachos change "ThreadedKernel.scheduler = nachos.threads.RoundRobinScheduler"
in the nachos.conf file in Nachos-Java directory to equal our Schedulers.
Nachos
	ThreadedKernel.scheduler = nachos.pa1.StaticPriorityScheduler
	ThreadedKernel.scheduler = nachos.pa1.DynamicPriorityScheduler
	ThreadedKernel.scheduler = nachos.pa1.MultiLevelScheduler

In addtion,
The configuration parameters added were added for all Schedulers and are as follows:
	scheduler.maxPriorityValue: sets the maxPriorityValue of the Scheduler if it has a value
	scheduler.agingTime: sets the agingTime value of the Scheduler if it has a value
	statistics.logFile: specifies the name of the log File to be created.
	runTests: When true, this tells KThread.selfTest() to run user tests
    printDebug: When true, additional information will be printed to the logfile


File Locations/Modifications
-----------------------------------------------------------------------------
All files that implement the new Schedulers are located in
Nachos-Java/nachos/pa1.  
All new configuration files and bash scripts are located in Nachos-Java

In addition to new files, we modified the existing files
*Machine.java
*KThread.java
*Makefile


Tests
-------------------------------------------------------------------------------
To perform tests the selfTest function KThread was modified to create senarios based which scheduler is used at the time.
To run these tests run the each new conf file (pa1_*.conf) added to Nachos-Java/
EG:
%java nachos.machine.Machine -[] pa1_sptest.conf

Conversely, run the bash script "runTests.sh" to run all tests automaticly.

This will generate two sets of test files.  One file per scheduler per set.
The *LogFile.txt files  outputs as expect by the assignment.
And *TestFile.txt files output additional information that help understanding the results.

To ouput logs to the terminal run the pa1_*console.conf configurations.
The output is iden

Test Threads
    All test threads created are DelayTest objects.  This class will wait a specified number of miliseconds.  
Every 5ms controll is yielded so that the scheduler can run.  This design allows us to run test for enough time for aging to take effect in the dynamic and multilevel schedulers.

Static Priority
	The test for static priority creates four thread of various output (priorities 7, 6, 6, 5).  The output generated shows that the threads with the high priority is executed before other and that threads of equal priority trade off runtime in a round robin fashion.

Dynamic Priority
	Dynamic Priorities test verfies that a running thread is "aged" correctly. Two thread are created, one with high priority and one with lower.  As shown in the test files, the high priority thread will execute and it priority will decrease. 
Around priority level 5, the high priority and low priority (which has been gaining priority) will meet and trade off running time.

Multi-Level
	Two threads are create with priorities 0 and 23 (q level 0 and 2 respectively).
As shown in the output, the first thread continues to age until it moves into the level one q.  At this point, the second thread as well as main and ping threads will elevate to level 1.  Here we will see threads trading running time in a round robin fasion.

These tests will create log files in which you can see when each thread was scheduled and finished.
The log files are located in the Nachos-Java directory and named:
	Static Priority: "SPLogFile.txt", "SPTestFile.txt"
	Dynamic Priority: "DPLogFile.txt", "DPTestFile.txt"
	Multi-Level: "MLLogFile.txt", "MLTestFile.txt"


Logging Issues
----------------------------------------------------------------------------------
Due to the way main and ping thread are schedule, there are a few strange results that occur in the satistics when they finish.

Both thread types will be unscheduled for brief periods of time.  They are neither
running nor are they waiting.
This causes the end time for these threads to be less than the current time.
This is because the end time is calculated as the start time plus the wait and run
time.









