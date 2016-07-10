package com.jelectro.testobjects;

import java.util.ArrayList;
import java.util.List;

public class PrimeImpl implements Prime {

	@Override
	public void getPrimeNumber(int limit, PrimeSender callback) {
		List<Integer> primeNumbers = new ArrayList<Integer>();
		for (int i = 2; i <= limit; i++) {
			double sqrt = Math.sqrt(i);
			boolean isPrime = true;
			for (int prime : primeNumbers) {
				if (prime > sqrt)
					break;
				if (i % prime == 0) {
					isPrime = false;
					break;
				}
			}
			if (isPrime) {
				callback.onPrime(i);
				primeNumbers.add(i);
			}

		}

	}
}