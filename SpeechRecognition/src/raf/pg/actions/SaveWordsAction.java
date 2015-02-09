package raf.pg.actions;

import java.awt.event.ActionEvent;

import raf.pg.app.AppCore;
import raf.pg.db.model.WordModel;

@SuppressWarnings("serial")
public class SaveWordsAction extends AbstractSRAction {
	
	public SaveWordsAction() {
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for(int i = 0 ; i < AppCore.getInstance().getWords().size(); i++){
			if(AppCore.getInstance().getIncludeWord(i)){
					WordModel wm = new WordModel(AppCore.getInstance().getWord(i), AppCore.getInstance().getWordValue(i));
					AppCore.getInstance().getDatabase().saveWord(wm);
			}
					//System.out.print(i + ":" + AppCore.getInstance().getWordValue(i));
		}
		
	}

}
