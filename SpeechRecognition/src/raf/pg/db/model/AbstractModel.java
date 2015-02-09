package raf.pg.db.model;

import java.io.Serializable;

import raf.pg.model.Model;

@SuppressWarnings("serial")
public abstract class AbstractModel implements Model, Serializable{

	protected String name;
	protected String extension;
	
	public AbstractModel(String name, String extension) {
		this.name = name;
		this.extension = extension;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getExtension() {
		return extension;
	}

}
