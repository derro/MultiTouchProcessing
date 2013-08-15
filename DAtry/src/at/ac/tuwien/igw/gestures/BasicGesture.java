package at.ac.tuwien.igw.gestures;

import java.util.List;

import at.ac.tuwien.igw.blob.Blob;
import at.ac.tuwien.igw.types.BasicGestureType;
import at.ac.tuwien.igw.types.Movements;

public class BasicGesture {
	private long id;
	private BasicGestureType type;
	private boolean completed;
	
	private long timestampStarted;
	private long timestampStopped;
	private int xStart;
	private int yStart;
	private int xEnd;
	private int yEnd;
	private int averageMass;
	private Movements movement;
	private boolean moved; 
	private boolean sendStatus;
	private int degree;
	private List<Blob> history;
	
	public BasicGesture() {}
	
	public BasicGesture(long id, BasicGestureType type, boolean completed,
			long timestampStarted, long timestampStopped, int xStart, int yStart,
			Movements momevment) {
		this.id = id;
		this.type = type;
		this.completed = completed;
		this.timestampStarted = timestampStarted;
		this.timestampStopped = timestampStopped;
		this.xStart = xStart;
		this.yStart = yStart;
		this.movement = momevment;
		this.moved = false;
		this.sendStatus = false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BasicGestureType getType() {
		return type;
	}

	public void setType(BasicGestureType type) {
		this.type = type;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public int getxStart() {
		return xStart;
	}

	public void setxStart(int xStart) {
		this.xStart = xStart;
	}

	public int getyStart() {
		return yStart;
	}

	public void setyStart(int yStart) {
		this.yStart = yStart;
	}

	public int getxEnd() {
		return xEnd;
	}

	public void setxEnd(int xEnd) {
		this.xEnd = xEnd;
	}

	public int getyEnd() {
		return yEnd;
	}

	public void setyEnd(int yEnd) {
		this.yEnd = yEnd;
	}

	public long getTimestampStarted() {
		return timestampStarted;
	}

	public void setTimestampStarted(long timestampStarted) {
		this.timestampStarted = timestampStarted;
	}

	public long getTimestampStopped() {
		return timestampStopped;
	}

	public void setTimestampStopped(long timestampStopped) {
		this.timestampStopped = timestampStopped;
	}

	public int getAverageMass() {
		return averageMass;
	}

	public void setAverageMass(int averageMass) {
		this.averageMass = averageMass;
	}

	public Movements getMovement() {
		return movement;
	}

	public void setMovement(Movements momevment) {
		this.movement = momevment;
	}

	public List<Blob> getHistory() {
		return history;
	}

	public void setHistory(List<Blob> history) {
		this.history = history;
	}
	
	public boolean isMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public boolean isSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(boolean sendStatus) {
		this.sendStatus = sendStatus;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}
	
	public long getActualDuration() {
		return System.currentTimeMillis() - this.getTimestampStarted();
	}

	@Override
	public String toString() {
		return "Basic Gesture [id:"+id+", type:"+type+", movement:"+movement+",isMoved:"+moved+",sendStatus:"+sendStatus+", degree:"+degree+", Start["+xStart+","+yStart+"], End["+xEnd+","+yEnd+"], timestampStarted:"+timestampStarted+", timestampStopped:"+timestampStopped+", duration:"+(timestampStopped-timestampStarted);
	}
}

