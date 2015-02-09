package raf.pg.audio.feature;

public class DCT {

	int numCepstra;
	
	int M;

	public DCT(int numCepstra, int M) {
		this.numCepstra = numCepstra;
		this.M = M;
	}

	public double[] performDCT(double y[]) {
		double cepc[] = new double[numCepstra];
		for (int n = 1; n <= numCepstra; n++) {
			for (int i = 1; i <= M; i++) {
				cepc[n - 1] += y[i - 1] * Math.cos(Math.PI * (n - 1) / M * (i - 0.5));
			}
		}
		return cepc;
	}
}
