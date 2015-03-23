package aub.edu.lb.architectures;


import java.util.Arrays;
import java.util.LinkedList;

import ujf.verimag.bip.Core.Interactions.CompoundType;
import BIPTransformation.TransformationFunction;
import aub.edu.lb.model.BIPAPI;

public class Test {
	public static void main(String[] args) {
		String bipFile = "BIPExamples/elevator2.bip";
		CompoundType ct = TransformationFunction.ParseBIPFile(bipFile);
		BIPAPI.initialize(ct);

		//StrongNonInterference sni = new StrongNonInterference(BIPAPI.getComponent("c1"), BIPAPI.getComponent("c2"));
		//System.out.println(sni.checkStrongNonInterference());

		
		LinkedList<String> idleStateA1 = new LinkedList<String>();
		LinkedList<String> idleStateA2 = new LinkedList<String>();
		idleStateA1.add("l0");
		idleStateA2.add("l0");
	//	idleStateA2.add("l1");
	//	idleStateA2.add("l2");



		
		StrongNonInterferenceSubmitted sniFinal = new StrongNonInterferenceSubmitted(
				BIPAPI.getComponent("c1"), 
				BIPAPI.getComponent("c3"), 
				idleStateA1, 
				idleStateA2
		);
		
		System.out.println(Arrays.toString(sniFinal.checkStrongNonInterference1()));

	}
	
	/**
	 * We take (c1 + c2)(B) are non-interfering 
	 * (c1 + c3)(B) liveness is not satisfied since c3 can be infinitely executed
	 * c3: infinite "add" and "sub" (singleton) interactions without executing 
	 * other architecture c1. 
	 * 
	 */

}
