package com.util;

public enum EventType {
	A, I, D, C, T, E, R;
	
	public static EventType getEventType(String e) {
		e = e.toLowerCase();
		EventType evnetType;
		
		switch(e) {
			case "a": evnetType = EventType.A; break;
			case "i": evnetType = EventType.I; break;
			case "d": evnetType = EventType.D; break;
			case "t": evnetType = EventType.T; break;
			case "e": evnetType = EventType.E; break;
			default: evnetType = EventType.R; break;
		}
		
		return evnetType;
	}
	
	//TODO: Override .toString()
}
