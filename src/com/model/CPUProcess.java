package com.model;

public class CPUProcess {
	Job job;
	int quantum;
	
	public CPUProcess(Job job, int quantum, int startTime) {
		this.job = job;
		this.quantum = quantum;
		if(this.job.getStartTime() == -1) {
			this.job.setStartTime(startTime);
		}
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
	
	public void tick() {
		this.job.tick();
		this.quantum--;
	}
}
