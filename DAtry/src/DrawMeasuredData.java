import java.awt.Graphics;
import java.awt.Color;

import javax.swing.JComponent;

public class DrawMeasuredData extends JComponent {
	private static final long serialVersionUID = 1L;
	
	int x = 0;
	int y = 0;
	int width = 0;
	int length = 0;
	double value = 0;
	int color = 0;
	
	public DrawMeasuredData(int x, int y, int width, int length, double value){
		this.x = x;
		this.y = y;
		this.width = width;
		this.length = length;
		this.value = value;
		this.color = (int)value * 255;
	}

	public void setValue(double value) {
		this.value = value;
		this.color = (int) Math.round(value * 255);
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(new Color(this.color,this.color,this.color));
		g.fillRect(this.x, this.y, this.width, this.length);
	}
	
}
