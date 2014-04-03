package nachos.pa2;

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

	protected static LinkedList<ThreadState> states = new LinkedList<ThreadState>();

	protected int maxPriorityValue = 10;                    // The max priority value for the queue
	protected int minPriorityValue = 0;                     // The min priority value for the queue
    protected long startTime = System.nanoTime();           // beginning of scheduler's life
    protected long prevTime = System.nanoTime();            // stores the previous time call.
    File outfile = null;                                    // log file
    FileWriter file;                                        // object used to write to the log file
    PrintWriter writer;                                     // object used to write to the log file
    
    //
    public void addLock(KThread thread, Lock l){
        getThreadState(thread).addLock(l);
    }
    
    public void removeLock(KThread thread, Lock l){
        ThreadState s = getThreadState(thread);
        s.removeLock(l);
        resetDonatedPriority(thread);
        int newDonationValue = getPriority(thread);
        for(Lock lock:s.getHeldLocks())
            if(lock.getHighestPriority() < newDonationValue)
                newDonationValue = lock.getHighestPriority();
        s.setDonatedPriority(newDonationValue);
        Lock waitingLock;
        if((waitingLock = s.getWaitingLock()) != null)
            donate(thread, waitingLock.getLockHolder(), waitingLock, false);
    }
    
    // donates priority of thread from to thread to if
    //    if from's donated priority is less than to's
    //    donated priority.
    // params: from: thread waiting
    //         to: thread currently holding lock l
    //         l: lock held by thread to
    //         setWaiting: flag to set the waiting lock of a thread
    public void donate(KThread from, KThread to, Lock l, boolean setWaiting){
        if(from == null || to == null)
            return;
        
        if(setWaiting)
            getThreadState(from).setWaitingLock(l);
        
        // copy over max donated priority
        if(l.getHighestPriority() < getThreadState(to).getPriority()){
            ThreadState s = getThreadState(to);
            s.setDonatedPriority(l.getHighestPriority());
            
            // this donates the priority of FROM to the threads that TO is waitng for
            Lock lock;
            if((lock = s.getWaitingLock()) != null){
                KThread newTo;
                if((newTo = lock.getLockHolder()) != null){
                    lock.updateHighestPriority();  // update's lock's highest priority value
                    donate(from, newTo, lock, false); // donate value to newTo
                }
            }
        }
    }
    
    // sets donted priority to actual priority of the thread
    public void resetDonatedPriority(KThread thread){
        getThreadState(thread).resetDonatedPriority();
    }
    
    // for debugging purposes
    public int getMaxPriorityValue(){
        return maxPriorityValue;
    }

    // gets the age of the scheduler in ms
    // div by 1000000 is to convert ns to ms
    public int getSchedulerTime() {
        return (int) ((System.nanoTime() - startTime) / 1000000);
    }

    //prints the status of every thread that is not ping or main
    
    // prints stats about the scheduled thread
    // param ThreadState thread: the thread state to be printed
    public void printScheduledThread(ThreadState ts){
        String db = "";
        KThread thread = ts.thread;
        if(Config.getString("printDebug") != null){      // debug output
            db = thread.getName() + ":";
            if(db.equals("main:") || db.equals("ping:")) return;
        }
        System.out.println("S," + getSchedulerTime() + "," + db + thread.getID()+ "," + ts.getPriority());
    }

    public void printTryLock(KThread thread, Lock l){
        if (thread == null) return;
        String db = "";
        ThreadState ts = getThreadState(thread);
        if(Config.getString("printDebug") != null)      // debug output
            db = thread.getName() + ":";
            if(db.equals("main:") || db.equals("ping:")) return;
        System.out.println("W," + l + "," + getSchedulerTime() + "," + db + thread.getID()+ "," + ts.getPriority());
    }
    
    public void printAquireLock(KThread thread, Lock l){
        String db = "";
        ThreadState ts = getThreadState(thread);
        if(Config.getString("printDebug") != null)      // debug output
            db = thread.getName() + ":";
            if(db.equals("main:") || db.equals("ping:")) return;
        System.out.println("A," + l + "," + getSchedulerTime() + "," + db + thread.getID()+ "," + ts.getPriority());
    }
    
    public void printReleaseLock(KThread thread, Lock l){
        String db = "";
        ThreadState ts = getThreadState(thread);
        if(Config.getString("printDebug") != null)      // debug output
            db = thread.getName() + ":";
            if(db.equals("main:") || db.equals("ping:")) return;
        System.out.println("R," + l + "," + getSchedulerTime() + "," + db + thread.getID()+ "," + ts.getPriority());
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
        else{
        }
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
        
        if(thread == null)
            return null;
        
        for(ThreadState s : states){
            if(thread.compareTo(s.getThread()) == 0){
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
                if(currThread != null && currThread.compareTo(s.thread) != 0){
                    s.status = ThreadState.QueueStatus.LIMBO;
                }
            }
        }
            
        if(currThread != null){
            ThreadState st = getThreadState(currThread);
            st.status = ThreadState.QueueStatus.CURRENT;
        }
    }
}
