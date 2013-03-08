package aub.edu.lb.tests;


import ujf.verimag.bip.Core.Interactions.CompoundType;
import BIPTransformation.TransformationFunction;
import aub.edu.lb.conditions.GDFC;
import aub.edu.lb.conditions.LDFC;
import aub.edu.lb.model.BIPAPI;

public class Test {
	public static void main(String[] args) {
		long startTime, stopTime;
	
		if(args.length != 2) {
			System.err.print("java -jar DFBIP.jar [-ldfc|-gdfc] example.bip");
			return;
		}
		
		CompoundType ct = TransformationFunction.ParseBIPFile(args[1]);
		BIPAPI.initialize(ct);
		if(args[0].equals("-ldfc")) {
			startTime = System.currentTimeMillis();
			LDFC.check();
			//System.out.println("LDFC: " + LDFC.check());
			stopTime = System.currentTimeMillis();
			System.out.println("Time (ms): " + (stopTime-startTime));
		}
		else if(args[0].equals("-gdfc")) {
			startTime = System.currentTimeMillis();
			GDFC.check();
			//System.out.println("GDFC: " + GDFC.check());
			stopTime = System.currentTimeMillis();
			System.out.println("Time (ms): " + (stopTime-startTime));
		}
		else
			System.err.print("java -jar DFBIP.jar [-ldfc|-gdfc] example.bip");	
	}

}
