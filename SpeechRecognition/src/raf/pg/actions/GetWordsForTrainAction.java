package raf.pg.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import raf.pg.app.AppCore;
import raf.pg.audio.inputoutput.SoundCapture;
import raf.pg.preProcessings.EndPointDetection;
import raf.pg.preProcessings.Interval;
import raf.pg.preProcessings.Word;
import raf.pg.util.SignalCalculations;

@SuppressWarnings("serial")
public class GetWordsForTrainAction extends AbstractSRAction {
	
	public GetWordsForTrainAction() {
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		AudioInputStream audioInputStream = AppCore.getInstance().getSoundCapture().getAudioInputStream();
		AudioFormat format = audioInputStream.getFormat();
		
		double[] samples = SignalCalculations.audioInputStreamToSamples(audioInputStream);
		ArrayList<Interval> intervals = new ArrayList<>();
		ArrayList<AudioInputStream> streams = new ArrayList<AudioInputStream>();
		ArrayList<Word> words = new ArrayList<Word>();
		
		intervals = new EndPointDetection(20, (int)format.getSampleRate()).getEndPoints(samples);
		
		for (int i = 0; i < intervals.size(); i++) {
			AudioInputStream slice = SignalCalculations.getAudioInputStreamSlice(
					AppCore.getInstance().getSoundCapture().getAudioBytes(),
					intervals.get(i), SoundCapture.getFormat().getFormat());
			streams.add(slice);
			words.add(new Word(slice));
		}
		AppCore.getInstance().showDetectedWordsTrain(AppCore.getInstance().getTraDinamicPanel(), words, streams);
		
	}

}
