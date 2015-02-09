package raf.pg.actions;

import java.awt.event.ActionEvent;

import raf.pg.app.AppCore;

@SuppressWarnings("serial")
public class StopRecordingAction extends AbstractSRAction{

	public StopRecordingAction() {
	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		AppCore.getInstance().getSoundCapture().stop();
	}
}
