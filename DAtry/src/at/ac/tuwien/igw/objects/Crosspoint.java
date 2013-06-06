package at.ac.tuwien.igw.objects;
import at.ac.tuwien.igw.config.Configuration;


public class Crosspoint {
	private int x;
	private int y;
	private double measuredSignal;			//value measured
	private double measuredSignalAverage;	//average zero signal
	private boolean initial = true;
	private double signalStrength;			//calculated signal
	
	public Crosspoint (int x, int y) {
		this.x = x;
		this.y = y;
		this.measuredSignal = 1;
		this.measuredSignalAverage = 0;
		this.signalStrength = 1;
	}
	
	public void calculateSignalStrength(int msr) {
		this.measuredSignal = msr;
		
		/*
		this.signalStrength = this.measuredSignal - this.measuredSignalAverage * Configuration.treshold;
		if(this.signalStrength < 0.0) {
			this.signalStrength = new Double("0.0");
		}
		else {
			this.signalStrength /= (1023 - this.measuredSignalAverage * Configuration.treshold);
		}*/
		
		this.signalStrength = (this.measuredSignal / (this.measuredSignalAverage + 1));
		if(this.measuredSignal > (this.measuredSignalAverage * Configuration.cutoff)) {
			this.signalStrength = 1.0;
		}
		
		//turn the values: 0 is no input, 1 is input
		this.signalStrength = 1 - this.signalStrength;
		
		/*
		System.out.println("[x/y]["+x+"/"+y+"]-  signalStrength: " + signalStrength);
		System.out.println("[x/y]["+x+"/"+y+"]-  measuredSignal: " + measuredSignal);
		System.out.println("[x/y]["+x+"/"+y+"]-  measuredSignalAverage: " + measuredSignalAverage);
		System.out.println("[x/y]["+x+"/"+y+"]-  measuredSignalAverage*0.4: " + this.measuredSignalAverage * Configuration.cutoff);
		*/
	}
	
	public void accumulateAvgSig(double val) {
		if(initial) {
			this.measuredSignalAverage = val;
			initial=false;
		}	
		this.measuredSignalAverage = (this.measuredSignalAverage/2) + (val/2);
	}

	public void setSignalStrength(double signalStrength) {
		this.signalStrength = signalStrength;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getMeasuredSignal() {
		return measuredSignal;
	}

	public void setMeasuredSignal(double measuredSignal) {
		this.measuredSignal = measuredSignal;
	}

	public double getMeasuredSignalAverage() {
		return measuredSignalAverage;
	}

	public void setMeasuredSignalAverage(double measuredSignalAverage) {
		this.measuredSignalAverage = measuredSignalAverage;
	}

	public double getSignalStrength() {
		return signalStrength;
	}
}
