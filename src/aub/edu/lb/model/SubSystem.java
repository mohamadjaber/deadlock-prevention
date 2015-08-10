package aub.edu.lb.model;

import java.util.ArrayList;

import ujf.verimag.bip.Core.Interactions.Component;

/**
 * 
 * @author Mohamad Jaber
 * This class constructs a BIP subsystem from a BIP system 
 * and a set of atomic components and a set of interactions.   
 */
public class SubSystem {
	
	protected GlobalState initialState;
	protected ArrayList<Component> components;
	protected ArrayList<BIPInteraction> interactions; 	
	
	

	public SubSystem() {
	}

	/**
	 * 
	 * @param components
	 * @param interactions
	 */
	public SubSystem(ArrayList<Component> components, ArrayList<BIPInteraction> interactions) {
		this.components = new ArrayList<Component>(components); 
		this.interactions = new ArrayList<BIPInteraction>(interactions); 
		setInitialState();
	}
	
	/**
	 *
	 */
	private void setInitialState() {
		GlobalState globalState = new GlobalState(BIPAPI.getInitialStates(), this);
		initialState = globalState.projection(components, this);
	}
	
	
	protected void addComponent(Component comp) {
		components.add(comp);
		setInitialState();
	}
	
	protected void addInteraction(BIPInteraction inter) {
		interactions.add(inter);
	}
	
	/**
	 * 
	 * @return
	 */
	public GlobalState getInitialState() {
		return initialState;
	}
	
	public ArrayList<Component> getComponents() {
		return components; 
	}
	
	public ArrayList<BIPInteraction> getInteractions() {
		return interactions; 
	}
	
	public long getNumberStates() {
		long numberStates = 1; 		
		for(Component comp: components) {
			numberStates *= BIPAPI.getNumberStates(comp);
		}
		return numberStates;
	}
	
	
	public String toString() {
		String componentName = "Components: [ ";
		for(Component c: components) {
			componentName += c.getName() + " ";
		}
		componentName += "]";
		
		String initialStateName = "InitialState: " + initialState;
		
		String interactionName = "Interaction[s] : [ ";
		for(BIPInteraction interaction: interactions) {
			interactionName += interaction + " ";
		}
		interactionName += "]";
		return "SubSystem:\n" + componentName + "\n" +
				interactionName +  "\n" +
				initialStateName;
	}


}
