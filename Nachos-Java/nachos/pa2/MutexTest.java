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

public class MutexTest implements Runnable {
    public MutexTest(Lock lock1, int count) {
        this.lock1 = lock1;
        this.count = count;
    }

    // Aqu
    public void run() {
        int temp = 0;
        if(lock1 != null){
            for(int i = 0; i < 5; i++) {
                lock1.acquire();
                temp = 1 + sharedTotal;
                KThread.currentThread().yield();
                sharedTotal = temp;
                System.out.println("Shared Total is " + sharedTotal);
                lock1.release();
            }
        }
    }
    
    private int count = 0;
    private Lock lock1;
    static private int sharedTotal = 0;
}
