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
	 * Erstelle einen Monomial mit dem Koeffizienten und Grad
	 * 
	 * @param coefficient
	 * @param degree
	 */
	public Monomial(double coefficient, int degree) {
		this.coefficient = coefficient;
		this.degree = degree;
	}

	/**
	 * Erstelle einen Monomial mit dem festgelegten Koeffizienten und dem Grad 0
	 * 
	 * @param coefficient
	 */
	public Monomial(double coefficient) {
		this.coefficient = coefficient;
		this.degree = 0;
	}

	/**
	 * Erstelle einen Monomial, mit dem festgelegten Grad und dem Koeffizienten 1
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
	 * Wenn zwei Monomiale den gleichen Grad haben, dann können die Monomiale
	 * kombiniert werden.
	 * 
	 * @param t der Monomial, der mit dem aufrufenden kombiniert wird
	 * @return this
	 */
	public Monomial combine(Monomial t) {
		if (!this.isCombinable(t))
			throw new IllegalArgumentException("Diese Teile können nicht kombiniert werden!");
		this.coefficient += t.getCoefficient();
		return this;
	}

	/**
	 * Sofern der Grad > 0, kann der Monomial abgeleitet werden.
	 * 
	 * @return true, if term derivable
	 */
	public boolean isDerivable() {
		return this.degree > 0;
	}

	/**
	 * Leitet den Monomial ab, sofern dies überhaupt m&oumlglich ist.
	 * 
	 * @throws IllegalAccessException if Monomial is not derivable
	 * @return derivated Monomial
	 */
	public Monomial getDerivation() {
		if (!this.isDerivable())
			throw new IllegalArgumentException("Dieser Teil ist nicht ableitbar");
		return new Monomial(this.coefficient * this.degree, this.degree - 1);
	}

	/**
	 * Multipliziert den Monomial mit dem Skalar
	 * 
	 * @param scl
	 * @return this
	 */
	public Monomial mult(double scl) {
		this.coefficient *= scl;
		return this;
	}

	/**
	 * Gibt den Monomial als String zur&uumlck
	 * 
	 * @return String Monomial
	 */
	@Override
	public String toString() {
		return this.coefficient + (this.degree > 0 ? "x^" + this.degree : "");
	}

	/**
	 * Gibt den Monomial in formatierter Version zur&uumlck
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
	 * Gibt den Grad des aufrufenden Monomials zur&uumlck
	 * 
	 * @return degree
	 */
	public int getDegree() {
		return this.degree;
	}

	/**
	 * Setzt den Grad des aufrufenden Monomials
	 * 
	 * @param degree
	 */
	public void setDegree(int degree) {
		this.degree = degree;
	}

	/**
	 * Gibt den Koeffizienten des aufrufenden Monomials zur&uumlck
	 * 
	 * @return coefficient
	 */
	public double getCoefficient() {
		return this.coefficient;
	}

	/**
	 * Setzt den Koeffizienten des aufrufenden Monomials
	 * 
	 * @param coefficient
	 */
	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}

	/**
	 * 
	 * @return eine unabhängige Kopie des aufrufenden Monomials
	 */
	public Monomial copy() {
		return new Monomial(this.coefficient, this.degree);
	}

}
