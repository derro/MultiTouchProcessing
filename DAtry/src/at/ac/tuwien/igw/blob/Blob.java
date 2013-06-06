package at.ac.tuwien.igw.blob;
import java.awt.Color;
import javax.swing.JPanel;

import at.ac.tuwien.igw.config.Configuration;

public class Blob extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private int xMin;
	private int xMax;
	private int yMin;
	private int yMax;
	private int mass;
	private long id;
	private long createdAt;
	private double xMiddle;
	private double yMiddle;
	
	public Blob(long id, int xMin, int xMax, int yMin, int yMax, int mass, long createdAt)
	{
		this.id = id;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.mass = mass;
		this.createdAt = createdAt;
		
		this.xMiddle = xMin + (double)getBlobWidth()/2;
		this.yMiddle = yMin + (double)getBlobHeight()/2;
		
		//Stuff for drawing BLOB
		this.setBounds(this.xMin*Configuration.pixelSize , this.yMin* Configuration.pixelSize, getBlobWidth()*Configuration.pixelSize, getBlobHeight()*Configuration.pixelSize);
		this.setBackground(new Color(1f, 1f, 1f, 0.3f));
		this.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0.1f, 1f, 1f, 0.9f)));
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getBlobWidth() {
		return (this.xMax-this.xMin+1);
	}
	
	public int getBlobHeight() {
		return (this.yMax-this.yMin+1);
	}

	public int getxMin() {
		return xMin;
	}

	public void setxMin(int xMin) {
		this.xMin = xMin;
	}

	public int getxMax() {
		return xMax;
	}

	public void setxMax(int xMax) {
		this.xMax = xMax;
	}

	public int getyMin() {
		return yMin;
	}

	public void setyMin(int yMin) {
		this.yMin = yMin;
	}

	public int getyMax() {
		return yMax;
	}

	public void setyMax(int yMax) {
		this.yMax = yMax;
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public double getxMiddle() {
		return xMiddle;
	}

	public void setxMiddle(double xMiddle) {
		this.xMiddle = xMiddle;
	}

	public double getyMiddle() {
		return yMiddle;
	}

	public void setyMiddle(double yMiddle) {
		this.yMiddle = yMiddle;
	}

	@Override
	public String toString()
	{
		return String.format("id %d: %4d -> %4d (widht: %4d), Y: %4d -> %4d (height: %4d), mass: %6d # Middle: x:%4.2f, y:%4.2f", id, xMin, xMax, getBlobWidth(), yMin, yMax, getBlobHeight(), mass, xMiddle, yMiddle);
	}
}