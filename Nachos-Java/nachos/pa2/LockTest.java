package nachos.pa2;

import nachos.threads.*;

// Testing program for pa2
// When executed this program:
// gets a lock,
// waits x miliseconds
// and releases the lock

public class LockTest implements Runnable {
    public LockTest(String name, int time, Lock lock) {
        this.time = time;
        this.name = name;
        this.lock = lock;
    }

    // Aqu
    public void run() {
        lock.acquire();
        for (int i=0; i<time; i++) {
            waitMS();
            
            if(i%5 == 0)   {
                System.out.println("*** " + name + " waited "
                       + i + " ms");
            }
            KThread.currentThread().yield();
        }
        System.out.println("*** " + name + " has finished");
        lock.release();
    }

    // wait for 1ms
    public void waitMS(){
        long start = System.nanoTime();
        
        while((System.nanoTime() - start) < 1000000);
    }
    
    private String name;
    private int count = 0;
    private int time;
    private Lock lock;
}
