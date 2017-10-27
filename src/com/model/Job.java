package com.model;

public class Job {
	int id;
	int arrivalTime;
	int memory;
	int runtime;
	int startTime;
	int comTime;
	int remainingTime;
	int waitTime;
	int ioTime;
	int timeOffJSQ;
	
	public Job() {}
	
	public Job(int arrivalTime, int id,int memory, int runtime) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.memory = memory;
		this.runtime = runtime;
		this.startTime = -1;
		this.comTime = 0;
		this.remainingTime = runtime;
		this.waitTime = 0;
		this.ioTime = 0;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public int getRuntime() {
		return runtime;
	}
	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getComTime() {
		return comTime;
	}

	public void setComTime(int comTime) {
		this.comTime = comTime;
	}
	
	public int getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}
	
	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public int getIoTime() {
		return ioTime;
	}

	public void setIoTime(int ioTime) {
		this.ioTime = ioTime;
	}

	public int getTimeOffJSQ() {
		return timeOffJSQ;
	}

	public void setTimeOffJSQ(int timeOffJSQ) {
		this.timeOffJSQ = timeOffJSQ;
	}

	public void tick() {
		this.remainingTime--;
	}
	
	public void idle() {
		waitTime++;
	}

	public void ioWait() {
		ioTime--;
		
	}
}
