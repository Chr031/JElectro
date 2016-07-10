package com.jelectro.testobjects;

import com.jelectro.exception.JElectroException;

public interface Calc {
	int add(int... ints) throws JElectroException;

	double divide(double a, double b) throws JElectroException;

}