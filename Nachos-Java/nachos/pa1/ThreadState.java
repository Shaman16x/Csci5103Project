package nachos.pa1;

import nachos.threads.KThread;

/*
 * ThreadState is a container used to hold a thread, an associated
 * priority, and the 
 * 
 */

public class ThreadState {
    
    private int maxPriorityValue = 10;
    private int minPriorityValue = 0;
    private int agingTime = 1;
    protected long runTime = 0;
    protected long waitTime = 0;
    protected long startTime = 0;       // when the thread was first scheduled (in ms)
    protected QueueStatus status = QueueStatus.NOT_SCHEDULED;
	/** The thread with which this object is associated. */	   
	protected KThread thread;
	/** The priority of the associated thread. */
	protected int priority;
    
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
        
        setPriority(7);     //TODO: use better default
    }

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
        return priority;
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
        int ep = priority + (int)(runTime - waitTime)/(1000);
        
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
    
    public String toString() {
        return thread.toString() + ":" + priority;
    }
    
    // prints the final stats of a thread
    public String getStats(){
        long run = runTime/1000;
        long wait = waitTime/1000;
        long end = startTime + run + wait;
        return thread.getName() + "," + startTime + "," + run + "," + wait + "," + end;     //TODO: use getID
    }
}
