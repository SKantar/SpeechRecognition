package raf.pg.actions;


public class ActionManager {
	private StartRecordingAction startRecordingAction;
	private StopRecordingAction stopRecordingAction;
	
	private RecognizeWordsAction recognizeWordsAction;
	private GetWordsForTrainAction getWordsForTrainAction;
	
	private SaveWordsAction saveWordsAction;
	private DoTrainingAction doTrainingAction;
	
	public ActionManager() {
		startRecordingAction = new StartRecordingAction();
		stopRecordingAction = new StopRecordingAction();
		
		recognizeWordsAction = new RecognizeWordsAction();
		getWordsForTrainAction = new GetWordsForTrainAction();
		
		saveWordsAction = new SaveWordsAction();
		
		doTrainingAction = new DoTrainingAction();
	}
	
	public StartRecordingAction getStartRecordingAction() {
		return startRecordingAction;
	}
	
	public StopRecordingAction getStopRecordingAction() {
		return stopRecordingAction;
	}
	
	public RecognizeWordsAction getRecognizeWordsAction() {
		return recognizeWordsAction;
	}
	
	public GetWordsForTrainAction getGetWordsForTrainAction() {
		return getWordsForTrainAction;
	}
	
	public SaveWordsAction getSaveWordsAction() {
		return saveWordsAction;
	}
	
	public DoTrainingAction getDoTrainingAction() {
		return doTrainingAction;
	}
}
