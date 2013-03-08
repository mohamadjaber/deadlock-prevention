package aub.edu.lb.tests;

import java.io.FileNotFoundException;

import ujf.verimag.bip.Core.Interactions.CompoundType;
import BIPTransformation.TransformationFunction;
import aub.edu.lb.bip.examples.*;
import aub.edu.lb.conditions.LDFC;
import aub.edu.lb.model.BIPAPI;

public class Test1 {
	

	public static void main(String[] args) throws FileNotFoundException {
		long startTime, stopTime;
		
		GasStation gs = new GasStation();
		gs.generateGasStation(9);
		
		
		String bipFile = "BIPExamples/gas_station9.bip";
		CompoundType ct = TransformationFunction.ParseBIPFile(bipFile);
		BIPAPI.initialize(ct);
		
		//LDFC
		startTime = System.currentTimeMillis();
		System.out.println("LDFC " + LDFC.check());
		stopTime = System.currentTimeMillis();
		System.out.println(stopTime-startTime);

	}
	

}
