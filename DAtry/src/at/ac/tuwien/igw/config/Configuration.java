package at.ac.tuwien.igw.config;

public class Configuration {
	// General settings
	public static int verticalWires = 32;			// count of vertical wires
	public static int horizontalWires = 22;			// count of horizontal wires
	
	// Advanced settings
	public static int removeBorderVal = 1; 			// how many measure rows/cols should be removed from the side (disruption values)
	public static double cutoff = 0.4;				// value for signal processing (Schwankungsbereich)
	public static boolean realData = true;			// is the device connected?
	
	// Layout settings
	public static int pixelSize = 20;				// size of pixel to display if interpolation is disabled 
	public static int pixelSpace = 0;				// space between pixels if interpolation is disabled
	
	// Treshold Settings							// set values to two defined areas: 1.0 and 0.5
	public static boolean useTreshold = false;		// activate/disable treshold
	public static double tresholdMiddle = 0.3;		// treshold for 0.5
	public static double tresholdTop = 0.5;			// treshold for 1.0

	// Gauss Settings
	public static boolean useGauss = false;			// enable/disable gauss filtering
	public static double sigma = 0.4;				// sigma to calculate gauss
	public static int kernelsize = 3;				// kernelsize
	
	// Blob Settings
	public static boolean blobDetection = true;		// enable/disable blob detection
	public static double blobTreshold = 0.6;		// treshold for counting to a blob
	public static double blobRangeRadius = 2;		// how far can a blob move in one measure-loop
	public static double blobRangeRadiusInterpolation = 8;		// how far can a blob move in one measure-loop
	public static int minBlobMass = 0;				// how big the blob has to be to be assigned as a blob
	public static int minBlobMassInterpolation = 4; // - // - for interpolation
	
	// Tap and Hold Setting
	public static double blobRangeRadiusInterpolationTapAndHold = 3;		// how far can a blob move in one measure-loop
	public static double blobRangeRadiusTapAndHold = 1;		// how far can a blob move in one measure-loop
	
	// Interpolation Settings
	public static boolean applyInterpolator = true;	// enable/disable interpolation
	public static InterpolatorType interpolatorUsed = InterpolatorType.CATMULLROM;
	public static int interpolatorResolution = 3;	// resolution of interpolation. e.g. 3 - calculates 3 pixel out of every original pixel
	public static int pixelSizeInterpolation = 7;	// size of pixel to display
	public static int pixelSpaceInterpolation = 0;	// space of pixel
	
	public enum InterpolatorType {
		CUBIC, CATMULLROM
	}
}
