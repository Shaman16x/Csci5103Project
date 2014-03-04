package nachos.pa1;

import nachos.threads.*;
import java.util.*;
/**
 * Schedules access to some sort of resource with limited access constraints. A
 * thread queue can be used to share this limited access among multiple
 * threads.
 *
 * <p>
 * Examples of limited access in Nachos include:
 *
 * <ol>
 * <li>the right for a thread to use the processor. Only one thread may run on
 * the processor at a time.
 *
 * <li>the right for a thread to acquire a specific lock. A lock may be held by
 * only one thread at a time.
 *
 * <li>the right for a thread to return from <tt>Semaphore.P()</tt> when the
 * semaphore is 0. When another thread calls <tt>Semaphore.V()</tt>, only one
 * thread waiting in <tt>Semaphore.P()</tt> can be awakened.
 *
 * <li>the right for a thread to be woken while sleeping on a condition
 * variable. When another thread calls <tt>Condition.wake()</tt>, only one
 * thread sleeping on the condition variable can be awakened.
 *
 * <li>the right for a thread to return from <tt>KThread.join()</tt>. Threads
 * are not allowed to return from <tt>join()</tt> until the target thread has
 * finished.
 * </ol>
 *
 * All these cases involve limited access because, for each of them, it is not
 * necessarily possible (or correct) for all the threads to have simultaneous
 * access. Some of these cases involve concrete resources (e.g. the processor,
 * or a lock); others are more abstract (e.g. waiting on semaphores, condition
 * variables, or join).
 *
 * <p>
 * All thread queue methods must be invoked with <b>interrupts disabled</b>.
 */
public class MultiLevelQueue extends ThreadQueue{
	
	protected ArrayList<ThreadState> queue0 = new ArrayList<ThreadState>();
    protected ArrayList<ThreadState> queue1 = new ArrayList<ThreadState>();
    protected ArrayList<ThreadState> queue2 = new ArrayList<ThreadState>();
	

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
        DynamicPriorityScheduler sched = new DynamicPriorityScheduler();
        int i=0;
        ThreadState s = sched.getThreadState(thread);
        sched.updatePriorities(KThread.currentThread());
        updateQueues();
        
        if(s.getEffectivePriority() <= 10)
            queue0.add(s);
        else if(s.getEffectivePriority() <= 20)
            queue1.add(s);
        else
            queue2.add(s);
        
        s.status = ThreadState.QueueStatus.INQUEUE;
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
        DynamicPriorityScheduler sched = new DynamicPriorityScheduler();
        sched.updatePriorities(KThread.currentThread());
        updateQueues();
        KThread thread = null;
        if(!queue0.isEmpty())
            thread = queue0.remove(0).getThread();
        else if(!queue1.isEmpty())
            thread = queue1.remove(0).getThread();
        else if(!queue2.isEmpty())
            thread = queue2.remove(0).getThread();
        
        if(thread != null)
            sched.printScheduled(thread);
        
        return thread;
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
                queue0.remove(i);
        for(int i=0; i<queue1.size(); i++)
            if(thread.compareTo(queue1.get(i).getThread()) == 0)
                queue1.remove(i);
        for(int i=0; i<queue2.size(); i++)
            if(thread.compareTo(queue2.get(i).getThread()) == 0)
                queue2.remove(i);
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
    
    protected void updateQueues(){
        for(ThreadState s:queue2)
            if(s.getEffectivePriority()<=20){
                for(int i=0; i<queue2.size(); i++)
                    if(queue2.get(i).thread.compareTo(s.thread)==0){
                        s = queue2.remove(i);
                        break;
                    }
                if(s != null){
                    if(s.getEffectivePriority()>10)
                        queue1.add(s);
                    else
                        queue0.add(s);
                }
            }
            
        for(ThreadState s:queue1)
            if(s.getEffectivePriority()<=10){
                for(int i=0; i<queue1.size(); i++)
                    if(queue1.get(i).thread.compareTo(s.thread)==0){
                        s = queue1.remove(i);
                        break;
                    }
                if(s != null)
                    queue0.add(s);
            }
    }
}
