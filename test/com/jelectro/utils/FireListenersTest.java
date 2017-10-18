package com.jelectro.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class FireListenersTest {

	public static final Logger log = Logger.getLogger(FireListenersTest.class);

	
	
	@Parameterized.Parameters
	public static List<Object[]> data() {
		return Arrays.asList(new Object[10000][0]);
	}

	
	
	@Test
	public void testSynchronization() throws InterruptedException {

			
		LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

		FireListeners<Consumer<String>> fireListeners = new FireListeners<>();

		final int maxLoop = 10;
		
		AtomicInteger aInt = new AtomicInteger(0);
		AtomicInteger checkInt = new AtomicInteger(0);
		

		Runnable r1 = () -> {
			int i = 0;
			while (i++ < maxLoop) {
				try {
					String message = messageQueue.take();
					// Assert.assertEquals(1, fireListeners.getListeners().size());
					checkInt.incrementAndGet();
					fireListeners.getFireProxy().accept(message);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			//Assert.assertEquals("listener not registered", 1, fireListeners.getListeners().size());
		};

		

		Consumer<String> messageListener = s -> {
			aInt.incrementAndGet();
		};

		Thread t1 = new Thread(r1); t1.start();

		fireListeners.addListener(messageListener);

		int i = 0;

		while (i++ < maxLoop) {
			messageQueue.put("Message " + i);
		}
		
		t1.join();
		
		Assert.assertEquals("Check int not ok ",maxLoop, checkInt.get());
		Assert.assertEquals("A int not ok ", maxLoop, aInt.get());

	}
	
	
	/*@Test
	public void testWeakStatusOfAListener() throws InterruptedException {
		LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

		FireListeners<Consumer<String>> fireListeners = new FireListeners<>();

		final int maxLoop = 10;
		
		AtomicInteger aInt = new AtomicInteger(0);
		AtomicInteger checkInt = new AtomicInteger(0);
		

		Runnable r1 = () -> {
			int i = 0;
			while (i++ < maxLoop) {
				try {
					String message = messageQueue.take();
					// Assert.assertEquals(1, fireListeners.getListeners().size());
					checkInt.incrementAndGet();
					fireListeners.getFireProxy().accept(message);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			Assert.assertEquals("listener not registered", 1, fireListeners.getListeners().size());
		};

		

		Consumer<String> messageListener = s -> {
			aInt.incrementAndGet();
		};

		Thread t1 = new Thread(r1); t1.start();

		fireListeners.addListener(messageListener);

		int i = 0;

		while (i++ < maxLoop) {
			messageQueue.put("Message " + i);
			if (i==5) {
				messageListener = null;
				// free the variable and it's reference
				System.gc();
			}
		}
		
		t1.join();
		
		Assert.assertEquals("Check int not ok ",maxLoop, checkInt.get());
		Assert.assertTrue("A int not ok " + aInt.get(), 5 >= aInt.get());

	}*/

}
