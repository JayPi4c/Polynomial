package com.JayPi4c;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Polynomial {

	ArrayList<Term> polynomial;

	public Polynomial() {
		polynomial = new ArrayList<Term>();
		this.fill();
	}

	public Polynomial(Term... terms) {
		polynomial = new ArrayList<Term>(Arrays.asList(terms));
		this.fill();
	}

	public Polynomial(ArrayList<Term> terms) {
		this.polynomial = new ArrayList<Term>();
		for (Term t : terms)
			this.polynomial.add(t.copy());
		this.combine();
		this.fill();
		this.reorder();

	}

	public Polynomial(double[] coefficients, int[] degrees) {
		if (coefficients.length != degrees.length)
			throw new IllegalArgumentException("Die Anzahl der Koeffizienten und Grade ist nicht identisch!");
		this.polynomial = new ArrayList<Term>();
		for (int i = 0; i < coefficients.length; i++)
			this.polynomial.add(new Term(coefficients[i], degrees[i]));
		this.fill();
	}

	public Polynomial add(Polynomial p) {
		this.polynomial.addAll(p.getPolynomial());
		this.combine();
		this.reorder();
		this.fill();
		return this;
	}

	public Polynomial add(Term t) {
		this.polynomial.add(t);
		this.combine();
		this.reorder();
		this.fill();
		return this;
	}

	public Polynomial mult(double scl) {
		for (Term t : this.polynomial)
			t.mult(scl);
		this.fill();
		return this;
	}

	public Polynomial mult(Polynomial p) {
		ArrayList<Term> terms = new ArrayList<Term>();
		for (Term t : this.polynomial) {
			for (Term other : p.getPolynomial()) {
				terms.add(new Term(t.getCoefficient() * other.getCoefficient(), t.getDegree() + other.getDegree()));
			}
		}
		this.polynomial = terms;
		this.combine();
		this.reorder();
		this.fill();
		return this;
	}

	public void combine() {
		for (int i = 0; i < this.polynomial.size() - 1; i++) {
			for (int j = i + 1; j < this.polynomial.size(); j++) {
				Term a = this.polynomial.get(i);
				Term b = this.polynomial.get(j);
				if (a.isCombinable(b)) {
					a.combine(b);
					this.polynomial.remove(j);
				}
			}
		}
	}

	public void reorder() {
		for (int i = 0; i < this.polynomial.size(); i++) {
			int degree = -1;
			int index = -1;
			for (int j = i; j < this.polynomial.size(); j++) {
				Term t = this.polynomial.get(j);
				if (degree < t.getDegree()) {
					degree = t.getDegree();
					index = j;
				}
			}
			this.polynomial.add(i, this.polynomial.remove(index));
		}
	}

	public void fill() {
		for (int i = this.getDegree(); i >= 0; i--) {
			boolean found = false;
			for (Term t : this.polynomial)
				if (found = i == t.getDegree())
					break;
			if (!found)
				this.polynomial.add(this.getDegree() - i, new Term(0, i));
		}
	}

	public Polynomial getDerivation() {
		Polynomial p = new Polynomial();
		for (Term t : this.polynomial)
			try {
				p.add(t.getDerivation());
			} catch (IllegalArgumentException e) {
				// e.printStackTrace();
				// System.out.println("Da ist wohl ein Fehler passiert");
			}
		return p;
	}

	public int getDegree() {
		int degree = -1;
		for (Term t : polynomial) {
			int d = t.getDegree();
			if (degree < d)
				degree = d;
		}
		return degree;
	}

	public void print() {
		System.out.println(getFormular());
	}

	// https://de.wikipedia.org/wiki/Polynomdivision#Algorithmus
	// TODO: Implementation von Polynomdivision

	public Polynomial copy() {
		return new Polynomial(this.polynomial);
	}

	public String getFormular() {
		String s = "";
		for (int i = 0; i < this.polynomial.size() - 1; i++) {
			s += this.polynomial.get(i).print();
			s += this.polynomial.get(i + 1).getCoefficient() < 0 ? "" : "+";
		}
		s += this.polynomial.get(this.polynomial.size() - 1).print();
		return s;
	}

	public String getFormularFormatted() {
		String s = "";
		for (int i = 0; i < this.polynomial.size() - 1; i++) {
			s += this.polynomial.get(i).printFormatted();
			s += this.polynomial.get(i + 1).getCoefficient() < 0 ? "" : "+";
		}
		if (this.polynomial.size() > 0)
			s += this.polynomial.get(this.polynomial.size() - 1).printFormatted();
		return s;

	}

	// https://en.wikipedia.org/wiki/Newton%27s_method
	public double getRoot(double guess, int n) {
		double new_guess = guess - (this.getY(guess) / this.getDerivation().getY(guess));
		if (n == 0)
			return new_guess;
		return getRoot(new_guess, n - 1);
	}

	public double getY(double x) {
		double sum = 0;
		for (int i = 0; i < this.polynomial.size(); i++) {
			Term t = this.getPolynomial().get(i);
			sum += t.getCoefficient() * Math.pow(x, t.getDegree());
		}
		return sum;
	}

	// ----------------------HELPER--------------

	public ArrayList<Term> getPolynomial() {
		return this.polynomial;
	}

	public class Term {

		private int degree;
		private double coefficient;

		public Term(double coefficient, int degree) {
			this.coefficient = coefficient;
			this.degree = degree;
		}

		public Term(double coefficient) {
			this.coefficient = coefficient;
			this.degree = 0;
		}

		public Term(int degree) {
			this.degree = degree;
			this.coefficient = 1;
		}

		// ------------------FUNCTIONS-----------------

		public boolean isCombinable(Term t) {
			return this.degree == t.getDegree();
		}

		public Term combine(Term t) {
			if (!this.isCombinable(t))
				throw new IllegalArgumentException("Diese Teile können nicht kombiniert werden!");
			this.coefficient += t.getCoefficient();
			return this;
		}

		public boolean isDerivable() {
			return this.degree > 0;
		}

		public Term getDerivation() {
			if (!this.isDerivable())
				throw new IllegalArgumentException("Dieser Teil ist nicht ableitbar");
			return new Term(this.coefficient * this.degree, this.degree - 1);
		}

		public Term mult(double scl) {
			this.coefficient *= scl;
			return this;
		}

		public String print() {
			return this.coefficient + (this.degree > 0 ? "x^" + this.degree : "");
		}

		public String printFormatted() {
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.HALF_UP);
			return df.format(coefficient) + (this.degree > 0 ? "x^" + this.degree : "");
		}

		// ---------------------HELPER-----------------------------

		public int getDegree() {
			return this.degree;
		}

		public void setDegree(int degree) {
			this.degree = degree;
		}

		public double getCoefficient() {
			return this.coefficient;
		}

		public void setCoefficient(double coefficient) {
			this.coefficient = coefficient;
		}

		public Term copy() {
			return new Term(this.coefficient, this.degree);
		}

	}
}
