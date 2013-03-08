package aub.edu.lb.model;

import java.util.ArrayList;

import ujf.verimag.bip.Core.Interactions.Component;

/**
 * 
 * @author Mohamad Jaber
 *
 */
public class GlobalState {
	private ArrayList<LocalState> localStates;
	private SubSystem subSystem;
	
	private int size; 
	
	 public GlobalState(ArrayList<LocalState> states, SubSystem subSystem) {
		 size = states.size();
		 localStates = new ArrayList<LocalState>(states);
		 this.subSystem = subSystem;
	 }
	 

	 
	 /**
	  * 
	  * @param components
	  * @return 
	  */
	 public GlobalState projection(ArrayList<Component> components, SubSystem subSystem) {
		 ArrayList<LocalState> localStatesProjection = new ArrayList<LocalState>(components.size());
		 
		 for(LocalState ls : localStates) {
			 if(components.contains(ls.getComponent()))
				 localStatesProjection.add(ls);
		 }
		 return new GlobalState(localStatesProjection, subSystem); 
	 }
	 

	 
	 /**
	  * 
	  * @return
	  */
	 public int size() {
		 return size;
	 }
	 
	 /**
	  * 
	  * @return
	  */
	 public SubSystem getSubSystem() {
		 return subSystem;
	 }
	 

	 /**
	  * 
	  * @return
	  */
	 public ArrayList<LocalState> getLocalStates() {
		 return localStates;
	 }
	 
	 /**
	  * @param gs
	  * @return
	  */
	 @Override
	 public boolean equals(Object obj) {
		if(obj instanceof GlobalState) {
			GlobalState gs = (GlobalState) obj;
			if(gs.size() != size)
				return false;
			return gs.localStates.containsAll(localStates);
		}
		return super.equals(obj);
	 }
	 
	 
	 
	 /**
	  * 
	  */
	 public int hashCode(){
		 int hash = 0; 
		 for(LocalState ls: localStates) {
			 hash += ls.hashCode()/size();
		 }
		 return hash;
	 }
	 
	 public String toString() {
		 String globalStateName = "[";
		 for(LocalState ls: localStates) {
			 globalStateName += ls + " ";
		 }
		 return globalStateName + "]";
		 
	 }
	 


}
