package raf.pg.db.model;

import raf.pg.preProcessings.Word;

public class WordModel extends AbstractModel{

	private static final long serialVersionUID = 5409395563452722260L;

	private static String EXTENSION = ".wrd";
	private Word word;
	
	public WordModel(Word word, String name) {
		super(name, EXTENSION);
		this.word = word;
	}
	
	public Word getWord() {
		return word;
	}
	
	public void setWord(Word word) {
		this.word = word;
	}

}
