package nachos.pa1;

import nachos.threads.*;
import java.util.ArrayList;

/* This is the Queue class for the MultiLevelScheduler.
 * It maintains three queues of different orders of priority.
 * These Queues store the ThreadState of a thread.
 * Each queue is ordered in a round robin fashion so that
 * the effective priority of a thread only matters when
 * choosing which queue to place it in.
 * The first queue (queue0) is the highest priority.
 * The middle queue (queue1) is the next level of priority.
 * And the last queue (queue2) is the lowest level of priority.
 * The next thread to be scheduled comes from queue0 unless it
 * is empty in which case from queue1 unless it is empty in which
 * case from queue2.
 */

public class MultiLevelQueue extends ThreadQueue{
	
	protected ArrayList<ThreadState> queue0 = new ArrayList<ThreadState>();     // highest priority queue
    protected ArrayList<ThreadState> queue1 = new ArrayList<ThreadState>();     // middle priority queue
    protected ArrayList<ThreadState> queue2 = new ArrayList<ThreadState>();     // lowest priority queue
    protected MultiLevelScheduler parentScheduler;
	


    public void setParentScheduler(MultiLevelScheduler sch){
        parentScheduler = sch;
    }

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
        parentScheduler.updatePriorities(null);
        updateQueues();
        ThreadState s = parentScheduler.getThreadState(thread);
        if(s.status == ThreadState.QueueStatus.NOT_SCHEDULED){
            s.setStartTime(parentScheduler.getSchedulerTime());
        }
        s.status = ThreadState.QueueStatus.INQUEUE;
        
        if(s.getEffectivePriority() <= 10)
            queue0.add(s);
        else if(s.getEffectivePriority() <= 20)
            queue1.add(s);
        else
            queue2.add(s);
    }

    /**
     * Notify this thread queue that another thread can receive access. Choose
     * and return the next thread to receive access, or <tt>null</tt> if there
     * are no threads waiting.
     *
     * The next thread to be scheduled comes from queue0 unless it
     * is empty in which case from queue1 unless it is empty in which
     * case from queue2.
     *
     * @return	the next thread to receive access, or <tt>null</tt> if there
     *		are no threads waiting.
     */
    public KThread nextThread() {
        
        updateQueues();
        ThreadState thread = null;
        if(!queue0.isEmpty()){
            thread = queue0.remove(0);
            thread.queueLevel = 0;
        }
        else if(!queue1.isEmpty()){
            thread = queue1.remove(0);
            thread.queueLevel = 1;
        }
        else if(!queue2.isEmpty()){
            thread = queue2.remove(0);
            thread.queueLevel = 2;
        }
        
        if(thread != null){
            parentScheduler.updatePriorities(thread.getThread());
            parentScheduler.printScheduledThread(thread);
            thread.status = ThreadState.QueueStatus.CURRENT;
            return thread.getThread();
        }
        return null;
        
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
        for(int i=0; i<queue0.size(); i++)
            if(thread.compareTo(queue0.get(i).getThread()) == 0)
                {queue0.remove(i); return;}
        for(int i=0; i<queue1.size(); i++)
            if(thread.compareTo(queue1.get(i).getThread()) == 0)
                {queue1.remove(i); return;}
        for(int i=0; i<queue2.size(); i++)
            if(thread.compareTo(queue2.get(i).getThread()) == 0)
                {queue2.remove(i); return;}
    }

    /**
     * Print out all the threads waiting for access, in no particular order.
     */
    public void print() {
        for(ThreadState s:queue0)
            System.out.println(s.getThread());
        for(ThreadState s:queue1)
            System.out.println(s.getThread());
        for(ThreadState s:queue2)
            System.out.println(s.getThread());
    }
    
    // moves up threads to the next priority queue
    // based on effective priority values.
    // only moves from lower to higher priority.
    protected void updateQueues(){
        ThreadState ts;
        // make sure priorities are correct
        parentScheduler.updatePriorities(null);
        for(int i = 0; i < queue1.size(); i++){
            // move to higher priority level
            if(queue1.get(i).getEffectivePriority() < 11){
                queue0.add(queue1.remove(i));
                i--;    // to maintain position in queue
            }
        }
        for(int i = 0; i < queue2.size(); i++){
            // move to highest priority level
            if(queue2.get(i).getEffectivePriority() < 11){
                queue0.add(queue2.remove(i));
                i--;    // to maintain position in queue
            }
            // move to middle priority level
            else if(queue2.get(i).getEffectivePriority() < 21){
                queue1.add(queue2.remove(i));
                i--;    // to maintain position in queue
            }
        }
    }
}
