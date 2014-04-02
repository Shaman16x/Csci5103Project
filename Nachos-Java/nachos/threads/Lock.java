package nachos.threads;
import nachos.pa2.*;
import nachos.machine.*;

/**
 * A <tt>Lock</tt> is a synchronization primitive that has two states,
 * <i>busy</i> and <i>free</i>. There are only two operations allowed on a
 * lock:
 *
 * <ul>
 * <li><tt>acquire()</tt>: atomically wait until the lock is <i>free</i> and
 * then set it to <i>busy</i>.
 * <li><tt>release()</tt>: set the lock to be <i>free</i>, waking up one
 * waiting thread if possible.
 * </ul>
 *
 * <p>
 * Also, only the thread that acquired a lock may release it. As with
 * semaphores, the API does not allow you to read the lock state (because the
 * value could change immediately after you read it).
 */
public class Lock {
    /**
     * Allocate a new lock. The lock will initially be <i>free</i>.
     */
    public Lock() {
        lockID = numLocks;
        numLocks++;
        if(Config.getString("Locks.usePriorityDonation") != null && Config.getBoolean("Locks.usePriorityDonation"))
            useDonation = true;
        if(Config.getString("ThreadedKernel.scheduler") == "nachos.pa2.StaticPriorityScheduler")
            useSPS = true;
    }
    
    /**
     * Atomically acquire this lock. The current thread must not already hold
     * this lock.
     */
    public void acquire() {
        Lib.assertTrue(!isHeldByCurrentThread());
        int p;
        boolean intStatus = Machine.interrupt().disable();
        KThread thread = KThread.currentThread();
        if(useSPS)
            temp.printTryLock(thread, this);
        
        // check if highest priority can be changed
        if((p = temp.getPriority(thread)) < highestPriority)
            highestPriority = p;
        
        // thread must wait
        if (lockHolder != null) {
            if(useDonation)
                temp.donate(thread, lockHolder, this, true);
            waitQueue.waitForAccess(thread);
            KThread.sleep();
        }
        // can aquire right away
        else {
            lockHolder = thread;
            if(useSPS)
                temp.printAquireLock(lockHolder, this);
            if(useDonation)
                temp.addLock(thread,this);
        }

        Lib.assertTrue(lockHolder == thread);

        Machine.interrupt().restore(intStatus);
    }

    /**
     * Atomically release this lock, allowing other threads to acquire it.
     */
    public void release() {
        Lib.assertTrue(isHeldByCurrentThread());

        boolean intStatus = Machine.interrupt().disable();
        KThread thread = lockHolder;
        if(useSPS)
            temp.printReleaseLock(lockHolder, this);
        
        if(useDonation){
            updateHighestPriority();
        }
        if ((lockHolder = waitQueue.nextThread()) != null){
            temp.printAquireLock(lockHolder, this);
            lockHolder.ready();
        }
        
        if(useSPS)
            temp.removeLock(thread, this);
        Machine.interrupt().restore(intStatus);
    }

    /**
     * Test if the current thread holds this lock.
     *
     * @return	true if the current thread holds this lock.
     */
    public boolean isHeldByCurrentThread() {
        return (lockHolder == KThread.currentThread());
    }
    
    public KThread getLockHolder(){
        return lockHolder;
    }
    
    public int getHighestPriority(){
        return highestPriority;
    }
    
    // determine the highest priority out of all waiting threads
    public void updateHighestPriority(){
        highestPriority = temp.getMaxPriorityValue();
        // reset the donation value
        for(KThread t:((LockScheduler.FifoQueue)waitQueue).getList()){
            int p;
            if((p = temp.getThreadState(t).getPriority()) < highestPriority)
                highestPriority = p;
        }
    }
    
    public String toString(){
        return "L" + lockID;
    }
    
    StaticPriorityScheduler temp = (StaticPriorityScheduler) ThreadedKernel.scheduler;
    private KThread lockHolder = null;
    protected int highestPriority = temp.getMaxPriorityValue();
    LockScheduler sched = new LockScheduler();          //this is fifo scheduler
    private ThreadQueue waitQueue =	sched.newThreadQueue(true);
    private int lockID;
    static private int numLocks = 0;
    private boolean useDonation = false;
    private boolean useSPS = false;
}
