package raf.pg.actions;

import java.awt.event.ActionEvent;

import raf.pg.app.AppCore;

@SuppressWarnings("serial")
public class StartRecordingAction extends AbstractSRAction{
	
	public StartRecordingAction() {
	
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		AppCore.getInstance().newSoundCapture().start();
	}
	

}
