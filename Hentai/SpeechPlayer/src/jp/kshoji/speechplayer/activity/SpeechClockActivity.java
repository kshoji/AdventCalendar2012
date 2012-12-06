package jp.kshoji.speechplayer.activity;

import java.util.ArrayList;
import java.util.List;

import jp.kshoji.speechplayer.R;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class SpeechClockActivity extends AbstractSpeechActivity {
	String speechText;
	int octave = 2;
	private List<Button> buttons = new ArrayList<Button>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_to_speech);

		speechText = getString(R.string.speect_text);

		buttons.add((Button) findViewById(R.id.buttonC));
		buttons.add((Button) findViewById(R.id.buttonCis));
		buttons.add((Button) findViewById(R.id.buttonD));
		buttons.add((Button) findViewById(R.id.buttonDis));
		buttons.add((Button) findViewById(R.id.buttonE));
		buttons.add((Button) findViewById(R.id.buttonF));
		buttons.add((Button) findViewById(R.id.buttonFis));
		buttons.add((Button) findViewById(R.id.buttonG));
		buttons.add((Button) findViewById(R.id.buttonGis));
		buttons.add((Button) findViewById(R.id.buttonA));
		buttons.add((Button) findViewById(R.id.buttonAis));
		buttons.add((Button) findViewById(R.id.buttonB));
		buttons.add((Button) findViewById(R.id.buttonC2));
		
		Spinner octaveSpinner = (Spinner) findViewById(R.id.spinner1);
		octaveSpinner.setSelection(octave);
		octaveSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				octave = arg2;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}
		});
		
		final EditText speechEditText = (EditText) findViewById(R.id.editText1);
		speechEditText.setText(speechText);
		LinearLayout parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
		parentLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				speechText = speechEditText.getText().toString();
				
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(SpeechClockActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				return false;
			}
		});

		for (int i = 0; i < buttons.size(); i++) {
			Button button = buttons.get(i);
			final double index = i;
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					speak(speechText, TextToSpeech.QUEUE_FLUSH, 0.25f * (float) Math.pow(2., (index + octave * 12.) / 12.), 1f);
				}
			});
		}
	}

	@Override
	void onInitSuccess() {
		for (Button button : buttons) {
			button.setEnabled(true);
		}
	}

	@Override
	void onInitFailure(int reason) {
		Toast.makeText(this, "TextToSpeech init failed. reason:" + reason, Toast.LENGTH_SHORT).show();
		
		for (Button button : buttons) {
			button.setEnabled(false);
		}
	}
	
	@Override
	public void onMidiNoteOn(int cable, int channel, int note, int velocity) {
		speak(speechText, TextToSpeech.QUEUE_FLUSH, 0.125f * (float) Math.pow(2., note / 12.), 1f);
	}
	
	@Override
	public void onMidiNoteOff(int cable, int channel, int note, int velocity) {
		// do nothing
		
	}
}