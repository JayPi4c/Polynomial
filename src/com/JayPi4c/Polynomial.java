package com.JayPi4c;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Die Klasse Polynomial bietet die Möglichkeit mit einem Polynom zu rechnen und
 * arbeiten, was die Arbeit mit einem Polynom deutlich vereinfachen kann.
 * 
 * @author JayPi4c
 *
 */
public class Polynomial {

	/**
	 * Ein Polynom besteht aus einzelnen Teilen und diese werden in diesem Array
	 * gespeichert.
	 */
	private ArrayList<Term> polynomial;

	/**
	 * Erstelle ein leeres Polynomobjekt
	 */
	public Polynomial() {
		polynomial = new ArrayList<Term>();
	}

	/**
	 * Erstelle einen Polynom aus einem Array aus Termen
	 * 
	 * @param terms
	 */
	public Polynomial(Term... terms) {
		polynomial = new ArrayList<Term>(Arrays.asList(terms));
		this.fill();
	}

	/**
	 * Erstelle einen Polynom aus einer ArrayList aus Termen
	 * 
	 * @param terms
	 */
	public Polynomial(ArrayList<Term> terms) {
		this.polynomial = new ArrayList<Term>();
		for (Term t : terms)
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
		this.polynomial = new ArrayList<Term>();
		for (int i = 0; i < coefficients.length; i++)
			this.polynomial.add(new Term(coefficients[i], degrees[i]));
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
	 * Addiert den Term zu dem Polynom
	 * 
	 * @param t
	 * @return this after math is done
	 */
	public Polynomial add(Term t) {
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
		for (Term t : this.polynomial)
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

	/**
	 * Diese Funktion kombiniert die Terme mit den gleichen Graden.
	 */
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

	/**
	 * Diese Funktion sortiert den Polynom anhand des Grades.
	 */
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

	/**
	 * Ein Polynom kann quasi immer auch Terms beinhalten, die den Koeffizienten 0
	 * haben. Da die Konstruktoren keine sortierten und vollst&aumlndigen Terme
	 * brauchen können hiermit die fehlenden Terme eingef&uumlgt werden, sodass der
	 * Polynom auch vollst&aumlndig geschrieben werden kann.
	 */
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

	/**
	 * 
	 * @return a new derivated Polynomial
	 */
	public Polynomial getDerivation() {
		Polynomial p = new Polynomial();
		for (Term t : this.polynomial)
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
	 * Terms, dann wird -1 zur&uumlckgegeben.
	 * 
	 * @return degree or -1
	 */
	public int getDegree() {
		int degree = -1;
		for (Term t : polynomial) {
			int d = t.getDegree();
			if (degree < d)
				degree = d;
		}
		return degree;
	}

	/**
	 * Schreibt den Polynom in die Konsole
	 */
	public void print() {
		System.out.println(getFormular());
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
			s += this.polynomial.get(i).print();
			s += this.polynomial.get(i + 1).getCoefficient() < 0 ? "" : "+";
		}
		s += this.polynomial.get(this.polynomial.size() - 1).print();
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
			s += this.polynomial.get(i).printFormatted();
			s += this.polynomial.get(i + 1).getCoefficient() < 0 ? "" : "+";
		}
		if (this.polynomial.size() > 0)
			s += this.polynomial.get(this.polynomial.size() - 1).printFormatted();
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
			Term t = this.getPolynomial().get(i);
			sum += t.getCoefficient() * Math.pow(x, t.getDegree());
		}
		return sum;
	}

	// ----------------------HELPER--------------
	/**
	 * Gibt die List der Terms, die den Polynom darstellen zur&uumlck
	 * 
	 * @return Term ArrayList
	 */
	public ArrayList<Term> getPolynomial() {
		return this.polynomial;
	}

	/**
	 * 
	 * @author JayPi4c
	 *
	 */
	public class Term {

		private int degree;
		private double coefficient;

		/**
		 * Erstelle einen Term mit dem Koeffizienten und Grad
		 * 
		 * @param coefficient
		 * @param degree
		 */
		public Term(double coefficient, int degree) {
			this.coefficient = coefficient;
			this.degree = degree;
		}

		/**
		 * Erstelle einen Term mit dem festgelegten Koeffizienten und dem Grad 0
		 * 
		 * @param coefficient
		 */
		public Term(double coefficient) {
			this.coefficient = coefficient;
			this.degree = 0;
		}

		/**
		 * Erstelle einen Term, mit dem festgelegten Grad und dem Koeffizienten 1
		 * 
		 * @param degree
		 */
		public Term(int degree) {
			this.degree = degree;
			this.coefficient = 1;
		}

		// ------------------FUNCTIONS-----------------
		/**
		 * 
		 * @param t
		 * @return true, if same degree
		 */
		public boolean isCombinable(Term t) {
			return this.degree == t.getDegree();
		}

		/**
		 * Wenn zwei Terme den gleichen Grad haben, dann können die Terme kombiniert
		 * werden.
		 * 
		 * @param t der Term, der mit dem aufrufenden kombiniert wird
		 * @return this
		 */
		public Term combine(Term t) {
			if (!this.isCombinable(t))
				throw new IllegalArgumentException("Diese Teile können nicht kombiniert werden!");
			this.coefficient += t.getCoefficient();
			return this;
		}

		/**
		 * Sofern der Grad > 0, kann der Term abgeleitet werden.
		 * 
		 * @return true, if term derivable
		 */
		public boolean isDerivable() {
			return this.degree > 0;
		}

		/**
		 * Leitet den Term ab, sofern dies überhaupt m&oumlglich ist.
		 * 
		 * @throws IllegalAccessException if Term is not derivable
		 * @return derivated Term
		 */
		public Term getDerivation() {
			if (!this.isDerivable())
				throw new IllegalArgumentException("Dieser Teil ist nicht ableitbar");
			return new Term(this.coefficient * this.degree, this.degree - 1);
		}

		/**
		 * Multipliziert den Term mit dem Skalar
		 * 
		 * @param scl
		 * @return this
		 */
		public Term mult(double scl) {
			this.coefficient *= scl;
			return this;
		}

		/**
		 * Gibt den Term als String zur&uumlck
		 * 
		 * @return String Term
		 */
		public String print() {
			return this.coefficient + (this.degree > 0 ? "x^" + this.degree : "");
		}

		/**
		 * Gibt den Term in formatierter Version zur&uumlck
		 * 
		 * @return formatted Term
		 */
		public String printFormatted() {
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.HALF_UP);
			return df.format(coefficient) + (this.degree > 0 ? "x^" + this.degree : "");
		}

		// ---------------------HELPER-----------------------------
		/**
		 * Gibt den Grad des aufrufenden Terms zur&uumlck
		 * 
		 * @return degree
		 */
		public int getDegree() {
			return this.degree;
		}

		/**
		 * Setzt den Grad des aufrufenden Terms
		 * 
		 * @param degree
		 */
		public void setDegree(int degree) {
			this.degree = degree;
		}

		/**
		 * Gibt den Koeffizienten des aufrufenden Terms zur&uumlck
		 * 
		 * @return coefficient
		 */
		public double getCoefficient() {
			return this.coefficient;
		}

		/**
		 * Setzt den Koeffizienten des aufrufenden Terms
		 * 
		 * @param coefficient
		 */
		public void setCoefficient(double coefficient) {
			this.coefficient = coefficient;
		}

		/**
		 * 
		 * @return eine unabhängige Kopie des aufrufenden Terms
		 */
		public Term copy() {
			return new Term(this.coefficient, this.degree);
		}

	}
}
