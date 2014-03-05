Jeremiah Thomas:4145047
Kyle Michaels:3914066

How To Compile

	In the Nachos-Java directory type make.

How To Run Schedulers
Tests
	Static Priority
		After compiling, type
		%java nachos.machine.Machine -[] pa1_sptest.conf
		sptest.conf is the configuration file for Static Priority

	Dynamic Priority
		After compiling, type
		%java nachos.machine.Machine -[] pa1_dptest.conf
		dptest.conf is the configuration file for Dynamic Priority

	Multi-Level
		After compiling, type
		%java nachos.machine.Machine -[] pa1_mltest.conf
		mltest.conf is the configuration file for Multi-Level

These tests will create log files in which you can see when each thread was scheduled and finished.
The log files are located in the Nachos-Java directory and named:
	Static Priority: "SPLogFile.txt"
	Dynamic Priority: "DPLogFile.txt"
	Multi-Level: "MLLogFile.txt"

To use these Schedulers in Nachos change "ThreadedKernel.scheduler = nachos.threads.RoundRobinScheduler"
in the nachos.conf file in Nachos-Java directory to equal our Schedulers.
Nachos
	ThreadedKernel.scheduler = nachos.pa1.StaticPriorityScheduler
	ThreadedKernel.scheduler = nachos.pa1.DynamicPriorityScheduler
	ThreadedKernel.scheduler = nachos.pa1.MultiLevelScheduler

The configuration parameters added were added for all Schedulers and are as follows:
	runTests: When true, this tells KThread.selfTest() to run user tests
	testNumber: ?
	scheduler.maxPriorityValue: sets the maxPriorityValue of the Scheduler if it has a value
	scheduler.agingTime: sets the agingTime value of the Scheduler if it has a value
	statistics.logFile: specifies the name of the log File to be created.

All of Scheduler files are located in Nachos-Java/nachos/pa1.  We also added functionality to KThread
to meet the requirments of the assignment.
