package raf.pg.audio;

public class PreProcess {
	
	private static int samplePerFrame = 512;
	
	public static double[] normalizePCM(double[] samples) {
		double max = samples[0];
		for (int i = 1; i < samples.length; i++) {
			if (max < Math.abs(samples[i])) {
				max = Math.abs(samples[i]);
			}
		}
		// System.out.println("max PCM =  " + max);
		for (int i = 0; i < samples.length; i++) {
			samples[i] = samples[i] / max;
		}
		return samples;
	}
	
	public static double[][] doFraming(double[] samples){
		// calculate no of frames, for framing
		double[][] framedSignal;
		int noOfFrames = 2 * samples.length / samplePerFrame - 1;
		/*System.out.println("noOfFrames       " + noOfFrames + "  samplePerFrame     " + samplePerFrame + "  EPD length   "
				+ samples.length);*/
		framedSignal = new double[noOfFrames][samplePerFrame];
		for (int i = 0; i < noOfFrames; i++) {
			int startIndex = (i * samplePerFrame / 2);
			for (int j = 0; j < samplePerFrame; j++) {
				framedSignal[i][j] = samples[startIndex + j];
			}
		}
		return framedSignal;
	}
	
	public static double[][] doWindowing(double[][] framedSignal) {
		// prepare hammingWindow
		double[] hammingWindow = new double[samplePerFrame + 1];
		// prepare for through out the data
		for (int i = 1; i <= samplePerFrame; i++) {
			hammingWindow[i] = (float) (0.54 - 0.46 * (Math.cos(2 * Math.PI * i / samplePerFrame)));
		}
		// do windowing
		for (int i = 0; i < framedSignal.length; i++) {
			for (int j = 0; j < samplePerFrame; j++) {
				framedSignal[i][j] = framedSignal[i][j] * hammingWindow[j + 1];
			}
		}
		return framedSignal;
	}
	
	public static int getSamplePerFrame() {
		return samplePerFrame;
	}
	

}
