package raf.pg.preProcessings;

import java.util.ArrayList;

import raf.pg.util.SignalCalculations;

public class EndPointDetection {
	
	
	private static final double SCALE = 50.0;
	
	private static int noiseWindowCount = 10;
	private static int windowCount = 10;
	private static int ZCRExpandLength = 250;
	private static int spikeLength = 300;
	private static int mergeLength = 180;
	
	
	private int windowLength;
	private int sampleRate;
	private int windowSamples;
	private double energyThreshold;
	private double ZCRThreshold;
	
	public EndPointDetection(int windowLength, int sampleRate) {
		this.sampleRate = sampleRate;
		this.windowLength = windowLength;
		this.windowSamples = windowLength * sampleRate / 1000;
	}
	
	public ArrayList<Interval> getEndPoints(double[] samples){
		
		int totalWindows = samples.length / windowSamples;
		calculateThreshold(samples, noiseWindowCount, windowLength, windowSamples, sampleRate);
		
		boolean[] voiced = new boolean[totalWindows];
		
		ArrayList<Double> energies = new ArrayList<Double>();
		
		for (int i = 0; i < windowCount - 1; i++) {
			double[] window = SignalCalculations.getSamples(samples, i * windowSamples, windowSamples);
			energies.add(SignalCalculations.calculateEnergy(window));
		}
		
		for (int i = windowCount; i < totalWindows; i++) {
			double[] window = SignalCalculations.getSamples(samples, i * windowSamples, windowSamples);
			double energy = SignalCalculations.calculateEnergy(window);
			energies.add(energy);
			energies.remove(0);
			
			double W = SignalCalculations.calculatePower(energies.toArray(new Double[energies.size()]));

			if (W > energyThreshold) voiced[i] = true;
			else voiced[i] = false;
		}
		
		int mergeFrames = mergeLength / windowLength;
		smoothUp(voiced, mergeFrames);
		
		int spikeFrames = spikeLength / windowLength;
		smoothDown(voiced, spikeFrames);
		
		int ZCRExpandFrames = ZCRExpandLength / windowLength;
		extraSmoothUsingZCR(voiced, ZCRExpandFrames, samples);
		
		return takeIntervals(voiced);
	}
	
	
	private void calculateThreshold(double[] samples, int noiseWindowCount, int windowLength, int windowSamples, int sampleRate) {
		double[] noiseSamples = SignalCalculations.getSamples(samples, 0, noiseWindowCount * windowLength * sampleRate / 1000);
		double[] energyBuffer = new double[noiseWindowCount];
		double[] zcrBuffer = new double[noiseWindowCount];
		
		for (int i = 0; i < noiseWindowCount; i++) {
			double[] noise = SignalCalculations.getSamples(noiseSamples, i * windowSamples, windowSamples);
			energyBuffer[i] = SignalCalculations.calculatePower(noise);
			zcrBuffer[i] = SignalCalculations.calculateZCR(noise);
		}
		
		double energyMean = SignalCalculations.calculateMean(energyBuffer);
		double energyStandardDeviation = SignalCalculations.calculateStandardDeviation(energyBuffer);
		this.energyThreshold = 1.5 * energyMean + 3.0 * energyStandardDeviation;
		
		double zcrMean = SignalCalculations.calculateMean(zcrBuffer);
		double zcrStandardDeviation = SignalCalculations.calculateStandardDeviation(zcrBuffer);
		this.ZCRThreshold = zcrMean + 1.0 * zcrStandardDeviation;
		
		/* bolje sljaka */
		this.energyThreshold = this.energyThreshold * SCALE;

	}
	
	
	private void smoothUp(boolean[] voiced, int mergeFrames){
		int last = -1;
		for(int i = 1; i < voiced.length - 1; i++){
			if(voiced[i] && !voiced[i-1])
				if(last != -1 && i - last < mergeFrames)
					for(int j = last; j < i; j++)
						voiced[j] = true;
		
			if(voiced[i] && !voiced[i+1])
				last = i;
		}	
	}
	
	private void smoothDown(boolean[] voiced, int spikeFrames){
		int count = 0;
		for(int i = 0; i < voiced.length; i++)
			if(voiced[i])
				count++;
			else 
				if(count < spikeFrames){
					for(int j = i-count; j < i; j++)
						voiced[j] = false;
					count = 0;
				}
	}
	
	private void extraSmoothUsingZCR(boolean[] voiced, int ZCRExpandFrames, double[] samples){
		for(int i = 1; i < voiced.length-1; i++){
			if(voiced[i] && !voiced[i-1]){
				int bound = Math.max(0, i - ZCRExpandFrames);
				int start = i * windowSamples;
				for(int j = i - 1; j >= bound; j--){
					start -= windowSamples;
					double[] currentWindow = SignalCalculations.getSubArray(samples, start, windowSamples);
					double ZCR = SignalCalculations.calculateZCR(currentWindow);
					if(ZCR < ZCRThreshold)
						break;
					voiced[j] = true;
				}
			}
			
			if(voiced[i] && !voiced[i+1]){
				int bound = Math.min(voiced.length, i + ZCRExpandFrames);
				int start = i * windowSamples;
				for(int j = i + 1; j < bound; j++){
					start += windowSamples;
					double[] currentWindow = SignalCalculations.getSubArray(samples, start, windowSamples);
					double ZCR = SignalCalculations.calculateZCR(currentWindow);
					if(ZCR < ZCRThreshold)
						break;
					voiced[j] = true;
				}
			}
		}
	}
	
	
	private ArrayList<Interval> takeIntervals(boolean[] voiced){
		ArrayList<Interval> intervals = new ArrayList<>();
		
		boolean temp = voiced[voiced.length-1];
		voiced[voiced.length-1] = false;
		
		int time = windowSamples;
		
		int start = -1;
		int end = -1;
		
		for(int i = 0; i < voiced.length; i++){
			if(voiced[i]){
				if(start == -1)
					start = i;
			}
			else
				if(start != -1){
					end = i;
					intervals.add(new Interval((start - windowCount / 2) * time, (end - windowCount /2) * time));
					start = end = -1;
					
				}
		}
		
		if(start != -1){
			end = voiced.length;
			/*TODO: windowLenght*/
			intervals.add(new Interval((start - windowCount / 2) * time, (end - windowCount /2) * time));
			
		}
		
		voiced[voiced.length-1] = temp;
		return intervals;
	}
	
}
