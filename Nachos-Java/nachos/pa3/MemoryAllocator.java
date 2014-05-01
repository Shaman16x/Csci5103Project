package nachos.pa3;

import java.util.List;
import java.util.LinkedList;
import nachos.machine.Machine;
import nachos.machine.Processor;
import nachos.threads.Semaphore;
import nachos.threads.Lock;

// keeps track of available physical memory
public class MemoryAllocator {
    private Semaphore freeMemory;           // semaphore for free pages.
    private List<Integer> freePages;        // list of free page numbers
    private int numReservedPages;           // number of current reserved pages
    private int numPhysPages;               // number of total physical pages
    private int numMapped;                  // number of current physical pages mapped
    private int maxNumReserved;             // max number of concurrently reseved pages
    private int maxNumMapped;               // max number of concurrently mapped pages
    private Semaphore waiting;              // implements waiting queue for programs
    private Lock rLock;                     // lock for reserving pages
    private Lock aLock;                     // lock for allocating pages

    public MemoryAllocator(){
        numPhysPages = Machine.processor().getNumPhysPages();
        freeMemory = new Semaphore(numPhysPages);
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
    
    // unreserves the pages when a program finishes
    public void freeReservedMemory(int numPages){
        rLock.acquire();
        numReservedPages -= numPages;
        rLock.release();
    }
    
    // adds a program to the waiting queue
    public void addToWaiting(){
        waiting.P();
    }
    
    // alerts a program from the waiting queue
    public void wakeUpWaiting(){
        waiting.V();
    }
    
    // returns the page number to be allocated
    // and removes it from list of available pages.
    // increments the number of mapped pages and
    // updates max number of mapped pages.
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
    
    // adds a page number back to list of free pages
    // and decrements the number of mapped pages
    public void freePage(int i){
        aLock.acquire();
        numMapped--;
        freeMemory.V();
        freePages.add(i);
        aLock.release();
    }
    
    // returns the number of unmapped pages
    public int getNumAvailablePages(){
        return freePages.size();
    }
    
    // returns the number of unreserved pages
    public int getNumUnreservedPages(){
        return numPhysPages - numReservedPages;
    }
    
    // returns the max number of concurrently mapped pages
    public int getMaxNumMapped(){
        return maxNumMapped;
    }
    
    // returns the max number of concurrently reserved pages
    public int getMaxNumReserved(){
        return maxNumReserved;
    }
}
