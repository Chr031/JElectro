package com.jelectro;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jelectro.exception.JElectroException;
import com.jelectro.testobjects.Calc;
import com.jelectro.testobjects.CalcImpl;
import com.jelectro.utils.InstanceCache;

public class JElectroStressTest {

	private static final Logger log = Logger.getLogger(JElectroStressTest.class);
	private static int instanceNbr;
	private static int nbrOfCalls;

	@BeforeClass
	public static void initTest() {
		//Logger.setBaseConfiguration();
		instanceNbr = 100;
		nbrOfCalls = 10000;
	}

	@Test
	public void testStressExecute_2Instances() throws IOException, JElectroException, InterruptedException {

		JElectro j1 = new JElectro("1");
		JElectro j2 = new JElectro("2");

		try {
			j1.listenTo(12001);
			j2.connectTo("localhost", 12001);

			CalcImpl calc = new CalcImpl();
			j1.bind("calc", calc);

			Calc rCalc = j2.lookupUnique("calc", Calc.class);

			int sum = 0;

			for (int i = 0; i < nbrOfCalls; i++) {
				sum = rCalc.add(sum, i);
				// System.out.println(i + " : " + (sum));
			}
			System.out.println(sum);

			Assert.assertEquals(nbrOfCalls * (nbrOfCalls - 1) / 2, sum);

		} finally {
			j1.close();
			j2.close();
		}

	}

	@Test
	public void testStressExecute_4Instances() throws IOException, JElectroException, InterruptedException {

		JElectro j1 = new JElectro("1");
		JElectro j2 = new JElectro("2");
		JElectro j3 = new JElectro("3");
		JElectro j4 = new JElectro("4");

		try {
			j2.listenTo(12001);
			j1.connectTo("localhost", 12001);
			j3.connectTo("localhost", 12001);
			j4.listenTo(12002);
			j3.connectTo("localhost", 12002);

			CalcImpl calc = new CalcImpl();
			j1.bind("calc", calc);

			Calc rCalc = j4.lookupUnique("calc", Calc.class);

			int sum = 0;

			for (int i = 0; i < nbrOfCalls; i++) {
				sum = rCalc.add(sum, i);
				// System.out.println(i + " : " + (sum));
			}
			System.out.println(sum);

			Assert.assertEquals(nbrOfCalls * (nbrOfCalls - 1) / 2, sum);

		} finally {
			j1.close();
			j2.close();
			j3.close();
			j4.close();
		}

	}

	@Test
	public void stressTest_100Nodes_InStar() throws IOException, JElectroException {

		JElectro.setDebugMode(true);
		
		JElectro[] nodes = new JElectro[instanceNbr];
		try {
			for (int i = 0; i < instanceNbr; i++) {
				nodes[i] = new JElectro("" + i);
			}

			nodes[0].listenTo(12001);
			for (int i = 1; i < instanceNbr; i++) {
				nodes[i].connectTo("localhost", 12001);
			}

			Calc c = new CalcImpl();

			nodes[0].bind("calc", c);

			for (int i = 1; i < instanceNbr; i++) {
				Calc stub = nodes[i].lookupUnique("calc", Calc.class);
				Assert.assertEquals(6, stub.add(1, 2, 3));
			}

		} finally {
			for (JElectro node : nodes) {
				node.close();
			}
		}
	}

	@Test
	public void stressTest_100Nodes_Inline() throws IOException, JElectroException, InterruptedException {

		JElectro[] nodes = new JElectro[instanceNbr];
		try {
			for (int i = 0; i < instanceNbr; i++) {
				nodes[i] = new JElectro("" + i);
			}

			for (int i = 1; i < instanceNbr; i++) {
				nodes[i - 1].listenTo(12000 + i);
				nodes[i].connectTo("localhost", 12000 + i);
			}

			Calc c = new CalcImpl();

			nodes[0].bind("calc", c);

			for (int i = instanceNbr - 1; i > 0; i--) {
				Calc stub = nodes[i].lookupUnique("calc", Calc.class);
				Assert.assertEquals(82, stub.add(1, 2, 3, 4, 5, 6 ,7 ,8,9,9,9,9,10));
				// Thread.sleep(1000);
			}

			InstanceCache.clear();

		} finally {
			for (JElectro node : nodes) {
				node.close();
			}
		}

	}
}
