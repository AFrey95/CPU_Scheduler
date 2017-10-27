package com.util;

import java.util.List;
import java.util.Queue;

import com.model.CPUProcess;
import com.model.Event;
import com.model.Job;

public class EventHandler {
	public void eventOccurs(Event event, int systemTime) {
//		System.out.print(systemTime + " -- ");
		System.out.println("Event: " + event.getType().toString() + "   Time: " + event.getTime());
//		if(event.getJob() != null) {
//			System.out.println("   Job ID: " + event.getJob().getId());
//		} else {
//			System.out.println();
//		}
	}
	
	public int schedule(Queue<Job> jobSchedulingQ, Queue<Job> readyQ1, int usedMemory, int MAX_MEMORY, int systemTime) {
		while(jobSchedulingQ.size() > 0 && jobSchedulingQ.peek().getMemory() <= (MAX_MEMORY - usedMemory)) {
//			System.out.println("Scheduling...");
//			usedMemory += jobSchedulingQ.peek().getMemory();
//			readyQ1.add(jobSchedulingQ.poll());
//			System.out.println("There are " + (MAX_MEMORY - usedMemory) + " blocks of main memory available in the system.");
			Job job = jobSchedulingQ.poll();
			job.setTimeOffJSQ(systemTime);
			usedMemory += job.getMemory();
			readyQ1.add(job);
			
		}
		return usedMemory;
	}
	
	public void handleEventA(Event event, int MAX_MEMORY, Queue<Job> jobSchedulingQ) {
		Job j = event.getJob();
		if(j.getMemory() > (MAX_MEMORY)) {
			System.out.println("This job exceeds the system's main memory capacity.");
		} else {
			jobSchedulingQ.add(j);
		}
	}
	
	public void handleEventD(int systemTime, int MAX_MEMORY, int usedMemory, Queue<Job> jobSchedulingQ, 
			Queue<Job> readyQ1, Queue<Job> readyQ2, Queue<Job> ioWaitQ, CPUProcess onCPU, List<Job> finishedJobs) {
		System.out.println("\n************************************************************\n");
		System.out.print("The status of the simulator at time " + systemTime + ".\n\n");
		
		//Job Scheduling Queue
		printQueue(jobSchedulingQ, "Job Scheduling Queue");
		
		//First Level Ready Queue
		printQueue(readyQ1, "First Level Ready Queue");
		
		//Second Level Ready Queue
		printQueue(readyQ2, "Second Level Ready Queue");
		
		//IO Wait Queue
		printIoQueue(ioWaitQ, "I/O Wait Queue");
		
		//CPU
		printCPU(onCPU);
		
		//Finished List
		printList(finishedJobs, "Finished List");
		
		System.out.println("There are " + (MAX_MEMORY - usedMemory) + " blocks of main memory available in the system.\n");

	}
	
	/**
	 * Production version. Handle job finished. Sets job's com. time, adds job to finished list. Loads a new job to CPU.
	 * @return 
	 */
	public CPUProcess handleEventT(Event event, List<Job> finishedJobs, Queue<Job> readyQ1, Queue<Job>readyQ2) {
		event.getJob().setComTime(event.getTime());
		finishedJobs.add(event.getJob());
		return loadCPU(readyQ1, readyQ2, event.getTime());
	}
	
	public CPUProcess handleEventT(Event event, List<Job> finishedJobs, Queue<Job> readyQ1, Queue<Job>readyQ2, CPUProcess off) {
		event.getJob().setComTime(event.getTime());
		finishedJobs.add(event.getJob());
		return loadCPU(readyQ1, readyQ2, event.getTime(), off);
	}

	/**
	 * Production Version. Handle quantum expired. Puts job in readyQ2. Loads new job to CPU.
	 * @return 
	 */
	public CPUProcess handleEventE(Event event, Queue<Job> readyQ1, Queue<Job> readyQ2) {
		readyQ2.add(event.getJob());
		return loadCPU(readyQ1, readyQ2, event.getTime());
	}
	
	public CPUProcess handleEventE(Event event, Queue<Job> readyQ1, Queue<Job> readyQ2, CPUProcess off) {
		readyQ2.add(event.getJob());
		return loadCPU(readyQ1, readyQ2, event.getTime(), off);
	}
	
	public CPUProcess loadCPU(Queue<Job> readyQ1, Queue<Job> readyQ2, int time) {
		if(readyQ1.size() > 0) {
			//put a process on the CPU
			return new CPUProcess(readyQ1.poll(), 100, time);
		} else if(readyQ2.size() > 0) {
			return new CPUProcess(readyQ2.poll(), 300, time);
		} else {
			return null;
		}
	}
	
