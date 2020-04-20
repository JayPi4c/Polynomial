package com.JayPi4c;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The polynomial class offers the possibility to calculate and work with a
 * polynomial, which makes working with polynomials much easier.
 * 
 * @author JayPi4c
 *
 */
public class Polynomial {

	/**
	 * a polynomial is a collection of monomials, which are stored in this array
	 */
	private ArrayList<Monomial> polynomial;

	/**
	 * Create an empty polynomial object.
	 */
	public Polynomial() {
		polynomial = new ArrayList<Monomial>();
	}

	/**
	 * Create a polynomial from an array of monomials.
	 * 
	 * @param monomials
	 */
	public Polynomial(Monomial... monomials) {
		polynomial = new ArrayList<Monomial>(Arrays.asList(monomials));
		this.fill();
	}

	/**
	 * Create a polynomial from an ArrayList of monomials.
	 * 
	 * @param monomials
	 */
	public Polynomial(ArrayList<Monomial> monomials) {
		this.polynomial = new ArrayList<Monomial>();
		for (Monomial t : monomials)
			this.polynomial.add(t.copy());
		this.combine();
		this.fill();
		this.reorder();

	}

	/**
	 * Create a polynomial with the degrees and coefficients.
	 * 
	 * @throws IllegalArgumentException if coefficients and degrees are not of same
	 *                                  length
	 * @param coefficients
	 * @param degrees
	 */
	public Polynomial(double[] coefficients, int[] degrees) {
		if (coefficients.length != degrees.length)
			throw new IllegalArgumentException("The number of coefficients and degrees is not identical!");
		this.polynomial = new ArrayList<Monomial>();
		for (int i = 0; i < coefficients.length; i++)
			this.polynomial.add(new Monomial(coefficients[i], degrees[i]));
		this.fill();
	}

	/**
	 * Adds the polynomial to the polynomial
	 * 
	 * @param p
	 * @return this after math is done
	 */
	public Polynomial add(Polynomial p) {
		this.polynomial.addAll(p.getPolynomial());
		this.combine();
		this.reorder();
		this.fill();
		return this;
	}

	/**
	 * Adds the monomial to the polynomial
	 * 
	 * @param t
	 * @return this after math is done
	 */
	public Polynomial add(Monomial t) {
		this.polynomial.add(t);
		this.combine();
		this.reorder();
		this.fill();
		return this;
	}

	/**
	 * Returns the product of the calling polynomial with the corresponding scalar.
	 * 
	 * @param scl a scalar
	 * @return this after math is done
	 */
	public Polynomial mult(double scl) {
		for (Monomial t : this.polynomial)
			t.mult(scl);
		this.fill();
		return this;
	}

	/**
	 * Returns the product of the two polynomials.
	 * 
	 * @param p
	 * @return this
	 */
	public Polynomial mult(Polynomial p) {
		ArrayList<Monomial> monomials = new ArrayList<Monomial>();
		for (Monomial t : this.polynomial) {
			for (Monomial other : p.getPolynomial()) {
				monomials.add(
						new Monomial(t.getCoefficient() * other.getCoefficient(), t.getDegree() + other.getDegree()));
			}
		}
		this.polynomial = monomials;
		this.combine();
		this.reorder();
		this.fill();
		return this;
	}

	/**
	 * This function combines the monomials with the same degrees.
	 */
	public void combine() {
		for (int i = 0; i < this.polynomial.size() - 1; i++) {
			for (int j = i + 1; j < this.polynomial.size(); j++) {
				Monomial a = this.polynomial.get(i);
				Monomial b = this.polynomial.get(j);
				if (a.isCombinable(b)) {
					a.combine(b);
					this.polynomial.remove(j);
				}
			}
		}
	}

	/**
	 * This function sorts the polynomial by degree.
	 */
	public void reorder() {
		for (int i = 0; i < this.polynomial.size(); i++) {
			int degree = -Integer.MAX_VALUE;
			int index = -1;
			for (int j = i; j < this.polynomial.size(); j++) {
				Monomial t = this.polynomial.get(j);
				if (degree < t.getDegree()) {
					degree = t.getDegree();
					index = j;
				}
			}
			this.polynomial.add(i, this.polynomial.remove(index));
		}
	}

