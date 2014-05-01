package nachos.pa3;

import java.util.List;
import java.util.LinkedList;
import nachos.machine.Machine;
import nachos.machine.Processor;
import nachos.threads.Semaphore;
import nachos.threads.Lock;

// keeps track of available physical memory
public class MemoryAllocator {
    private Semaphore freeMemory;
    private List<Integer> freePages;
    private int numReservedPages;
    private int numPhysPages;
    private int maxNumReserved;
    private int maxNumMapped;
    private int numMapped;
    private Semaphore reservedMemory;
    public Semaphore waiting;
    private Lock rLock;
    private Lock aLock;

    public MemoryAllocator(){
        numPhysPages = Machine.processor().getNumPhysPages();
        freeMemory = new Semaphore(numPhysPages);
        reservedMemory = new Semaphore(numPhysPages);
        waiting = new Semaphore(0);
        rLock = new Lock();
        aLock = new Lock();
        numReservedPages = 0;
        maxNumReserved = 0;
        numMapped = 0;
        freePages = new LinkedList<Integer>();
        for(int i=0; i<numPhysPages; i++){
            freePages.add(i);
        }
    }
    
    // Memory is reserved on a fcfs basis
    // Prevents potential deadlocks due to lack of memory
    public void reservePages(int numPages){
        rLock.acquire();
        numReservedPages += numPages;
        if(numReservedPages > maxNumReserved)
            maxNumReserved = numReservedPages;
        rLock.release();
    }
    
    public void freeReservedMemory(int numPages){
                rLock.acquire();
        numReservedPages -= numPages;
        rLock.release();
    }
    
    public void addToWaiting(){
        waiting.P();
    }
    
    public void wakeUpWaiting(){
        waiting.V();
    }
    
    public Integer allocatePage(){
        aLock.acquire();
        numMapped++;
        if(numMapped > maxNumMapped)
            maxNumMapped = numMapped;
        freeMemory.P();
        int ret = freePages.remove(0);
        aLock.release();
        return ret;
    }
    
    public void freePage(int i){
        aLock.acquire();
        numMapped--;
        freeMemory.V();
        freePages.add(i);
        aLock.release();
    }
    
    public int getNumAvailablePages(){
        return freePages.size();
    }
    
    public int getNumUnreservedPages(){
        return numPhysPages - numReservedPages;
    }
    
    public int getMaxNumMapped(){
        return maxNumMapped;
    }
    
    public int getMaxNumReserved(){
        return maxNumReserved;
    }
}
