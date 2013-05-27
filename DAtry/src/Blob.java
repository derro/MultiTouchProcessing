import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

public class Blob extends JComponent {
	public int xMin;
	public int xMax;
	public int yMin;
	public int yMax;
	public int mass;
	public int id;

	public Blob(int xMin, int xMax, int yMin, int yMax, int mass)
	{
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.mass = mass;
	}

	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString()
	{
		return String.format("id %d: %4d -> %4d, Y: %4d -> %4d, mass: %6d", id, xMin, xMax, yMin, yMax, mass);
	}
	
	public void paint(Graphics g) {
		//System.out.println("draw blob: " +  this.toString());
		g.setColor(Color.RED);
		g.drawRect(this.xMin*Configuration.pixelSize , this.yMin* Configuration.pixelSize, (this.xMax-this.xMin+1)*Configuration.pixelSize, (this.yMax-this.yMin+1)*Configuration.pixelSize);
	}
}