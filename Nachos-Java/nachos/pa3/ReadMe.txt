Programming Assignment 3
-------------------------------------------------------------------------------
Jeremiah Thomas:4145047
Kyle Michaels:3914066


How To Compile
-------------------------------------------------------------------------------
In the Nachos-Java directory type "make".


How to Run
-------------------------------------------------------------------------------
To run this code use any of the following conf files
    * pa3_x200.conf
    * pa3_x400.conf
    * pa3_x800.conf
    * pa3_smallMemory.conf
    * pa3_mediumMemory.conf
    

To create your own conf files first take one of the provided conf.
Then to modify the behavior of the program you can change:
    * Processor.pageSize -  determines page size
    * Processof.numPhysPages - determines number of phsyical memory pages
    * Kernel.shellPrograms - determine what user programs to run

Notes: 
User programs MUST be compiled to use that pageSize.

ShellPrograms must be formatted as follows.
    #:<prog1>,<prog2>,...

You will also need to specify the appropriate location of the Directory
containing user programs by setting "FileSystem.testDirectory"


File Locations
-------------------------------------------------------------------------------
The New class MemoryAllocator is located in "Nachos-Java/nachos/pa3"

Modified files UserProces and UserKernel are in "Nachos-Java/nachos/userprog"

Modified file Processor is located in "Nachos-Java/nachos/machine"

The test user programs are located in:
"Nachos-Java/nachos/test/x###"
"x###" determines the page size of programs (eg x200 -> 0x200 -> 512 bytes)


Potential Errors
-------------------------------------------------------------------------------
A know error with nachos is when too many programs are loaded at once.
This causes an security error within the Nachos system.
To avoid this all conf files run a safe number of programs.


Debug Output
-------------------------------------------------------------------------------
To better understand the operation of the userKernel some debug information
has been left in the console output.
These are:
    * <Attempting to load (program name)>
    * <(program name) has loaded successfully>
    * <Pagefault>


Tests
-------------------------------------------------------------------------------
Test for this assignment were run using the conf files mentioned in the how to
run section of this document.  There are additional conf files in the analysis
folder however they are only used for performance analysis and will not be
mentioned in this document.

All tests were ran under the same conditions.  Two copies of two programs
(test.coff and safematmult.coff) were run.  "test.coff" simply exits with the 
number 12345 and "safematmult.coff" preforms a matrix multiplication before
exiting.

The "pa3_x###.conf" files test valid running conditions with pages of 
varrying size.

x400 represents the original page size of the program (1024 bytes).  x200 and
x800 are half (512 bytes) and double (2048 bytes)

Output from pa3_x400.conf
===============================================================================
<Attempting to load test.coff>
<test.coff has loaded successfully>
<pagefault>
test.coff,exit,2,12345
<Attempting to load test.coff>
<test.coff has loaded successfully>
<pagefault>
test.coff,exit,3,12345
<Attempting to load safematmult.coff>
<safematmult.coff has loaded successfully>
<pagefault>
<Attempting to load safematmult.coff>
<safematmult.coff has loaded successfully>
<Kernel has finished loading
<pagefault>
safematmult.coff,exit,4,0
safematmult.coff,exit,5,0


PA3 Statistics
Maximum Processes: 2
Maximum Reserved Pages: 30
Maximum Mapped Frames: 14
Machine halting!

Ticks: total 180802, kernel 19260, user 161542
Disk I/O: reads 0, writes 0
Console I/O: reads 0, writes 0
Paging: page faults 4, TLB misses 0
Network I/O: received 0, sent 0
===============================================================================

Was we can see all programs load and exit successfully.
The number of page faults matches the number of times <pagefault> occurs in
the program.  The maximum number of cocurrent processes match
We also see the statistics at the bottom report.

The meaning of these statistics is given in depth with the design document.

"pa3_littleMemory.conf" tests when programs require more frames than what is
in physical memory.

Output from "pa3_littleMemory.conf"
===============================================================================
<Attempting to load test.coff>
<test.coff has loaded successfully>
<pagefault>
test.coff,exit,2,12345
<Attempting to load test.coff>
<test.coff has loaded successfully>
<pagefault>
test.coff,exit,3,12345
<Attempting to load safematmult.coff>
safematmult.coff,reject,21
<Attempting to load safematmult.coff>
safematmult.coff,reject,21
<Kernel has finished loading


PA3 Statistics
Maximum Processes: 1
Maximum Reserved Pages: 10
Maximum Mapped Frames: 2
Machine halting!

Ticks: total 22142, kernel 22110, user 32
Disk I/O: reads 0, writes 0
Console I/O: reads 0, writes 0
Paging: page faults 2, TLB misses 0
Network I/O: received 0, sent 0
===============================================================================

In this test, there is only 20 physical pages available.  However, safematmult
requires 21.  This means that the programs are rejected by the system
immediately after the program attempts to load.

We also se that the max reserved frame is lower than the previous test
because test.coff require fewer frames for execution.

This test shows that the system successfully rejects programs that are too
memory heavy to run.

"pa3_mediumMemory.conf" Tests the situation where there is enough memory within
to run a program, but not both at the same time.

Output from "pa3_mediumMemory.conf"
===============================================================================
<Attempting to load test.coff>
<test.coff has loaded successfully>
<pagefault>
test.coff,exit,2,12345
<Attempting to load test.coff>
<test.coff has loaded successfully>
<pagefault>
test.coff,exit,3,12345
<Attempting to load safematmult.coff>
<safematmult.coff has loaded successfully>
<pagefault>
<Attempting to load safematmult.coff>
safematmult.coff,exit,4,0
<safematmult.coff has loaded successfully>
<Kernel has finished loading
<pagefault>
safematmult.coff,exit,5,0


PA3 Statistics
Maximum Processes: 1
Maximum Reserved Pages: 21
Maximum Mapped Frames: 13
Machine halting!

Ticks: total 184512, kernel 22970, user 161542
Disk I/O: reads 0, writes 0
Console I/O: reads 0, writes 0
Paging: page faults 4, TLB misses 0
Network I/O: received 0, sent 0
===============================================================================

As shown in the output all programs run succesffuly.  However, only one 
instance of safematmult.coff runs at a time.

As seen in the output, the second safematmult attempts to load.  It successfully
loads immediately after the first one has exited and released the memory needed.

This leads to the one max process at any time while running.

This means program successfully forces programs to wait until there is enough 
unreserved memory to run.

-------------------------------------------------------------------------------

For thorough analysis of the paging system, refer to Performance Analysis
section of Design.txt



