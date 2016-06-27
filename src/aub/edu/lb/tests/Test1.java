package aub.edu.lb.tests;

import java.io.FileNotFoundException;

import aub.edu.lb.bip.examples.*;
import aub.edu.lb.conditions.CheckableCondition;
import aub.edu.lb.conditions.LocalDeadlockFreeCondition;
import aub.edu.lb.conditions.localAndOr.LALT;
import aub.edu.lb.configuration.Configuration;

public class Test1 {
	

	public static void main(String[] args) throws FileNotFoundException {
		long startTime, stopTime;
		
		GasStation gs = new GasStation();
		gs.generateGasStation(15, 3);
				
		LocalDeadlockFreeCondition ldfc = new LocalDeadlockFreeCondition("BIPExamples/gas_station15.bip", true);
		// LocalDeadlockFreeCondition ldfc = new LocalDeadlockFreeCondition(gs.generateGasStation(2), false);

		startTime = System.currentTimeMillis();
		System.out.println("LDFC " + ldfc.check());
		stopTime = System.currentTimeMillis();
		System.out.println(stopTime-startTime);
		System.out.println(Configuration.totalTime);
	}
	

}
