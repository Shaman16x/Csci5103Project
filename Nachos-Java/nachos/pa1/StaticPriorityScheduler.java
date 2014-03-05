package nachos.pa1;

import nachos.threads.*;
import nachos.machine.*;
import java.util.*;
import java.io.*;

/**
 * Static priority scheduler used for scheduling thread
 * threads are assigned a priority once and are 
 * ran based on that order
 *
 * @see	nachos.threads.ThreadQueue
 */
public class StaticPriorityScheduler extends Scheduler{

	protected static ArrayList<ThreadState> states = new ArrayList<ThreadState>();

	protected int maxPriorityValue = 10;
	protected int minPriorityValue = 0;
    protected long startTime = System.nanoTime();           // beginning of scheduler's life
    protected long prevTime = System.nanoTime();            //stores the previous time call.
    File outfile = null;
    FileWriter file;
    PrintWriter writer;
    
    
    // for debugging purposes
    public int getMaxPriorityValue(){
        return maxPriorityValue;
    }

    // gets the age of the scheduler in ms
    public int getSchedulerTime() {
        return (int) ((System.nanoTime() - startTime) / 1000000);
    }

    //TODO: allow for printing to a file
    
    // prints stats about the scheduled thread
    public void printScheduledThread(ThreadState thread){
        if(outfile != null){
            try{
            file = new FileWriter(outfile, true);
            writer = new PrintWriter(file);
            writer.println(getSchedulerTime() + "," + thread.getThread().getName() +":"+thread.getThread().getID()+ "," + thread.getPriority());
            writer.close();
            }catch(IOException e){}
        }
        else
            System.out.println(getSchedulerTime() + "," + thread.getThread().getName()+":"+thread.getThread().getID()+ "," + thread.getPriority());
    }

    // prints the final stats of a thread that has executed
    public void printThreadStats(ThreadState thread){
        if(outfile != null){
            try{
            file = new FileWriter(outfile, true);
            writer = new PrintWriter(file);
            writer.println(thread.getStats());
            writer.close();
            }catch(IOException e){}
        }
        else
            System.out.println(thread.getStats());
        
    }

    // Prints Final statistics of the scheduler
    public void printFinalStats(){
        //TODO: write me
        System.out.println("Done!");
    }

    /**
     * Allocate a new scheduler.
     */
    public StaticPriorityScheduler() {
        Integer i = Config.getInteger("scheduler.maxPriorityValue");
        if(i != null)
            maxPriorityValue = i;
            
        String filename = Config.getString("statistics.logFile");
        if(filename != null){
            try{
                outfile = new File(filename);
                file = new FileWriter(outfile);
                writer = new PrintWriter(file);
                writer.close();
            }catch(IOException e){}
        }
        else
            System.out.println("did this happen");
        
    }
    
    /**
     * Allocate a new thread queue. If <i>transferPriority</i> is
     * <tt>true</tt>, then threads waiting on the new queue will transfer their
     * "priority" to the thread that has access to whatever is being guarded by
     * the queue. This is the mechanism used to partially solve priority
     * inversion.
     *
     * <p>
     * If there is no definite thread that can be said to have "access" (as in
     * the case of semaphores and condition variables), this parameter should
     * be <tt>false</tt>, indicating that no priority should be transferred.
     *
     * <p>
     * The processor is a special case. There is clearly no purpose to donating
     * priority to a thread that already has the processor. When the processor
     * wait queue is created, this parameter should be <tt>false</tt>.
     *
     * <p>
     * Otherwise, it is beneficial to donate priority. For example, a lock has
     * a definite owner (the thread that holds the lock), and a lock is always
     * released by the same thread that acquired it, so it is possible to help
     * a high priority thread waiting for a lock by donating its priority to
     * the thread holding the lock. Therefore, a queue for a lock should be
     * created with this parameter set to <tt>true</tt>.
     *
     * <p>
     * Similarly, when a thread is asleep in <tt>join()</tt> waiting for the
     * target thread to finish, the sleeping thread should donate its priority
     * to the target thread. Therefore, a join queue should be created with
     * this parameter set to <tt>true</tt>.
     *
     * @param	transferPriority	<tt>true</tt> if the thread that has
     *					access should receive priority from the
     *					threads that are waiting on this queue.
     * @return	a new thread queue.
     */
    public StaticPriorityQueue newThreadQueue(boolean transferPriority) {
        StaticPriorityQueue q = new StaticPriorityQueue();
        q.setParentScheduler(this);
        return q;
    }

