package edu.iris.sacpz;

public class AbstractPZ implements PZ {
	protected double real;
	protected double imaginary;
	protected char type;

	public AbstractPZ(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public double getReal() {
		return this.real;
	}

	public double getImaginary() {
		return this.imaginary;
	}

	@Override
	public char getType() {
		return type;
	}
}
