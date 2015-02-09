package raf.pg.speech.vq;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class Centroid extends Points implements Serializable {
	private Set<Points> points;
	
	public Centroid(double[] coordinates) {
		super(coordinates);
		points = new HashSet<>();
	}
	
	public Centroid(Points point) {
		super(point);
		points = new HashSet<>();
	}
	
	public void addPoint(Points point) {
		points.add(point);
	}
	
	public void removePoint(Points point) {
		points.remove(point);
	}
	
	public void update() {
		reset();
		for (Points p : points) {
			for (int i = 0; i < coordinates.length; i++) {
				coordinates[i] += p.getCoordinate(i);
			}
		}
		
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] /= points.size();
		}
	}
	
	private void reset() {
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = 0.0f;
		}
	}
	
	public void removeAllPoints() {
		points.clear();
	}

}