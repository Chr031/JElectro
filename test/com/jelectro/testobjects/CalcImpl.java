package com.jelectro.testobjects;

import com.jelectro.exception.JElectroException;

public class CalcImpl implements Calc {

	private final int base;

	public CalcImpl() {
		base = 0;
	}

	public CalcImpl(int base) {
		this.base = base;
	}

	@Override
	public int add(int... ints) {
		int sum = base;
		for (int a : ints) {
			sum += a;
		}
		return sum;
	}

	@Override
	public double divide(double a, double b) throws JElectroException {
		if (b == 0)
			throw new JElectroException("Divide by 0 !");
		return a / b;
	}
}