package aub.edu.lb.architectures;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ujf.verimag.bip.Core.Interactions.Component;
import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;
import aub.edu.lb.model.BIPAPI;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.SubSystem;

public class KripkeAbstractArchitecture {

	private Component architecture;
	private Kripke kripkeWithoutTransArch;
	private Kripke kripkeGlobal;

	/**
	 * 
	 * @param subSystem
	 * @param architecture
	 *            is the component denoting the architecture.
	 */
	public KripkeAbstractArchitecture(SubSystem subSystem,
			Component architecture) {
		kripkeGlobal = new Kripke(subSystem);
		kripkeWithoutTransArch = new Kripke(kripkeGlobal);
		this.architecture = architecture;
	}

	public KripkeAbstractArchitecture(SubSystem subSystem,
			String architectureName) {
		this(subSystem, BIPAPI.getComponent(architectureName));
	}

	public void removeTransitionsInvolvingArchitecture() {
		for (KripkeState state : kripkeWithoutTransArch.getStates()) {
			Collection<Transition> transitionsToRemove = new LinkedList<Transition>();
			for (Transition transition : state.getTransitions()) {
				if (isArchitectureInvolved(transition))
					transitionsToRemove.add(transition);
			}
			state.getTransitions().removeAll(transitionsToRemove);
		}
	}

	public void removeIdleStates(List<String> idleStates) {
		Collection<KripkeState> statesToRemove = new LinkedList<KripkeState>();
		for (KripkeState state : kripkeWithoutTransArch.getStates()) {
			String archStateProjection = state.getState()
					.getLocalState(architecture).getState().getName();
			if (idleStates.contains(archStateProjection))
				statesToRemove.add(state);
		}
		kripkeWithoutTransArch.getStates().removeAll(statesToRemove);
	}

	private boolean isArchitectureInvolved(Transition t) {
		BIPInteraction label = t.getLabel();
		for (Component component : label.getComponents()) {
			if (component.equals(architecture))
				return true;
		}
		return false;
	}

	public Kripke getKripkeGlobal() {
		return kripkeGlobal;
	}

	public Kripke getKripkeWithoutTransArch() {
		return kripkeWithoutTransArch;
	}

	public Component getArchitecture() {
		return architecture;
	}
}
