package aub.edu.lb.tests;

import java.io.FileNotFoundException;

import aub.edu.lb.bip.examples.*;
import aub.edu.lb.conditions.LocalCompleteDeadlockFreeCondition;
import aub.edu.lb.configuration.Configuration;

public class Test2 {
	

	public static void main(String[] args) throws FileNotFoundException {
		long startTime, stopTime;
		
		GasStation gs = new GasStation();
		gs.generateGasStation(9, 3);
				
		LocalCompleteDeadlockFreeCondition ldfc = new LocalCompleteDeadlockFreeCondition("BIPExamples/atm6.bip", true);
		startTime = System.currentTimeMillis();
		System.out.println("LCDFC " + ldfc.check());
		stopTime = System.currentTimeMillis();
		System.out.println(stopTime-startTime);
		System.out.println(Configuration.totalTime);
	}
	

}
