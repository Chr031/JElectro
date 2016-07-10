package com.jelectro.testobjects;

public class WaiterImpl implements Waiter{

	@Override
	public long waitLoop(long timeToWaitInMillis) throws InterruptedException {
		
		long start = System.currentTimeMillis();
		
		while (System.currentTimeMillis() < start + timeToWaitInMillis) {
			Thread.sleep(Math.min(timeToWaitInMillis/2,Math.max ( 0, start + timeToWaitInMillis -System.currentTimeMillis() )/2 ));
		}
		
		return System.currentTimeMillis() - start;
	}

}
