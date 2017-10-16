package com.util;

import java.util.List;
import java.util.Queue;

import com.model.Event;
import com.model.Job;

public class EventHandler {
	public void handleEventA(Event event, int MAX_MEMORY, Queue<Job> jobSchedulingQ) {
		Job j = event.getJob();
		if(j.getMemory() > (MAX_MEMORY)) {
			System.out.println("This job exceeds the system's main memory capacity.");
		} else {
			jobSchedulingQ.add(j);
		}
	}
	
	public void handleEventD(int systemTime, int MAX_MEMORY, int usedMemory, Queue<Job> jobSchedulingQ, Queue<Job> readyQ1, Queue<Job> readyQ2, List<Job> finishedJobs) {
		StringBuilder sb = new StringBuilder();
		sb.append("The status of the simulator at time " + systemTime + ".\n\n");
		
		//TODO: Formatting tables
		
		//Job Scheduling Queue
		sb.append("The contents of the JOB SCHEDULING QUEUE\n")
		  .append("----------------------------------------\n\n");
		if(jobSchedulingQ.size() > 0) {
			sb.append("Job #\tArr. Time\tMem. Req.\tRun Time\n")
			  .append("-----\t---------\t---------\t--------\n\n");
			
			for(Job job : jobSchedulingQ) {
				sb.append(job.getId() + "\t" + job.getArrivalTime() + "\t"
						+ job.getMemory() + "\t"  + job.getRuntime() + "\n\n");
			}
		} else {
			sb.append("The Job Scheduling Queue is empty.\n\n");
		}
		
		//First Level Ready Queue
		sb.append("\n\nThe contents of the FIRST LEVEL READY QUEUE\n") 
		  .append("-------------------------------------------\n\n");
		if(readyQ1.size() > 0) {
			sb.append("Job #\tArr. Time\tMem. Req.\tRun Time\n")
			  .append("-----\t---------\t---------\t--------\n\n");
			
			for(Job job : readyQ1) {
				sb.append(job.getId() + "\t" + job.getArrivalTime() + "\t"
						+ job.getMemory() + "\t"  + job.getRuntime() + "\n\n");
			}
		} else {
			sb.append("The First Ready Queue is empty.\n\n");
		}
		
		//Second Level Ready Queue
		sb.append(" contents of the SECOND LEVEL READY QUEUE\n") 
		  .append("-------------------------------------------\n\n");
		if(readyQ2.size() > 0) {
			sb.append("Job #\tArr. Time\tMem. Req.\tRun Time\n")
			  .append("-----\t---------\t---------\t--------\n\n");
			
			for(Job job : readyQ2) {
				sb.append(job.getId() + "\t" + job.getArrivalTime() + "\t"
						+ job.getMemory() + "\t"  + job.getRuntime() + "\n\n");
			}
		} else {
			sb.append("The Second Ready Queue is empty.\n\n");
		}
		
		//IO Wait Queue
		
		//Finished List
		sb.append("The contents of the FINISHED LIST\n\n")
		  .append("-------------------------------------------\n\n");
		if(finishedJobs.size() > 0) {
			sb.append("Job #\tArr. Time\tMem. Req.\tRun Time\tStart Time\tCom. Time\n")
			  .append("-----\t---------\t---------\t--------\t----------\t---------\n\n");
			for(Job job : finishedJobs) {
				sb.append(job.getId() + "\t" + job.getArrivalTime() + "\t"
						+ job.getMemory() + "\t" + job.getRuntime() + "\t"
						+ job.getStartTime() + "\t" + job.getArrivalTime() + "\n\n");
			}
		} else {
			sb.append("The Finished List is empty.\n\n");
		}
		
		sb.append("There are " + (MAX_MEMORY - usedMemory) + " blocks available in the system.\n");
		System.out.println(sb.toString());
	}
	
	public void handleEventT(Event event, List<Job> finishedJobs) {
		event.getJob().setComTime(event.getTime());
		finishedJobs.add(event.getJob());
	}

	public void handleEventE(Event event, Queue<Job> readyQ2) {
		readyQ2.add(event.getJob());
	}

	public void handleEventEndSim(int systemTime, List<Job> finishedJobs) {
		double average_TA = 0;
		double average_wait = 0;
		StringBuilder sb = new StringBuilder();
		
		sb.append("The contents of the FINAL FINISHED LIST\n")
		  .append("---------------------------------------\n\n");
		
		if(finishedJobs.size() > 0) {
			sb.append("Job #  Arr. Time  Mem. Req.  Run Time  Start Time  Com. Time\n")
			  .append("-----  ---------  ---------  --------  ----------  ---------");
		}
		
		for(Job job : finishedJobs) {
			sb.append(job.getId() + "\t" + job.getArrivalTime() + "\t"
					+ job.getMemory() + "\t" + job.getRuntime() + "\t"
					+ job.getStartTime() + "\t" + job.getArrivalTime() + "\n\n");
			average_TA += (job.getComTime() - job.getArrivalTime());
//			average_wait += (job.getStartTime())
		}
		average_TA = average_TA/finishedJobs.size();
		
		sb.append("\nThe Average Turnaround Time for the simulation was " + average_TA + " units.\n\n");
//		sb.append()
	}
}