	/**
	 * A polynomial can almost always contain monomials with a coefficient of 0.
	 * Since the constructors do not need sorted and complete monomials, the missing
	 * monomials can be inserted, so that the polynomial can be written completely.
	 */
	public void fill() {
		for (int i = this.getDegree(); i >= 0; i--) {
			boolean found = false;
			for (Monomial t : this.polynomial)
				if (found = i == t.getDegree())
					break;
			if (!found)
				this.polynomial.add(this.getDegree() - i, new Monomial(0, i));
		}
	}

	/**
	 * @return a new derived polynomial
	 */
	public Polynomial getDerivation() {
		Polynomial p = new Polynomial();
		for (Monomial t : this.polynomial)
			p.add(t.getDerivation());
		return p;
	}

	/**
	 * Returns the degree of the polynomial. If the polynomial is empty or has no
	 * monomials, then 0 is returned.
	 * 
	 * @return degree or 0
	 */
	public int getDegree() {
		int degree = 0;
		for (Monomial t : polynomial) {
			int d = t.getDegree();
			if (degree < d)
				degree = d;
		}
		return degree;
	}

	/**
	 * Prints the Polynomial into the PrintStream.
	 */
	public void print(PrintStream stream) {
		stream.println(getFormular());
	}

	// https://de.wikipedia.org/wiki/Polynomdivision#Algorithmus
	// TODO: Implementation von Polynomdivision
	/**
	 * Returns an independent copy of the calling polynomial object.
	 * 
	 * @return independent Polynomial object
	 */
	public Polynomial copy() {
		return new Polynomial(this.polynomial);
	}

	/**
	 * Returns the string of the polynomial.
	 * 
	 * @return Formular of Polynomial
	 */
	public String getFormular() {
		String s = "";
		for (int i = 0; i < this.polynomial.size() - 1; i++) {
			s += this.polynomial.get(i).toString();
			s += this.polynomial.get(i + 1).getCoefficient() < 0 ? "" : "+";
		}
		s += this.polynomial.get(this.polynomial.size() - 1).toString();
		return s;
	}

	/**
	 * Returns the string of the formatted polynomial.
	 * 
	 * @return formatted Formular of Polynomial
	 */
	public String getFormularFormatted() {
		String s = "";
		for (int i = 0; i < this.polynomial.size() - 1; i++) {
			s += this.polynomial.get(i).toStringFormatted();
			s += this.polynomial.get(i + 1).getCoefficient() < 0 ? "" : "+";
		}
		if (this.polynomial.size() > 0)
			s += this.polynomial.get(this.polynomial.size() - 1).toStringFormatted();
		return s;

	}

	/**
	 * With the Newton's method one can get closer and closer to the zero point by
	 * approximation, provided one has an initial guess.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Newton%27s_method">Wikipedia
	 *      Newton Method</a>
	 * @param guess
	 * @param n
	 * @return Approximation of x for the root
	 */

	public double getRoot(double guess, int n) {
		double new_guess = guess - (this.getY(guess) / this.getDerivation().getY(guess));
		if (n == 0)
			return new_guess;
		return getRoot(new_guess, n - 1);
	}

	/**
	 * Calculate y for a given x.
	 * 
	 * @param x
	 * @return calculated y
	 */
	public double getY(double x) {
		double sum = 0;
		for (int i = 0; i < this.polynomial.size(); i++) {
			Monomial t = this.getPolynomial().get(i);
			sum += t.getCoefficient() * Math.pow(x, t.getDegree());
		}
		return sum;
	}

	// ----------------------HELPER--------------

	/**
	 * Returns the list of monomials representing the polynomial.
	 * 
	 * @return Monomial ArrayList
	 */
	public ArrayList<Monomial> getPolynomial() {
		return this.polynomial;
	}

}
