
public class GaussianBlur {
	private double sigma = Configuration.sigma;
	private int kernelsize = Configuration.kernelsize; 	//only odd numbers accepted
	private int max = kernelsize/2;
	
	double[][] kernel;
	
	GaussianBlur() {
		kernel = new double[this.kernelsize][this.kernelsize];
		calculateKernel();
	}
	
	private void calculateKernel() {
		for(int x=0; x<kernelsize; x++) {
			for(int y=0; y<kernelsize; y++) {
				
				double first = 1/(2*Math.PI*sigma*sigma);
				int x2 = max-x;
				x2 = (x2<0)?x2*-1:x2;
				int y2 = max-y;
				y2 = (y2<0)?y2*-1:y2;
				
				double second = -1*((x2*x2 + y2*y2)/(2*sigma*sigma));
				double gaussval = first * Math.exp(second);
				
				kernel[x][y] = gaussval; 
				//System.out.println(x + "/" + y + ": " + kernel[x][y]);
			}
		}
		
		double sum = calculateSum(kernel);
		//System.out.println("sum: " +sum);
		
		for(int x=0; x<kernelsize; x++) {
			for(int y=0; y<kernelsize; y++) {
				kernel[x][y] *= (1/sum); 
				System.out.println(x + "/" + y + ": " + kernel[x][y]);
			}
		}
		//sum = calculateSum(kernel);
		//System.out.println("sum: " +sum);
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
