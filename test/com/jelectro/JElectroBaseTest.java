package com.jelectro;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.jelectro.JElectro;
import com.jelectro.exception.JElectroException;
import com.jelectro.exception.PathNotAvailableException;
import com.jelectro.node.Node;
import com.jelectro.stubs.StubSet;
import com.jelectro.stubs.StubSetListener;
import com.jelectro.testobjects.Calc;
import com.jelectro.testobjects.CalcImpl;
import com.jelectro.testobjects.Prime;
import com.jelectro.testobjects.PrimeImpl;
import com.jelectro.testobjects.PrimeSender;

import tools.logger.Logger;

@RunWith(Parameterized.class)
public class JElectroBaseTest {

	private static final Logger log = Logger.getLogger(JElectroBaseTest.class);

	@Parameterized.Parameters
	public static List<Object[]> data() {
		return Arrays.asList(new Object[1][0]);
	}

	@BeforeClass
	public static void initTest() {
		Logger.setBaseConfiguration();
	}

	@Before
	public void initMode() {
		JElectro.setDebugMode(false);
		JElectro.setInfoMode(true);

	}

	/**
	 * This is the simplest test : Check that instances are well defined and
	 * launched. Check that stubs are well instantiated and exported. Check that
	 * stubs are accessible and executable.
	 * 
	 * @throws IOException
	 * @throws JElectroException
	 * @throws InterruptedException
	 */
	@Test
	public void testSingleExportAndExecute() throws IOException, JElectroException, InterruptedException {

		log.info("Start testSingleExportAndExecute");
		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");

		log.info("Instanciation successfull");
		try {
			j1.listenTo(12001);
			j2.connectTo("localhost", 12001);

			Assert.assertEquals(1, j1.getActiveConnections().size());
			Assert.assertEquals(1, j2.getActiveConnections().size());

			log.info("Connection successfull");

			Calc add = new CalcImpl();

			j1.bind("add", add);

			StubSet<Calc> addStubs = j2.lookup("add", Calc.class);
			addStubs.waitFor(1);
			Assert.assertEquals("exported stub is not visible", 1, addStubs.size());
			log.info("Export successfull");

			Calc addStub = addStubs.get(0);
			int sum = addStub.add(1, 2, 3, 4, 5, 6);

			log.info("Sum is : " + sum);
			Assert.assertEquals("Result is not the one expected ", 21, sum);

			log.info("Call successfull");
		} finally {
			j1.close();
			j2.close();
		}
		log.info("Close successfull");

	}

	@Test
	public void testSingleBindLookupAndExecute() throws IOException, JElectroException {
		log.info("Start testSingleExportAndExecute");
		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");

		log.info("Instanciation successfull");
		try {
			j1.listenTo(12001);
			j2.connectTo("localhost", 12001);

			Assert.assertEquals(1, j1.getActiveConnections().size());
			Assert.assertEquals(1, j2.getActiveConnections().size());
			log.info("Connection successfull");

			Calc add = new CalcImpl();

			j1.bind("add", add);
			StubSet<Calc> stubs = j2.lookup("add", Calc.class);
			stubs.waitFor(1);
			for (Calc c : stubs) {
				Assert.assertEquals(6, c.add(1, 2, 3));
			}

			log.info("Call successfull");
		} finally {
			j1.close();
			j2.close();
		}
		log.info("Close successfull");

	}

