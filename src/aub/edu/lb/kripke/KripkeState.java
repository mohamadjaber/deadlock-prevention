package aub.edu.lb.kripke;

import java.util.LinkedList;

import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.GlobalState;

public class KripkeState {

	private GlobalState state;
	private LinkedList<Transition> transitions;

	public KripkeState(GlobalState state) {
		this.state = state;
		transitions = new LinkedList<Transition>();
	}

	public KripkeState(KripkeState kripkeState) {
		this.state = kripkeState.state;
		setTransitions(new LinkedList<Transition>(kripkeState.transitions));
	}

	public void addTransition(Transition t) {
		transitions.add(t);
	}

	public void setTransitions(LinkedList<Transition> transitions) {
		this.transitions = transitions;
	}

	public KripkeState nextState(BIPInteraction label) {
		for (Transition transition : transitions) {
			if (transition.getLabel().equals(label))
				return transition.getEndState();
		}
		return null;
	}

	public GlobalState getState() {
		return state;
	}

	public LinkedList<Transition> getTransitions() {
		return transitions;
	}

	/**
	  * 
	  */
	public int hashCode() {
		return state.hashCode();
	}

	public String toString() {
		String kripkeStateName = "[ ";
		for (Transition t : transitions)
			kripkeStateName += t + "  ";
		return kripkeStateName + "]";
	}

	public boolean isEnabled(BIPInteraction interaction) {
		for (Transition transition : getTransitions()) {
			if (transition.getLabel().equals(interaction))
				return true;
		}
		return false;
	}

	/**
	 * @param gs
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KripkeState) {
			KripkeState kripkeState = (KripkeState) obj;
			return state.equals(kripkeState.state);
		}
		return super.equals(obj);
	}

}
