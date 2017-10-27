/*******************************************************************************
/
/      filename:  com.util.EventType.java
/
/   description:  Enum to make event types type-safe. 
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
******************************************************************************/

package com.util;

public enum EventType {
	A, I, D, C, T, E, R;
	
	public static EventType getEventType(String e) {
		e = e.toLowerCase();
		EventType eventType;
		
		switch(e) {
			case "a": eventType = EventType.A; break;
			case "i": eventType = EventType.I; break;
			case "d": eventType = EventType.D; break;
			case "t": eventType = EventType.T; break;
			case "e": eventType = EventType.E; break;
			default: eventType = EventType.R; break;
		}
		
		return eventType;
	}
	
	//TODO: Override .toString()
}
