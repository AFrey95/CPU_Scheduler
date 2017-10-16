package com.objects;

public class Job {
	int id;
	int arrivalTime;
	int memory;
	int runtime;
	int startTime;
	int comTime;
	
	public Job() {}
	
	public Job(int arrivalTime, int id,int memory, int runtime) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.memory = memory;
		this.runtime = runtime;
		this.startTime = 0;
		this.comTime = 0;
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
}
