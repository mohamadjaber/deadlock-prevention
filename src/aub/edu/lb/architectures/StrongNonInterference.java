package aub.edu.lb.architectures;

import java.util.Collection;
import java.util.List;

import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.model.BIPAPI;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.SubSystem;
import ujf.verimag.bip.Core.Interactions.Component;

public class StrongNonInterference {

	private Component architectureA1;
	private Component architectureA2;

	public StrongNonInterference(Component architectureA1,
			Component architectureA2) {
		this.architectureA1 = architectureA1;
		this.architectureA2 = architectureA2;
	}

	/**
	 * Fig. 2: Pseudo-code for checking strong non-interference (page 11-12).
	 * For more info, see architecture-tacas14.pdf in the papers folder.
	 * @param architecture
	 * @return
	 */
	private boolean checkSTrongNonInterference(Component architecture) {
		SubSystem top = new SubSystem(BIPAPI.getComponents(),
				BIPAPI.getInteractions());
		KripkeAbstractArchitecture kripkeRemoveTransitionArch = 
				new KripkeAbstractArchitecture(top, architecture);
		Kripke kripkeGlobal = kripkeRemoveTransitionArch.getKripkeGlobal();
		kripkeRemoveTransitionArch.removeTransitionsInvolvingArchitecture();
		KripkeMatrix kripkeMatrix = new KripkeMatrix(kripkeRemoveTransitionArch.getKripkeWithoutTransArch());

		List<List<Integer>> components = new TarjanSCC().scc(kripkeMatrix.getGraph());
		for (List<Integer> scc : components) {
			// The projection on the architecture (given as input) of all the
			// states gives the same state. Because we removed all the transitions labeled
			// by an interaction that contains a port of the architecture.
			if (scc.size() > 0) {
				KripkeState state = kripkeMatrix.getKripkeState(scc.get(0));
				Collection<String> pCC = state.getState()
						.getLocalState(architecture).getPorts();
				for (String port : pCC) {
					for (BIPInteraction interaction : BIPAPI.getInteractionsContainsPort(port)) {
						for (Integer stateSCC : scc) {
							KripkeState kripkeStateSCC = kripkeMatrix.getKripkeState(stateSCC);
							
							// gets the corresponding kripke state in the global system. 
							// because kripke state kripkeStateSCC it is in a kripke
							// where some transitions are removed.
							// TO VERIFY
							if(!kripkeGlobal.getKripkeState(kripkeStateSCC).isEnabled(interaction)) {
								System.out.println(interaction);
								System.out.println(kripkeGlobal.getKripkeState(kripkeStateSCC));
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean checkStrongNonInterference() {
		return checkSTrongNonInterference(architectureA1)
				&& checkSTrongNonInterference(architectureA2);
	}



}
