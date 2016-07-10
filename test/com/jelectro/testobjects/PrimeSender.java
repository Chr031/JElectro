package com.jelectro.testobjects;

import com.jelectro.JElectroCallback;

public interface PrimeSender extends JElectroCallback {
	void onPrime(int primeNumber);
}