	@Test
	public void test3InstancesExportAndExecute() throws IOException, JElectroException, InterruptedException {
		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");
		JElectro j3 = new JElectro("c");

		log.info("Instanciation successfull");
		try {
			j2.listenTo(12001);
			j1.connectTo("localhost", 12001);
			j3.connectTo("localhost", 12001);

			Thread.yield();

			Assert.assertEquals(1, j1.getActiveConnections().size());
			Assert.assertEquals(2, j2.getActiveConnections().size());
			Assert.assertEquals(1, j3.getActiveConnections().size());

			log.info("Connection successfull");

			Calc addInstance = new CalcImpl();

			j1.bind("add", addInstance);

			StubSet<Calc> addStubs = j3.lookup("add", Calc.class);

			Calc addStub = addStubs.get(0);

			int sum = addStub.add(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

			log.info("Result is : " + sum);
			Assert.assertEquals(55, sum);
		} finally {
			j1.close();
			j2.close();
			j3.close();
		}
	}

	@Test
	public void test4InstancesInLine_ExportAndExecute() throws IOException, JElectroException, InterruptedException {
		JElectro j1 = new JElectro("1");
		JElectro j2 = new JElectro("2");
		JElectro j3 = new JElectro("3");
		JElectro j4 = new JElectro("4");

		log.info("Instanciation successfull");
		try {
			j2.listenTo(12001);
			j1.connectTo("localhost", 12001);
			j3.connectTo("localhost", 12001);
			j3.listenTo(12002);
			j4.connectTo("localhost", 12002);

			Assert.assertEquals(1, j1.getActiveConnections().size());
			Assert.assertEquals(2, j2.getActiveConnections().size());
			Assert.assertEquals(2, j3.getActiveConnections().size());
			Assert.assertEquals(1, j4.getActiveConnections().size());

			log.info("Connection successfull");

			Calc addInstance = new CalcImpl();

			j1.bind("add", addInstance);

			StubSet<Calc> addStubs = j4.lookup("add", Calc.class);

			Calc addStub = addStubs.get(0);

			int sum = addStub.add(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

			log.info("Result is : " + sum);
			Assert.assertEquals(55, sum);
		} finally {
			j1.close();
			j2.close();
			j3.close();
			j4.close();
		}
	}

	@Test
	public void testSingleExportAndExecuteWithCallback() throws IOException, JElectroException, InterruptedException {
		log.info("Start testSingleExportAndExecute");
		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");

		log.info("Instanciation successfull");
		try {
			j1.listenTo(12001);
			j2.connectTo("localhost", 12001);

			Assert.assertEquals(1, j1.getActiveConnections().size());
			Assert.assertEquals(1, j2.getActiveConnections().size());

			log.info("Connection successfull");

			Prime primeInstance = new PrimeImpl();
			j1.bind("prime", primeInstance);

			final LinkedBlockingQueue<Integer> lbQueue = new LinkedBlockingQueue<Integer>();

			Prime primeStub = j2.lookupUnique("prime", Prime.class);
			PrimeSender primeSenderCallBack = new PrimeSender() {

				@Override
				public void onPrime(int primeNumber) {
					log.info("On prime : " + primeNumber);
					lbQueue.add(primeNumber);

				}
			};

			primeStub.getPrimeNumber(12, primeSenderCallBack);
			Assert.assertEquals(2, lbQueue.poll(1, TimeUnit.SECONDS).intValue());
			Assert.assertEquals(3, lbQueue.poll(1, TimeUnit.SECONDS).intValue());
			Assert.assertEquals(5, lbQueue.poll(1, TimeUnit.SECONDS).intValue());
			Assert.assertEquals(7, lbQueue.poll(1, TimeUnit.SECONDS).intValue());
			Assert.assertEquals(11, lbQueue.poll(1, TimeUnit.SECONDS).intValue());

			Assert.assertEquals(0, lbQueue.size());
		} finally {
			j1.close();
			j2.close();
		}
	}

	@Test
	public void testMultipleInstanceExport() throws JElectroException, InterruptedException, IOException {
		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");

		try {

			j1.listenTo(12001);
			j2.connectTo("localhost", 12001);

			Calc add1 = new CalcImpl(10);
			Calc add2 = new CalcImpl(20);

			j1.bind("add1", add1);
			j2.bind("add2", add2);

			Calc add1Stub = j2.lookupUnique("add1", Calc.class);
			Calc add2Stub = j1.lookupUnique("add2", Calc.class);

			int sum1 = add1Stub.add(1, 2, 3);
			Assert.assertEquals(16, sum1);
			Assert.assertEquals(28, add2Stub.add(1, 2, 5));

		} finally {
			j1.close();
			j2.close();
		}
	}

	/**
	 * out of scope now!!<br>
	 * TODO re-implement it. See method {@link Node#lookup(String, Class)}
	 * 
	 * @throws JElectroException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	// @Test
	public void testSelfInstanceExport() throws JElectroException, InterruptedException, IOException {
		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");

		try {

			j1.listenTo(12001);
			j2.connectTo("localhost", 12001);

			Calc add1 = new CalcImpl(10);
			Calc add2 = new CalcImpl(20);

			j1.bind("add1", add1);
			j2.bind("add2", add2);

			Calc add1Stub = j1.lookupUnique("add1", Calc.class);
			Calc add2Stub = j2.lookupUnique("add2", Calc.class);

			int sum1 = add1Stub.add(1, 2, 3);
			Assert.assertEquals(16, sum1);
			Assert.assertEquals(28, add2Stub.add(1, 2, 5));

		} finally {
			j1.close();
			j2.close();
		}
	}

	@Test(expected = JElectroException.class)
	public void testExceptionCall() throws Exception {
		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");

		try {

			j1.listenTo(12001);
			j2.connectTo("localhost", 12001);

			Calc calc = new CalcImpl(10);
			j1.bind("calc", calc);

			Calc stub = j2.lookupUnique("calc", Calc.class);
			Assert.assertEquals(5d, stub.divide(10, 2), 0.001d);

			System.out.println(stub.divide(10, 0));

		} finally {
			j1.close();
			j2.close();
		}

	}

	@Test
	public void testInitialExportOfStub() throws Exception {
		JElectro j1 = new JElectro("a");
		JElectro j2 = new JElectro("b");

		try {

			j1.listenTo(12001);

			Calc calc = new CalcImpl(10);
			j1.bind("calc", calc);

			j2.connectTo("localhost", 12001);

			Calc stub = j2.lookupUnique("calc", Calc.class);
			Assert.assertEquals(5d, stub.divide(10, 2), 0.001d);

			Assert.assertEquals(10, stub.divide(10, 1), 0.001d);

		} finally {
			j1.close();
			j2.close();
		}
	}

	static int i;
	
	@Test
	public void testUniqueStubInstanceWithMultiplePaths() throws Exception {

		
		log.info("-***************** Execution number " +  i++ + " ******************");
		
		JElectro j1 = new JElectro("1");
		JElectro j2 = new JElectro("2");
		JElectro j3 = new JElectro("3");
		JElectro j4 = new JElectro("4");
		JElectro j5 = new JElectro("5");

		try {

			j2.listenTo(12001);
			j3.listenTo(12002);
			j5.listenTo(12003);

			j1.connectTo("localhost", 12001);
			j1.connectTo("localhost", 12002);

			j4.connectTo("localhost", 12001);
			j4.connectTo("localhost", 12002);
			
			j1.connectTo("localhost", 12003);
			j4.connectTo("localhost", 12003);


			Calc calc = new CalcImpl(0);
			j1.bind("calc", calc);

			final Object lock = new Object();
			final AtomicInteger stubRecievedCount = new AtomicInteger(0);
			final AtomicInteger stubPathAddedCount = new AtomicInteger(0);
			final StubSetListener<Calc> ssl = new StubSetListener<Calc>() {

				@Override
				public void onStubReceived(Calc stub) {
					stubRecievedCount.incrementAndGet();
					synchronized (lock) {
						lock.notifyAll();
					}
				}

				@Override
				public void onStubPathUpdated(Calc stub) {
					stubPathAddedCount.incrementAndGet();
					synchronized (lock) {
						lock.notifyAll();
					}
				}
			};

			StubSet<Calc> calcStubSet = j4.lookup("calc", Calc.class, ssl);
			int i = 0;
			while ((stubRecievedCount.get() == 0 || stubPathAddedCount.get() == 0) && i < 20) {
				synchronized (lock) {
					while ((stubRecievedCount.get() == 0 || stubPathAddedCount.get() == 0) && i < 20) {
						i++;
						if ((stubRecievedCount.get() > 0 || stubPathAddedCount.get() > 0))
							break;
						
						lock.wait(10);
					}

				}
			}

			Assert.assertEquals(1, stubRecievedCount.get());
			/**
			 * According to the way the propagation of the message is made, all the paths may not be discovered.
			 */
			Assert.assertTrue(2 >= stubPathAddedCount.get());

			Assert.assertEquals("Only one instance with two pathes should be present", 1, calcStubSet.size());

			Calc c = calcStubSet.get(0);
			int sum = c.add(1, 2, 3, 4, 5, 6);
			Assert.assertEquals(1 + 2 + 3 + 4 + 5 + 6, sum);

		} finally {
			j1.close();
			j2.close();
			j3.close();
			j4.close();
			j5.close();
		}
	}

