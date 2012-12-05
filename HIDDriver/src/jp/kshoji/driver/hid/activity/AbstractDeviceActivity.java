package jp.kshoji.driver.hid.activity;

import jp.kshoji.driver.hid.device.InputDevice;
import jp.kshoji.driver.hid.device.OutputDevice;
import jp.kshoji.driver.hid.listener.OnDeviceDetachedListener;
import jp.kshoji.driver.hid.listener.OnDeviceEventListener;
import jp.kshoji.driver.hid.receiver.UsbDeviceDetachedReceiver;
import jp.kshoji.driver.hid.util.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

/**
 * base Activity for using USB interface.
 * launchMode must be "singleTask" or "singleInstance".
 * 
 * @author K.Shoji
 */
public abstract class AbstractDeviceActivity extends Activity implements OnDeviceDetachedListener, OnDeviceEventListener {
	private UsbDeviceDetachedReceiver deviceDetachedReceiver;
	private UsbDeviceConnection deviceConnection = null;
	private InputDevice inputDevice = null;
	private OutputDevice outputDevice = null;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String action = getIntent().getAction();
		if (!UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
			Log.d(Constants.TAG, "Intent action is not 'android.hardware.usb.action.USB_DEVICE_ATTACHED'. Usb device can only attached with category 'android.intent.category.LAUNCHER'.");
		}
		
		deviceDetachedReceiver = new UsbDeviceDetachedReceiver(this);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(deviceDetachedReceiver, filter);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		Intent intent = getIntent();
		String action = intent.getAction();
		
		if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
			UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			if (device != null) {
				UsbInterface usbInterface = findUsbInterface(device);
				if (usbInterface != null) {
					onDeviceAttached(device, usbInterface);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (inputDevice != null) {
			inputDevice.stop();
		}
		
		inputDevice = null;
		outputDevice = null;
		
		if (deviceDetachedReceiver != null) {
			unregisterReceiver(deviceDetachedReceiver);
		}
	}

	/**
	 * USB device has been attached.
	 * 
	 * @param attachedDevice
	 * @param usbInterface
	 */
	private synchronized final void onDeviceAttached(UsbDevice attachedDevice, UsbInterface usbInterface) {
		UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		
		deviceConnection = usbManager.openDevice(attachedDevice);

		try {
			if (inputDevice == null) {
				inputDevice = new InputDevice(deviceConnection, usbInterface, this);
				inputDevice.start();
			}
		} catch (IllegalArgumentException iae) {
			Log.i(Constants.TAG, "This device didn't have any input endpoints.", iae);
		}
		
		try {
			if (outputDevice == null) {
				outputDevice = new OutputDevice(deviceConnection, usbInterface);
			}
		} catch (IllegalArgumentException iae) {
			Log.i(Constants.TAG, "This device didn't have any output endpoints.", iae);
		}
		
		onDeviceAttached();
	}
	
	/**
	 * Will be called when device has been attached.
	 */
	protected void onDeviceAttached() {
		// do nothing. must be override
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.kshoji.driver.hid.listener.OnDeviceDetachedListener#onDeviceDetached(android.hardware.usb.UsbDevice)
	 */
	@Override
	public final void onDeviceDetached(UsbDevice detachedDevice) {
		if (deviceConnection != null) {
			UsbInterface usbInterface = findUsbInterface(detachedDevice);
			if (usbInterface != null) {
				deviceConnection.releaseInterface(usbInterface);
			}
			deviceConnection.close();
		}
		
		Log.d(Constants.TAG, "Device has been detached. The activity must be finished.");
		onDeviceDetached();
	}
	
	/**
	 * Will be called when device has been detached.
	 * The activity must be finished in this method.
	 */
	protected void onDeviceDetached() {
		// do nothing. must be override
	}

	/**
	 * get USB output device, if available.
	 * 
	 * @return
	 */
	public final OutputDevice getOutputDevice() {
		return outputDevice;
	}
	
	/**
	 * @param device
	 * @return
	 */
	private final UsbInterface findUsbInterface(UsbDevice device) {
		int count = device.getInterfaceCount();
		for (int i = 0; i < count; i++) {
			UsbInterface usbInterface = device.getInterface(i);
			
			UsbEndpoint inputEndpoint = null;
			UsbEndpoint outputEndpoint = null;
			
			if (usbInterface.getEndpointCount() >= 1) {
				// has more than 1 endpoint
				
				for (int endpointIndex = 0; endpointIndex < usbInterface.getEndpointCount(); endpointIndex++) {
					UsbEndpoint endpoint = usbInterface.getEndpoint(endpointIndex);
					if ((endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK || endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_INT)) {
						if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
							inputEndpoint = inputEndpoint == null ? endpoint : inputEndpoint;
						} else {
							outputEndpoint = outputEndpoint == null ? endpoint : outputEndpoint;
						}
					}
				}
				
				if (inputEndpoint != null || outputEndpoint != null) {
					return usbInterface;
				}
			}
		}
		return null;
	}
}
