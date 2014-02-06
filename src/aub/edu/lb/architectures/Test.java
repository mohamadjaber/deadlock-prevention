package aub.edu.lb.architectures;


import java.util.LinkedList;

import ujf.verimag.bip.Core.Interactions.CompoundType;
import BIPTransformation.TransformationFunction;
import aub.edu.lb.model.BIPAPI;

public class Test {
	public static void main(String[] args) {
		String bipFile = "BIPExamples/elevator.bip";
		CompoundType ct = TransformationFunction.ParseBIPFile(bipFile);
		BIPAPI.initialize(ct);

		//StrongNonInterference sni = new StrongNonInterference(BIPAPI.getComponent("c1"), BIPAPI.getComponent("c2"));
		//System.out.println(sni.checkStrongNonInterference());

		
		LinkedList<String> idleStateA1 = new LinkedList<String>();
		LinkedList<String> idleStateA2 = new LinkedList<String>();
		idleStateA2.add("l0");
		
		StrongNonInterferenceSubmitted sniFinal = new StrongNonInterferenceSubmitted(
				BIPAPI.getComponent("c1"), 
				BIPAPI.getComponent("c2"), 
				idleStateA1, 
				idleStateA2
		);
		
		System.out.println(sniFinal.checkStrongNonInterference());

	}

}
