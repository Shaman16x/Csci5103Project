package nachos.pa1;

import nachos.threads.*;
import nachos.machine.*;


// performs some basic verification tests
public class SchedTest1 implements Runnable{
    
    public SchedTest1(int which) {
    }

	public void run() {
        System.out.println("Scheduler Test Suite 1");
        
        //TODO: make a better test framework out of this
        testThreadState();
        testScheduler();
        testQueue();
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
    
    private static boolean testScheduler(){
        System.out.println("testScheduler....................");
        StaticPriorityScheduler sched = new StaticPriorityScheduler();
        KThread t0 = new KThread();
        KThread t1 = new KThread();
        KThread t2 = new KThread();
        t0.setName("Thread0");
        t1.setName("Thread1");
        t2.setName("Thread2");
        Machine.interrupt().disable();
        sched.setPriority(t0, 3);
        sched.setPriority(t1, 1);
        sched.setPriority(t2, 6);
        Machine.interrupt().enable();
        System.out.println("maxPriority: "+sched.maxPriorityValue);
        for(ThreadState s:sched.states)
            System.out.println(s);
        return true;
    }
    private static boolean testQueue(){
        System.out.println("testQueue.....................");
        StaticPriorityScheduler sched = new StaticPriorityScheduler();
        StaticPriorityQueue q = sched.newThreadQueue(false);
        KThread t0 = new KThread();
        KThread t1 = new KThread();
        KThread t2 = new KThread();
        t0.setName("Thread0");
        t1.setName("Thread1");
        t2.setName("Thread2");
        Machine.interrupt().disable();
        sched.setPriority(t0, 3);
        sched.setPriority(t1, 1);
        sched.setPriority(t2, 6);
        Machine.interrupt().enable();
        q.waitForAccess(t0);
        q.waitForAccess(t1);
        q.waitForAccess(t2);
        KThread thread;
        while((thread = q.nextThread()) != null)
            System.out.println(thread);
        return true;
    }

}
