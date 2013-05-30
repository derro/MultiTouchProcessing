
public class Configuration {
	public static int verticalWires = 32;
	public static int horizontalWires = 22;
	
	public static int removeBorderVal = 1; 	//how many measure rows/cols should be removed from the side
	
	public static int printoutloop = 1;

	public static int pixelSize = 20;
	public static int pixelSpace = 0;
	
	public static double cutoff = 0.4;
	public static boolean realData = false;
	
	public static double tresholdMiddle = 0.3;
	public static double tresholdTop = 0.6;
	public static boolean useTreshold = false;

	public static boolean useGauss = true;
	public static double sigma = 0.5;
	public static int kernelsize = 3;
	
	public static boolean blobDetection = true;
	public static double blobTreshold = 0.6;
}
