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
    }
    
    

    /**
     * Atomically acquire this lock. The current thread must not already hold
     * this lock.
     */
    public void acquire() {
        Lib.assertTrue(!isHeldByCurrentThread());

        boolean intStatus = Machine.interrupt().disable();
        KThread thread = KThread.currentThread();
        if (lockHolder != null) {
            if((int p = temp.getPriority(thread)) < highestPriority)
                highestPriority = p;
            temp.donate(thread, lockHolder, this, true);
            waitQueue.waitForAccess(thread);
            KThread.sleep();
        }
        else {
            waitQueue.acquire(thread);
            lockHolder = thread;
            highestPriority = temp.getPriority(thread);
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
        
        highestPriority = temp.getMaxPriorityValue();
        for(KThread t:waitQueue.getList())
            if((int p = temp.getThreadState(t).getPriority()) < highestPriority)
                highestPriority = p;
        
        if ((lockHolder = waitQueue.nextThread()) != null)
            lockHolder.ready();
        
        temp.removeLock(thread, this);
        Machine.interrupt().restore(intStatus);
    }
    
    pulbic boolean isWaitingFor(KThread thread){
        
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
    
    
    StaticPriorityScheduler temp = new StaticPriorityScheduler();
    private KThread lockHolder = null;
    int highestPriority = temp.getMaxPriorityValue();
    LockScheduler sched = new LockScheduler();          //this is fifo scheduler
    private ThreadQueue waitQueue =	sched.newThreadQueue(true);
}
