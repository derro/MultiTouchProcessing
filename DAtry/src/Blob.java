import java.awt.Color;
import javax.swing.JPanel;

public class Blob extends JPanel {
	private static final long serialVersionUID = 1L;
	
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
		
		//Stuff for drawing BLOB
		this.setBounds(this.xMin*Configuration.pixelSize , this.yMin* Configuration.pixelSize, (this.xMax-this.xMin+1)*Configuration.pixelSize, (this.yMax-this.yMin+1)*Configuration.pixelSize);
		this.setBackground(new Color(1f, 1f, 1f, 0.3f));
		this.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0.1f, 1f, 1f, 0.9f)));
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
}