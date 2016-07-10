package com.jelectro;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.BindException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jelectro.exception.JElectroException;
import com.jelectro.exception.TimeoutException;
import com.jelectro.stubs.IStub;
import com.jelectro.testobjects.Waiter;
import com.jelectro.testobjects.WaiterImpl;

import tools.logger.Logger;

public class JelectroGeneralTest {

	@BeforeClass
	public static void initTest() {
		Logger.setBaseConfiguration();
	}

	@Before
	public void initMode() {
		JElectro.setDebugMode(true);
		JElectro.setInfoMode(true);

	}

	@Test
	public void testTimeoutStubException() throws Throwable {
		JElectro j1 = new JElectro("1");
		JElectro j2 = new JElectro("2");

		try {
			j2.listenTo(12001);
			j1.connectTo("localhost", 12001);

			Waiter waiter = new WaiterImpl();

			j1.bind("waiter", waiter);
			Waiter wStub = j2.lookupUnique("waiter", Waiter.class);

			long time = wStub.waitLoop(100);
			System.out.print("Time loop for 500 " + time);

			try {
				wStub.waitLoop(1100);
				Assert.fail("No time out triggered");
			} catch (Throwable t) {
				if (t instanceof UndeclaredThrowableException) {
					if (((UndeclaredThrowableException) t).getUndeclaredThrowable() instanceof TimeoutException)
						t = ((UndeclaredThrowableException) t).getUndeclaredThrowable();
				}

				if (t instanceof TimeoutException) {
					System.out.println("Time out triggered : " + t.getMessage());
				} else {
					Assert.fail("No time out triggered");
					throw t;
				}
			}

			((IStub) wStub).setTimeout(500);

			time = wStub.waitLoop(100);
			System.out.print("Time loop for 1500 " + time);

			try {
				wStub.waitLoop(2500);
				Assert.fail("No time out triggered");
			} catch (Throwable t) {
				if (t instanceof UndeclaredThrowableException) {
					if (((UndeclaredThrowableException) t).getUndeclaredThrowable() instanceof TimeoutException)
						t = ((UndeclaredThrowableException) t).getUndeclaredThrowable();
				}

				if (t instanceof TimeoutException) {
					System.out.println("Time out triggered " + t.getMessage());
				} else {
					Assert.fail("No time out triggered");
					throw t;
				}
			}

		} finally {
			j1.close();
			j2.close();

		}

	}

	@Test(expected = BindException.class)
	public void testAddressAlreadyBind() throws IOException, JElectroException {

		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");

		try {

			j1.listenTo(12001);
			j2.listenTo(12001);

		} finally {
			j1.close();
			j2.close();
		}

	}
	
	interface Calc { int sum(int a, int b);}
	
	@Test
	public void TestInnerClassBinding() throws IOException, JElectroException {
		
		JElectro j1 = new JElectro("Node-1");
		// j1 will open the port 12001 and listen to any incomming connection
		j1.listenTo(12001);
				
		Calc c = new Calc() {public int sum(int a, int b) {return a+b;}};
		
		// exposing/exporting/binding the instance c of Calc
		j1.bind("calc",c);
		
		
		
		JElectro j2 = new JElectro("Node-2");
		j2.connectTo("localhost", 12001);
		
		Calc c2 = j2.lookupUnique("calc",Calc.class);
		int sum = c2.sum(12,9);
		
		Assert.assertEquals(21, sum);
		
		j1.close();
		j2.close();
	}
	

}
