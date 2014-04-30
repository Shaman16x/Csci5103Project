package nachos.pa3;

import java.util.List;
import nachos.machine.Machine;
import nachos.machine.Processor;
import nachos.threads.Semaphore;

public class MemoryAllocator {
    Semaphore freeMemory;
    List<Integer> freePages;

    MemoryAllocator(){
        freeMemory = new Semaphore(Processor.numPhysPages);
        freePages = new LinkedList<Integer>();
        for(int i=0; i<Processor.numPhysPages; i++){
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
