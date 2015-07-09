package aub.edu.lb.tests;

import java.io.FileNotFoundException;

import aub.edu.lb.bip.examples.GasStation;
import aub.edu.lb.conditions.CheckableCondition;
import aub.edu.lb.conditions.LocalCompleteDeadlockFreeCondition;
import aub.edu.lb.conditions.LocalDeadlockFreeCondition;
import aub.edu.lb.conditions.localAndOr.LALT;
import aub.edu.lb.configuration.Configuration;

public class Test3 {
	public static void main(String[] args) throws FileNotFoundException {
		
		GasStation gs = new GasStation();
		gs.generateGasStation(9);
		
		String bipFile = "BIPExamples/dealocked_system1.bip"; 
		boolean debug = false; 
		
		LALT lalt = new LALT(bipFile, debug);
		LocalCompleteDeadlockFreeCondition localComplete = new LocalCompleteDeadlockFreeCondition(bipFile, debug);
		LocalDeadlockFreeCondition llin = new LocalDeadlockFreeCondition(bipFile, debug);

		check(lalt);
		check(localComplete);
		check(llin);
		
	}
	
	public static void check(CheckableCondition condition) {
		long startTime, stopTime;
		startTime = System.currentTimeMillis();
		System.out.println(condition.check());
		stopTime = System.currentTimeMillis();
		System.out.println(stopTime-startTime);
		
		System.out.println(Configuration.totalTime);
	}
	
}
