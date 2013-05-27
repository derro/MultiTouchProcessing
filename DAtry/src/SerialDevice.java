import jssc.SerialPort;
import jssc.SerialPortException;


public class SerialDevice {
	private SerialPort serialPort = null;
	private String deviceName = null;
	
	public SerialDevice(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public SerialPort getSerialPort() {
		return serialPort;
	}

	public byte[] readData(int bytes) {
		try {
			byte[] buffer = this.serialPort.readBytes(bytes);
			return buffer;
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void closePort() {
		System.out.println("SerialDevice::closePort");
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public void writeData(String data) {
		System.out.println("SerialDevice::writeData::"+data);
		try {
			serialPort.writeBytes(data.getBytes());
			System.out.println("SerialDevice::writeData::"+data+"::[success]");
		} catch (SerialPortException ex) {
			System.out.println(ex);
		}
	}
	
	public boolean openPort() {
		return openPort(deviceName);		
	}

	public boolean openPort(String deviceName) {
		System.out.println("SerialDevice::openPort::"+deviceName);
		/*
		 * String[] portNames = SerialPortList.getPortNames(); for(int p = 0; p
		 * < portNames.length; p++){ if
		 * (portNames[p].equals("/dev/tty.usbserial-A4001KsV")) {
		 * 
		 * } }
		 */

		serialPort = new SerialPort(deviceName);
		try {
			serialPort.openPort();// Open serial port
			serialPort.setParams(500000,		
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);// Set params. Also you can set
											// params by this string:
											// serialPort.setParams(9600, 8, 1,
											// 0);
											//SerialPort.BAUDRATE_115200
			// int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS +
			// SerialPort.MASK_DSR;//Prepare mask
			// serialPort.setEventsMask(mask);//Set mask
			// serialPort.addEventListener(new SerialPortReader()); //Add
			// SerialPortEventListener

			System.out.println("SerialDevice::openPort::"+deviceName+"::[success]");
			return true;
		} catch (SerialPortException ex) {
			System.out.println(ex);
			return false;
		}
	}
}

/*
class SerialPortReader implements SerialPortEventListener {

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.isRXCHAR()) {// If data is available
			if (event.getEventValue() == 10) {// Check bytes count in the
												// input buffer
				// Read data, if 10 bytes available
				try {
					byte buffer[] = serialPort.readBytes(10);
					System.out.println(buffer);
				} catch (SerialPortException ex) {
					System.out.println(ex);
				}
			}
		} else if (event.isCTS()) {// If CTS line has changed state
			if (event.getEventValue() == 1) {// If line is ON
				System.out.println("CTS - ON");
			} else {
				System.out.println("CTS - OFF");
			}
		} else if (event.isDSR()) {// /If DSR line has changed state
			if (event.getEventValue() == 1) {// If line is ON
				System.out.println("DSR - ON");
			} else {
				System.out.println("DSR - OFF");
			}
		}
	}
}
*/