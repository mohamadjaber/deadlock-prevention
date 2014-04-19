package aub.edu.lb.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import aub.edu.lb.kripke.Edge;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.model.BIPInteraction;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.InteractionsFactory;

public class TestSuperCycle {
	private static InteractionsFactory interFactory = InteractionsFactory.eINSTANCE ;

	@Test
	public void testSuperCyclePath() {
		WaitForGraph wfg = new WaitForGraph();
		Component b1 = comp();
		Component b2 = comp();
		Component b3 = comp();

		BIPInteraction a = new BIPInteraction();
		BIPInteraction b = new BIPInteraction();
		BIPInteraction c = new BIPInteraction();
		BIPInteraction d = new BIPInteraction();
		
		ready(wfg, b1, a);
		wait(wfg, a, b2);
		ready(wfg, b2, c);
		wait(wfg, c, b1);
		ready(wfg, b1, b);
		wait(wfg, b, b3);
		ready(wfg, b3, d);
		// does not contain a super cycle
		assertEquals("", wfg.superCycle(), false);	
		assertEquals("", wfg.existPath(b3, b), false);
		assertEquals("", wfg.existPath(b3, b1), false);
		assertEquals("", wfg.existPath(b1, c), true);
		assertEquals("", wfg.existPath(b1, b3), true);
	}
	
	private Component comp() {
		Component comp = interFactory.createComponent();
		comp.setName("");
		return comp;
	}
	
	private void ready(WaitForGraph wfg, Component c, BIPInteraction i) {
		wfg.getComponents().add(c);
		wfg.getInteractions().add(i);
		Edge e = new Edge(c, i);
		wfg.addReadyEdge(e);
	}
	
	private void wait(WaitForGraph wfg, BIPInteraction i, Component c) {
		wfg.getComponents().add(c);
		wfg.getInteractions().add(i);
		Edge e = new Edge(c, i);
		wfg.addWaitEdge(e);
	}

}
