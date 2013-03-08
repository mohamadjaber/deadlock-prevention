package aub.edu.lb.kripke;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import ujf.verimag.bip.Core.Interactions.Component;

import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.GlobalState;
import aub.edu.lb.model.LocalState;
import aub.edu.lb.model.SubSystem;

public class Kripke {
	
	private GlobalState initialState;
	private SubSystem subSystem; 
	
	//DFS
	private Stack<KripkeState> stack = new Stack<KripkeState>();
	private HashSet<KripkeState> stateSpace = new HashSet<KripkeState>();
	
	/**
	 * 
	 * @param subSystem
	 */
	public Kripke(SubSystem subSystem) {
		this.subSystem = subSystem; 
		initialState = subSystem.getInitialState();
		KripkeState kripkeStateInitial = new KripkeState(initialState);

		stack.push(kripkeStateInitial);
		stateSpace.add(kripkeStateInitial);
		DFS();
	}
	
	
	/**
	 * Depth-First Search
	 */
	private void DFS() {
		KripkeState kripkeState = stack.peek();
		GlobalState s = kripkeState.getState();
		LinkedList<BIPInteraction> enabledInteractions = getEnabledInteractions(s);
		for(BIPInteraction interaction : enabledInteractions) {
			KripkeState kripkeStateNext = new KripkeState(next(s,interaction));		
			if(!stateSpace.contains(kripkeStateNext)) {
				Transition t = new Transition(kripkeState, kripkeStateNext, interaction);
				kripkeState.addTransition(t);
				stateSpace.add(kripkeStateNext);
				stack.push(kripkeStateNext);
				DFS();
			}
			else {
				if(!kripkeState.getTransitions().contains(interaction)) {
					Transition t = new Transition(kripkeState, kripkeStateNext, interaction);
					kripkeState.addTransition(t);
				}
			}
		}
		stack.pop();
	}
	
	
	
	
	 /**
	  * TO VERIFY
	  */
	 private LinkedList<BIPInteraction> getEnabledInteractions(GlobalState state) {
		 LinkedList<BIPInteraction> enabledInteractions = new LinkedList<BIPInteraction>();
		 for(BIPInteraction interaction: subSystem.getInteractions()) {
			 boolean isEnable = true;
			 boolean leastOneParticipant = false;
			 for(LocalState ls: state.getLocalStates()) {
				 Component comp = ls.getComponent();
				 if(interaction.getComponents().contains(comp)) {
					 leastOneParticipant = true;
					 String port = interaction.getPort(comp);
					 if(ls.next(port) == null) {
						   isEnable = false; // component comp does not ready the interaction
						   break;
					 }
				 }
			 }
			 if(isEnable && leastOneParticipant) {
				 enabledInteractions.add(interaction);
			 }
		 }
		 return enabledInteractions;
	 }
	 
	 
	 
	 /**
	  * TO VERIFY
	  */
	 public GlobalState next(GlobalState state, BIPInteraction interaction) {
			 ArrayList<LocalState> nextLocalStates = new ArrayList<LocalState>();
			 for(LocalState ls: state.getLocalStates()) {
				 Component comp = ls.getComponent();
				 String port = interaction.getPort(comp);
				 LocalState nextLocalState = new LocalState(ls.next(port), comp);
				 nextLocalStates.add(nextLocalState);
			 }
			 return new GlobalState(nextLocalStates,subSystem);
	 }
	 

	public HashSet<KripkeState> getStates() {
		return stateSpace;
	}
	
	public SubSystem getSubSystem() {
		return subSystem;
	}
	
	 public String toString() {
		 String kripkeName = "[ ";
		 for(KripkeState s: stateSpace) 
			 kripkeName += s + "  ";
		 return kripkeName + "]";
	 }
	
	
	

}
