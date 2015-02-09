package raf.pg.audio.inputoutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import raf.pg.audio.FormatConfig;

public class SoundCapture implements Runnable{

	private static int BITS_PER_BYTE = 8;
	
	private TargetDataLine line;
	private static FormatConfig format;
	private byte[] audioBytes;
	private AudioInputStream audioInputStream;
	
	private Thread thread;
	
	public SoundCapture() {
		format = new FormatConfig();
	}
	
	public void start() {
		thread = new Thread(this);
		thread.setName("Capture");
		thread.start();
	}

	public void stop() {
		thread = null;
	}

	
	@Override
	public void run() {
		audioInputStream = null;
		
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format.getFormat());
		if (!AudioSystem.isLineSupported(info)) {
			System.err.println("Line supported info error: " + info);
			return;
		}
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format.getFormat(), line.getBufferSize());
		} catch (LineUnavailableException ex) {
			System.err.println(ex.toString());
			return;
		} catch (SecurityException ex) {
			System.err.println(ex.toString());
			return;
		} catch (Exception ex) {
			System.err.println(ex.toString());
			return;
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int frameSizeInBytes = format.getFormat().getFrameSize();		
		int bufferLenghtInBytes = (line.getBufferSize() / BITS_PER_BYTE) * frameSizeInBytes;
		
		byte[] data = new byte[bufferLenghtInBytes];
		
		int numBytesRead;
		
		line.start();
		
		while (thread != null) {
			if ((numBytesRead = line.read(data, 0, bufferLenghtInBytes)) == -1) {
				break;
			}
			out.write(data, 0, numBytesRead);
		}
		
		line.stop();
		line.close();
		line = null;
		
		try {
			out.flush();
			out.close();
		} catch (IOException ex) {
			System.err.println("Error on inputstream");
		}
		
		audioBytes = out.toByteArray();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		audioInputStream = new AudioInputStream(bais, format.getFormat(), audioBytes.length / frameSizeInBytes);
		
		try {
			audioInputStream.reset();
		} catch (Exception ex) {
			System.err.println("Eor in reseting inputStream");
		}

	}
	
	public AudioInputStream getAudioInputStream() {
		return audioInputStream;
	}
	
	public byte[] getAudioBytes() {
		return audioBytes;
	}
	
	public static FormatConfig getFormat() {
		return format;
	}
	

}
