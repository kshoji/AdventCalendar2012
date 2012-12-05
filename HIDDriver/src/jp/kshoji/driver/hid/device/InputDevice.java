package jp.kshoji.driver.hid.device;

import jp.kshoji.driver.hid.listener.OnDeviceEventListener;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

/**
 * Input Device
 * 
 * @author K.Shoji
 */
public class InputDevice {
	
	/**
	 * USB Message parser
	 * 
	 * @author K.Shoji
	 */
	class MessageCallback implements Callback {
		
		private final OnDeviceEventListener onDeviceEventListener;
		
		/**
		 * @param onDeviceEventListener
		 */
		public MessageCallback(OnDeviceEventListener onDeviceEventListener) {
			this.onDeviceEventListener = onDeviceEventListener;
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.os.Handler.Callback#handleMessage(android.os.Message)
		 */
		@Override
		public boolean handleMessage(Message msg) {
			if (onDeviceEventListener == null) {
				return false;
			}
			onDeviceEventListener.onDataTransfer((byte[]) msg.obj);
			return false;
		}
	}

	final UsbDeviceConnection deviceConnection;
	UsbEndpoint inputEndpoint;
	private final WaiterThread waiterThread;

	/**
	 * @param connection
	 * @param intf
	 * @param onDeviceEventListener
	 * @throws IllegalArgumentException
	 */
	public InputDevice(UsbDeviceConnection connection, UsbInterface intf, OnDeviceEventListener onDeviceEventListener) throws IllegalArgumentException {
		deviceConnection = connection;

		waiterThread = new WaiterThread(new Handler(new MessageCallback(onDeviceEventListener)));

		// look for our bulk endpoints
		for (int i = 0; i < intf.getEndpointCount(); i++) {
			UsbEndpoint endpoint = intf.getEndpoint(i);
			if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK || endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_INT) {
				if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
					inputEndpoint = endpoint;
					break;
				}
			}
		}
		
		if (inputEndpoint == null) {
			throw new IllegalArgumentException("Input endpoint was not found.");
		}

		if (deviceConnection != null) {
			deviceConnection.claimInterface(intf, true);
		}
	}

	public void start() {
		waiterThread.start();
	}

	public void stop() {
		synchronized (waiterThread) {
			waiterThread.stopFlag = true;
		}
	}

	/**
	 * Polling thread for input data.
	 * Loops infinitely while stopFlag == false.
	 * 
	 * @author K.Shoji
	 */
	private class WaiterThread extends Thread {
		private byte[] readBuffer = new byte[64];

		public boolean stopFlag;
		
		private Handler receiveHandler;

		/**
		 * Constructor
		 * 
		 * @param handler
		 */
		public WaiterThread(Handler handler) {
			stopFlag = false;
			this.receiveHandler = handler;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			while (true) {
				synchronized (this) {
					if (stopFlag) {
						return;
					}
				}
				if (inputEndpoint == null) {
					continue;
				}
				
				int length = deviceConnection.bulkTransfer(inputEndpoint, readBuffer, readBuffer.length, 0);
				if (length > 0) {
					byte[] read = new byte[length];
					System.arraycopy(readBuffer, 0, read, 0, length);
					
					Message message = new Message();
					message.obj = read;
					receiveHandler.sendMessage(message);
				}
			}
		}
	}
}
