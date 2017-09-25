package aub.edu.lb.kripke;

import aub.edu.lb.model.BIPInteraction;

public class Transition {

	private KripkeState startState;
	private KripkeState endState;
	private BIPInteraction label; // label of transition

	public Transition(KripkeState startState, KripkeState endState,
			BIPInteraction label) {
		this.startState = startState;
		this.endState = endState;
		this.label = label;
	}

	public KripkeState getStartState() {
		return startState;
	}

	public KripkeState getEndState() {
		return endState;
	}

	public BIPInteraction getLabel() {
		return label;
	}

	/**
	 * @param gs
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Transition) {
			Transition t = (Transition) obj;
			return t.getStartState().equals(startState)
					&& t.getEndState().equals(endState)
					&& t.label.equals(label);
		}
		return super.equals(obj);
	}

	public String toString() {
		return startState.getState() + "--- " + label + " --->"
				+ endState.getState();
	}

}
