package aub.edu.lb.tests;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.junit.Test;

import aub.edu.lb.conditions.LocalCompleteDeadlockFreeCondition;
import aub.edu.lb.conditions.LocalDeadlockFreeCondition;
import aub.edu.lb.conditions.localAndOr.LALT;

public class Test3 {
	@Test
	public void testDeadlock() throws FileNotFoundException {
		String bipFile = "BIPExamples/dealocked_system1.bip"; 
		boolean debug = false; 
		LALT lalt = new LALT(bipFile, debug);
		LocalCompleteDeadlockFreeCondition localComplete = new LocalCompleteDeadlockFreeCondition(bipFile, debug);
		LocalDeadlockFreeCondition llin = new LocalDeadlockFreeCondition(bipFile, debug);
		
		assertEquals("", lalt.check(), false);	
		assertEquals("", localComplete.check(), false);	
		assertEquals("", llin.check(), false);			
	}
}
