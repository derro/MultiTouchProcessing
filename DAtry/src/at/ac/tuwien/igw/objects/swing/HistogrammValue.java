package at.ac.tuwien.igw.objects.swing;
import java.awt.Color;

import javax.swing.JPanel;

public class HistogrammValue extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private int width = 3;
	private int maxheight = 100;
	
	private final int signalValue;
	private double val;
	
	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
		if(val>1.0)
			val = 1.0;
		this.setBounds(signalValue*width,(100-(int)(val*maxheight)),width,(int)(val*maxheight));
	}

	public HistogrammValue(int signalValue, double val){
		this.signalValue = signalValue;
		this.val = val;
		
		this.setBounds(signalValue*width,(100-(int)(val*maxheight)),width,(int)(val*maxheight));
		this.setBackground(new Color(0.1f, 1f, 1f, 0.8f));
	}
	
	public HistogrammValue(int signalValue, double val, boolean helper){
		this.signalValue = signalValue;
		this.val = val;
		
		this.setBounds(signalValue*width,0,width,100);
		this.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.4f));
	}
}
