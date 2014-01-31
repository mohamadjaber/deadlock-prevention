package aub.edu.lb.architectures;


import ujf.verimag.bip.Core.Interactions.CompoundType;
import BIPTransformation.TransformationFunction;
import aub.edu.lb.model.BIPAPI;

public class Test {
	public static void main(String[] args) {
		String bipFile = "BIPExamples/elevator.bip";
		CompoundType ct = TransformationFunction.ParseBIPFile(bipFile);
		BIPAPI.initialize(ct);

		StrongNonInterference sni = new StrongNonInterference(BIPAPI.getComponent("c1"), BIPAPI.getComponent("c2"));
		System.out.println(sni.checkStrongNonInterference());
	}

}
