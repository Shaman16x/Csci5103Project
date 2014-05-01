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
    private Semaphore reservedMemory;
    private Lock rLock;

    public MemoryAllocator(){
        int numPhysPages = Machine.processor().getNumPhysPages();
        freeMemory = new Semaphore(numPhysPages);
        reservedMemory = new Semaphore(numPhysPages);
        rLock = new Lock();
        freePages = new LinkedList<Integer>();
        for(int i=0; i<numPhysPages; i++){
            freePages.add(i);
        }
    }
    
    // Memory is reserved on a fcfs basis
    // Prevents potential deadlocks due to lack of memory
    public void reserveMemory(int numPages){
        rLock.acquire();
        while(numPages > 0){
            reservedMemory.P();
            numPages--;
        }
        rLock.release();
    }
    
    public void freeReservedMemory(int numPages){
        while(numPages > 0){
            reservedMemory.V();
            numPages--;
        }
    }
    
    public Integer allocatePage(){
        freeMemory.P();
        return freePages.remove(0);
    }
    
    public void freePage(int i){
        freeMemory.V();
        freePages.add(i);
    }
    
    public int getNumAvailablePages(){
        return freePages.size();
    }
}
