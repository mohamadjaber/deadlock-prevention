package aub.edu.lb.cmd;


import java.io.File;

import ujf.verimag.bip.Core.Interactions.CompoundType;
import BIPTransformation.TransformationFunction;
import aub.edu.lb.conditions.LDFC;
import aub.edu.lb.configuration.Configuration;
import aub.edu.lb.model.BIPAPI;

public class Main {
	public static void main(String[] args) {
		double startTime, stopTime;
		
		// Default case: debug is set to false.
		CompoundType ct;
		if(args.length == 1 || args.length == 2) {
			File f = new File(args[0]);
			if(!f.exists()) {
				System.err.println("BIP file does not exist.");
				return;
			}
			else {
				ct = TransformationFunction.ParseBIPFile(args[0]);
				BIPAPI.initialize(ct);
				if(args.length == 2) {
					if(args[1].equals("--debug"))
						Configuration.debug = true;
					else {
						System.err.print(Configuration.cmdHelp());
						System.exit(0);
					}
				}	
				startTime = System.currentTimeMillis();
				if(LDFC.check())
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
