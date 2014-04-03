package nachos.pa2;

import java.util.Comparator;

public class ThreadStateComparator implements Comparator<ThreadState> {
    public int compare(ThreadState o1, ThreadState o2){
        return o1.getPriority() - o2.getPriority();
    }
    
}
