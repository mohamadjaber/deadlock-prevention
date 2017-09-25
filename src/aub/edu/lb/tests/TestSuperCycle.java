package aub.edu.lb.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import aub.edu.lb.kripke.Edge;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.model.BIPInteraction;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.Connector;
import ujf.verimag.bip.Core.Interactions.InteractionsFactory;

public class TestSuperCycle {
	private static InteractionsFactory interFactory = InteractionsFactory.eINSTANCE ;

	
	@Test
	public void testSuperCyclePath() {
		WaitForGraph wfg = new WaitForGraph();
		Component c1 = comp("c1");
		Component c2 = comp("c2");
		Component c3 = comp("c3");

		BIPInteraction i1 = new BIPInteraction(conn("i1"));
		BIPInteraction i2 = new BIPInteraction(conn("i2"));
		BIPInteraction i3 = new BIPInteraction(conn("i3"));
		BIPInteraction i4 = new BIPInteraction(conn("i4"));
		
		ready(wfg, c1, i1);
		wait(wfg, i1, c2);
		ready(wfg, c2, i3);
		wait(wfg, i3, c1);
		ready(wfg, c1, i2);
		wait(wfg, i2, c3);
		ready(wfg, c3, i4);
		
		ready(wfg, c2, i4);
		wait(wfg, i4, c1);
		
		// does not contain a super cycle
		assertEquals("", wfg.superCycle(), true);	
		assertEquals("", wfg.existPath(c3, i2), true);
		assertEquals("", wfg.existPath(c3, c1), true);
		assertEquals("", wfg.existPath(c1, i3), true);
		assertEquals("", wfg.existPath(c1, c3), true);
		
		List<ArrayList<Object>> paths = wfg.findAllPaths(c2,  c1);
		for (ArrayList<Object> path : paths) {
			for (Object node : path) {
				System.out.print(node);
				System.out.print(" -> ");
			}
			System.out.println();
		}
		assertEquals("", paths.size(), 2);
	}
	
	private Component comp(String name) {
		Component comp = interFactory.createComponent();
		comp.setName(name);
		return comp;
	}
	
	private Connector conn(String name) {
		Connector connector = interFactory.createConnector();
		connector.setName(name);
		return connector;
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
