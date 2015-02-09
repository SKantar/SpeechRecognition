package raf.pg.db.model;

import raf.pg.speech.vq.Codebook;

@SuppressWarnings("serial")
public class CodebookModel extends AbstractModel{
	
	private static String EXTENSION = ".cbm";
	private Codebook codebook;
	
	public CodebookModel(Codebook codebook, String name) {
		super(name, EXTENSION);
		this.codebook = codebook;
	}
	
	public void setCodebook(Codebook codebook) {
		this.codebook = codebook;
	}
	
	public Codebook getCodebook() {
		return codebook;
	}
}
