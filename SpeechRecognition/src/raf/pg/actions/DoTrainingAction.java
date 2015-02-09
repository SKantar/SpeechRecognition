package raf.pg.actions;

import java.awt.event.ActionEvent;

import raf.pg.util.Functions;

@SuppressWarnings("serial")
public class DoTrainingAction extends AbstractSRAction{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			System.out.println("Generating codebook...");
			Functions.generateCodebook();
			System.out.println("Saving codebook...");
			Functions.saveCodebook();
			System.out.println("Retraining all HMMs...");
			Functions.retrainAllHmms();
	    	System.out.println("Saving all HMMs...");
	    	Functions	.saveCurrentHmmModels();
	    	System.out.println("Finished...");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
