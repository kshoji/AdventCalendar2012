package jp.kshoji.driver.hid.listener;

/**
 * Listener for USB data
 * 
 * @author K.Shoji
 */
public interface OnDeviceEventListener {
	
	/**
	 * 
	 * @param bytes
	 */
	void onDataTransfer(byte[] bytes);
}
