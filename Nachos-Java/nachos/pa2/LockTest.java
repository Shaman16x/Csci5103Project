package nachos.pa2;

import nachos.threads.*;

// waits for x number of miliseconds
// then finishes execution
public class DelayTest implements Runnable {
	public DelayTest(int num, int time) {
	    this.time = time;
        this.num = num;
	}
	
	public void run() {
	    for (int i=0; i<time; i++) {
            waitMS();
            
            if(i%5 == 0)   {
                System.out.println("*** thread " + num + " waited "
                       + i + " ms");
            }
            KThread.currentThread().yield();
	    }
	}

    // wait for 1ms
    public void waitMS(){
        long start = System.nanoTime();
        
        while((System.nanoTime() - start) < 1000000);
    }
    
    private int num;
    private int count = 0;
	private int time;
}
