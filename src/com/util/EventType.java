package com.util;

public enum EventType {
	A, I, D, C, T, E, R, F;
	
	public static EventType getEventType(String e) {
		e = e.toLowerCase();
		EventType eventType;
		
		switch(e) {
			case "a": eventType = EventType.A; break;
			case "i": eventType = EventType.I; break;
			case "d": eventType = EventType.D; break;
			case "t": eventType = EventType.T; break;
			case "e": eventType = EventType.E; break;
			case "f": eventType = EventType.F; break;
			default: eventType = EventType.R; break;
		}
		
		return eventType;
	}
	
	//TODO: Override .toString()
}
