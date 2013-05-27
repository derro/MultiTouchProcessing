public class DataManager {
	
	int sb2ub(byte p) {		//signed to unsigned
		return p < 0 ? 256 + p : p;
	}

	void consumeSerialBuffer(byte[] buffer, boolean calibrate, boolean draw) {
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
					Main.crosspoints[x][y].accumulateAvgSig(sig);
				} else {
					Main.crosspoints[x][y].calculateSignalStrength(sig);
					if(draw)
						Main.rects[x][y].setValue(Main.crosspoints[x][y].getSignalStrength());
				}
				cnt++;
			}
		}
		if(draw)
			Main.frame.repaint();
	}

	void printData(int nr) {
		StringBuilder sb = new StringBuilder();
		sb.append(">>>> Refreshing number " + nr + "\n");
		sb.append("measured signals:\n");
		for (int i = 0; i < Configuration.verticalWires; i++) {
			for (int j = 0; j < Configuration.horizontalWires; j++) {
				sb.append("["+i+"/"+j+"]" + Main.crosspoints[i][j].getMeasuredSignal()
						+ ",");
			}
		}
		sb.append("\n measured signal average:\n");
		for (int i = 0; i < Configuration.verticalWires; i++) {
			for (int j = 0; j < Configuration.horizontalWires; j++) {
				sb.append("["+i+"/"+j+"]" + Main.crosspoints[i][j].getMeasuredSignalAverage()
						+ ",");
			}
		}
		sb.append("\n signal strength:\n");
		for (int i = 0; i < Configuration.verticalWires; i++) {
			for (int j = 0; j < Configuration.horizontalWires; j++) {
				sb.append("["+i+"/"+j+"]" + Main.crosspoints[i][j].getSignalStrength()
						+ ",");
			}
		}
		System.out.println(sb.toString());
	}
}
