package raf.pg.preProcessings;

import java.io.Serializable;

import javax.sound.sampled.AudioInputStream;

import raf.pg.audio.PreProcess;
import raf.pg.audio.feature.FeatureVector;
import raf.pg.util.SignalCalculations;

@SuppressWarnings("serial")
public class Word implements Serializable{
	private double[] samples;
	//private AudioInputStream audioInputStream;
	private double[][] framedSignal;
	private FeatureVector featureVector;
	
	public Word(AudioInputStream audioInputStream) {
		//this.audioInputStream = audioInputStream;
		this.samples = SignalCalculations.audioInputStreamToSamples(audioInputStream);
		this.framedSignal = PreProcess.doFraming(this.samples);
		this.framedSignal = PreProcess.doWindowing(this.framedSignal);
		FeatureExtract featureExtract = new FeatureExtract(framedSignal, (int)audioInputStream.getFormat().getSampleRate(), PreProcess.getSamplePerFrame());
		featureExtract.makeMfccFeatureVector();
		featureVector = featureExtract.getFeatureVector();
		/*for(int i = 0; i < featureVector.getFeatureVector().length; i++){
			for(int j = 0 ; j < featureVector.getFeatureVector()[0].length; j++){
				System.out.println(featureVector.getFeatureVector()[i][j]+" ");
			}
		}*/
		System.out.println(featureVector.getFeatureVector()[0].length+ " - " +featureVector.getFeatureVector().length );
	}
	
	public double[] getSamples() {
		return samples;
	}
	
	public FeatureVector getFeatureVector() {
		return featureVector;
	}
	
	/*TODO FEATURE VECTOR*/
	public String toString() {
		StringBuilder sbResult = new StringBuilder();
		sbResult.append(featureVector.getFeatureVector().length);
		sbResult.append(" ");
		sbResult.append(featureVector.getFeatureVector()[0].length);
		sbResult.append("\n");
		sbResult.append(featureVector.toString());
		return sbResult.toString();
	}
	
	
}
