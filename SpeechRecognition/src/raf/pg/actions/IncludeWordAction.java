package raf.pg.actions;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class IncludeWordAction extends AbstractSRAction{

	boolean[] includeWord;
	int index;
	
	public IncludeWordAction(boolean[] includeWord, int index) {
		this.includeWord = includeWord;
		this.index = index;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		includeWord[index] = !includeWord[index];
	}

}
