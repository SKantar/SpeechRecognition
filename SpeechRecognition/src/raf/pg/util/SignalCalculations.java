package raf.pg.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import raf.pg.preProcessings.Interval;

public abstract class SignalCalculations {
	
	public static AudioInputStream getAudioInputStreamSlice(byte[] audioBytes, Interval interval, AudioFormat format) {
		int frameSize = format.getFrameSize();
		System.out.println("Frame size = " + frameSize);
		
		byte[] slice = new byte[interval.getLength() * frameSize];
		
		for (int i = interval.getStart(); i < interval.getEnd(); i++) {
			int idx = (i - interval.getStart());
			
			for (int j = 0; j < frameSize; j++) {
				slice[frameSize * idx + j] =
						audioBytes[frameSize * i + j];
			}
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(slice);
		int frameSizeInBytes = format.getFrameSize();
		return new AudioInputStream(bais, format, slice.length / frameSizeInBytes);
	}
	
	public static double[] audioInputStreamToSamples(AudioInputStream audioInputStream){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AudioFormat format = audioInputStream.getFormat();
		
		int frameSizeInBytes = format.getFrameSize();
		long bufferLengthInBytes = audioInputStream.getFrameLength() * frameSizeInBytes;
		byte[] data = new byte[(int)bufferLengthInBytes];
		int numBytesRead;

		try {
			audioInputStream.reset();
			while ((numBytesRead = audioInputStream.read(data, 0, (int) bufferLengthInBytes)) != -1) {
				out.write(data, 0, numBytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] audioBytes = out.toByteArray();
		return extractSamples(audioBytes, format);
		
	}
	
	private static double[] extractSamples(byte[] audioBytes, AudioFormat format) {
		// convert
		double[] audioData = null;
		if (format.getSampleSizeInBits() == 16) {
			int nlengthInSamples = audioBytes.length / 2;

			audioData = new double[nlengthInSamples];
			if (format.isBigEndian()) {
				for (int i = 0; i < nlengthInSamples; i++) {
					/* First byte is MSB (high order) */
					int MSB = audioBytes[2 * i];
					/* Second byte is LSB (low order) */
					int LSB = audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			} else {
				for (int i = 0; i < nlengthInSamples; i++) {
					/* First byte is LSB (low order) */
					int LSB = audioBytes[2 * i];
					/* Second byte is MSB (high order) */
					int MSB = audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			}
		} else if (format.getSampleSizeInBits() == 8) {
			int nlengthInSamples = audioBytes.length;
			audioData = new double[nlengthInSamples];
			if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
				for (int i = 0; i < audioBytes.length; i++) {
					audioData[i] = audioBytes[i];
				}
			} else {
				for (int i = 0; i < audioBytes.length; i++) {
					audioData[i] = audioBytes[i] - 128;
				}
			}
		}
		
		for (int i = 0; i < audioData.length; i++) {
			audioData[i] /= Short.MAX_VALUE;
		}
		
		double[] oneChannelData = new double[audioData.length / format.getChannels()];
		for (int i = 0; i < oneChannelData.length; i++) {
			oneChannelData[i] = 0;
			for (int j = i * format.getChannels(); j < (i + 1) * format.getChannels(); j++) {
				oneChannelData[i] += audioData[j];
			}
			oneChannelData[i] /= format.getChannels();
		}
		
		return oneChannelData;
	}
	
	
	
	public static double[] getSamples(double[] samples, int start, int length) {
		double[] temp = new double[length];
		for (int i = start; i < start + length; i++) {
			temp[i - start] = samples[i];
		}
		return temp;
	}
	
	public static double[] getSubArray(double[] samples, int start, int length) {
		double[] temp = new double[length];
		for (int i = start; i < start + length; i++) {
			temp[i - start] = samples[i];
		}
		return temp;
	}
	
	public static double calculateMean(double[] samples) {
		double temp = 0.0;
		for (double sample : samples) {
			temp += sample;
		}
		return temp / samples.length;
	}
	
	public static double calculateVariance(double[] samples){
		double temp = 0.0;
		double mean = calculateMean(samples);
		for (double sample : samples) {
			temp += (sample - mean) * (sample - mean);
		}
		return temp / samples.length;
	}
	
	public static double calculateEnergy(double[] samples) {
		double energy = 0.0;
		for (double sample : samples) {
			energy += sample * sample;
		}
		return energy;
	}
	
	public static double calculateEnergy(Double[] samples) {
		double energy = 0.0;
		for (double sample : samples) {
			energy += sample * sample;
		}
		return energy;
	}
	
	public static double calculatePower(double[] samples) {
		return calculateEnergy(samples) / samples.length;
	}
	
	public static double calculatePower(Double[] samples) {
		return calculateEnergy(samples) / samples.length;
	}
	
	public static double calculateZCR(double[] samples) {
		double temp = 0.0;
		for (int i = 1; i < samples.length; i++) {
			temp += Math.abs(sgn(samples[i]) - sgn(samples[i - 1])) / 2.0;
		}
		return temp / samples.length;
	}
	
	public static int sgn(double value) {
		if (value < 0.0) return -1;
		else if (value > 0.0) return +1;
		else return 0;
	}
	
	public static double calculateStandardDeviation(double[] samples) {
		return Math.sqrt(calculateVariance(samples));
	}

}
