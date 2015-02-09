package raf.pg.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import raf.pg.db.model.CodebookModel;
import raf.pg.db.model.HMMModel;
import raf.pg.db.model.TrainingSet;
import raf.pg.db.model.WordModel;
import raf.pg.model.Model;

public class Database {
	
	private static String DATABASE_FN = "database";
	
	private static String TRAININGSET_FN = "trainingset";
	private static String CODEBOOK__FN = "codebook";
	private static String HMM_FN = "hiddenmarkovmodels";
	
	private File databaseFolder;
	private File trainingSetFolder;
	private File codebookFolder;
	private File hmmFolder;
	
	public Database(){
		databaseFolder = new File(DATABASE_FN);
		trainingSetFolder = new File(DATABASE_FN +"\\"+ TRAININGSET_FN);
		codebookFolder = new File(DATABASE_FN +"\\"+ CODEBOOK__FN);
		hmmFolder = new File(DATABASE_FN +"\\"+ HMM_FN);
		
		
		if (!databaseFolder.exists()) {
			databaseFolder.mkdir();
		}
		if (!trainingSetFolder.exists()) {
			trainingSetFolder.mkdir();
		}
		if (!codebookFolder.exists()) {
			codebookFolder.mkdir();
		}
		if (!hmmFolder.exists()) {
			hmmFolder.mkdir();
		}
		
	}
	
	public void save(Model model){
		File folder = getFolder(model.getClass());
		
			try {
				File file = new File(folder.getAbsoluteFile() + "/" + model.getName() + model.getExtension());
				
				FileOutputStream fileOut = new FileOutputStream(file);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				
				out.writeObject(model);
				
				fileOut.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public Model load(Class<? extends Model> model, String name, String extension){
		File folder = getFolder(model);
		try {
			File file = new File(folder.getAbsoluteFile() + "/" + name + extension );
			
			if (!file.exists()) {
				return null;
			}
			
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			
			Model loadedModel = (Model) in.readObject();
			
			in.close();
			fileIn.close();
			return loadedModel;
		}
		catch (IOException | ClassNotFoundException e) {
			System.err.println(e);
		}
		return null;
	}
	
	
	public void saveWord(WordModel word){
		String folderPath = trainingSetFolder.getAbsolutePath() + "/" + word.getName();
		
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		File myFile = new File(folder.getAbsolutePath() + "/" + "0" + word.getExtension());
		int i = 0;
		while (myFile.exists()) {
			String temp = String.format("%d", i++);
			myFile = new File(folder.getAbsolutePath() + "/" + temp + word.getExtension());
		}
		try{
			FileOutputStream fileOut = new FileOutputStream(myFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
		
			out.writeObject(word);
		
			fileOut.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

	public ArrayList<HMMModel> loadHMModels() {
		String[] modelList = hmmFolder.list();
		ArrayList<HMMModel> hmmModels = new ArrayList<HMMModel>();
		
		for (String folderName : modelList) {
			hmmModels.add( (HMMModel) load(HMMModel.class, folderName, "") );
		}
		return hmmModels;
	}
	
	public TrainingSet loadTrainingSets() {
		TrainingSet trainingSetFiles = new TrainingSet("training");
		String[] folders = trainingSetFolder.list();
		
		for (String folderName : folders) {
			trainingSetFiles.addSet(new File(trainingSetFolder.getAbsoluteFile() + "/" + folderName));
		}
		return trainingSetFiles;
	}
	
	 public static WordModel readWord(File file) throws IOException, ClassNotFoundException {
		 FileInputStream fis = new FileInputStream(file);
		 ObjectInputStream ois = new ObjectInputStream(fis);
		 WordModel wrd = (WordModel)ois.readObject();
		 ois.close();
		 return wrd;
	 }
	
	
	private File getFolder(Class<? extends Model> model){
		if(model.equals(CodebookModel.class)) return codebookFolder;
		else if(model.equals(HMMModel.class)) return hmmFolder;
		else if(model.equals(WordModel.class)) return trainingSetFolder;
		return null;
				
	}

	
}
