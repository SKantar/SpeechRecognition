package raf.pg.audio.feature;

public class Energy {

	private int samplePerFrame;

	public Energy(int samplePerFrame) {
		this.samplePerFrame = samplePerFrame;
	}

	public double[] calcEnergy(double[][] framedSignal) {
		double[] energyValue = new double[framedSignal.length];
		for (int i = 0; i < framedSignal.length; i++) {
			double sum = 0;
			for (int j = 0; j < samplePerFrame; j++) sum += Math.pow(framedSignal[i][j], 2);
			energyValue[i] = Math.log(sum);
		}
		return energyValue;
	}
}
