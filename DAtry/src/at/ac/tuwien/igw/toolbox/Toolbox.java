package at.ac.tuwien.igw.toolbox;

public class Toolbox {
	public static int limitVal(int val, int min, int max){
		if(val < min) {
			return min;
		}
		if(val > max) {
			return max;
		}
		return val;
	}
	
	public static double limitVal(double val, double min, double max){
		if(val < min) {
			return min;
		}
		if(val > max) {
			return max;
		}
		return val;
	}
}
