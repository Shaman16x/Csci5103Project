package nachos.pa2;

import java.util.*;
import nachos.threads.*;

/*
 * ThreadState is a container used to hold a thread, an associated
 * priority, status of thread in the queue, run time, wait time
 * and the start time of the associated thread.
 * Other local variables are used for quick calculation of various
 * statistics including effective priority
 */

public class ThreadState {
    
    private int maxPriorityValue = 10;  // local copy of the max Priority Value
    private int minPriorityValue = 0;   // local copy of the min Priority Value
    private int agingTime = 1;          // local copy of the aging time
    protected long runTime = 0;         // how much time was spent running
    protected long waitTime = 0;        // how much time was spent waiting
    protected long startTime = 0;       // when the thread was first scheduled (in ms)
    public int queueLevel = 0;          // queue level (used for debug)
    protected QueueStatus status = QueueStatus.NOT_SCHEDULED;   // Queue Status
    protected KThread thread;           // associated thread
    protected int priority;             // priority of thread
    protected int donatedPriority;         // donated priority
    
    protected LinkedList<Lock> heldLocks = new LinkedList<Lock>();
    
    
    public void addLock(Lock l){
        heldLocks.add(l);
    }
    
    public void removeLock(Lock l){
        heldLocks.remove(l);
        if(heldLocks.size() == 0)
            setDonatedPriority(priority);
        
    }
    
    // status of thread in queue
    public enum QueueStatus{
        NOT_SCHEDULED, INQUEUE, CURRENT, LIMBO
    }

    /**
     * Allocate a new <tt>ThreadState</tt> object and associate it with the
     * specified thread.
     *
     * @param	thread	the thread this state belongs to.
     */
    public ThreadState(KThread thread) {
        this.thread = thread;
        setPriority(maxPriorityValue);
    }

    // Perfered constructor call for ThreadState
    public ThreadState(KThread thread, int priority, int maxP, int a){
        this.thread = thread;
        setPriority(priority);
        maxPriorityValue = maxP;
        agingTime = a;
    }
    
    // set the time that the thread was first scheduled
    public void setStartTime(int time){
        startTime = time;
    }

    /**
     * Return the priority of the associated thread.
     *
     * @return	the priority of the associated thread.
     */
    public int getPriority() {
        return donatedPriority;
    }

    // gets the accossiated thread
    public KThread getThread(){
        return thread;
    }

    /**
     * Return the effective priority of the associated thread.
     * 
     * @return	the effective priority of the associated thread.
     */
    public int getEffectivePriority() {
        if(agingTime <= 0) return getPriority();    // prevent div 0
        // div by 1000000 is to convert ns to ms
        // change in priority is based on the difference of run and wait
        int ep = priority + (int)(((runTime - waitTime)/(1000000))/agingTime);
        
        // cap the effective priority as needed
        if(ep > maxPriorityValue)
            return maxPriorityValue;
        else if(ep < minPriorityValue)
            return minPriorityValue;
        else
            return ep;
    }

    // checks if two thread states are referencing the same thread
    public boolean sameThread(ThreadState state){
        return (this.thread.compareTo(state.getThread())) == 0;
    }

    /**
     * Set the priority of the associated thread to the specified value.
     *
     * @param	priority	the new priority.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public void setDonatedPriority(int priority){
        this.donatedPriority = priority;
    }
    
    public int getDonatedPriority(){
        return donatedPriority;
    }
    
    public String toString() {
        return thread.toString() + ":" + priority;
    }
    
    // prints the final stats of a thread
    // div by 1000000 is to convert ns to ms
    public String getStats(){
        long run = runTime/1000000;
        long wait = waitTime/1000000;
        long end = startTime + (runTime + waitTime)/1000000;
        return thread.getID() + "," + startTime + "," + run + "," + wait + "," + end;
    }
}
