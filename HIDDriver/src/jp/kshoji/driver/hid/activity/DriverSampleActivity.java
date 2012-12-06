package jp.kshoji.driver.hid.activity;

import jp.kshoji.driver.hid.R;
import jp.kshoji.driver.hid.device.OutputDevice;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Sample Activity for USB driver library
 * 
 * @author K.Shoji
 */
public class DriverSampleActivity extends AbstractDeviceActivity {
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	SeekBar seekBar1;

	/*
	 * (non-Javadoc)
	 * @see jp.kshoji.driver.hid.activity.AbstractDeviceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(getIntent().getAction())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("USB device required.");
			builder.setMessage("Please start with connecting USB device. This screen will be closed.");
			builder.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.create().show();
			return;
		}

		setContentView(R.layout.main);

		android.view.View.OnClickListener onButtonClickListener = new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OutputDevice outputDevice = getOutputDevice();
				if (outputDevice == null) {
					return;
				}

				if (v.getTag().equals("1")) {
					v.setTag("0");
					v.getBackground().setColorFilter(0xff333333, Mode.SRC);
				} else {
					v.setTag("1");
					v.getBackground().setColorFilter(0xff008080, Mode.SRC);
				}
				
				// send data
				byte data1 = button1.getTag().equals("1") ? (byte) 1 : 0;
				byte data2 = button2.getTag().equals("1") ? (byte) 2 : 0;
				byte data3 = button3.getTag().equals("1") ? (byte) 4 : 0;
				byte data4 = button4.getTag().equals("1") ? (byte) 8 : 0;
				outputDevice.onDataTransfer(new byte[]{(byte) (data1 | data2 | data3 | data4)});
			}
		};

		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		
		button1.setOnClickListener(onButtonClickListener);
		button2.setOnClickListener(onButtonClickListener);
		button3.setOnClickListener(onButtonClickListener);
		button4.setOnClickListener(onButtonClickListener);
		
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
	}

	/*
	 * (non-Javadoc)
	 * @see jp.kshoji.driver.hid.activity.AbstractDeviceActivity#onDeviceAttached()
	 */
	@Override
	protected void onDeviceAttached() {
		Toast.makeText(this, "USB Device has been attached.", Toast.LENGTH_LONG).show();
	}

	/*
	 * (non-Javadoc)
	 * @see jp.kshoji.driver.hid.activity.AbstractDeviceActivity#onDeviceDetached()
	 */
	@Override
	protected void onDeviceDetached() {
		Toast.makeText(this, "USB Device has been detached.", Toast.LENGTH_LONG).show();
		finish();
	}

	/*
	 * (non-Javadoc)
	 * @see jp.kshoji.driver.hid.listener.OnDeviceEventListener#onDataTransfer(byte[])
	 */
	@Override
	public void onDataTransfer(byte[] bytes) {
		int value = ((0xff & bytes[0]) << 8) | (0xff & bytes[1]);
		seekBar1.setProgress(value);
	}
}
