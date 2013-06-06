package at.ac.tuwien.igw.interpolator;

import at.ac.tuwien.igw.objects.Crosspoint;
import at.ac.tuwien.igw.toolbox.Toolbox;

public class Interpolator {
	private int horizontalSamples, verticalSamples; // count of measurements (32x22)
	private int pixelWidth, pixelHeight; // the actual pixel count of the interpolated image
	private int horizontalMultiplier, verticalMultiplier; // used to calculate pixelWidth and pixelHeight
	//int resizedWidth, resizedHeight; // the resized, blown up image; the rendered picture in the sketch.
	
	private float _fx, _fy;
	private double[] interpolPixels;

	public Interpolator(int horizontalSamples, int verticalSamples, int horizontalMultiplier, int verticalMultiplier){ //, int imageWidth, int imageHeight) {
		this.horizontalSamples = horizontalSamples; 	//32
		this.verticalSamples = verticalSamples;     	//22
		this.horizontalMultiplier = horizontalMultiplier;
		this.verticalMultiplier = verticalMultiplier;
		this.pixelWidth = (horizontalSamples - 1) * horizontalMultiplier;  //31*3=93
		this.pixelHeight = (verticalSamples - 1) * verticalMultiplier;	   //21*3=63	
		//this.resizedWidth = imageWidth;
		//this.resizedHeight = imageHeight;

		this._fx = (float) (1.0 / (2.0 * (float)horizontalMultiplier));
		this._fy = (float) (1.0 / (2.0 * (float)verticalMultiplier));

		this.interpolPixels = new double[pixelWidth * pixelHeight];
	}
	
	// includes value repetition at the borders
	protected double sensorVal(Crosspoint[][] cp, int x, int y) {
		x = Toolbox.limitVal(x, 0, horizontalSamples - 1);
		y = Toolbox.limitVal(y, 0, verticalSamples - 1);
		
		return cp[x][y].getSignalStrength();
	}
	
	public void interpolate(Crosspoint[][] cp) {
		beginInterpolation(cp);
		
		//horizontalSamples=3;
		//verticalSamples=2;
		
		for (int i = 0; i < horizontalSamples - 1; i++)			//32
			for (int j = 0; j < verticalSamples - 1; j++) {		//22
				beginInterpolate4(cp, i, j);

				for (int k = 0; k < horizontalMultiplier; k++)
					for (int l = 0; l < verticalMultiplier; l++) {
						double val = interpolate4(cp, i, j, k, l, _fx
								* (1.0 + 2 * k), _fy * (1.0 + 2 * l));
						val = Toolbox.limitVal(val, 0.0, 1.0);
						int arrayindex = ((j * verticalMultiplier + l) * pixelWidth)
								+ i * horizontalMultiplier + k;
						this.interpolPixels[arrayindex] = val;
						//System.out.println("i: "+i+" j: "+j+" k: "+k+" l: "+l+" val:" + val + " arrayindex: " + arrayindex);
					}

				finishInterpolate4(cp, i, j);
			}

		finishInterpolation(cp);
	}
	
	protected void beginInterpolation(Crosspoint[][] cp) {
		// override in subclass if necessary
	}
	
	protected void finishInterpolation(Crosspoint[][] cp) {
		// override in subclass if necessary
	}

	protected void beginInterpolate4(Crosspoint[][] cp, int xm, int ym) {
		// override in subclass if necessary
	}
	
	protected void finishInterpolate4(Crosspoint[][] cp, int xm, int ym) {
		// override in subclass if necessary
	}

	protected double interpolate4(Crosspoint[][] cp, int xm, int ym, int ix, int iy, double fx, double fy) {
		// override in subclass to implement 2x2 interpolation
		return 0;
	}
	
	public double[] getInterpolPixels() {
		return interpolPixels;
	}
	
	public int getPixelWidth() {
		return pixelWidth;
	}

	public int getPixelHeight() {
		return pixelHeight;
	}
}
