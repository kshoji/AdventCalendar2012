package jp.kshoji.driver.hid.listener;

import android.hardware.usb.UsbDevice;

/**
 * Listener for USB device detached events
 * 
 * @author K.Shoji
 */
public interface OnDeviceDetachedListener {
	/**
	 * device has been detached
	 * 
	 * @param device
	 */
	void onDeviceDetached(UsbDevice device);
}
