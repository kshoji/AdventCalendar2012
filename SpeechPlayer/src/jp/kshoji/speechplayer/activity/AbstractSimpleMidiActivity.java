package jp.kshoji.speechplayer.activity;

import jp.kshoji.driver.midi.activity.AbstractMidiActivity;

public abstract class AbstractSimpleMidiActivity extends AbstractMidiActivity {
	
	@Override
	public void onMidiMiscellaneousFunctionCodes(int cable, int byte1, int byte2, int byte3) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiCableEvents(int cable, int byte1, int byte2, int byte3) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiSystemCommonMessage(int cable, byte[] bytes) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiSystemExclusive(int cable, byte[] systemExclusive) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiNoteOff(int cable, int channel, int note, int velocity) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiPolyphonicAftertouch(int cable, int channel, int note, int pressure) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiControlChange(int cable, int channel, int function, int value) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiProgramChange(int cable, int channel, int program) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiChannelAftertouch(int cable, int channel, int pressure) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiPitchWheel(int cable, int channel, int amount) {
		// TODO or not TODO, that is the question...
		
	}
	
	@Override
	public void onMidiSingleByte(int cable, int byte1) {
		// TODO or not TODO, that is the question...
		
	}
	
}
