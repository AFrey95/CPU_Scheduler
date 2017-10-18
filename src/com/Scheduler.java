package com;

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

		Queue<Job> jobSchedulingQ = new ConcurrentLinkedQueue<Job>();
		Queue<Job> readyQ1 = new ConcurrentLinkedQueue<Job>();
		Queue<Job> readyQ2 = new ConcurrentLinkedQueue<Job>();
		Queue<Job> ioWaitQ = new ConcurrentLinkedQueue<Job>();
		List<Job> finishedJobs = new ArrayList<Job>();
		CPUProcess onCPU = null;
		
		 //queue of events from command line
		Queue<Event> externalQ = new ConcurrentLinkedQueue<Event>();
		Queue<Event> internalQ = new ConcurrentLinkedQueue<Event>();
		
		Scanner sc = new Scanner(System.in);
		String lineIn;
		String[] argsIn;
		
		//USER IO
		while(sc.hasNextLine() && !(lineIn = sc.nextLine()).equalsIgnoreCase("done")) {
			
			try {
				argsIn = lineIn.split("(\\s)+");

				//parse input into event
				Event e = new Event(EventType.getEventType(argsIn[0]), Integer.parseInt(argsIn[1]), null);
				
				if(e.getType().equals(EventType.A)) {
					e.setJob(new Job(Integer.parseInt(argsIn[1]),
										Integer.parseInt(argsIn[2]),
										Integer.parseInt(argsIn[3]),
										Integer.parseInt(argsIn[4])));
				}
				
				//add event to q
				externalQ.add(e);
			} catch (NumberFormatException e) {
				System.out.println("Fatal Error: Could not parse numbers. Please use only integers.");
				System.exit(1);
			} catch (Exception e) {
				//do nothing
			}
		}
		
		Event exEvent = externalQ.poll();
		Event inEvent = null;
		
		//SCHEDULING LOOP
		while(externalQ.size() > 0 || internalQ.size() > 0 || readyQ1.size() > 0
				|| readyQ2.size() > 0 || jobSchedulingQ.size() > 0 || onCPU != null) {
			
			
			//handle internals
			if(internalQ.size() > 0) {
				inEvent = internalQ.poll();
				handler.eventOccurs(inEvent, systemTime);

				if(inEvent.getType().equals(EventType.T)) {
					onCPU = handler.handleEventT(inEvent, finishedJobs, readyQ1, readyQ2);
//					onCPU = handler.handleEventT2(inEvent, finishedJobs, readyQ1, readyQ2, onCPU, systemTime);
				} else if(inEvent.getType().equals(EventType.E)) {
					onCPU = handler.handleEventE(inEvent, readyQ1, readyQ2);
//					onCPU = handler.handleEventE2(inEvent, readyQ1, readyQ2, onCPU, systemTime);
				}
				
			} else {
				// handle (external) events
				if(exEvent != null && systemTime == exEvent.getTime()) {
					handler.eventOccurs(exEvent, systemTime);
					// Event A
					if (exEvent.getType().equals(EventType.A)) {
						handler.handleEventA(exEvent, MAX_MEMORY, jobSchedulingQ);
							
					// Event D
					} else if (exEvent.getType().equals(EventType.D)) {
						handler.handleEventD(systemTime, MAX_MEMORY, usedMemory, jobSchedulingQ, readyQ1, readyQ2, ioWaitQ, onCPU, finishedJobs);
					} 
//					else if (exEvent.getType().equals(EventType.F)) {
//						handler.handleEventF(finishedJobs);
//					}
					
					//get next event
					exEvent = externalQ.poll();
				}
				
				//handle queues
				if(jobSchedulingQ.size() > 0) {
					if(jobSchedulingQ.peek().getMemory() <= (MAX_MEMORY - usedMemory)) {
						usedMemory += jobSchedulingQ.peek().getMemory();
						readyQ1.add(jobSchedulingQ.poll());
					}
				}
				
				//if there is nothing on the CPU
				if(onCPU == null) { //should only happen until first job arrives
					//if there is something on the ready Q
					onCPU = handler.loadCPU(readyQ1, readyQ2, systemTime);
				}
				if(onCPU != null) {
					//update the process timer (runtime and quantum)
					onCPU.tick();
					
					//if job is done
					if(onCPU.getJob().getRemainingTime() <= 0) {
						//generate T event
						Event e = new Event(EventType.T, systemTime+1, onCPU.getJob());
						internalQ.add(e);
						usedMemory -=  onCPU.getJob().getMemory();
						//take process off CPU
//						onCPU = null;
					//if quantum expired
					} else if(onCPU.getQuantum() <= 0) {
						//generate E event
						Event e = new Event(EventType.E, systemTime+1, onCPU.getJob());
						internalQ.add(e);
						//take process off CPU
//						onCPU = null;
					}
				}
				
				systemTime++;
			}
		}
		handler.handleEventEndSim(systemTime, finishedJobs);	
	}

}
