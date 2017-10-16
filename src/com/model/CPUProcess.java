package com.model;

public class CPUProcess {
	Job job;
	int quantum;
	
	public CPUProcess(Job job, int quantum, int startTime) {
		this.job = job;
		this.job.setStartTime(startTime);
		this.quantum = quantum;
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
