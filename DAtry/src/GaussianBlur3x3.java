
public class GaussianBlur3x3 extends GaussianBlur {
	private int kernelsize = 3; 
	private int max = kernelsize/2;
	
	double[][] kernel;
	
	GaussianBlur3x3() {
		kernel = new double[this.kernelsize][this.kernelsize];
		calculateKernel();
	}
	
	private void calculateKernel() {
		kernel[0][0] = 0.011343736558495071;
		kernel[0][1] = 0.0838195058022106;
		kernel[0][2] = 0.011343736558495071;
		kernel[1][0] = 0.0838195058022106;
		kernel[1][1] = 0.6193470305571772;
		kernel[1][2] = 0.0838195058022106;
		kernel[2][0] = 0.011343736558495071;
		kernel[2][1] = 0.0838195058022106;
		kernel[2][2] = 0.011343736558495071;
	}
	
	public void apply(boolean draw) {
		
		int width = Configuration.verticalWires;
		int height = Configuration.horizontalWires;
		
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				
				if((x>=max) && (x < (width-max)) && (y >= max) && (y< (height-max)))
				{
					double[][] weightedValues = new double[this.kernelsize][this.kernelsize];
					
					for(int xg=0; xg<kernelsize; xg++) {
						for(int yg=0; yg<kernelsize; yg++) {
							int x2 = xg-max;
							int y2 = yg-max;
							double signal = MultiTouchProcessing.crosspoints[x+x2][y+y2].getSignalStrength();
							weightedValues[xg][yg] = signal * kernel[xg][yg];
						}
					}
					double sum = calculateSum(weightedValues);
					MultiTouchProcessing.crosspoints[x][y].setSignalStrength(sum);
					if(draw)
						MultiTouchProcessing.rects[x][y].setValue(sum);
				}
			}
		}
	}

	private double calculateSum(double[][] weightedValues) {
		double sum = 0.0;
		for(int i=0; i< kernelsize; i++) {
			for(int j=0; j< kernelsize; j++) {
				sum += weightedValues[i][j];
			}
		}
		return sum;
	}
}
