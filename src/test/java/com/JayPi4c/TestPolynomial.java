package com.JayPi4c;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TestPolynomial {

	@Test
	void testGetY() {
		Polynomial p = new Polynomial();
		// an empty polynomial is defined here as f(x)=0
		assertEquals(0, p.getY(5));

		// defining polynomial: f(x)=2x
		p.add(new Monomial(2, 1));
		assertEquals(0, p.getY(0));
		assertEquals(6, p.getY(3));

		// redefining polynomial: f(x)=2x+0.5x^3
		p.add(new Monomial(0.5, 3));
		assertEquals(0, p.getY(0));
		assertEquals(8, p.getY(2));
		assertEquals(-19.5, p.getY(-3));
	}

	@Test
	void testBasicArithmetic() {
		Polynomial p = new Polynomial(new Monomial(2));
		assertEquals(4, p.getY(2));
		p.add(new Polynomial(new Monomial(1)));
		assertEquals(12, p.getY(3));
		// redefining to f(x)=-x^2-x
		p.mult(-1);
		assertEquals(-12, p.getY(3));

		// multiplying by polynomial f(x)=2x^3-0.5x^2 => -2 x^5 - 1.5 x^4 + 0.5 x^3
		p.mult(new Polynomial(new double[] { 2, -0.5 }, new int[] { 3, 2 }));
		assertEquals(-84, p.getY(2));
		assertEquals(351, p.getY(-3));
		assertEquals(0, p.getY(0));

	}

	@Test
	void testGetRoot() {
		ArrayList<Monomial> monomials = new ArrayList<Monomial>();
		monomials.add(new Monomial(-1d));
		monomials.add(new Monomial(2));
		Polynomial p = new Polynomial(monomials);
		assertEquals(-1, p.getRoot(-2, 20), 0.001);
		assertEquals(1, p.getRoot(0.1, 20), 0.001);
	}

}
