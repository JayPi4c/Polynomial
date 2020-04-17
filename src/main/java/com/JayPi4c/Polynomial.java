package com.JayPi4c;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Die Klasse Polynomial bietet die Möglichkeit mit einem Polynom zu rechnen
 * und arbeiten, was die Arbeit mit einem Polynom deutlich vereinfachen kann.
 * 
 * @author JayPi4c
 *
 */
public class Polynomial {

	/**
	 * Ein Polynom besteht aus einzelnen Teilen und diese werden in diesem Array
	 * gespeichert.
	 */
	private ArrayList<Monomial> polynomial;

	/**
	 * Erstelle ein leeres Polynomobjekt
	 */
	public Polynomial() {
		polynomial = new ArrayList<Monomial>();
	}

	/**
	 * Erstelle einen Polynom aus einem Array aus Monomialen
	 * 
	 * @param monomials
	 */
	public Polynomial(Monomial... monomials) {
		polynomial = new ArrayList<Monomial>(Arrays.asList(monomials));
		this.fill();
	}

	/**
	 * Erstelle einen Polynom aus einer ArrayList aus Monomialen
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
	 * Erstelle einen Polynom mit den Graden und Koeffizienten
	 * 
	 * @throws IllegalArgumentException if coefficients and degrees are not of same
	 *                                  length
	 * @param coefficients
	 * @param degrees
	 */
	public Polynomial(double[] coefficients, int[] degrees) {
		if (coefficients.length != degrees.length)
			throw new IllegalArgumentException("Die Anzahl der Koeffizienten und Grade ist nicht identisch!");
		this.polynomial = new ArrayList<Monomial>();
		for (int i = 0; i < coefficients.length; i++)
			this.polynomial.add(new Monomial(coefficients[i], degrees[i]));
		this.fill();
	}

	/**
	 * Addiert den Polynom zu dem Polynom
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
	 * Addiert den Monomial zu dem Polynom
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
	 * Gibt das Produkt des aufrufenden Polynoms mit dem entsprechenden Skalar
	 * zur&uumlck
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
	 * Gibt das Produkt der beiden Polynome zur&uumlck
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
	 * Diese Funktion kombiniert die Monomiale mit den gleichen Graden.
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
	 * Diese Funktion sortiert den Polynom anhand des Grades.
	 */
	public void reorder() {
		for (int i = 0; i < this.polynomial.size(); i++) {
			int degree = -1;
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
	 * Ein Polynom kann quasi immer auch Monomials beinhalten, die den Koeffizienten
	 * 0 haben. Da die Konstruktoren keine sortierten und vollst&aumlndigen
	 * Monomiale brauchen können hiermit die fehlenden Monomiale eingef&uumlgt
	 * werden, sodass der Polynom auch vollst&aumlndig geschrieben werden kann.
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
	 * 
	 * @return a new derivated Polynomial
	 */
	public Polynomial getDerivation() {
		Polynomial p = new Polynomial();
		for (Monomial t : this.polynomial)
			try {
				p.add(t.getDerivation());
			} catch (IllegalArgumentException ex) {
				// Wenn hier eine Exception auftaucht ist das kein grosses Problem. Dennoch kann
				// es in manchen Situationen gut sein, die Exception zu werfen.
			}
		return p;
	}

	/**
	 * Gibt den Grad des Polynoms zur&uumlck. Ist der Polynom leer, bzw. hat keine
	 * Monomials, dann wird -1 zur&uumlckgegeben.
	 * 
	 * @return degree or -1
	 */
	public int getDegree() {
		int degree = -1;
		for (Monomial t : polynomial) {
			int d = t.getDegree();
			if (degree < d)
				degree = d;
		}
		return degree;
	}

	/**
	 * Schreibt den Polynom in die Konsole
	 */
	public void print(PrintStream stream) {
		stream.println(getFormular());
	}

	// https://de.wikipedia.org/wiki/Polynomdivision#Algorithmus
	// TODO: Implementation von Polynomdivision
	/**
	 * Gibt eine unabhägige Kopie des aufrufenden Polynomobjekts zur&uumlck
	 * 
	 * @return independent Polynomial object
	 */
	public Polynomial copy() {
		return new Polynomial(this.polynomial);
	}

	/**
	 * Gibt den String des Polynoms zur&uumlck
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
	 * Gibt den String des formatierten Polynoms zur&uumlck
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
	 * Durch die Newtonsche Methode kann man durch Annäherung immer genauer an die
	 * Nullstelle kommen. Sofern man einen anfänglichen guess hat
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
	 * Berechne y für ein gegebenes x
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
	 * Gibt die List der Monomials, die den Polynom darstellen zur&uumlck
	 * 
	 * @return Monomial ArrayList
	 */
	public ArrayList<Monomial> getPolynomial() {
		return this.polynomial;
	}

}
