package aub.edu.lb.model;



import java.util.ArrayList;
import java.util.List;

import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.PetriNet;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.Connector;

public class BIPAPI {
	
	private static ArrayList<LocalState> initialStates; 
	private static CompoundType compoundType;
	private static ArrayList<BIPInteraction> interactions;
	private static ArrayList<Component> components;

	private static long numberStates = 1; 
	
	public static void initialize(CompoundType compoundType) {
		BIPAPI.compoundType = compoundType;
		setInitialStates();
		setInteractions();
		setComponents();
	}
	
	public static long getNumberStates() {
		return numberStates; 
	}
	
	private static void setComponents() {
		components = new ArrayList<Component>(compoundType.getSubcomponent().size());
		for(Component component: compoundType.getSubcomponent()) {
			components.add(component);
			numberStates *= getNumberStates(component);
		}
	}
	
	private static void setInteractions() {
		interactions = new ArrayList<BIPInteraction>(compoundType.getConnector().size());
		for(Connector connector: compoundType.getConnector()) {
			BIPInteraction interaction = new BIPInteraction(connector);
			interactions.add(interaction);
		}
	}
	
	/**
	 * REQUIRES: comp is a component of type atomic
	 * @param comp 
	 * @return
	 */
	public static long getNumberStates(Component comp) {
		AtomType atomicType = (AtomType) comp.getType();
		return ((PetriNet)atomicType.getBehavior()).getState().size();
	}
	

	/**
	 * 
	 * @param compoundType
	 * @return
	 */
	private static void setInitialStates() {
		initialStates = new ArrayList<LocalState>(compoundType.getSubcomponent().size());
		for(Component c : compoundType.getSubcomponent()) { 
			assert(c.getType() instanceof AtomType);
			PetriNet PN =  (PetriNet) ((AtomType)c.getType()).getBehavior();
			assert(PN.getInitialState().size() == 1); // behavior of atomic component is LTS
			initialStates.add(new LocalState(PN.getInitialState().get(0), c));
		}
	}
	
	public static ArrayList<LocalState> getInitialStates() {
		return initialStates;
	}
	
	public static ArrayList<BIPInteraction> getInteractions(){
		return interactions;
	}
	
	public static CompoundType getCompoundType() {
		return compoundType;
	}
	
	public static ArrayList<Component> getComponents() {
		return components;
	}
	

	public static Component getComponent(String name) {
		for(Component component: components) {
			if(component.getName().equals(name))
				return component;
		}
		return null;
	}
	
	public static ArrayList<BIPInteraction> getInteractions(Component component) {
		ArrayList<BIPInteraction> interactionsofComponent = new ArrayList<BIPInteraction>();
		for(BIPInteraction inter: interactions) {
			if(inter.getComponents().contains(component) && !interactionsofComponent.contains(inter))
				interactionsofComponent.add(inter);
		}
		return interactionsofComponent;
	}
	
	public static List<BIPInteraction> getInteractionsContainsPort(String port) {
		List<BIPInteraction> interactions = new ArrayList<BIPInteraction>();
		for(BIPInteraction interaction: BIPAPI.interactions) {
			if(interaction.getPorts().contains(port))
				interactions.add(interaction);
		}
		return interactions; 
	}
	
 
}
