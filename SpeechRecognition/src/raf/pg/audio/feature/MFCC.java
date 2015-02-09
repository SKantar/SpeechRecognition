package raf.pg.audio.feature;

/*
 * 	Podeliti signal na kratke prozore
	Racunanje DFT za svaki prozor
	Primena mel banke filtera; sumiranje energije
	Racuna se logaritam za svaki koeficijent
	Racuna se DCT za svaki koeficijent

 */



public class MFCC {

	private int numMelFilters = 30;
	private int numCepstra;
	private double preEmphasisAlpha = 0.95;
	private double lowerFilterFreq = 80.00;
	private double samplingRate;
	private double upperFilterFreq;
	private double bin[];
	private int samplePerFrame;
	
	FFT fft;
	DCT dct;

	public MFCC(int samplePerFrame, int samplingRate, int numCepstra) {
		this.samplePerFrame = samplePerFrame;
		this.samplingRate = samplingRate;
		this.numCepstra = numCepstra;
		upperFilterFreq = samplingRate / 2.0;
		fft = new FFT();
		dct = new DCT(this.numCepstra, numMelFilters);
	}

	public double[] doMFCC(double[] framedSignal) {
		
		bin = magnitudeSpectrum(framedSignal);
		framedSignal = preEmphasis(framedSignal);
		
		int cbin[] = fftBinIndices();
		
		double fbank[] = melFilter(bin, cbin);
		
		double f[] = nonLinearTransformation(fbank);
		
		double cepc[] = dct.performDCT(f);
		
		return cepc;
	}

	private double[] magnitudeSpectrum(double frame[]) {
		double magSpectrum[] = new double[frame.length];
		
		fft.computeFFT(frame);
		
		for (int k = 0; k < frame.length; k++) {
			magSpectrum[k] = Math.sqrt(fft.real[k] * fft.real[k] + fft.imag[k] * fft.imag[k]);
		}
		return magSpectrum;
	}
	
	private double[] preEmphasis(double inputSignal[]) {
		
		double outputSignal[] = new double[inputSignal.length];
		
		for (int n = 1; n < inputSignal.length; n++) {
			outputSignal[n] = (double) (inputSignal[n] - preEmphasisAlpha * inputSignal[n - 1]);
		}
		return outputSignal;
	}

	private int[] fftBinIndices() {
		int cbin[] = new int[numMelFilters + 2];
		cbin[0] = (int) Math.round(lowerFilterFreq / samplingRate * samplePerFrame);// cbin0
		cbin[cbin.length - 1] = (samplePerFrame / 2);	// cbin29
		for (int i = 1; i <= numMelFilters; i++) {		// from cbin1 to cbin28
			double fc = centerFreq(i);					// center freq for i th filter
			cbin[i] = (int) Math.round(fc / samplingRate * samplePerFrame);
		}
		return cbin;
	}

	private double[] melFilter(double bin[], int cbin[]) {
		double temp[] = new double[numMelFilters + 2];
		for (int k = 1; k <= numMelFilters; k++) {
			double num1 = 0.0, num2 = 0.0;
			for (int i = cbin[k - 1]; i <= cbin[k]; i++) {
				num1 += ((i - cbin[k - 1] + 1) / (cbin[k] - cbin[k - 1] + 1)) * bin[i];
			}

			for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++) {
				num2 += (1 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1))) * bin[i];
			}

			temp[k] = num1 + num2;
		}
		double fbank[] = new double[numMelFilters];
		for (int i = 0; i < numMelFilters; i++) {
			fbank[i] = temp[i + 1];
		}
		return fbank;
	}
	
	private double[] nonLinearTransformation(double fbank[]) {
		double f[] = new double[fbank.length];
		final double FLOOR = -50;
		for (int i = 0; i < fbank.length; i++) {
			f[i] = Math.log(fbank[i]);
			
			if (f[i] < FLOOR) {
				f[i] = FLOOR;
			}
		}
		return f;
	}

	private double centerFreq(int i) {
		double melFLow, melFHigh;
		melFLow = freqToMel(lowerFilterFreq);
		melFHigh = freqToMel(upperFilterFreq);
		double temp = melFLow + ((melFHigh - melFLow) / (numMelFilters + 1)) * i;
		return inverseMel(temp);
	}

	private double inverseMel(double x) {
		double temp = Math.pow(10, x / 2595) - 1;
		return 700 * (temp);
	}

	protected double freqToMel(double freq) {
		return 2595 * log10(1 + freq / 700);
	}

	private double log10(double value) {
		return Math.log(value) / Math.log(10);
	}
}
