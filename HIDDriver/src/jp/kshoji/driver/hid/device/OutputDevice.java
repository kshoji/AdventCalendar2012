package jp.kshoji.driver.hid.device;

import jp.kshoji.driver.hid.listener.OnDeviceEventListener;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

/**
 * Output Device
 * 
 * @author K.Shoji
 */
public class OutputDevice implements OnDeviceEventListener {

	private final UsbDeviceConnection deviceConnection;
	private UsbEndpoint outputEndpoint;

	/**
	 * @param connection
	 * @param intf
	 */
	public OutputDevice(UsbDeviceConnection connection, UsbInterface intf) {
		deviceConnection = connection;

		for (int i = 0; i < intf.getEndpointCount(); i++) {
			UsbEndpoint endpoint = intf.getEndpoint(i);
			if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK || endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_INT) {
				if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
					outputEndpoint = endpoint;
				}
			}
		}

		if (outputEndpoint == null) {
			throw new IllegalArgumentException("Output endpoint was not found.");
		}

		deviceConnection.claimInterface(intf, true);
	}

	@Override
	public void onDataTransfer(byte[] bytes) {
		final int maxPacketSize = outputEndpoint.getMaxPacketSize();

		for (int i = 0; i < bytes.length; i += maxPacketSize) {
			byte[] writeBuffer = new byte[maxPacketSize];
			int length = (bytes.length - i) < writeBuffer.length ? bytes.length - i : writeBuffer.length;
			System.arraycopy(bytes, i, writeBuffer, 0, length);
			deviceConnection.bulkTransfer(outputEndpoint, writeBuffer, writeBuffer.length, 0);
		}
	}
}
