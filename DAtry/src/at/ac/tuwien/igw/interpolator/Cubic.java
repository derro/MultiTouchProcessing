package at.ac.tuwien.igw.interpolator;

public class Cubic extends Interpolator4x4 {
	public Cubic(int horizontalSamples, int verticalSamples, int horizontalMultiplier, int verticalMultiplier) { //, int imageWidth, int imageHeight) {
		super(horizontalSamples, verticalSamples, horizontalMultiplier,	verticalMultiplier); //, imageWidth, imageHeight);
	}

	protected double interp_func(double p0, double p1, double p2, double p3, double f) {
		double f2 = f * f;
		double i0 = p3 - p2 - p0 + p1;
		double i1 = p0 - p1 - i0;
		double i2 = p2 - p0;

		return i0 * f * f2 + i1 * f2 + i2 * f + p1;
	}
}
