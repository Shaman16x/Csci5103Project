package nachos.pa1;

import nachos.threads.*;
import nachos.machine.Config;

// performs some basic verification tests
public class SchedTest1 implements Runnable{
    
    public SchedTest1(int which) {
    }

	public void run() {
        System.out.println("Scheduler Test Suite 1");
        
        //TODO: make a better test framework out of this
        testThreadState();
	}
    
    // Tests that ThreadState provides the necessary functions
    private static boolean testThreadState(){
        System.out.print("ThreadState...........................");
        
        KThread thread = new KThread();
        int priority = 10;
        ThreadState test = new ThreadState(thread, priority);
        
        if(test.getThread() != thread) {
            System.out.println("Failed");
            System.out.println("Thread not assigned correctly");
            return false;
        }
        if(test.getPriority() != priority) {
            System.out.println("Failed");
            System.out.println("Thread not assigned correctly");
            return false;
        }
        System.out.println("Passed");
        return true;
    }
}