    /**
     * Get the priority of the specified thread. Must be called with
     * interrupts disabled.
     *
     * @param	thread	the thread to get the priority of.
     * @return	the thread's priority.
     */
    public int getPriority(KThread thread) {
        Lib.assertTrue(Machine.interrupt().disabled());

        ThreadState s = getThreadState(thread);
        
        return s.getPriority();
    }

    public ThreadState getThreadState(KThread thread){
        ThreadState state;

        for(ThreadState s : states){
            if(s.getThread().compareTo(thread) == 0){
                return s;
            }
        }

        state = new ThreadState(thread,maxPriorityValue, maxPriorityValue, 0); 
        states.add(state);

        return state;
    }

    /**
     * Get the priority of the current thread. Equivalent to
     * <tt>getPriority(KThread.currentThread())</tt>.
     *
     * @return	the current thread's priority.
     */
    public int getPriority() {
        return getPriority(KThread.currentThread());
    }

    /**
     * Get the effective priority of the specified thread. Must be called with
     * interrupts disabled.
     *
     * <p>
     * The effective priority of a thread is the priority of a thread after
     * taking into account priority donations.
     *
     * <p>
     * For a priority scheduler, this is the maximum of the thread's priority
     * and the priorities of all other threads waiting for the thread through a
     * lock or a join.
     *
     * <p>
     * For a lottery scheduler, this is the sum of the thread's tickets and the
     * tickets of all other threads waiting for the thread through a lock or a
     * join.
     *
     * @param	thread	the thread to get the effective priority of.
     * @return	the thread's effective priority.
     */
    public int getEffectivePriority(KThread thread) {
        Lib.assertTrue(Machine.interrupt().disabled());
        return 0;
    }

    /**
     * Get the effective priority of the current thread. Equivalent to
     * <tt>getEffectivePriority(KThread.currentThread())</tt>.
     *
     * @return	the current thread's priority.
     */
    public int getEffectivePriority() {
	return getEffectivePriority(KThread.currentThread());
    }

    /**
     * Set the priority of the specified thread. Must be called with interrupts
     * disabled.
     *
     * @param	thread	the thread to set the priority of.
     * @param	priority	the new priority.
     */
    public void setPriority(KThread thread, int priority) {
        Lib.assertTrue(Machine.interrupt().disabled());
        boolean found = false;
        ThreadState state;

        if(priority>maxPriorityValue) priority = maxPriorityValue;
        if(priority<minPriorityValue) priority = minPriorityValue;

        for(ThreadState s : states){
            if(s.getThread().compareTo(thread) == 0){
                found = true;
                s.setPriority(priority);
            }
        }

        if(!found){
            state = new ThreadState(thread, priority, maxPriorityValue, 0);
            states.add(state);
        }
    }

    /**
     * Set the priority of the current thread. Equivalent to
     * <tt>setPriority(KThread.currentThread(), priority)</tt>.
     *
     * @param	priority	the new priority.
     */
    public void setPriority(int priority) {
        setPriority(KThread.currentThread(), priority);
    }

    /* 
     * updates ThreadStates base on how much time has changed
     * updates wait and running times
     * determines if a thread has finished
     */
    public void updateThreads(KThread currThread) {
        long time = System.nanoTime() - prevTime;
        prevTime = System.nanoTime();
        
        for(ThreadState s:states){
            if(s.status == ThreadState.QueueStatus.INQUEUE){
                s.waitTime += time;
            }
            else if(s.status == ThreadState.QueueStatus.CURRENT){
                s.runTime += time;
                if(s.thread.compareTo(currThread) != 0){
                    printThreadStats(s);
                    s.status = ThreadState.QueueStatus.LIMBO;
                }
            }
        }
            
        ThreadState st = getThreadState(currThread);
        st.status = ThreadState.QueueStatus.CURRENT;
    }

    /*
     * Defined to implement scheduler
     */
    public boolean increasePriority() {
        return false;
    }
    public boolean decreasePriority() {
        return false;
    }
}
