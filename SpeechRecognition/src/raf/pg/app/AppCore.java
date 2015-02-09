package raf.pg.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import raf.pg.actions.ActionManager;
import raf.pg.actions.IncludeWordAction;
import raf.pg.actions.PlaySoundAction;
import raf.pg.audio.inputoutput.SoundCapture;
import raf.pg.db.Database;
import raf.pg.db.model.CodebookModel;
import raf.pg.db.model.HMMModel;
import raf.pg.preProcessings.Word;
import raf.pg.util.Functions;

@SuppressWarnings("serial")
public class AppCore extends JFrame{
	
	private static AppCore instance = null;
	private SoundCapture soundCapture;
	private ActionManager actionManager;
	
	private ArrayList<Word> words;
	private ArrayList<JTextField> wordsValue;
	
	private JPanel recStaticPanel;
	private JPanel recDinamicPanel;
	
	private JPanel traStaticPanel;
	private JPanel traDinamicPanel;
	
	private JDesktopPane recognizePane;
	private JDesktopPane trainPane;
	
	private Database database;
	private CodebookModel codebookModel;
	private ArrayList<HMMModel> hmmModels;
	private Map<String, Integer> hmmNameToIndex;
	
	
	private boolean[] includeWord;
	
	public AppCore() {
		
		actionManager = new ActionManager();
		wordsValue = new ArrayList<JTextField>();
		
		try {
			UIManager.setLookAndFeel(
			            UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setSize(600, 600);
		setTitle("SpeechRecognition");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		getContentPane().setBackground(Color.gray);
		setResizable(false);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 11, 584, 547);
		tabbedPane.setBackground(Color.gray);
		add(tabbedPane);

		recognizePane = new JDesktopPane();
		tabbedPane.addTab("<html><body><table width='70'>Recognize</table></body></html>", null, recognizePane, null);
		recognizePane.setLayout(null);
		recognizePane.setBackground(Color.gray);
		
		trainPane = new JDesktopPane();
		tabbedPane.addTab("<html><body><table width='70'>Train</table></body></html>", null, trainPane, null);
		trainPane.setLayout(null);
		trainPane.setBackground(Color.gray);
		
		initialize();
		
	}
	
	private void initialize(){
		initializeModels();
		initializeRecognizePanel();
		initializeTrainPanel();	
	}

	private void initializeModels(){
		database = new Database();
		codebookModel = new CodebookModel(null,	null);
		hmmModels = database.loadHMModels();
		initializeHmmNameToIndex();
	
		Object codebookObj = database.load(CodebookModel.class, "codebook", ".cbm");
		
		if (codebookObj != null) {
			codebookModel = (CodebookModel) codebookObj;
		}
	
	}
	
	private void initializeRecognizePanel() {

		recStaticPanel = new JPanel();
		recStaticPanel.setBounds(0, 0, 200, 520);
		recStaticPanel.setLayout(null);
		
		JButton startRecordingRec = new JButton(actionManager.getStartRecordingAction()/*new StartRecordingAction()*/);
		startRecordingRec.setBounds(50, 50, 100, 30);
		startRecordingRec.setText("Start recording");
		recStaticPanel.add(startRecordingRec);
		
		JButton stopRecordingRec = new JButton(actionManager.getStopRecordingAction()/*new StopRecordingAction()*/);
		stopRecordingRec.setBounds(50, 110, 100, 30);
		stopRecordingRec.setText("Stop recording");
		recStaticPanel.add(stopRecordingRec);
		
		JButton recognizeRec = new JButton(actionManager.getRecognizeWordsAction()/*new RecognizeWordsAction()*/);
		recognizeRec.setBounds(50, 170, 100, 30);
		recognizeRec.setText("Recognize words");
		recStaticPanel.add(recognizeRec);
		
		recDinamicPanel = new JPanel();
		recDinamicPanel.setBounds(205, 0, 375, 520);
		recDinamicPanel.setLayout(null);
		
		recognizePane.add(recStaticPanel);
		recognizePane.add(recDinamicPanel);
		

	}
	
	private void initializeTrainPanel() {

		traStaticPanel = new JPanel();
		traStaticPanel.setBounds(0, 0, 200, 520);
		traStaticPanel.setLayout(null);
		
		JButton startRecordingRec = new JButton(actionManager.getStartRecordingAction());
		startRecordingRec.setBounds(50, 50, 100, 30);
		startRecordingRec.setText("Start recording");
		traStaticPanel.add(startRecordingRec);
		
		JButton stopRecordingRec = new JButton(actionManager.getStopRecordingAction());
		stopRecordingRec.setBounds(50, 110, 100, 30);
		stopRecordingRec.setText("Stop recording");
		traStaticPanel.add(stopRecordingRec);
		
		JButton recognizeRec = new JButton(actionManager.getGetWordsForTrainAction());
		recognizeRec.setBounds(50, 170, 100, 30);
		recognizeRec.setText("Get words");
		traStaticPanel.add(recognizeRec);
		
		JButton saveWords = new JButton(actionManager.getSaveWordsAction());
		saveWords.setBounds(50, 230, 100, 30);
		saveWords.setText("Save words");
		traStaticPanel.add(saveWords);
		
		JButton train = new JButton(actionManager.getDoTrainingAction());
		train.setBounds(50, 290, 100, 30);
		train.setText("Retrain");
		traStaticPanel.add(train);
		
		traDinamicPanel = new JPanel();
		traDinamicPanel.setBounds(205, 0, 375, 520);
		traDinamicPanel.setLayout(null);
		
		trainPane.add(traStaticPanel);
		trainPane.add(traDinamicPanel);
		
	}
	
	
	
	public void showDetectedWordsRecognize(JPanel panel, ArrayList<Word> words, ArrayList<AudioInputStream> streams) {
		this.words = words;
		panel.removeAll();
		includeWord = new boolean[words.size()];
		Arrays.fill(includeWord, true);
		wordsValue.clear();
		for (int i = 0; i < words.size(); i++) {
			
			
			String recognizedWord = "Message: ERROR";
			try {
				recognizedWord = Functions.recognizeSpeech(words.get(i).getFeatureVector());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JLabel wordText = new JLabel(recognizedWord);
			wordText.setBounds(100, 10 + i * 50, 100, 30);
			
			JButton playButton = new JButton(new PlaySoundAction(streams.get(i)));
			playButton.setBounds(200, 10 + i * 50, 100, 30);
		    playButton.setText("Ovo neka rec");
			
			panel.add(wordText);
		    panel.add(playButton);
		    
		    this.revalidate();
		}
	    this.repaint();
	}
	
	public void showDetectedWordsTrain(JPanel panel, ArrayList<Word> words, ArrayList<AudioInputStream> streams) {
		this.words = words;
		panel.removeAll();
		includeWord = new boolean[words.size()];
		Arrays.fill(includeWord, true);
		wordsValue.clear();
		for (int i = 0; i < words.size(); i++) {
			
			JTextField wordText = new JTextField();
			wordText.setBounds(40, 10 + i * 50, 100, 30);
			wordsValue.add(wordText);
			
			JButton playButton = new JButton(new PlaySoundAction(streams.get(i)));
			playButton.setBounds(200, 10 + i * 50, 100, 30);
		    playButton.setText("Ovo neka rec");
		    
		    JCheckBox checkbox = new JCheckBox(new IncludeWordAction(includeWord, i));
		    checkbox.setBounds(140, 10+i*50, 60, 30);
			checkbox.setSelected(true);
			checkbox.setText("include");
			
			panel.add(wordText);
			panel.add(checkbox);
		    panel.add(playButton);
		    
		    this.revalidate();
		}
	    this.repaint();
	}
	
	
	public static AppCore getInstance() {
		if(instance == null)
			instance = new AppCore();
		return instance;
	}
	
	public SoundCapture newSoundCapture() {
		soundCapture = new SoundCapture();
		return soundCapture;
	}
	
	public SoundCapture getSoundCapture() {
		return soundCapture;
	}
	
	public ActionManager getActionManager() {
		return actionManager;
	}
	
	public JPanel getRecDinamicPanel() {
		return recDinamicPanel;
	}
	
	public JPanel getRecStaticPanel() {
		return recStaticPanel;
	}
	
	public JPanel getTraDinamicPanel() {
		return traDinamicPanel;
	}
	
	public JPanel getTraStaticPanel() {
		return traStaticPanel;
	}
	
	public boolean getIncludeWord(int index) {
		return includeWord[index];
	}
	
	public ArrayList<Word> getWords() {
		return words;
	}
	
	public Word getWord(int index) {
		return words.get(index);
	}
	
	public String getWordValue(int index) {
		return wordsValue.get(index).getText().trim().toLowerCase();
	}
	
	public Database getDatabase() {
		return database;
	}
	
	public Database newDatabase(){
		database = new Database();
		return database;
	}
	
	public void setCodebookModel(CodebookModel codebookModel) {
		this.codebookModel = codebookModel;
	}
	
	public CodebookModel getCodebookModel() {
		return codebookModel;
	}
	
	public ArrayList<HMMModel> getHmmModels() {
		return hmmModels;
	}
	
	public HMMModel getHmmModel(int index){
		return hmmModels.get(index);
	}
	
	public Map<String, Integer> getHmmNameToIndex() {
		return hmmNameToIndex;
	}
	
	public void initializeHmmNameToIndex() {
		hmmNameToIndex = new HashMap<>();
		for (int i = 0; i < hmmModels.size(); i++) {
			hmmNameToIndex.put(getHmmModel(i).getName(), i);
		}
	}
	
	public HMMModel getHMMModelByName(String name) {
		return getHmmModel(hmmNameToIndex.get(name));
	}
}
