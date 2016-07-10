package com.jelectro;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.jelectro.JElectro;
import com.jelectro.connector.multicast.MulticastConnector.ConnectionState;
import com.jelectro.exception.JElectroException;
import com.jelectro.stubs.StubSet;
import com.jelectro.testobjects.Calc;
import com.jelectro.testobjects.CalcImpl;

import tools.logger.Logger;

@RunWith(Parameterized.class)
public class JElectroMulticastTest {

	private static final Logger log = Logger.getLogger(JElectroMulticastTest.class);

	@Parameterized.Parameters
	public static List<Object[]> data() {
		return Arrays.asList(new Object[5][0]);
	}

	@BeforeClass
	public static void initTest() {
		Logger.setBaseConfiguration();
		JElectro.setDebugMode(true);
	}

	@Test
	public void testMulticastConnection2Instances() throws IOException, JElectroException, InterruptedException {

		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");

		try {

			ConnectionState cs1 = j1.startLanDiscovery(12354, 12001);
			ConnectionState cs2 = j2.startLanDiscovery(12354, 12002);

			Assert.assertTrue(cs1.isConnected());
			Assert.assertTrue(cs2.isConnected());

			Calc c = new CalcImpl();
			j1.bind("calc1", c);

			Calc cr = j2.lookupUnique("calc1", Calc.class);

			int i = cr.add(123, 456, 789);

			log.info(i);

			Assert.assertEquals(123 + 456 + 789, i);

		} finally {
			j1.close();
			j2.close();
		}
	}

	@Test
	public void testMulticastConnectionXInstances() throws IOException, JElectroException, InterruptedException {

		int size = 20;
		JElectro[] j = new JElectro[size];
		for (int i = 0; i < size; i++) {
			j[i] = new JElectro("" + i);
		}
		try {
			ConnectionState[] states = new ConnectionState[size];
			for (int i = 0; i < size; i++) {
				states[i] = j[i].startLanDiscovery(12345, 12000 + i);
			}
			// publish the stubs 
			for (int i = 0; i < size; i++) {				
				Calc c = new CalcImpl(i);
				j[i].bind("calc" + i, c);
			}

			// checks that all the node are globally connected :
			StubSet<Calc> calcX;
			do {
				calcX = j[0].lookup("calc[0-9]+", Calc.class);
				calcX.waitFor(j.length-1);
			} while (calcX.size()<j.length-1);
			
			
			for (int i = 0; i < size; i++) {
				for (int k = 0; k < size; k++) {
					if (k == i)
						continue;
					StubSet<Calc> calc = j[i].lookup("calc" + k, Calc.class);
					calc.waitFor(1);
					
					for (Calc c : calc) {
						int sum = c.add(1, 2, 3);
						log.info(i + " " + k + " : " + sum + " (" + calc.size() + ")");
						Assert.assertEquals(6 + k, sum);
					}

				}

			}

		} finally {
			for (int i = 0; i < size; i++) {
				j[i].close();
			}
		}
	}

}
