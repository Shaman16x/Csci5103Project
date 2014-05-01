package nachos.pa3;

import nachos.machine.Processor;
import nachos.machine.Machine;

/**
 * Frame
 * maps a virtual address to a physical one
 * Also preforms memory accesses
 */
public class Frame {
    private Integer virtualIndex, physicalIndex;
    private static final int pageSize = Processor.pageSize;
    
    Frame(int v){
        virtualIndex = v;
        physicalIndex = -1;
    }
    
    Frame(int v, int p){
        virtualIndex = v;
        physicalIndex = p;
    }
    
    public void setVirtualIndex(int v){
        virtualIndex = v;
    }
    
    
    public void setPhysicalIndex(int p){
        physicalIndex = p;
    }
    
    public int getPhysicalIndex(){
        return physicalIndex;
    }
    
    // determine if this page has been mapped to a
    // physical page
    public boolean pageFault(){
        return (physicalIndex != -1);
    }
    
    // determines the address in physical memory
    private int getPhysAddr(int vaddr){
        int offset = vaddr - virtualIndex*pageSize;
        return physicalIndex*pageSize + offset;
    }
    
    // writes memory
    public int writeMemory(int vaddr, byte[] data, int offset, int length){
        byte[] memory = Machine.processor().getMemory();
        int paddr = getPhysAddr(vaddr);
        
        int amount = Math.min(length, pageSize*(virtualIndex+1)-vaddr);
        System.arraycopy(data, offset, memory, paddr, amount);
        return amount;
    }
    
    // reads memory
    public int readMemory(int vaddr, byte[] data, int offset, int length){
        byte[] memory = Machine.processor().getMemory();
        int paddr = getPhysAddr(vaddr);
        
        int amount = Math.min(length, pageSize*(virtualIndex+1)-vaddr);
        System.arraycopy(memory, paddr, data, offset, amount);
        return amount;
    }
}
