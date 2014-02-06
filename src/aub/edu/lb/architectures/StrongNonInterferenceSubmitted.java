package aub.edu.lb.architectures;

import java.util.List;

import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;
import aub.edu.lb.model.BIPAPI;
import aub.edu.lb.model.SubSystem;
import ujf.verimag.bip.Core.Interactions.Component;

public class StrongNonInterferenceSubmitted {

	private Component architectureA1;
	private Component architectureA2;
	
	private List<String> idleStatesA1;
	private List<String> idleStatesA2;


	public StrongNonInterferenceSubmitted(Component architectureA1,
			Component architectureA2, List<String> idleStatesA1, List<String> idleStatesA2) {
		this.architectureA1 = architectureA1;
		this.architectureA2 = architectureA2;
		
		this.idleStatesA1 = idleStatesA1;
		this.idleStatesA2 = idleStatesA2;
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
		KripkeAbstractArchitecture kripkeRemoveTransitionArch = new KripkeAbstractArchitecture(top, architecture);
		kripkeRemoveTransitionArch.removeTransitionsInvolvingArchitecture();
		kripkeRemoveTransitionArch.removeIdleStates(getIdleStates(architecture));
		
		KripkeMatrix kripkeMatrix = new KripkeMatrix(kripkeRemoveTransitionArch.getKripkeWithoutTransArch());

		List<List<Integer>> components = new TarjanSCC().scc(kripkeMatrix.getGraph());
		for (List<Integer> scc : components) {
			if(!isTrivialSCC(scc, kripkeMatrix))
				return false;
		}
		return true;
	}
	
	/**
	 * a scc is trivial if it consists of a (one) single state without any self-loop transition. 
	 * any transition in the scc is self-loop because otherwise it will be splitted to two 
	 * different scc-s. 
	 * @param scc
	 * @param kripkeMatrix
	 * @return
	 */
	private boolean isTrivialSCC(List<Integer> scc, KripkeMatrix kripkeMatrix) {
		if(scc.size() == 1 && !isSelfLoop(kripkeMatrix.getKripkeState(scc.get(0)))) {
			return true;
		}
		return false;
	}
	
	private boolean isSelfLoop(KripkeState kripkeState) {
		for(Transition t: kripkeState.getTransitions()) {
			if(t.getEndState().equals(kripkeState))
				return true;
		}
		return false;
	}
	
	private List<String> getIdleStates(Component architecture) {
		if(architecture.equals(architectureA1))
			return idleStatesA1;
		else if(architecture.equals(architectureA2))
			return idleStatesA2;
		else 
			throw new RuntimeException("Architecture/Idle states error - check Strong NonInterferenceSubmitted Class");
	}

	public boolean checkStrongNonInterference() {
		return checkSTrongNonInterference(architectureA1)
				 && checkSTrongNonInterference(architectureA2);
	}



}
