package nachos.pa3;

import java.util.List;
import java.util.LinkedList;
import nachos.machine.Machine;
import nachos.machine.Processor;
import nachos.threads.Semaphore;

// keeps track of available physical memory
public class MemoryAllocator {
    Semaphore freeMemory;
    List<Integer> freePages;

    MemoryAllocator(){
        int numPhysPages = Machine.processor().getNumPhysPages();
        freeMemory = new Semaphore(numPhysPages);
        freePages = new LinkedList<Integer>();
        for(int i=0; i<numPhysPages; i++){
            freePages.add(i);
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
}
