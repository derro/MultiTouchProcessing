package at.ac.tuwien.igw.gestures;

import java.util.List;

import at.ac.tuwien.igw.types.CombinedGestureType;

public class Gesture {
	private long id;
	private CombinedGestureType type;
	private List<BasicGesture> basicGestures;
	private long timestamp;
	
	public Gesture() {}

	public Gesture(long id, CombinedGestureType type, List<BasicGesture> basicGestures,
			long timestamp) {
		super();
		this.id = id;
		this.type = type;
		this.basicGestures = basicGestures;
		this.timestamp = timestamp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CombinedGestureType getType() {
		return type;
	}

	public void setType(CombinedGestureType type) {
		this.type = type;
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

	@Override
	public String toString() {
		return "Gesture [id=" + id + ", type=" + type + ", basicGestures="
				+ basicGestures + ", timestamp=" + timestamp + "]";
	}
}
