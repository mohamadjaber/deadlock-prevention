package aub.edu.lb.model;



import java.util.ArrayList;

import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.PetriNet;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.Connector;

public class BIPAPI {
	
	private static ArrayList<LocalState> initialStates; 
	private static CompoundType compoundType;
	private static ArrayList<BIPInteraction> interactions;

	
	public static void initialize(CompoundType compoundType) {
		BIPAPI.compoundType = compoundType;
		setInitialStates();
		setInteractions();
	}
	
	
	private static void setInteractions() {
		interactions = new ArrayList<BIPInteraction>(compoundType.getConnector().size());
		for(Connector connector: compoundType.getConnector()) {
			BIPInteraction interaction = new BIPInteraction(connector);
			interactions.add(interaction);
		}
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
	
	
 
}
