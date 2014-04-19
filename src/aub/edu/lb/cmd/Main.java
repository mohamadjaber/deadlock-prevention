package aub.edu.lb.cmd;


import java.io.File;

import aub.edu.lb.conditions.LocalDeadlockFreeCondition;
import aub.edu.lb.configuration.Configuration;

public class Main {
	public static void main(String[] args) {
		double startTime, stopTime;
		
		// Default case: debug is set to false.
		if(args.length == 1 || args.length == 2) {
			File f = new File(args[0]);
			if(!f.exists()) {
				System.err.println("BIP file does not exist.");
				return;
			}
			else {
				LocalDeadlockFreeCondition ldfc = null;
				if(args.length == 2) {
					if(args[1].equals("--debug"))
						ldfc = new LocalDeadlockFreeCondition(args[0], true);
					else {
						System.err.print(Configuration.cmdHelp());
						System.exit(0);
					}
				}	
				else 
					ldfc = new LocalDeadlockFreeCondition(args[0], false);
				startTime = System.currentTimeMillis();
				if(ldfc.check())
					System.out.println(Configuration.deadlockFreeMessage());
				else
					System.out.println(Configuration.deadlockMessage());
				stopTime = System.currentTimeMillis();
				System.out.println("\nTime (seconds) where locLDFC(a,l) == true: " + Configuration.totalTime/1000);
				System.out.println("Total Time (seconds): " + (stopTime-startTime)/1000);
			}
		}
		else {
			System.err.print(Configuration.cmdHelp());
		}
	
	}

}
