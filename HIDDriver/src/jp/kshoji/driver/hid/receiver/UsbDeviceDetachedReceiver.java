package jp.kshoji.driver.hid.receiver;

import jp.kshoji.driver.hid.listener.OnDeviceDetachedListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

/**
 * Broadcast receiver for USB device
 * 
 * @author K.Shoji
 */
public class UsbDeviceDetachedReceiver extends BroadcastReceiver {
	private OnDeviceDetachedListener onDeviceDetachedListener;

	/**
	 * @param onDeviceDetachedListener
	 */
	public UsbDeviceDetachedReceiver(OnDeviceDetachedListener onDeviceDetachedListener) {
		this.onDeviceDetachedListener = onDeviceDetachedListener;
	}

	/*
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
			UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			if (device != null) {
				if (onDeviceDetachedListener != null) {
					onDeviceDetachedListener.onDeviceDetached(device);
				}
			}
		}
	}
}
