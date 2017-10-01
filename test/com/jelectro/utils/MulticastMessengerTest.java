package com.jelectro.utils;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jelectro.JElectro;
import com.jelectro.utils.MulticastMessenger.MulticastResponseListener;

public class MulticastMessengerTest {

	
	@BeforeClass 
	public static void init() {
		
		
		JElectro.setDebugMode(true);
	}
	
	
	@Test
	public void test() throws IOException, InterruptedException {
		MulticastMessenger<HelloMulticast> mm1 = new MulticastMessenger<HelloMulticast>(1524);
		MulticastMessenger<HelloMulticast> mm2 = new MulticastMessenger<HelloMulticast>(1524);
		MulticastMessenger<HelloMulticast> mm3 = new MulticastMessenger<HelloMulticast>(1524);
		
		final ArrayBlockingQueue<HelloMulticast> queue = new ArrayBlockingQueue<HelloMulticast>(100);
		
		MulticastResponseListener<HelloMulticast> mrl = new MulticastResponseListener<HelloMulticast>() {

			@Override
			public void onResponse(HelloMulticast messageReceived) {
				try {
					queue.put(messageReceived);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		};
		mm2.addMulticastResponseListener(mrl);
		mm3.addMulticastResponseListener(mrl);
		
		
		HelloMulticast h = new HelloMulticast(125, "Hello");
		mm1.send(h);
		
		HelloMulticast h1 = queue.take();		
		Assert.assertEquals(h.getHello(), h1.getHello());
		Assert.assertEquals(h.getPort(), h1.getPort());
		HelloMulticast h2 = queue.take();	
		Assert.assertEquals(h.getHello(), h2.getHello());
		
		
		h = new HelloMulticast(15444, "45677");
		mm1.send(h);
		
		 h1 = queue.take();		
		Assert.assertEquals(h.getHello(), h1.getHello());
		Assert.assertEquals(h.getPort(), h1.getPort());
		 h2 = queue.take();	
		Assert.assertEquals(h.getHello(), h2.getHello());
		
		mm1.close();
		mm2.close();
		mm3.close();
	}

}
