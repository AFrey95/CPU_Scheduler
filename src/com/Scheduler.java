package com;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.model.CPUProcess;
import com.model.Event;
import com.model.Job;
import com.util.EventHandler;
import com.util.EventType;

public class Scheduler {
	
	public static void main(String[] args) {
		final int MAX_MEMORY = 512;
		int usedMemory = 0;
		int systemTime = 0;
		
		EventHandler handler = new EventHandler();
		
		Queue<Job> readyQ1 = new ConcurrentLinkedQueue<Job>();
		Queue<Job> readyQ2 = new ConcurrentLinkedQueue<Job>();
		Queue<Job> jobSchedulingQ = new ConcurrentLinkedQueue<Job>();
		List<Job> finishedJobs = new ArrayList<Job>();
		CPUProcess onCPU = null;
		
		 //queue of events from command line
		Queue<Event> externalQ = new ConcurrentLinkedQueue<Event>();
		Queue<Event> internalQ = new ConcurrentLinkedQueue<Event>();
		
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
									Integer.parseInt(argsIn[4])));
			}
			
			//add event to q
			externalQ.add(e);
		}
		
		Event inEvent = externalQ.poll();
		Event exEvent = null;
		
		while(externalQ.size() > 0 || internalQ.size() > 0 || readyQ1.size() > 0
				|| readyQ2.size() > 0 || jobSchedulingQ.size() > 0 || onCPU != null) {
			//handle internals
			if(internalQ.size() > 0) {
				exEvent = internalQ.poll();
				handler.eventOccurs(exEvent);

				if(exEvent.getType().equals(EventType.T)) {
					handler.handleEventT(exEvent, finishedJobs);
				} else if(exEvent.getType().equals(EventType.E)) {
					handler.handleEventE(exEvent, readyQ2);
				}
				
			} else {
				// handle (external) events
				if(inEvent != null && systemTime == inEvent.getTime()) {
					handler.eventOccurs(inEvent);
					// Event A
					if (inEvent.getType().equals(EventType.A)) {
						handler.handleEventA(inEvent, MAX_MEMORY, jobSchedulingQ);
							
					// Event D
					} else if (inEvent.getType().equals(EventType.D)) {
						handler.handleEventD(systemTime, MAX_MEMORY, usedMemory, jobSchedulingQ, readyQ1, readyQ2, finishedJobs);
					} else if (inEvent.getType().equals(EventType.F)) {
						handler.handleEventF(finishedJobs);
					}
					
					
					//get next event
					inEvent = externalQ.poll();
				}
				
				//handle queues
				if(jobSchedulingQ.size() > 0) {
					if(jobSchedulingQ.peek().getMemory() <= (MAX_MEMORY - usedMemory)) {
						usedMemory += jobSchedulingQ.peek().getMemory();
						readyQ1.add(jobSchedulingQ.poll());
					}
				}
				
				//if there is nothing on the CPU
				if(onCPU == null) {
					//if there is something on the ready Q
					if(readyQ1.size() > 0) {
						//put a process on the CPU
						onCPU = new CPUProcess(readyQ1.poll(), 100, systemTime);
//						usedMemory -= onCPU.getJob().getMemory();
					} else if(readyQ2.size() > 0) {
						onCPU = new CPUProcess(readyQ2.poll(), 300, systemTime);
//						usedMemory -= onCPU.getJob().getMemory();
					}
				} else {
					//update the process timer (runtime and quantum)
					onCPU.tick();
					if(onCPU.getJob().getRuntime() <= 0) {
						//generate T event
						Event e = new Event();
						e.setJob(onCPU.getJob());
						e.setTime(systemTime);
						e.setType(EventType.T);
						internalQ.add(e);
						usedMemory -=  onCPU.getJob().getMemory();
						//take process off CPU
						onCPU = null;
					} else if(onCPU.getQuantum() <= 0) {
						//generate E event
						Event e = new Event();
						e.setJob(onCPU.getJob());
						e.setTime(systemTime);
						e.setType(EventType.E);
						internalQ.add(e);
						//put job back in memory
//						usedMemory += onCPU.getJob().getMemory();
						//take process off CPU
						onCPU = null;
					}
				}
				
//				if(systemTime % 100 == 0 && systemTime <= 2000) {
//					System.out.println("There are " + (MAX_MEMORY - usedMemory) + " blocks available in the system.\n");
//				}
				systemTime++;
			}
		}
		handler.handleEventEndSim(systemTime, finishedJobs);	
	}

}
