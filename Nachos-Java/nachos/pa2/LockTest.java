package nachos.pa2;

import nachos.threads.*;
import nachos.machine.Machine;

// Testing program for pa2
// When executed this program:
// sets its priority to the correct value
// gets a lock,
// waits x miliseconds
// and releases the lock

// IMPORTANT: to function correctly, set each thread to the max priority
// value first
// that the thread will immediately set its own priority

public class LockTest implements Runnable {
    public LockTest(String name, int priority, int time, Lock lock1, Lock lock2) {
        this.time = time;
        this.name = name;
        this.lock1 = lock1;
        this.lock2 = lock2;
        this.priority = priority;
    }

    // Aqu
    public void run() {
        Machine.interrupt().disable();
        ThreadedKernel.scheduler.setPriority(KThread.currentThread(), priority);
        // Make sure threadState exists
        ThreadQueue waitQueue =	ThreadedKernel.scheduler.newThreadQueue(true);
        waitQueue.waitForAccess(KThread.currentThread());
        if(lock1 != null)
            lock1.acquire();
        if(lock2 != null)
            lock2.acquire();
        Machine.interrupt().enable();

        KThread.currentThread().yield();

        for (int i=0; i<time; i++) {
            waitMS();
        
            if(lock1 != null)
                sharedTotal++;
            
            if(lock1 != null && i%5 == 0)   {
                System.out.println("Shared total is at " + sharedTotal);
            }
            KThread.currentThread().yield();
        }
        if(lock1 != null);
            System.out.println("Shared total is at " + sharedTotal);
        if(lock2 != null)
            lock2.release();
        if(lock1 != null)
            lock1.release();
    }

    // wait for 1ms
    public void waitMS(){
        long start = System.nanoTime();
        
        while((System.nanoTime() - start) < 1000000);
    }
    
    private String name;
    private int count = 0;
    private int priority;
    private int time;
    private Lock lock1;
    private Lock lock2;
    static private int sharedTotal = 0;
}
