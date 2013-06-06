package at.ac.tuwien.igw.main;
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
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import at.ac.tuwien.igw.blob.Blob;
import at.ac.tuwien.igw.blob.BlobFinder;
import at.ac.tuwien.igw.config.Configuration;
import at.ac.tuwien.igw.filtering.GaussianBlur;
import at.ac.tuwien.igw.objects.Crosspoint;
import at.ac.tuwien.igw.objects.swing.DrawMeasuredData;
import at.ac.tuwien.igw.objects.swing.HistogrammValue;
import at.ac.tuwien.igw.serial.DataManager;
import at.ac.tuwien.igw.serial.SerialDevice;

public class MultiTouchProcessing {
	public static Crosspoint[][] crosspoints;
	public static DrawMeasuredData[][] rects;
	public static HistogrammValue[] histogrammValues;
	public static boolean triggerMode = false;
	public static List<List<Blob>> activeBlobs;
	
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
	public int blobCount;
	public long lastBlobId;
	
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
		activeBlobs = new ArrayList<List<Blob>>();
		
		fps = 0;
		lastMillis = -1;
		frames = 0;
		blobCount = 0;
		lastBlobId = 1;
		
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
		if(activeBlobs.size() == 1) {
			drawBlobPath(activeBlobs.get(0));
		}
			
		serialDevice.closePort();
	}

	/**********************
	 * TRESHHOLD FUNCTIONS
	 **********************/
	
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
	
	/**********************
	 * BLOB FUNCTIONS
	 **********************/

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
		long millis = System.currentTimeMillis();								//Timestamp for BlobCreation
		BlobFinder finder = new BlobFinder(Configuration.verticalWires, Configuration.horizontalWires, millis);
		double[] dstData = new double[binaryOneDim.length];
		ArrayList<Blob> blobList = new ArrayList<Blob>();

		// Detect Blobs
		lastBlobId = finder.detectBlobs(binaryOneDim, dstData, 0, -1, blobList, lastBlobId);
		blobCount = blobList.size();
		
		// Calculate Movement of Blobs
		checkMovements(blobList);
		
		// Draw Blobs
		drawBlobs(blobList);
		
		
		// List Blobs
		/*
		System.out.printf("Found %d blobs:\n", blobList.size());
		System.out.printf("=================\n");
		int i=1;
		StringBuilder sb = new StringBuilder();
		for(Blob blob: blobList) {
			sb.append("blob nr."+i+" with "+blob.mass+" points.\n");
			i++;
		}
		System.out.println(sb.toString());
		*/
	}
	
	private void checkMovements(ArrayList<Blob> blobList) {
		//Print out all blobs
		System.out.println("\n \n=== NEW BLOB ROUND ===");
		for(Blob b : blobList)
			System.out.println(b);
		System.out.println("=== Calculation starts now ===");
		
		//nothing in activeBlobList -> add all of them
		if(activeBlobs.size() == 0) {
			System.out.println("active blob size was " + activeBlobs.size() +" -> (1)");
			//Add each new blob in a new list tot the active blobs (for history)
			for(Blob b : blobList) {
				ArrayList<Blob> sbl = new ArrayList<Blob>();		//sbl: single blob list
				sbl.add(b);
				activeBlobs.add(sbl);
			}
		} else {
			System.out.println("active blob size was " + activeBlobs.size() +" -> (2)");
			for(Blob nb : blobList) {
				boolean found = false;
				for(List<Blob> sbl : activeBlobs) {
					Blob ob = sbl.get(sbl.size()-1);
					//System.out.println("Checking following blobs: \n > " + ob + " \n >" + nb + "\n");
					if( isInRange(nb.getxMiddle(), ob.getxMiddle(), Configuration.blobRangeRadius) &&
						isInRange(nb.getyMiddle(), ob.getyMiddle(), Configuration.blobRangeRadius)){
						System.out.println("found matching blob: \n >>" + ob + "\n >>" + nb + " \n -> (3)");
						//Blob seems to be the same -> add to list
						nb.setId(ob.getId());
						sbl.add(nb);
						found = true;
					}
				}
				if(!found) {
					//seems to be a new blob -> add a new list to active blob list
					System.out.println("no matching blob -> (4)");
					ArrayList<Blob> sbl = new ArrayList<Blob>();		//sbl: single blob list
					sbl.add(nb);
					activeBlobs.add(sbl);
				}
			}
			
			List<List<Blob>> toRemove = new ArrayList<List<Blob>>();
			
			//Check if there are some old blobs -> if yes delete them
			for(List<Blob> sbl : activeBlobs) {
				Blob ob = sbl.get(sbl.size()-1);
				if(ob.getCreatedAt() < System.currentTimeMillis()-500){
					System.out.println("found blob to delete -> (5)");
					toRemove.add(sbl);
				}
			}
			activeBlobs.removeAll(toRemove);
			
			System.out.println("ACTIVE BLOBS: " + activeBlobs.size());
			for(List<Blob> sbl : activeBlobs) {
				Blob b = sbl.get(sbl.size()-1);
				System.out.println("blob with id: " + b.getId() + " has " + sbl.size() + " pathitems stored");
			}
			
		}
	}
	
	private boolean isInRange(double a, double b, double maxrange) {
		double dif = (a-b);
		if(dif < 0.0)
			dif *= -1;
		
		return dif <= maxrange;
	}
	
	public void drawBlobs(List<Blob> blobList) {
		blobPanel.removeAll();

		for(Blob b : blobList) {
			blobPanel.add(b);
		}
		blobPanel.validate();
		blobPanel.repaint();
	}
	
	public void drawBlobPath(List<Blob> blobList) {
		blobPanel.removeAll();

		Iterator<Blob> iter = blobList.iterator();
		while(iter.hasNext()) {
			Blob b = iter.next();
			b.setBackground(new Color(0f, 1f, 1f, 0.1f));
			if(!iter.hasNext())
				b.setBackground(new Color(1f, 1f, 1f, 0.3f));
			blobPanel.add(b);
		}
		blobPanel.validate();
		blobPanel.repaint();
	}
	
	/**********************
	 * TRIGGERMODE FUNCTIONS
	 **********************/
	
	public void checkTriggerMode() {
		//TODO check histogramm values
		if(blobCount <= 5)
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
	
	/**********************
	 * HISTOGRAMM FUNCTIONS
	 **********************/
	
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
		/*
		for(int i=0;i<values.length;i++){
			System.out.println("array " + i + " " + values[i]/max);
		}*/
		for(int i=0; i<values.length;i++) {
			histogrammValues[i].setVal(values[i]/max);
		}
	}
	
	/**********************
	 * PRINT FUNCTIONS
	 **********************/
	
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
}
