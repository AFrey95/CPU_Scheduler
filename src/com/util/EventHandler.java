package com.util;

import java.util.List;
import java.util.Queue;

import com.model.CPUProcess;
import com.model.Event;
import com.model.Job;

public class EventHandler {
	public void eventOccurs(Event event, int systemTime) {
		System.out.print(systemTime + " -- ");
		System.out.print("Event: " + event.getType().toString() + "   Time: " + event.getTime());
		if(event.getJob() != null) {
			System.out.println("   Job ID: " + event.getJob().getId());
		} else {
			System.out.println();
		}
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
		printQueue(ioWaitQ, "I/O Wait Queue");
		
		//CPU
		printCPU(onCPU);
		
		//Finished List
		printList(finishedJobs, "Finished List");
		
		System.out.println("There are " + (MAX_MEMORY - usedMemory) + " blocks of main memory available in the system.\n");

	}
	
	public CPUProcess handleEventT(Event event, List<Job> finishedJobs, Queue<Job> readyQ1, Queue<Job>readyQ2) {
		event.getJob().setComTime(event.getTime());
		finishedJobs.add(event.getJob());
		return loadCPU(readyQ1, readyQ2, event.getTime());
	}
	
	/**
	 * Handle job finished. Sets job's com. time, adds job to finished list. Loads a new job to CPU.
	 * @return 
	 */
	public CPUProcess handleEventT2(Event event, List<Job> finishedJobs, Queue<Job> readyQ1, Queue<Job>readyQ2, CPUProcess off, int systemTime) {
		event.getJob().setComTime(event.getTime());
		finishedJobs.add(event.getJob());
		return loadCPU2(readyQ1, readyQ2, off, event.getTime(), systemTime);
	}

	public CPUProcess handleEventE(Event event, Queue<Job> readyQ1, Queue<Job> readyQ2) {
		readyQ2.add(event.getJob());
		return loadCPU(readyQ1, readyQ2, event.getTime());
	}
	/**
	 * Handle quantum expired. Puts job in readyQ2. Loads new job to CPU.
	 * @return 
	 */
	public CPUProcess handleEventE2(Event event, Queue<Job> readyQ1, Queue<Job> readyQ2, CPUProcess off, int systemTime) {
		readyQ2.add(event.getJob());
		return loadCPU2(readyQ1, readyQ2, off, event.getTime(), systemTime);
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
	
	public CPUProcess loadCPU2(Queue<Job> readyQ1, Queue<Job> readyQ2, CPUProcess off, int eventTime, int systemTime) {
		CPUProcess on;
		if(readyQ1.size() > 0) {
			//put a process on the CPU
			on = new CPUProcess(readyQ1.poll(), 100, eventTime);
		} else if(readyQ2.size() > 0) {
			on = new CPUProcess(readyQ2.poll(), 300, eventTime);
		} else {
			on = null;
		}
		
		System.out.println("\nSystem Time: "+ systemTime);
		System.out.println(" ---------       -----       ---------");
		 System.out.printf("| Job %3s | --> | CPU | --> | Job %3s |\n", on.getJob().getId(), off.getJob().getId());
		System.out.println(" ---------       -----       ---------");
		System.out.println("Event time: "+ eventTime);
		System.out.println();
		
		return on;
	}

	public void handleEventEndSim(int systemTime, List<Job> finishedJobs) {
		printList(finishedJobs, "Final Finished List");
//		double average_TA = 0;
//		double average_wait = 0;
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append("The contents of the FINAL FINISHED LIST\n")
//		  .append("---------------------------------------\n\n");
//		
//		if(finishedJobs.size() > 0) {
//			sb.append("Job #  Arr. Time  Mem. Req.  Run Time  Start Time  Com. Time\n")
//			  .append("-----  ---------  ---------  --------  ----------  ---------\n\n");
//		}
//		
//		for(Job job : finishedJobs) {
//			sb.append(job.getId() + "\t" + job.getArrivalTime() + "\t"
//					+ job.getMemory() + "\t" + job.getRuntime() + "\t"
//					+ job.getStartTime() + "\t" + job.getArrivalTime() + "\n\n");
//			average_TA += (job.getComTime() - job.getArrivalTime());
////			average_wait += (job.getStartTime())
//		}
//		average_TA = average_TA/finishedJobs.size();
//		
//		sb.append("\nThe Average Turnaround Time for the simulation was " + average_TA + " units.\n\n");
//
//		System.out.println(sb.toString());
	}
	
	public void handleEventF(List<Job> finishedJobs) {
		StringBuilder sb = new StringBuilder();
		sb.append("The contents of the FINAL FINISHED LIST\n")
		  .append("---------------------------------------\n\n");
		
		if(finishedJobs.size() > 0) {
			sb.append("Job #  Arr. Time  Mem. Req.  Run Time  Start Time  Com. Time\n")
			  .append("-----  ---------  ---------  --------  ----------  ---------");
		} else {
			sb.append("The Finished List is empty.\n\n");
		}
		
		for(Job job : finishedJobs) {
			sb.append(job.getId() + "\t" + job.getArrivalTime() + "\t"
					+ job.getMemory() + "\t" + job.getRuntime() + "\t"
					+ job.getStartTime() + "\t" + job.getArrivalTime() + "\n\n");
		}
		
		System.out.println(sb.toString());
	}
	
	private void printQueue(Queue<Job> jobQ, String name) {
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
	
	private void printList(List<Job> jobList, String name) {
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
	
	private void printCPU(CPUProcess onCPU) {
		System.out.println("The CPU  Start Time  CPU burst time left");
		System.out.println("-------  ----------  -------------------\n");
		if(onCPU != null) {
			int time = (onCPU.getQuantum() < onCPU.getJob().getRemainingTime()) ? onCPU.getQuantum() : onCPU.getJob().getRemainingTime(); 
			System.out.printf("%7s  %10s  %19s\n\n\n", onCPU.getJob().getId(), onCPU.getJob().getStartTime(), time);
		} else {
			System.out.println("THE CPU IS IDLE!\n\n");
		}
		
	}
}
