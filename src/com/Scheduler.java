package com;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
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
		Queue<Job> ioWaitQ = new PriorityQueue<Job>(4, (a,b) -> a.getIoComTime() - b.getIoComTime());
		List<Job> finishedJobs = new ArrayList<Job>();
		CPUProcess onCPU = null;
		
		 //queue of events from command line
		Queue<Event> externalQ = new ConcurrentLinkedQueue<Event>();
		
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
				} else if(e.getType().equals(EventType.I)) {
					e.setIoTime(Integer.parseInt(argsIn[2]));
					e.setJob(null);
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
				|| readyQ2.size() > 0 || jobSchedulingQ.size() > 0 || ioWaitQ.size() > 0 || onCPU != null) {
			
			exEvent = ((externalQ.size() > 0) && (externalQ.peek().getTime() == systemTime)) ? externalQ.poll() : null;
			
//			handler.idleCheck(onCPU, systemTime);
			
			//CPU
			if(onCPU != null) {
				onCPU.tick();
				//if job is done
				if(onCPU.getJob().getBurstTimeLeft() <= 0) {
					//generate T event
					inEvent = new Event(EventType.T, systemTime, onCPU.getJob());
					
				//if quantum expired
				} else if(onCPU.getQuantum() <= 0) {
					//generate E event
					inEvent = new Event(EventType.E, systemTime, onCPU.getJob());
					
				//if IO complete
				}
				else if(ioWaitQ.size() > 0 && ioWaitQ.peek().getIoComTime() == systemTime) {
					//generate C
					inEvent = new Event(EventType.C, systemTime, onCPU.getJob());
				}
			}
			
			if(inEvent != null) {
				handler.eventOccurs(inEvent, systemTime);
				
				if(inEvent.getType().equals(EventType.T)) {
					onCPU.getJob().setComTime(systemTime);
					finishedJobs.add(onCPU.getJob());
					usedMemory -= onCPU.getJob().getMemory();
					onCPU = null;
					
				} else if(inEvent.getType().equals(EventType.E)) {
					readyQ2.add(onCPU.getJob());
					onCPU = null;
					
				} else if(inEvent.getType().equals(EventType.C)) {
					readyQ1.add(ioWaitQ.poll());
				}
				
				
				inEvent = null;
				
			}
			
			if(exEvent != null && exEvent.getType().equals(EventType.A)) {
				handler.eventOccurs(exEvent, systemTime);
				handler.handleEventA(exEvent, MAX_MEMORY, jobSchedulingQ);
			}
			
			if(exEvent != null && exEvent.getType().equals(EventType.I)) {
				handler.eventOccurs(exEvent, systemTime);
				if(onCPU == null) {
					handler.schedule(jobSchedulingQ, readyQ1, usedMemory, MAX_MEMORY, systemTime);
					onCPU = handler.loadCPU(readyQ1, readyQ2, systemTime);
				}
				
				Job job = onCPU.getJob();
				job.setIoTimes(exEvent.getIoTime(), systemTime);
				ioWaitQ.add(job);
				onCPU = null;
			}
			
			usedMemory = handler.schedule(jobSchedulingQ, readyQ1, usedMemory, MAX_MEMORY, systemTime);
			if(onCPU == null) {
				onCPU = handler.loadCPU(readyQ1, readyQ2, systemTime);
			}
			
			if(exEvent != null && exEvent.getType().equals(EventType.D)) {
				handler.eventOccurs(exEvent, systemTime);
				handler.handleEventD(systemTime, MAX_MEMORY, usedMemory, jobSchedulingQ, readyQ1, readyQ2, ioWaitQ, onCPU, finishedJobs);
			
			}
			
			systemTime++;
		}
		handler.handleEventEndSim(systemTime, finishedJobs, usedMemory, MAX_MEMORY);
	}
}