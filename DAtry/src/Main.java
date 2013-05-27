import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
	public static SerialDevice serialDevice = null;
	public static DataManager dataManager = null;

	public static boolean ending = false;
	public static Crosspoint[][] crosspoints;
	public static DrawMeasuredData[][] rects;
	public static JFrame frame;
	public static JFrame blobFrame;
	public static JPanel blobPanel;
	public static ArrayList<Blob> drawnBlobs;
	
	public static double[][] binaryData = new double[Configuration.verticalWires][Configuration.horizontalWires]; 

	public static void main(String[] args) throws InterruptedException, IOException {
		// SWING FRAME
		frame = new JFrame("Data Visualization");
		frame.setSize(new Dimension(Configuration.verticalWires * Configuration.pixelSize + Configuration.verticalWires * Configuration.pixelSpace, Configuration.horizontalWires * Configuration.pixelSize + Configuration.horizontalWires * Configuration.pixelSpace + 70));
		frame.setVisible(true);
		//frame.pack();
		
		JButton b = new JButton("Stop");
		frame.add(b);
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ending = true;
			}
		};
		b.addActionListener(al);
		b.setBounds(20, 450, 40,20);
		
		blobFrame = new JFrame("Data Visualization - Blobs");
		blobFrame.setPreferredSize(new Dimension(Configuration.verticalWires * Configuration.pixelSize + Configuration.verticalWires * Configuration.pixelSpace + 20, Configuration.horizontalWires * Configuration.pixelSize + Configuration.horizontalWires * Configuration.pixelSpace + 40));
		blobFrame.setVisible(true);
		blobFrame.setLocation(frame.getSize().width,0);
		//JPanel panel = new JPanel();
		//panel.setSize(Configuration.verticalWires * Configuration.pixelSize + Configuration.verticalWires * Configuration.pixelSpace + 20, Configuration.horizontalWires * Configuration.pixelSize + Configuration.horizontalWires * Configuration.pixelSpace + 40);
		//blobFrame.setLayout()
		blobFrame.pack();

		// INITIALIZATION
		GaussianBlur gaus = new GaussianBlur();
		drawnBlobs = new ArrayList<Blob>();
		dataManager = new DataManager();
		crosspoints = new Crosspoint[Configuration.verticalWires][Configuration.horizontalWires];
		rects = new DrawMeasuredData[Configuration.verticalWires][Configuration.horizontalWires];
		
		for (int vert = 0; vert < Configuration.verticalWires; vert++) {
			for (int hor = 0; hor < Configuration.horizontalWires; hor++) {
				crosspoints[vert][hor] = new Crosspoint(vert, hor);
				rects[vert][hor] = new DrawMeasuredData(vert * Configuration.pixelSize, hor * Configuration.pixelSize, Configuration.pixelSize, Configuration.pixelSize, 0);
				frame.add(rects[vert][hor]);
				frame.revalidate();
			}
		}
		
		// REAL DATA
		if (Configuration.realData) {
			serialDevice = new SerialDevice("/dev/tty.usbserial-A4001KsV");
			if (!serialDevice.openPort()) {
				return;
			}

			Thread.sleep(5000);
			serialDevice.writeData("s\n");
			Thread.sleep(200);
		
			int run = 0;
			while (!ending) {
				byte[] data = serialDevice.readData((Configuration.verticalWires * Configuration.horizontalWires * 10 / 8));
				
				dataManager.consumeSerialBuffer(data,(run<40),!Configuration.useGauss);
				if(!(run<40) && Configuration.useGauss)
					gaus.apply(false);
				if(!(run<40) && Configuration.useTreshold)
					applyTreshold();
				if(!(run<40) && Configuration.blobDetection && run%100==0) {
					//printSignalData();
					//printBinaryData();
					applyBlobDetection(run<=100);
				}
				
				frame.validate();
				frame.repaint();
				
				/*
				if (run % 50 == 0) {
					dataManager.printData(run);
				}*/
				run++; 
			}
			serialDevice.closePort();
		} else {
		//FAKE DATA
			try {
				BufferedReader br = new BufferedReader(new FileReader(
						"fakehand.txt"));

				String msr = br.readLine();
				String avg = br.readLine();
				if(msr != null && avg != null) {
					String average[] = avg.split(",");
					String signalStrength[] = msr .split(",");

					int k = 0;
					for (int i = 0; i < Configuration.verticalWires; i++) {
				      for (int j = 0; j < Configuration.horizontalWires; j++) {
				        crosspoints[i][j].setMeasuredSignalAverage(Double.parseDouble(average[k]));    
				        double signal = new Double(signalStrength[k]);
				        crosspoints[i][j].calculateSignalStrength((int)signal);
				        rects[i][j].setValue(crosspoints[i][j].getSignalStrength());
				        k++;
				      }
				    }
				}
				br.close();
				if(Configuration.useGauss)
					gaus.apply(true);
				
				if((Configuration.useTreshold)) {
					printSignalData();
					applyTreshold();
					printBinaryData();
				}
				
				if((Configuration.blobDetection)) {
					applyBlobDetection(true);
				}	
				
				frame.validate();
				frame.repaint();
				
				//frame.revalidate();
				//dataManager.printData(1);
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			} catch (IOException e) {
				System.out.println("IO exception");
			}

		}
	}

	public static void applyBlobDetection(boolean first) {
		double[] binaryOneDim = new double[Configuration.verticalWires*Configuration.horizontalWires];
		int s = 0;
		for (int i = 0; i < Configuration.horizontalWires; i++) {
		      for (int j = 0; j < Configuration.verticalWires; j++) {
		    	  binaryOneDim[s] = binaryData[j][i];
		    	  s++;
		      }
		}      
		
		// Create Blob Finder
		BlobFinder finder = new BlobFinder(Configuration.verticalWires, Configuration.horizontalWires);
		double[] dstData = new double[binaryOneDim.length];
		ArrayList<Blob> blobList = new ArrayList<Blob>();
		finder.detectBlobs(binaryOneDim, dstData, 0, -1, blobList);

		// List Blobs
		System.out.printf("Found %d blobs:\n", blobList.size());
		System.out.printf("=================\n");
		int i=1;
		StringBuilder sb = new StringBuilder();
		for(Blob blob: blobList) {
			sb.append("blob nr."+i+" with "+blob.mass+" points.\n");
			i++;
		}
		System.out.println(sb.toString());
		
		//printBinaryDataOneDim(dstData);
		drawBlobs(blobList, first);
	}
	
	public static void applyTreshold() {
		for (int i = 0; i < Configuration.verticalWires; i++) {
		      for (int j = 0; j < Configuration.horizontalWires; j++) {
		        double signal = crosspoints[i][j].getSignalStrength();
		        if(signal > Configuration.tresholdMiddle) {
		        	//crosspoints[i][j].setSignalStrength(0.5);
	        		binaryData[i][j] = 0.5;
	        		
		        	if(signal > Configuration.tresholdTop) {
		        		//crosspoints[i][j].setSignalStrength(1.0);
		        		binaryData[i][j] = 1.0;
		        	}
		        }
		        else {
		        	//crosspoints[i][j].setSignalStrength(0.0);
		        	binaryData[i][j] = 0.0;
		        }
		        //rects[i][j].setValue(crosspoints[i][j].getSignalStrength());
		        rects[i][j].setValue(binaryData[i][j]);
		      }
		}
		//printBinaryData();
	}
	
	public static void printSignalData() {
		StringBuilder sb = new StringBuilder();
		DecimalFormat df = new DecimalFormat(".000");
		for (int i = 0; i < Configuration.horizontalWires; i++) {
		      for (int j = 0; j < Configuration.verticalWires; j++) {
		    	  sb.append("   ");
		    	  if(crosspoints[j][i].getSignalStrength()==0.0)
	    	  		sb.append("____");
		    	  else
					sb.append(df.format(crosspoints[j][i].getSignalStrength()));
		      }
		      sb.append("\n");
		}      
		System.out.println(sb.toString());
	}
	
	public static void printBinaryData() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Configuration.horizontalWires; i++) {
		      for (int j = 0; j < Configuration.verticalWires; j++) {
		    	  sb.append("   ");
		    	  if(binaryData[j][i]==0.0)
	    	  		sb.append("___");
		    	  else
					sb.append(binaryData[j][i]);
		      }
		      sb.append("\n");
		}      
		System.out.println(sb.toString());
	}
	
	public static void printBinaryDataOneDim(double array[]) {
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for(double d : array) {
			sb.append("   ");
			if(d==0.0)
				sb.append("___");
			else
				sb.append(d);
			if(i % Configuration.verticalWires == 0)
				sb.append("\n");
			i++;
		}
		System.out.println(sb.toString());
	}
	
	public static void drawBlobs(ArrayList<Blob> blobList, boolean first) {
		blobFrame.getContentPane().removeAll();
		
		/*for(Blob b : drawnBlobs) {
			blobFrame.remove(b);
			//frame.revalidate();
		}
		drawnBlobs.clear();
		*/
		
		for(Blob b : blobList) {
			blobFrame.getContentPane().add(b);
			//drawnBlobs.add(b);
			blobFrame.setComponentZOrder(b, 0);
			blobFrame.revalidate();
		}
		if(first)
			blobFrame.validate();
		else {
			blobFrame.revalidate();
		}
		blobFrame.repaint();
		//frame.pack();
		
	}
	
	public static void printDataInFile() throws IOException {
		File file = new File("calculateddata.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (int i = 0; i < Configuration.horizontalWires; i++) {
			  StringBuilder sb = new StringBuilder();
		      for (int j = 0; j < Configuration.verticalWires; j++) {
		    	sb.append(crosspoints[j][i].getSignalStrength());
		    	if(j<Configuration.verticalWires-1)
		    		sb.append(",");
		      }
		      bw.write(sb.toString());
		      bw.newLine();
		}
		bw.close();
	}
}
