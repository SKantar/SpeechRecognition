package raf.pg.db.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TrainingSet{
	
	private Map<String, File[]> trainingSets;
	
	public TrainingSet(String name) {
		trainingSets = new HashMap<>();
	}
	
	public void addSet(File folder){
		if(folder.isFile()) return;
		trainingSets.put(folder.getName(), folder.listFiles());
	}
	
	public Map<String, File[]> getTrainingSets() {
		return trainingSets;
	}

}
