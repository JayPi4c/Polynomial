package com.JayPi4c;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 
 * @author JayPi4c
 *
 */
public class Monomial {

	private int degree;
	private double coefficient;

	/**
	 * Create a monomial with the coefficient and degree.
	 * 
	 * @param coefficient
	 * @param degree
	 */
	public Monomial(double coefficient, int degree) {
		this.coefficient = coefficient;
		this.degree = degree;
	}

	/**
	 * Create a monomial with the specified coefficient and degree 0.
	 * 
	 * @param coefficient
	 */
	public Monomial(double coefficient) {
		this.coefficient = coefficient;
		this.degree = 0;
	}

	/**
	 * Create a monomial with the specified degree and the coefficient 1.
	 * 
	 * @param degree
	 */
	public Monomial(int degree) {
		this.degree = degree;
		this.coefficient = 1;
	}

	// ------------------FUNCTIONS-----------------
	/**
	 * 
	 * @param t
	 * @return true, if same degree
	 */
	public boolean isCombinable(Monomial t) {
		return this.degree == t.getDegree();
	}

	/**
	 * If two monomials have the same degree, then the monomials can be combined.
	 * 
	 * @param t the monomial that is combined with the calling one.
	 * @return this
	 */
	public Monomial combine(Monomial t) {
		if (!this.isCombinable(t))
			throw new IllegalArgumentException("These parts cannot be combined!");
		this.coefficient += t.getCoefficient();
		return this;
	}

	/**
	 * Derive the monomial.
	 * 
	 * @return derived monomial
	 */
	public Monomial getDerivation() {
		return new Monomial(this.coefficient * this.degree, this.degree - 1);
	}

	/**
	 * Multiply the monomial by the scalar.
	 * 
	 * @param scl
	 * @return this
	 */
	public Monomial mult(double scl) {
		this.coefficient *= scl;
		return this;
	}

	/**
	 * Returns the monomial as string.
	 * 
	 * @return String Monomial
	 */
	@Override
	public String toString() {
		return this.coefficient + (this.degree > 0 ? "x^" + this.degree : "");
	}

	/**
	 * Returns the monomial in formatted version.
	 * 
	 * @return formatted Monomial
	 */
	public String toStringFormatted(String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		df.setRoundingMode(RoundingMode.HALF_UP);
		return coefficient == 0 ? "" : (df.format(coefficient) + (this.degree > 0 ? "x^" + this.degree : ""));
	}

	public String toStringFormatted() {
		return toStringFormatted("#.####");
	}

	// ---------------------HELPER-----------------------------

	/**
	 * Returns the degree of the calling monomial.
	 * 
	 * @return degree
	 */
	public int getDegree() {
		return this.degree;
	}

	/**
	 * Sets the degree of the calling monomial.
	 * 
	 * @param degree
	 */
	public void setDegree(int degree) {
		this.degree = degree;
	}

	/**
	 * Returns the coefficient of the calling monomial.
	 * 
	 * @return coefficient
	 */
	public double getCoefficient() {
		return this.coefficient;
	}

	/**
	 * Sets the coefficient of the calling monomial.
	 * 
	 * @param coefficient
	 */
	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}

	/**
	 * 
	 * @return an independent copy of the calling monomial.
	 */
	public Monomial copy() {
		return new Monomial(this.coefficient, this.degree);
	}

}
