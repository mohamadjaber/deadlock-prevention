package aub.edu.lb.cmd;

import aub.edu.lb.conditions.CheckableCondition;
import aub.edu.lb.conditions.LocalDeadlockFreeCondition;
import aub.edu.lb.conditions.localAndOr.LALT;

public class CmdLine {

	public static void main(String[] args)  {
		
		CmdLineFactory cmdLine = new CmdLineFactory(args);
		
		String bipFile = cmdLine.getInputBIPFile();
		boolean debug = cmdLine.isDebugOn();
		
		CheckableCondition condition = null;
		
		switch(cmdLine.getConditionType()) {
			case CmdLineFactory.LocalAndOrName: 
				condition = new LALT(bipFile, debug);
				break;
			case CmdLineFactory.LocalLinearName:
				condition = new LocalDeadlockFreeCondition(bipFile, debug);
				break;
			default: 
				System.out.println(CmdLineFactory.conditionNotSupported);
		}
		
		if(condition != null) {
			long startTime, stopTime;
			startTime = System.currentTimeMillis();
			System.out.println(condition.check()? CmdLineFactory.deadlockFreeMessage(): CmdLineFactory.deadlockMessage());
			stopTime = System.currentTimeMillis();
			System.out.println(cmdLine.getConditionType() + " -- Total Time (seconds): " + (stopTime-startTime)/1000);
		}
	}
	
}
