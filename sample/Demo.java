

import java.io.IOException;

import com.jelectro.JElectro;
import com.jelectro.exception.JElectroException;

public class Demo {

	public static void main(String args[]) throws IOException, JElectroException {
		
		System.out.println("Demo starts");
		
		// Create the nodes
		JElectro j1 = new JElectro("Node1");
		JElectro j2 = new JElectro("Node2");
		
		
		try {
			
			// Connect them together 
			j1.listenTo(12001);
			j2.connectTo("localhost", 12001);
			
			
			// Create the stub to expose
			Calc calcInstance = new CalcImpl();
			j1.bind("calc", calcInstance);

			// retrieve the link to the stub
			Calc c = j2.lookupUnique("calc", Calc.class);

			// Just use it as a plain java instance
			System.out.println("1+2+3+4=" + c.add(1, 2, 3, 4));
			
			
		} finally {
			
			// close the connections
			j1.close();
			j2.close();
		}

	}

	static interface Calc {
		int add(int... is);

	}

	static class CalcImpl implements Calc {

		@Override
		public int add(int... is) {
			int sum = 0;
			for (int i : is) {
				sum += i;

			}
			return sum;
		}

	}

}
