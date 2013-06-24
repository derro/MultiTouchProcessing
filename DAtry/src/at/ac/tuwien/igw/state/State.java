package at.ac.tuwien.igw.state;

import java.util.List;

import at.ac.tuwien.igw.gestures.Gesture;

public class State {
	private String id;
	private String name;
	private State parent;
	private List<Gesture> gesturesAllowed;
	
	public State() {} 
	
	public State(String id, String name, State parent,
			List<Gesture> gesturesAllowed) {
		super();
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.gesturesAllowed = gesturesAllowed;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public State getParent() {
		return parent;
	}
	public void setParent(State parent) {
		this.parent = parent;
	}
	public List<Gesture> getGesturesAllowed() {
		return gesturesAllowed;
	}
	public void setGesturesAllowed(List<Gesture> gesturesAllowed) {
		this.gesturesAllowed = gesturesAllowed;
	}
	
	@Override
	public String toString() {
		String parentString = "";
		if(parent != null)
			parentString = " , parentState: " + parent.getName();
		return ">> State [" + name + parentString+"]";
	}
}
