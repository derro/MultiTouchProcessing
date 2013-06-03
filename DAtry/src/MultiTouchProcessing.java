import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MultiTouchProcessing {
	public static Crosspoint[][] crosspoints;
	public static DrawMeasuredData[][] rects;
	public static HistogrammValue[] histogrammValues;
	public static boolean triggerMode = false;
	
	public SerialDevice serialDevice = null;
	public DataManager dataManager = null;

	public boolean ending = false;
	
	public JFrame frame;
	public JFrame blobFrame;
	public JPanel blobPanel;
	public JLabel frameLabel;
	public JLabel frameFpsLabel;
	public JPanel histoPanel;
	public JLabel triggerModeLabel;
	
	public int frames, fps;
	public long lastMillis;
	public int blobs;
	
	public double[][] binaryData = new double[Configuration.verticalWires][Configuration.horizontalWires]; 

	public static void main(String[] args) throws InterruptedException, IOException {
		new MultiTouchProcessing();
	}
	
	public MultiTouchProcessing() {
		// SWING FRAME
		frame = new JFrame("Data Visualization");
		frame.setSize(new Dimension(Configuration.verticalWires * Configuration.pixelSize + Configuration.verticalWires * Configuration.pixelSpace, Configuration.horizontalWires * Configuration.pixelSize + Configuration.horizontalWires * Configuration.pixelSpace + 300));
		frame.setVisible(true);
		//frame.pack();
		
		JButton b = new JButton("Stop");
		frame.add(b);
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ending = true;
			}
		};
		b.addActionListener(al);
		b.setBounds(20, 450, 40,20);
		
		frameLabel = new JLabel("Starting device...");
		frame.add(frameLabel);
		frameLabel.setBounds(70, 450, 400, 20);
		
		frameFpsLabel =  new JLabel("fps goes here... ");
		frame.add(frameFpsLabel);
		frameFpsLabel.setBounds(120, 470, 40, 20);
		
		triggerModeLabel =  new JLabel("off");
		triggerModeLabel.setForeground(Color.RED);
		frame.add(triggerModeLabel);
		triggerModeLabel.setBounds(70, 470, 40, 20);
		
		histoPanel = new JPanel();
		frame.add(histoPanel);
		histoPanel.setBackground(Color.DARK_GRAY);
		histoPanel.setLayout(null);
		histoPanel.setSize(300,100);
		histoPanel.setBounds(0, 500, 300, 100);
		
		blobFrame = new JFrame("Data Visualization - Blobs");
		blobFrame.setPreferredSize(new Dimension(Configuration.verticalWires * Configuration.pixelSize + Configuration.verticalWires * Configuration.pixelSpace + 20, Configuration.horizontalWires * Configuration.pixelSize + Configuration.horizontalWires * Configuration.pixelSpace + 25));
		blobFrame.setVisible(true);
		blobFrame.setLocation(frame.getSize().width,0);
		
		blobPanel = new JPanel();
		blobPanel.setBackground(Color.DARK_GRAY);
		blobPanel.setLayout(null);
		blobPanel.setSize(Configuration.verticalWires * Configuration.pixelSize + Configuration.verticalWires * Configuration.pixelSpace + 20, Configuration.horizontalWires * Configuration.pixelSize + Configuration.horizontalWires * Configuration.pixelSpace + 40);
		blobFrame.add(blobPanel, BorderLayout.CENTER);
		
		blobFrame.pack();

		// INITIALIZATION
		dataManager = new DataManager();
		crosspoints = new Crosspoint[Configuration.verticalWires][Configuration.horizontalWires];
		rects = new DrawMeasuredData[Configuration.verticalWires][Configuration.horizontalWires];
		histogrammValues = new HistogrammValue[100];
		fps = 0;
		lastMillis = -1;
		frames = 0;
		blobs = 0;
		
		for (int vert = 0; vert < Configuration.verticalWires; vert++) {
			for (int hor = 0; hor < Configuration.horizontalWires; hor++) {
				crosspoints[vert][hor] = new Crosspoint(vert, hor);
				rects[vert][hor] = new DrawMeasuredData(vert * Configuration.pixelSize, hor * Configuration.pixelSize, Configuration.pixelSize, Configuration.pixelSize, 0);
				frame.add(rects[vert][hor]);
				frame.revalidate();
			}
		}
		for(int i=0; i<100; i++) {
			histogrammValues[i] = new HistogrammValue(i,0.0);
			histoPanel.add(histogrammValues[i]);
		}
		for(int i=1; i<10; i++) {
			histoPanel.add(new HistogrammValue((i*10),1.0,true));
		}
		histoPanel.revalidate();
		System.out.println("HistogrammPanel: " + histoPanel.getComponentCount());
		
		if (Configuration.realData) {
			// REAL DATA
			processRealData();
		} else {
			//FAKE DATA
			processFakeData();
		}
	}

	private void processFakeData() {
		frameLabel.setText("adding Fakedata for visualization...");
		
		GaussianBlur gaus = new GaussianBlur();
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
			        //Remove border measure values for getting better graphics
					if(i <= Configuration.removeBorderVal-1 || j <= Configuration.removeBorderVal-1 || i >= Configuration.verticalWires-Configuration.removeBorderVal || j >= Configuration.horizontalWires-Configuration.removeBorderVal)
						MultiTouchProcessing.crosspoints[i][j].setSignalStrength(0.0);
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
				applyBlobDetection();
			}	
			
			drawHistogrammValues();
			
			frame.validate();
			frame.repaint();
			
			//dataManager.printData(1);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IO exception");
		}
	}

	private void processRealData() {
		GaussianBlur gaus = new GaussianBlur();
		
		frameLabel.setText("Open Connection to SerialDevice");
		serialDevice = new SerialDevice("/dev/tty.usbserial-A4001KsV");
		if (!serialDevice.openPort()) {
			return;
		}

		try {
			Thread.sleep(5000);
			serialDevice.writeData("s\n");
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int run = 0;
		frameLabel.setText("Start Calibration");
		while (!ending) {
			byte[] data = serialDevice.readData((Configuration.verticalWires * Configuration.horizontalWires * 10 / 8));
			
			dataManager.consumeSerialBuffer(data,(run<40),!Configuration.useGauss);
			if(run == 41) 
				frameLabel.setText("Device calibrated... You can use it now ;)");
			if(!(run<40)) {
				if(Configuration.useGauss)
					gaus.apply(!Configuration.useTreshold);
				if(Configuration.useTreshold)
					applyTreshold();
				if(Configuration.blobDetection) {
					applyBlobDetection();
					checkTriggerMode();
					
				}
				drawHistogrammValues();
				frame.validate();
				frame.repaint();
			}
			
			frames++;
			if(System.currentTimeMillis() - lastMillis > 1000) {
				lastMillis = System.currentTimeMillis();
				fps = frames;
				frames = 0;
				frameFpsLabel.setText(fps + "fps");
			}
				
			/*
			if (run % 50 == 0) {
				dataManager.printData(run);
			}*/
			run++; 
		}
		serialDevice.closePort();
	}

	public void applyBlobDetection() {
		double[] binaryOneDim = new double[Configuration.verticalWires*Configuration.horizontalWires];
		int s = 0;
		for (int i = 0; i < Configuration.horizontalWires; i++) {
		      for (int j = 0; j < Configuration.verticalWires; j++) {
		    	  if(Configuration.useTreshold)
		    		  binaryOneDim[s] = binaryData[j][i];
		    	  else
		    		  binaryOneDim[s] = crosspoints[j][i].getSignalStrength();
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
		
		blobs = blobList.size();
		
		//printBinaryDataOneDim(dstData);
		drawBlobs(blobList);
	}
	
	public void drawBlobs(ArrayList<Blob> blobList) {
		blobPanel.removeAll();

		for(Blob b : blobList) {
			blobPanel.add(b);
		}
		blobPanel.validate();
		blobPanel.repaint();
	}
	
	public void applyTreshold() {
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
	
	public void drawHistogrammValues() {
		double max = 0;
		double[] values = new double[100];
		for(int i=0; i<values.length;i++) {
			values[i] = 0.0;
		}
		
		for (int i = 0; i < Configuration.verticalWires; i++) {
		      for (int j = 0; j < Configuration.horizontalWires; j++) {
		    	  double signal = crosspoints[i][j].getSignalStrength();
		    	  int val = (int) (signal * 100);
		    	  values[val]++;
		    	  if(val!=0 && values[val] > max)
		    		  max = values[val];
		      }
		}      
		for(int i=0;i<values.length;i++){
			System.out.println("array " + i + " " + values[i]/max);
		}
		for(int i=0; i<values.length;i++) {
			histogrammValues[i].setVal(values[i]/max);
		}
	}
	
	public void printSignalData() {
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
	
	public void printBinaryData() {
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
	
	public void printBinaryDataOneDim(double array[]) {
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
	
	public void printDataInFile() throws IOException {
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
	
	public void checkTriggerMode() {
		//TODO check histogramm values
		if(blobs <= 5)
			triggerMode = true;
		else
			triggerMode = false;
		updateTriggerMode();
	}
	
	public void updateTriggerMode() {
		if(triggerMode){
			triggerModeLabel.setForeground(Color.GREEN);
			triggerModeLabel.setText("on");
		}
		else {
			triggerModeLabel.setForeground(Color.RED);
			triggerModeLabel.setText("off");
		}
	}
}
