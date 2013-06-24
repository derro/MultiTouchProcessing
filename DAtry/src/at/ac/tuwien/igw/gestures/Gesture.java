package at.ac.tuwien.igw.gestures;

import java.util.List;

public class Gesture {
	private int id;
	private String name;
	private List<BasicGesture> basicGestures;
	private long timestamp;
	
	public Gesture() {}

	public Gesture(int id, String name, List<BasicGesture> basicGestures,
			long timestamp) {
		super();
		this.id = id;
		this.name = name;
		this.basicGestures = basicGestures;
		this.timestamp = timestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BasicGesture> getBasicGestures() {
		return basicGestures;
	}

	public void setBasicGestures(List<BasicGesture> basicGestures) {
		this.basicGestures = basicGestures;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
