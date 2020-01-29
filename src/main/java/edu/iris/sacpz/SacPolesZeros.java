package edu.iris.sacpz;

import java.util.ArrayList;
import java.util.List;

public class SacPolesZeros {
	private List<SacPole> poles = new ArrayList<>();
	private List<SacZero> zeros = new ArrayList<>();

	public List<SacPole> getPoles() {
		return this.poles;
	}

	public List<SacZero> getZeros() {
		return this.zeros;
	}

	public void addPole(SacPole pole) {
		this.poles.add(pole);
	}

	public void addZero(SacZero zero) {
		this.zeros.add(zero);
	}
}
