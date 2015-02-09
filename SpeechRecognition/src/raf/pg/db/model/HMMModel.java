package raf.pg.db.model;

import raf.pg.hmm.HiddenMarkov;

@SuppressWarnings("serial")
public class HMMModel extends AbstractModel{

	private static String EXTENSION = ".hmm";
	private HiddenMarkov hmm;
	
	public HMMModel(HiddenMarkov hmm, String name) {
		super(name, EXTENSION);
		this.hmm = hmm;
	}
	
	public HiddenMarkov getHmm() {
		return hmm;
	}
	
	public void setHmm(HiddenMarkov hmm) {
		this.hmm = hmm;
	}

}