	public CPUProcess loadCPU(Queue<Job> readyQ1, Queue<Job> readyQ2, int time, CPUProcess off) {
		CPUProcess on;
		if(readyQ1.size() > 0) {
			//put a process on the CPU
			on = new CPUProcess(readyQ1.poll(), 100, time);
			System.out.print("[readyQ1] --> [Job " + on.getJob().getId() 
							+ "(r: " + on.getJob().getBurstTimeLeft()
							+ ", q: " + on.getQuantum() + ")] --> ");
		} else if(readyQ2.size() > 0) {
			on = new CPUProcess(readyQ2.poll(), 300, time);
			System.out.print("[readyQ2] --> [Job " + on.getJob().getId()
					+ "(r: " + on.getJob().getBurstTimeLeft()
					+ ", q: " + on.getQuantum() + ")] --> ");
		} else {
			on = null;
		}
		System.out.print("[CPU]");
		
		if(off != null) {
			System.out.print(" --> [Job " + off.getJob().getId() + "] --> ");
			if(off.getJob().getBurstTimeLeft() == 0) {
				System.out.println("[Finished List]");
			} else {
				System.out.println("[readyQ2]");
			}
		} else {
			System.out.println();
		}
		System.out.println();
		return on;
	}

	public void handleEventEndSim(int systemTime, List<Job> finishedJobs, int usedMemory, int MAX_MEMORY) {
		System.out.println();
		printList(finishedJobs, "Final Finished List");
		double ta_sum = 0;
		double wait_sum = 0;
		
		for(Job job : finishedJobs) {
			ta_sum += (job.getComTime() - job.getArrivalTime());
			wait_sum += (job.getTimeOffJSQ() - job.getArrivalTime());
		}
		double average_TA = ta_sum/finishedJobs.size();
		double average_wait = wait_sum/finishedJobs.size();
		
		System.out.printf("The Average Turnaround Time for the simulation was %.3f units.\n\n", average_TA);
		System.out.printf("The Average Job Scheduling Wait Time for the simulation was %.3f units.\n\n", average_wait);
		System.out.println("There are " + (MAX_MEMORY - usedMemory) + " blocks of main memory available in the system.\n");

	}
	
	public void printQueue(Queue<Job> jobQ, String name) {
		System.out.println("The contents of the " + name.toUpperCase());
		  System.out.print("--------------------");
		for(int i = 0; i < name.length(); i++) System.out.print("-");
		System.out.println("\n");
		if(jobQ.size() > 0) {
			System.out.println("Job #  Arr. Time  Mem. Req.  Run Time");
			System.out.println("-----  ---------  ---------  --------\n");
			
			for(Job job : jobQ) {
				System.out.printf("%5s  %9s  %9s  %8s\n", job.getId(), job.getArrivalTime(), job.getMemory(), job.getRuntime());
			}
			System.out.println("\n");
		} else {
			System.out.println("The " + name + " is empty.\n\n");
		}
	}
	
	public void printIoQueue(Queue<Job> jobQ, String name) {
		System.out.println("The contents of the " + name.toUpperCase());
		  System.out.print("--------------------");
		for(int i = 0; i < name.length(); i++) System.out.print("-");
		System.out.println("\n");
		if(jobQ.size() > 0) {
			System.out.println("Job #  Arr. Time  Mem. Req.  Run Time  IO Start Time  IO Burst  Comp. Time");
			System.out.println("-----  ---------  ---------  --------  -------------  --------  ----------\n");
			
			for(Job job : jobQ) {
				System.out.printf("%5s  %9s  %9s  %8s  %13s  %8s  %10s\n", 
						job.getId(), job.getArrivalTime(), job.getMemory(), job.getRuntime(),
						job.getIoStartTime(), job.getIoBurstTime(), job.getIoComTime());
			}
			System.out.println("\n");
		} else {
			System.out.println("The " + name + " is empty.\n\n");
		}
	}
	
	public void printList(List<Job> jobList, String name) {
		System.out.println("The contents of the " + name.toUpperCase());
		  System.out.print("--------------------");
		for(int i = 0; i < name.length(); i++) System.out.print("-");
		System.out.println("\n");
		if(jobList.size() > 0) {
			System.out.println("Job #  Arr. Time  Mem. Req.  Run Time  Start Time  Com. Time");
			System.out.println("-----  ---------  ---------  --------  ----------  ---------\n");
			for(Job job : jobList) {
				System.out.printf("%5s  %9s  %9s  %8s  %10s  %9s\n", job.getId(), job.getArrivalTime(), job.getMemory(), job.getRuntime(), job.getStartTime(), job.getComTime());
			}
			System.out.println("\n");
		} else {
			System.out.println("The " + name + " is empty.\n");
		}
	}
	
	public void printCPU(CPUProcess onCPU) {
		System.out.println("The CPU  Start Time  CPU burst time left");
		System.out.println("-------  ----------  -------------------\n");
		if(onCPU != null) {
//			int time = (onCPU.getQuantum() < onCPU.getJob().getRemainingTime()) ? onCPU.getQuantum() : onCPU.getJob().getRemainingTime(); 
			System.out.printf("%7s  %10s  %19s\n\n\n", onCPU.getJob().getId(), onCPU.getJob().getStartTime(), onCPU.getJob().getBurstTimeLeft());
		} else {
			System.out.println("The CPU is idle.\n\n");
		}
	}

	public void idleCheck(CPUProcess onCPU, int systemTime) {
		if(onCPU == null) {
			System.out.println("THE CPU IS NULL!! TIME = " + systemTime);
		}
		
	}
}
