Jeremiah Thomas:4145047
Kyle Michaels:3914066

How To Compile
----------------------------------------------------------------------------
In the Nachos-Java directory type "make".

How to Run
----------------------------------------------------------------------------
To properly use static priority scheduling and priority donation the 
following configuration must be added to the file run by nachos
    ThreadedKernel.scheduler = nachos.pa1.StaticPriorityScheduler
    Locks.Locks.usePriorityDonation = true

We have provided a few configuration files to run them:
    pa2_noDonation.conf
    pa2_cascadeDonation.conf
    pa2_noDonationDebug.conf
    pa2_cascadeDonationDebug.conf

The noDonation file run tests under a situation where donation does not
occur.  The cascadeDonation file run with donation on.
The first two files produce output specified by the assignment.
The last two produce modified output for easier interpretation:
    * Thread names are display alongside ids
    * Remove output caused by main and ping threads.

We have decided to keep output caused by our tests as they do not interfere 
with the interrpretation of results.

File Locations
----------------------------------------------------------------------------
* All new java files are located in Nachos-Java/nachos/pa2
* The configuration files are found in Nachos-Java
* Lock.java and KThread.java can be found in Nachos-Java/nachos/


Tests
----------------------------------------------------------------------------
To run a test showing mutual exclusion type:
    % java nachos.machine.Machine -[] pa2_mutexTestDebug.conf

While mutual exclusion should still be maintained from the original Lock
module, this test helps prove that it still works.

This test runs two threads that increment a shared total.  To do this they
aquire a lock, set a temporary value to the total plus one, yields, 
and then assign the total to the temporary value, prints the total and
releases the lock.  By yielding, the thread gives control to it's sibling 
which excutes until it yields.

If mutual exclusion did not function properly, then both thread would save
the same temporary value.  This would cause the total to be assigned the same
value twice.

As observed in the output, the total is incremented normally and so
mutual exclusion is maintained.



To run donation tests type:
    % java nachos.machine.Machine -[] pa2_noDonationDebug.conf
    % java nachos.machine.Machine -[] pa2_cascadeDonationDebug.conf


The senarios for these tests are as follows:
There 5 threads that are assign up to 2 locks in the following order.
    Low   -> L0         -> priority 4
    Mid   -> L1, L0     -> priority 3
    Mid2  -> none       -> priority 3
    high  -> L1         -> priority 2
    high2 -> L1         -> priority 2

These are scheduled using the static priority scheulder and under one of
the two set ups.

The first test runs nachos with no donation.  In this test we see that mid2 is
executed first, Followed by Low, Mid, and the two high priority task.
This is an example of priority inversion.  High and high2 are waiting for mid
which waits for low.  However, mid2 waits for no one and has higher priority
than low, therefore runs first.

Note that the execution order still goes in the order of aquire calls.
This shows that the lock implements FCFS.

The second test runs nachos with donation.  This time the execution order is
low, mid, high and high2, and finally mid2.  Low and mid execute before mid2
because high and high2 donate their priority to mid which in turn donates
its donated priority to low.  This can be seen by observing the priority values
output by program.

These results show that cascading donation is achieved because low has the
donated priority of high, which it would not have without cascading donation.




