package at.ac.tuwien.igw.serial;
import at.ac.tuwien.igw.config.Configuration;
import at.ac.tuwien.igw.main.MultiTouchProcessing;

public class DataManager {
	
	public void consumeSerialBuffer(byte[] buffer, boolean calibrate, boolean draw) {
		byte[] buf = buffer;
		int bs = 0, br = 0, cnt = 0;

		for (int i = 0; i < buf.length; i++) {
			br |= sb2ub(buf[i]) << bs;
			bs += 8;
			while (bs >= 10) {
				int sig = br & 0x3ff;
				br >>= 10;
				bs -= 10;
				int x = cnt / Configuration.horizontalWires, y = cnt % Configuration.horizontalWires;

				if (calibrate) {
					MultiTouchProcessing.crosspoints[x][y].accumulateAvgSig(sig);
				} else {
					MultiTouchProcessing.crosspoints[x][y].calculateSignalStrength(sig);

					//Remove border measure values for getting better graphics
					if(x <= Configuration.removeBorderVal-1 || y <= Configuration.removeBorderVal-1 || x >= Configuration.verticalWires-Configuration.removeBorderVal || y >= Configuration.horizontalWires-Configuration.removeBorderVal)
						MultiTouchProcessing.crosspoints[x][y].setSignalStrength(0.0);
					if(draw)
						MultiTouchProcessing.rects[x][y].setValue(MultiTouchProcessing.crosspoints[x][y].getSignalStrength());
				}
				cnt++;
			}
		}
	}

	public void printData(int nr) {
		StringBuilder sb = new StringBuilder();
		sb.append(">>>> Refreshing number " + nr + "\n");
		sb.append("measured signals:\n");
		for (int i = 0; i < Configuration.verticalWires; i++) {
			for (int j = 0; j < Configuration.horizontalWires; j++) {
				sb.append("["+i+"/"+j+"]" + MultiTouchProcessing.crosspoints[i][j].getMeasuredSignal()
						+ ",");
			}
		}
		sb.append("\n measured signal average:\n");
		for (int i = 0; i < Configuration.verticalWires; i++) {
			for (int j = 0; j < Configuration.horizontalWires; j++) {
				sb.append("["+i+"/"+j+"]" + MultiTouchProcessing.crosspoints[i][j].getMeasuredSignalAverage()
						+ ",");
			}
		}
		sb.append("\n signal strength:\n");
		for (int i = 0; i < Configuration.verticalWires; i++) {
			for (int j = 0; j < Configuration.horizontalWires; j++) {
				sb.append("["+i+"/"+j+"]" + MultiTouchProcessing.crosspoints[i][j].getSignalStrength()
						+ ",");
			}
		}
		System.out.println(sb.toString());
	}
	
	private int sb2ub(byte p) {		//signed to unsigned
		return p < 0 ? 256 + p : p;
	}
}
