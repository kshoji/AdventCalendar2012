package jp.kshoji.speechplayer.activity;

import java.util.Locale;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public abstract class AbstractSpeechActivity extends AbstractSimpleMidiActivity {

	private static final String	TAG	= "Speech";
	TextToSpeech				textToSpeech;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		textToSpeech = new TextToSpeech(getApplicationContext(), new OnInitListener() {
			@Override
			public void onInit(int status) {
				int result = status;
				if (status == TextToSpeech.SUCCESS) {
					result = textToSpeech.setLanguage(Locale.JAPAN);
					if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e(TAG, "Language is not available.");
					} else {
						onInitSuccess();
						return;
					}
				} else {
					Log.e(TAG, "Could not initialize TextToSpeech.");
				}
				onInitFailure(result);
			}
		});
	}

	abstract void onInitSuccess();

	abstract void onInitFailure(int reason);

	@Override
	public void onDestroy() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}

		super.onDestroy();
	}

	/**
	 * 
	 * @param text
	 * @param queueMode
	 *            TextToSpeech.QUEUE_FLUSH, TextToSpeech.QUEUE_ADD
	 * @param pitch
	 * @param speechRate
	 */
	protected final void speak(String text, int queueMode, float pitch, float speechRate) {
		textToSpeech.setPitch(pitch);
		textToSpeech.setSpeechRate(speechRate);
		textToSpeech.speak(text, queueMode, null);
	}
}
