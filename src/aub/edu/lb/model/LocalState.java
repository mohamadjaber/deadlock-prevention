package aub.edu.lb.model;

import java.util.HashMap;

import ujf.verimag.bip.Core.Behaviors.PortDefinitionReference;
import ujf.verimag.bip.Core.Behaviors.State;
import ujf.verimag.bip.Core.Behaviors.Transition;
import ujf.verimag.bip.Core.Interactions.Component;

/**
 * 
 * @author Mohamad Jaber
 *
 */
public class LocalState {
	
	private State state; 
	private Component component; 
	HashMap<String,State> transitions = new HashMap<String,State>(); // port name -> state 
	
	/**
	 * 
	 * @param s
	 */
	public LocalState(State s, Component c) {
		state = s;
		component = c; 
		for(Object o: s.getOutgoing()) {
			Transition t = (Transition) o;
			State dest = t.getDestination().get(0);
			PortDefinitionReference pdr = (PortDefinitionReference)t.getTrigger();
			transitions.put(pdr.getTarget().getName(), dest);
		}
	}
	

	
	public Component getComponent() {
		return component;
	}
	
	public State getState() {
		return state;
	}
	
	public State next(String port) {
		if(port == null)
			return state;
		else 
			return transitions.get(port);
	}
	
	
	/**
	 * @param interaction
	 * @return
	 */
	public boolean readies(BIPInteraction interaction) {
		int indexOfComponent = interaction.getComponents().indexOf(component);
		if(indexOfComponent >= 0) {
			String port = interaction.getPorts().get(indexOfComponent);
			for(String p: transitions.keySet()) {
				if(p.equals(port)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof LocalState) {
			LocalState s = (LocalState) obj;
			return s.getState().getName().equals(state.getName()) &&
					s.getComponent().getName().equals(component.getName());
		}
		return super.equals(obj);
	}
	
	public int hashCode(){
		  return state.hashCode()/2 + component.hashCode()/2;
	}
	
	
	public String toString() {
		return component.getName() + "." + state.getName();
	}


}
