package raf.pg.audio.feature;

import java.io.Serializable;
import java.util.ArrayList;

import raf.pg.speech.vq.Points;

public class FeatureVector implements Serializable {
	private static final long serialVersionUID = 4827024411867642493L;
	private double[][] mfccVector;
	private double[][] featureVector; // mfcc + delta + deltadelta

	public double[][] getMfccVector() {
		return mfccVector;
	}
	
	public void setMfccVector(double[][] mfccVector) {
		this.mfccVector = mfccVector;
	}
	
	public int getNoOfFrames() {
		return featureVector.length;
	}

	public int getNoOfFeatures() {
		return featureVector[0].length;
	}
	public double[][] getFeatureVector() {
		return featureVector;
	}
	public void setFeatureVector(double[][] featureVector) {
		this.featureVector = featureVector;
	}
	
	public ArrayList<Points> toPointsList() {
		ArrayList<Points> list = new ArrayList<Points>();
		for (int i = 0; i < featureVector.length; i++)
			list.add(new Points(featureVector[i]));
		return list;
	}
	
}