	@Test(expected = PathNotAvailableException.class)
	public void testPathFailOnSquareNetwork() throws Exception {

		JElectro j1 = new JElectro("1");
		JElectro j2 = new JElectro("2");
		JElectro j3 = new JElectro("3");
		JElectro j4 = new JElectro("4");

		try {

			j2.listenTo(12001);
			j3.listenTo(12002);

			j1.connectTo("localhost", 12001);
			j1.connectTo("localhost", 12002);

			j4.connectTo("localhost", 12001);
			j4.connectTo("localhost", 12002);

			Calc calc = new CalcImpl(0);
			j1.bind("calc", calc);

			Calc calcStub = j4.lookupUnique("calc", Calc.class);

			Assert.assertEquals(60, calcStub.add(10, 20, 30));

			j3.close();
			j2.close();

			// Thread.sleep(10);

			Assert.assertEquals(60, calcStub.add(10, 20, 30));
			// Should throw path not found

		} finally {
			j1.close();
			j2.close();
			j3.close();
			j4.close();
		}
	}

	@Test(expected = PathNotAvailableException.class)
	public void testPathFailOnLineNetwork() throws Exception {

		JElectro j1 = new JElectro("1");
		JElectro j2 = new JElectro("2");
		JElectro j3 = new JElectro("3");
		JElectro j4 = new JElectro("4");

		try {

			j2.listenTo(12001);
			j4.listenTo(12002);

			j1.connectTo("localhost", 12001);
			j3.connectTo("localhost", 12001);
			j3.connectTo("localhost", 12002);

			Calc calc = new CalcImpl(0);
			j1.bind("calc", calc);

			Calc calcStub = j4.lookupUnique("calc", Calc.class);

			Assert.assertEquals(60, calcStub.add(10, 20, 30));

			// j3.close();
			j2.close();

			// Thread.sleep(10);

			Assert.assertEquals(60, calcStub.add(10, 20, 30));
			// Should throw path not found

		} finally {
			j1.close();
			j2.close();
			j3.close();
			j4.close();
		}
	}

	@Test
	public void testSerializationObjectUpdate() throws Exception {

		JElectro j1 = new JElectro("1");
		JElectro j2 = new JElectro("2");
		JElectro j3 = new JElectro("3");
		JElectro j4 = new JElectro("4");

		try {

			j2.listenTo(12001);
			j3.listenTo(12002);

			j1.connectTo("localhost", 12001);
			j1.connectTo("localhost", 12002);

			j4.connectTo("localhost", 12001);
			j4.connectTo("localhost", 12002);

			Calc calc = new CalcImpl(0);
			j1.bind("calc", calc);

			Calc calcStub = j4.lookupUnique("calc", Calc.class);

			int[] ints = new int[] { 1, 2, 3, 4, 5, 6 };

			Assert.assertEquals(21, calcStub.add(ints));

			ints[0] = 11;

			Assert.assertEquals(31, calcStub.add(ints));

		} finally {
			j1.close();
			j2.close();
			j3.close();
			j4.close();
		}
	}

}
