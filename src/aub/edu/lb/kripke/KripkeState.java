package aub.edu.lb.kripke;

import java.util.LinkedList;

import aub.edu.lb.model.GlobalState;

public class KripkeState {
	
	private GlobalState state;
	private LinkedList<Transition> transitions; 
	
	
	public KripkeState(GlobalState state) {
		this.state = state;
		transitions = new LinkedList<Transition>();
	}
	
	public void addTransition(Transition t) {
		transitions.add(t);
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
	 public int hashCode(){
		 return state.hashCode(); 	 
	 }
	 
	 public String toString() {
		 String kripkeStateName = "[ ";
		 for(Transition t: transitions) 
			 kripkeStateName += t + "  ";
		 return kripkeStateName + "]";
	 }
	 
	 /**
	  * @param gs
	  * @return
	  */
	 @Override
	 public boolean equals(Object obj) {
		if(obj instanceof KripkeState) {
			KripkeState kripkeState = (KripkeState) obj;
			return state.equals(kripkeState.state);
		}
		return super.equals(obj);
	 }
	 
	
	

}
