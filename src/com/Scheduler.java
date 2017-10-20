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
//		Queue<Event> internalQ = new ConcurrentLinkedQueue<Event>();
		
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
		
//		Event exEvent = externalQ.poll();
		Event exEvent = null;
		Event inEvent = null;
		
		//SCHEDULING LOOP
		while(exEvent != null || externalQ.size() > 0 || inEvent != null || readyQ1.size() > 0
				|| readyQ2.size() > 0 || jobSchedulingQ.size() > 0 || onCPU != null) {
			
			exEvent = ((externalQ.size() > 0) && (externalQ.peek().getTime() == systemTime)) ? externalQ.poll() : null;
			
			//CPU
			if(onCPU != null) {
				onCPU.tick();
				//if job is done
				if(onCPU.getJob().getRemainingTime() <= 0) {
					//generate T event
					inEvent = new Event(EventType.T, systemTime, onCPU.getJob());
					
				//if quantum expired
				} else if(onCPU.getQuantum() <= 0) {
					//generate E event
					inEvent = new Event(EventType.E, systemTime, onCPU.getJob());
				}
			}
			
			if(inEvent != null) {
				handler.eventOccurs(inEvent, systemTime);
				if(inEvent.getType().equals(EventType.T)) {
					onCPU.getJob().setComTime(systemTime);
					finishedJobs.add(onCPU.getJob());
					usedMemory -= onCPU.getJob().getMemory();
					
				} else if(inEvent.getType().equals(EventType.E)) {
					readyQ2.add(onCPU.getJob());
				}
				
				usedMemory = handler.schedule(jobSchedulingQ, readyQ1, usedMemory, MAX_MEMORY);
				onCPU = null;
				inEvent = null;
				
				if(exEvent == null) {
					onCPU = handler.loadCPU(readyQ1, readyQ2, systemTime); //TODO: load should be a member of CPUProcess
				}
			}
			
			if(exEvent != null) {
				handler.eventOccurs(exEvent, systemTime);
				if(exEvent.getType().equals(EventType.A)) {
					handler.handleEventA(exEvent, MAX_MEMORY, jobSchedulingQ);
					usedMemory = handler.schedule(jobSchedulingQ, readyQ1, usedMemory, MAX_MEMORY);
					if(onCPU == null) {
						onCPU = handler.loadCPU(readyQ1, readyQ2, systemTime);
					}
				} else if(exEvent.getType().equals(EventType.D)) {
					usedMemory = handler.schedule(jobSchedulingQ, readyQ1, usedMemory, MAX_MEMORY);
					if(onCPU == null) {
						onCPU = handler.loadCPU(readyQ1, readyQ2, systemTime);
					}
					handler.handleEventD(systemTime, MAX_MEMORY, usedMemory, jobSchedulingQ, readyQ1, readyQ2, ioWaitQ, onCPU, finishedJobs);
				}
			}
			
//			handler.idle(readyQ1);
//			handler.idle(readyQ2);
			
			systemTime++;
		}
		handler.handleEventEndSim(systemTime, finishedJobs, usedMemory, MAX_MEMORY);	
	}
}
			
			
			
//			//CPU
//			if(onCPU != null) {
//				onCPU.tick();
//				//if job is done
//				if(onCPU.getJob().getRemainingTime() <= 0) {
//					//generate T event
//					inEvent = new Event(EventType.T, systemTime, onCPU.getJob());
//					
//				//if quantum expired
//				} else if(onCPU.getQuantum() <= 0) {
//					//generate E event
//					inEvent = new Event(EventType.E, systemTime, onCPU.getJob());
//				}
//			}
//			
//			//handle internals (process leaves CPU)
//			if(inEvent != null) {
////				inEvent = internalQ.poll();
//				handler.eventOccurs(inEvent, systemTime);
//
//				if(inEvent.getType().equals(EventType.T)) {
//					onCPU.getJob().setComTime(systemTime); // set complete time
//					finishedJobs.add(onCPU.getJob()); //add to finished jobs list
//					usedMemory -= onCPU.getJob().getMemory(); // free up memory
//					// jobScheduling
//					usedMemory = handler.schedule(jobSchedulingQ, readyQ1, usedMemory, MAX_MEMORY);
//					
//				} else if(inEvent.getType().equals(EventType.E)) {
//					readyQ2.add(onCPU.getJob());
//					
//					usedMemory = handler.schedule(jobSchedulingQ, readyQ1, usedMemory, MAX_MEMORY);
//				}
//				//put a new process on CPU
//				onCPU = handler.loadCPU(readyQ1, readyQ2, systemTime);
//				inEvent = null;
//			}
//			
//			// handle (external) events
//			if(exEvent != null && systemTime == exEvent.getTime()) {
//				handler.eventOccurs(exEvent, systemTime);
//				// Event A
//				if (exEvent.getType().equals(EventType.A)) {
//					handler.handleEventA(exEvent, MAX_MEMORY, jobSchedulingQ);
//					usedMemory = handler.schedule(jobSchedulingQ, readyQ1, usedMemory, MAX_MEMORY);
//					
//					if(needsFirstJob) {
//						onCPU = handler.loadCPU(readyQ1, readyQ2, systemTime);
//						needsFirstJob = false;
//					}
//						
//				// Event D
//				} else if (exEvent.getType().equals(EventType.D)) {
//					handler.handleEventD(systemTime, MAX_MEMORY, usedMemory, jobSchedulingQ, readyQ1, readyQ2, ioWaitQ, onCPU, finishedJobs);
//				} 
//				
//				//get next event
//				exEvent = externalQ.poll();
//			}
			
//			if(needsFirstJob) {
//				if(jobSchedulingQ.size() > 0) {
//					if(jobSchedulingQ.peek().getMemory() <= (MAX_MEMORY - usedMemory)) {
//						usedMemory += jobSchedulingQ.peek().getMemory();
//						readyQ1.add(jobSchedulingQ.poll());
//					}
//					needsFirstJob = false;
//				}
//			}
//			
//			
//			systemTime++;
//		}
//		handler.handleEventEndSim(systemTime, finishedJobs);	
//	}
//
//}
