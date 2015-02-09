package raf.pg.actions;

import java.awt.event.ActionEvent;

import javax.sound.sampled.AudioInputStream;

import raf.pg.audio.inputoutput.SoundPlay;

@SuppressWarnings("serial")
public class PlaySoundAction extends AbstractSRAction{
	
private AudioInputStream audioInputStream;
	
	public PlaySoundAction(AudioInputStream audioInputStream) {
		this.audioInputStream = audioInputStream;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new SoundPlay(audioInputStream).start();
	}

}
