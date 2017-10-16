package com;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.util.Event;
import com.util.EventType;
import com.util.Job;

public class Scheduler {
	
	public static void main(String[] args) {
		
		final int MAX_MEMORY = 512;
		int usedMemory = 0;
		long systemTime = 0;
		
		Queue<Job> readyQ1 = new ConcurrentLinkedQueue<Job>();
		Queue<Job> readyQ2 = new ConcurrentLinkedQueue<Job>();
		Queue<Job> jobSchedulingQ = new ConcurrentLinkedQueue<Job>();
		List<Job> finishedJobs = new ArrayList<Job>();
		
		//queue of events from command line
		Queue<Event> eventQ = new ConcurrentLinkedQueue<Event>();
	
		Scanner sc = new Scanner(System.in);
		String lineIn;
		String[] argsIn;
		
		//get user input
		while(sc.hasNextLine() && !(lineIn = sc.nextLine()).equalsIgnoreCase("done")) {
			
			argsIn = lineIn.split("(\\s)+");

			//parse input into event
			Event e = new Event();
			e.setType(EventType.getEventType(argsIn[0]));
			e.setTime(Integer.parseInt(argsIn[1]));
			
			if(e.getType().equals(EventType.A)) {
				e.setJob(new Job(Integer.parseInt(argsIn[1]),
									Integer.parseInt(argsIn[2]),
									Integer.parseInt(argsIn[3]),
									Integer.parseInt(argsIn[3])));
			}
			
			//add event to q
			eventQ.add(e);
		}
		
		
		System.out.println("There are " + (MAX_MEMORY - usedMemory) + " blocks available in the system.\n");

		Event event = eventQ.poll();
		
		while(true) {
			// handle (external) events
			if(systemTime == event.getTime()) {
				System.out.println("Job: " + event.getType().toString() + "\tTime: " + event.getTime());
				// Event A
				if (event.getType().equals(EventType.A)) {
					Job j = event.getJob();
					if(j.getMemory() > (MAX_MEMORY)) {
						System.out.println("This job exceeds the system's main memory capacity.");
					} else {
						jobSchedulingQ.add(j);
					}
						
				// Event D
				} else if (event.getType().equals(EventType.D)) {
					
					System.out.println("There are " + (MAX_MEMORY - usedMemory) + " blocks available in the system.\n");
				}
				
				
				
				//get next event
				event = eventQ.poll();
			}
			
			//handle queues
			
			
			
			systemTime++;
		}
		
	}

}
