package nachos.pa2;

import nachos.threads.*;
import java.util.*;
import java.io.*;

/*
 * This is the Queue class for the Static Priority Scheduler.
 * This implements a priority queue such that the next thread
 * scheduled is the first item in the queue (highest priority).
 * The queue is sorted from highest priority to lowest priority.
 */

public class StaticPriorityQueue extends ThreadQueue{

    // list that keeps track of priorities
    protected PriorityQueue<ThreadState> queue = new PriorityQueue<ThreadState>(10, new ThreadStateComparator());
    protected StaticPriorityScheduler parentScheduler;

    /**
     * Notify this thread queue that the specified thread is waiting for
     * access. This method should only be called if the thread cannot
     * immediately obtain access (e.g. if the thread wants to acquire a lock
     * but another thread already holds the lock).
     *
     * <p>
     * A thread must not simultaneously wait for access to multiple resources.
     * For example, a thread waiting for a lock must not also be waiting to run
     * on the processor; if a thread is waiting for a lock it should be
     * sleeping.
     *
     * <p>
     * However, depending on the specific objects, it may be acceptable for a
     * thread to wait for access to one object while having access to another.
     * For example, a thread may attempt to acquire a lock while holding
     * another lock. Note, though, that the processor cannot be held while
     * waiting for access to anything else.
     *
     * @param	thread	the thread waiting for access.
     */
    public void waitForAccess(KThread thread){
        int i=0;
        
        ThreadState s = parentScheduler.getThreadState(thread);
        if(s.status == ThreadState.QueueStatus.NOT_SCHEDULED){
            s.setStartTime(parentScheduler.getSchedulerTime());
        }
        s.status = ThreadState.QueueStatus.INQUEUE;
        
        queue.add(s);
    }
    
    public void setParentScheduler(StaticPriorityScheduler sch){
        parentScheduler = sch;
    }

    /**
     * Notify this thread queue that another thread can receive access. Choose
     * and return the next thread to receive access, or <tt>null</tt> if there
     * are no threads waiting.
     *
     * <p>
     * If the limited access object transfers priority, and if there are other
     * threads waiting for access, then they will donate priority to the
     * returned thread.
     *
     * @return	the next thread to receive access, or <tt>null</tt> if there
     *		are no threads waiting.
     */
    public KThread nextThread() {
        
        if(queue.isEmpty())
            return null;
        ThreadState s = queue.poll();
        parentScheduler.printScheduledThread(s);
        
        return s.getThread();
    }

    /**
     * Notify this thread queue that a thread has received access, without
     * going through <tt>request()</tt> and <tt>nextThread()</tt>. For example,
     * if a thread acquires a lock that no other threads are waiting for, it
     * should call this method.
     *
     * <p>
     * This method should not be called for a thread returned from
     * <tt>nextThread()</tt>.
     *
     * @param	thread	the thread that has received access, but was not
     * 			returned from <tt>nextThread()</tt>.
     */
    public void acquire(KThread thread) {
        ThreadState ts = parentScheduler.getThreadState(thread);
        queue.remove(ts);
    }

    /**
     * Print out all the threads waiting for access, in no particular order.
     */
    public void print() {
        for(ThreadState s:queue)
            System.out.println(s.getThread());
    }
}
