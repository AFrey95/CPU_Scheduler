package com.model;

import com.util.EventType;

public class Event {
	EventType type;
	int time;
	Job job;
		
	public Event(EventType type, int time, Job job) {
		this.type = type;
		this.time = time;
		this.job = job;
	}
	
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
}
