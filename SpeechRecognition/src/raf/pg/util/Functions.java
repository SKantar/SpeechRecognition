package raf.pg.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import raf.pg.app.AppCore;
import raf.pg.audio.feature.FeatureVector;
import raf.pg.db.Database;
import raf.pg.db.model.CodebookModel;
import raf.pg.db.model.HMMModel;
import raf.pg.db.model.TrainingSet;
import raf.pg.db.model.WordModel;
import raf.pg.hmm.HiddenMarkov;
import raf.pg.preProcessings.Word;
import raf.pg.speech.vq.Codebook;
import raf.pg.speech.vq.Points;

public abstract class Functions{
	
	public static final int HMM_STATES = 6;
	
	public static void saveToFile(String location, String name, Word word) throws IOException {
		/*while (name == null) {
			name = JOptionPane.showInputDialog(null, "Enter File Name", "Getting File Name");
		}*/
		File myFile = new File(location);
		if (!myFile.exists()) myFile.mkdir();
		if (word == null) { return; }
		
		myFile = new File(location + "\\" + name + ".mfcc");
		int i = 0;
		while (myFile.exists()) {
			String temp = String.format(name + "%d", i++);
			myFile = new File(location + "\\" + temp + ".mfcc");
		}
		FileWriter fw = new FileWriter(myFile);
        fw.write(word.toString());
        fw.close();
		System.out.println(myFile.getAbsolutePath());
	}
	
	
	public static void generateCodebook() throws Exception {
		
		TrainingSet trainingSetFiles = AppCore.getInstance().newDatabase().loadTrainingSets();
		Map<String, File[]> trainingSets = trainingSetFiles.getTrainingSets();

		ArrayList<Points> points = new ArrayList<Points>();
		for (Entry<String, File[]> entry : trainingSets.entrySet()) {
			for (File file : entry.getValue()) {
				WordModel wordModel = Database.readWord(file);
				for (double[] p : wordModel.getWord().getFeatureVector().getFeatureVector()) {
					points.add(new Points(p));
				}
			}
		}

		Codebook codebook = new Codebook(points, Codebook.DEFAULT_SIZE);
		codebook.initialize();
		AppCore.getInstance().setCodebookModel(new CodebookModel(codebook, "codebook"));
	}
	
	public static void saveCodebook() {
		AppCore.getInstance().getDatabase().save(AppCore.getInstance().getCodebookModel());
	}
	
	public static void retrainAllHmms() throws Exception {
		TrainingSet trainingSetFiles = AppCore.getInstance().newDatabase().loadTrainingSets();
		Map<String, File[]> trainingSets = trainingSetFiles.getTrainingSets();

		//ArrayList<HMMModel> hmmModels = new ArrayList<HMMModel>();
		AppCore.getInstance().getHmmModels().clear();
		for (Entry<String, File[]> entry : trainingSets.entrySet()) {	
			int[][] trainingSet = new int[entry.getValue().length][];
			int m = 0;
			
			for (File file : entry.getValue()) {
				WordModel wordModel = Database.readWord(file);
				FeatureVector featureVector = wordModel.getWord().getFeatureVector();
				List<Points> points = featureVector.toPointsList();
				
				int[] quantized = AppCore.getInstance().getCodebookModel().getCodebook().quantize(points);
				

				/*if (file.getName().equals("0.wav") && file.getParentFile().getName().equals("three")) {
					System.out.println("======= FEATURE VECTOR (TRAINING) ======");
					System.out.println("Number of points = " + quantized.length);
					for (int i = 0; i < quantized.length; i++) {
						System.out.println(quantized[i]);
					}
					System.out.println("========================================");
				}*/
				
				trainingSet[m++] = quantized;
			}

			HiddenMarkov hmm = new HiddenMarkov(HMM_STATES, AppCore.getInstance().getCodebookModel().getCodebook().getSize());
			hmm.setTrainSeq(trainingSet);
			hmm.train();
			HMMModel hmmModel = new HMMModel(hmm, entry.getKey());
			AppCore.getInstance().getHmmModels().add(hmmModel);
		}
	}
	
	public static void saveCurrentHmmModels() {
		for (HMMModel hmmModel : AppCore.getInstance().getHmmModels()) {
			AppCore.getInstance().getDatabase().save(hmmModel);
		}
	}
	
	public static String recognizeSpeech(FeatureVector fv) throws Exception {

		
		int[] quantized = AppCore.getInstance().getCodebookModel().getCodebook().quantize(fv.toPointsList());

//		System.out.println();
//		System.out.println("Feature vectors (RECOGNITION):");
//		System.out.println("Number of points = " + quantized.length);
//		for (int i = 0; i < quantized.length; i++) {
//			System.out.println(quantized[i]);
//		}
//		System.out.println("-----------------------");
		
		String recognizedWord = null;
		double probability = Double.NEGATIVE_INFINITY;
		
		for (HMMModel hmmModel : AppCore.getInstance().getHmmModels()) {
			double tmpProbability = hmmModel.getHmm().viterbi(quantized);
			System.out.println(hmmModel.getName() + "   " + tmpProbability);
			if (tmpProbability > probability) {
				probability = tmpProbability;
				recognizedWord = hmmModel.getName();
			}
		}		
		return recognizedWord;
	}
}
