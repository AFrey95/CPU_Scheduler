package com.model;

public class Job {
	int id;
	int arrivalTime;
	int memory;
	int runtime;
	
	int startTime;
	int comTime;
	int burstTimeLeft;
	
	int timeOffJSQ;

	int ioBurstTime;
	int ioStartTime;
	int ioComTime;
	
	public Job() {}
	
	public Job(int arrivalTime, int id, int memory, int runtime) {
//		this.id = id;
//		this.arrivalTime = arrivalTime;
//		this.memory = memory;
//		this.runtime = runtime;
//		this.startTime = -1;
//		this.comTime = 0;
//		this.burstTimeLeft = runtime;
//		this.ioBurstTime = 0;
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.memory = memory;
		this.runtime = runtime;
		this.burstTimeLeft = runtime;
		
		this.startTime = -1;
		this.comTime = -1;
		this.ioBurstTime = -1;
		this.ioStartTime = -1;
		this.ioComTime = -1;
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

	public int getBurstTimeLeft() {
		return burstTimeLeft;
	}

	public void setBurstTimeLeft(int burstTimeLeft) {
		this.burstTimeLeft = burstTimeLeft;
	}

	public int getTimeOffJSQ() {
		return timeOffJSQ;
	}

	public void setTimeOffJSQ(int timeOffJSQ) {
		this.timeOffJSQ = timeOffJSQ;
	}

	public int getIoBurstTime() {
		return ioBurstTime;
	}

	public void setIoBurstTime(int ioBurstTime) {
		this.ioBurstTime = ioBurstTime;
	}

	public int getIoStartTime() {
		return ioStartTime;
	}

	public void setIoStartTime(int ioStartTime) {
		this.ioStartTime = ioStartTime;
	}

	public int getIoComTime() {
		return ioComTime;
	}

	public void setIoComTime(int ioComTime) {
		this.ioComTime = ioComTime;
	}

	public void setIoTimes(int ioBurstTime, int ioStartTime) {
		this.ioBurstTime = ioBurstTime;
		this.ioStartTime = ioStartTime;
		this.ioComTime = ioBurstTime + ioStartTime;
	}
	
	public void tick() {
		this.burstTimeLeft--;
	}

//	public void ioWait() {
//		ioBurstTime--;
//		
//	}
}
