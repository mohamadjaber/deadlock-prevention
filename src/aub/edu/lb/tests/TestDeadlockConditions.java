package aub.edu.lb.tests;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.junit.Test;

import aub.edu.lb.conditions.LocalCompleteDeadlockFreeCondition;
import aub.edu.lb.conditions.LocalDeadlockFreeCondition;
import aub.edu.lb.conditions.localAndOr.LALT;

public class TestDeadlockConditions {
	
	boolean debug = false; 
	
	@Test
	public void testFalsePositive() throws FileNotFoundException {
		String bipFile = "BIPExamples/gas_station9.bip"; 
		LALT lalt = new LALT(bipFile, debug);
		LocalDeadlockFreeCondition llin = new LocalDeadlockFreeCondition(bipFile, debug);
		
		assertEquals("", lalt.check(), true);	
		assertEquals("", llin.check(), false);	// false positive		
	}
	
	
	@Test
	public void testDeadlockFree() throws FileNotFoundException {
		String bipFile = "BIPExamples/dining10.bip"; 
		LALT lalt = new LALT(bipFile, debug);
		LocalDeadlockFreeCondition llin = new LocalDeadlockFreeCondition(bipFile, debug);
		
		assertEquals("", lalt.check(), true);	
		assertEquals("", llin.check(), true);			
	}
	
	
	@Test
	public void testDeadlock() throws FileNotFoundException {
		String bipFile = "BIPExamples/dining-deadlock.bip"; 
		LALT lalt = new LALT(bipFile, debug);
		LocalDeadlockFreeCondition llin = new LocalDeadlockFreeCondition(bipFile, debug);
		
		assertEquals("", lalt.check(), false);	
		assertEquals("", llin.check(), false);			
	}
	
	@Test
	public void testDeadlock1() throws FileNotFoundException {
		String bipFile = "BIPExamples/dealocked_system1.bip"; 
		LALT lalt = new LALT(bipFile, debug);
		LocalCompleteDeadlockFreeCondition localComplete = new LocalCompleteDeadlockFreeCondition(bipFile, debug);
		LocalDeadlockFreeCondition llin = new LocalDeadlockFreeCondition(bipFile, debug);
		
		assertEquals("", lalt.check(), false);	
		assertEquals("", localComplete.check(), false);	
		assertEquals("", llin.check(), false);			
	}
}
