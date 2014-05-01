package nachos.pa3;

import nachos.machine.Processor;
import nachos.machine.Machine;


// class that maps all virtual pages to
// physical ones
public class PageTable {
    Frame[] table;
    private static final int pageSize = Processor.pageSize;
    private static MemoryAllocator allocator = new MemoryAllocator();
    
    public PageTable(int numPages){
        table = new Frame[numPages];
        for (int i = 0; i < table.length; i++){
            table[i] = new Frame(i);
        }
    }
    
    // writes memory
    public int writeMemory(int vaddr, byte[] data, int offset, int length){
        int index = vaddr/pageSize;
        int total = 0;
        while (length > 0){
            // detect page fault
            if(table[index].pageFault()) {
                table[index].setPhysicalIndex(allocator.allocatePage());
            }
            int amount = table[index].writeMemory(vaddr, data, offset, length); 
            index++;
            length -= amount;
            total += amount;
            vaddr = index*pageSize;
        }
        
        return total;
    }
    
    // reads memory
    public int readMemory(int vaddr, byte[] data, int offset, int length){
        int index = vaddr/pageSize;
        int total = 0;
        while (length > 0){
            if(table[index].pageFault()){
                // should not happen
                break;
            }
            int amount = table[index].readMemory(vaddr, data, offset, length); 
            index++;
            length -= amount;
            total += amount;
            vaddr = index*pageSize;
        }
        
        return total;
    }
}
