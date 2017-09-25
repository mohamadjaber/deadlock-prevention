package aub.edu.lb.kripke;

import aub.edu.lb.model.BIPInteraction;
import ujf.verimag.bip.Core.Interactions.Component;

public class Edge {
	
	private Component component; 
	private BIPInteraction interaction; 
	
	public Edge(Component component, BIPInteraction interaction) {
		this.component = component; 
		this.interaction = interaction; 
	}
	
	public Component getComponent() {
		return component;
	}
	
	public BIPInteraction getInteraction() {
		return interaction;
	}
	
	public String toString() {
		return component.getName() + "--" + interaction;
	}
	

}
