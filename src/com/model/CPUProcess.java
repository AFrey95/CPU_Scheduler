/*******************************************************************************
/
/      filename:  com.model.CPUProcess.java
/
/   description:  This file contains the definition of an object which represents
  				  a process on the CPU.
/				 
/        author:  Frey, Andrew
/      login id:  FA_17_CPS356_26
/
/         class:  CPS 356
/    instructor:  Perugini
/    assignment:  Midterm Project
/
/      assigned:  October 12, 2017
/           due:  October 27, 2017
/
/******************************************************************************/

package com.model;

public class CPUProcess {
	Job job;
	int quantum;
	int source;
	
	public CPUProcess(Job job, int quantum, int startTime, int source) {
		this.job = job;
		this.quantum = quantum;
		if(this.job.getStartTime() == -1) {
			this.job.setStartTime(startTime);
		}
		this.source = source;
	}
	
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public int getQuantum() {
		return quantum;
	}
	public void setQuantum(int quantum) {
		this.quantum = quantum;
	}
	
	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public void tick() {
		this.job.tick();
		this.quantum--;
	}
}
