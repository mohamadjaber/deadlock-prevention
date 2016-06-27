package aub.edu.lb.conditions;

import java.util.ArrayList;
import java.util.List;

import ujf.verimag.bip.Core.Interactions.Component;
import aub.edu.lb.configuration.Configuration;
import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.model.BIPInteraction;

public class LocalCompleteDeadlockFreeCondition extends
		LocalDeadlockFreeCondition {

	
	public LocalCompleteDeadlockFreeCondition(String fileName, boolean debug) {
		super(fileName, debug);
	}

	/**
	 * 
	 * @param subSystem
	 * @param interaction
	 * @return
	 */
	@Override
	protected boolean localLDFC(BIPInteraction interaction) {
		// Debug
		Configuration.startTime = System.currentTimeMillis();

		Kripke kripke = new Kripke(subSystem);
	
		for (KripkeState state : kripke.getStates()) {
			for (Transition transition : state.getTransitions()) {
				// sa -- a --> ta
				if (transition.getLabel().equals(interaction)) {
					WaitForGraph wfg = new WaitForGraph(transition.getEndState().getState());
					if (checkPath(wfg, interaction) || checkSC(wfg)) {
						// Debug
						Configuration.stopTime = System.currentTimeMillis();
						Configuration.totalTime += (Configuration.stopTime - Configuration.startTime);
						return false;
					}

				}
			}
		}
		// Debug
		Configuration.stopTime = System.currentTimeMillis();
		Configuration.totalTime += (Configuration.stopTime - Configuration.startTime);
		return true;
	}

	/**
	 * TO BE OPTIMIZED
	 * 
	 * @param wfg
	 * @param interaction
	 * @return
	 */
	protected boolean checkPath(WaitForGraph wfg, BIPInteraction interaction) {
		List<BIPInteraction> borders = subSystem.bordersInteraction();
		ArrayList<Component> components = interaction.getComponents();

		for (BIPInteraction node : borders) {
			// D(a,l) = G(a, 2*l), i.e. a component cannot be a border node. 
			for (Component component : components) {
				if (wfg.existPath(node, component)) {
					for (Object node1 : borders) {
						if (wfg.existPath(component, node1))
							return true;
					}
				}
			}
		}
		return false;
	}

	protected boolean checkSC(WaitForGraph wfg) {
		return wfg.superCycle();
	}
	
	public String getName() {
		return "locLCDFC";
	}

}
