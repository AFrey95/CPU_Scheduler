package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.PriorityQueue;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import com.model.Job;
import com.util.EventHandler;

class WaitQTest {

	@Test
	void test() {
		EventHandler handler = new EventHandler();
		Queue<Job> ioWaitQ = new PriorityQueue<Job>(4, (a,b) -> a.getIoBurstTime() - b.getIoBurstTime());
		
		Job j1 = new Job(1,1,1,1);
		Job j2 = new Job(2,2,1,1);
		Job j3 = new Job(3,3,1,1);
		Job j4 = new Job(4,4,1,1);
		
		j1.setIoBurstTime(10);
		j2.setIoBurstTime(100);
		j3.setIoBurstTime(500);
		j4.setIoBurstTime(1);
		
		ioWaitQ.add(j1);
		ioWaitQ.add(j2);
		ioWaitQ.add(j3);
		ioWaitQ.add(j4);
		
		System.out.println("Printing using non-destructive for loop:");
		for(Job job : ioWaitQ) {
			System.out.println(job.getId());
		}
		System.out.println();
		
		System.out.println("Printing using destructive while loop:");
		while(ioWaitQ.size() > 0) {
			System.out.println(ioWaitQ.poll().getId());
		}
		
//		handler.printQueue(ioWaitQ, "I/O Wait Queue");
		
	}
	
//	@Test
//	void test2() {
//		PriorityQueue<String> pq=
//                new PriorityQueue<String>(5, (a,b) -> b.length() - a.length());
//        pq.add("Apple");
//        pq.add("PineApple");
//        pq.add("Custard Apple");
//        while (pq.size() != 0)
//        {
//            System.out.println(pq.remove());
//        }
//	}

}
