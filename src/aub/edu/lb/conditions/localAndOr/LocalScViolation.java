package aub.edu.lb.conditions.localAndOr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ujf.verimag.bip.Core.Interactions.Component;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.SubSystemDepthLocalAndOr;

public class LocalScViolation {
	
	private Component component; 
	private WaitForGraph wfg;
	private SubSystemDepthLocalAndOr subSystem; 
 
	
	private Map<Component, Integer> scViolationsComponents;
	private Map<BIPInteraction, Integer> scViolationsInteractions;
	
	List<BIPInteraction> bordersInteraction;

	
	public LocalScViolation(Component component, WaitForGraph wfg, SubSystemDepthLocalAndOr subSystem) {
		this.component = component; 
		this.wfg = wfg; 
		this.subSystem = subSystem; 
		
		scViolationsComponents = new HashMap<Component,Integer>();
		scViolationsInteractions = new HashMap<BIPInteraction, Integer>();
		bordersInteraction = subSystem.bordersInteraction();
		computeScViolations();
	}


	private void computeScViolations() {
		int dd = longestSimplePathOverApproximation();
		for(int i = 1; i <= dd; i++) {
			computeScViolations(i);
		}
	}
	
	
	
	private void computeScViolations(int d) {
		if(d == 1) {
			for(BIPInteraction interaction: wfg.getInteractions()) {
				if(isInteriorInteraction(interaction) && !wfg.hasOutgoing(interaction)) {
					scViolationsInteractions.put(interaction, 1);
				}
			}
		}
		else {
			
			for(BIPInteraction interaction:wfg.getInteractions()) {
				if(isInteriorInteraction(interaction) && scViolateInteraction(interaction, d)) {
					scViolationsInteractions.put(interaction, d);
				}
			}
			
			for(Component component: wfg.getComponents()) {
				
			}
			
			
		}
	}
	
	private boolean scViolateInteraction(BIPInteraction interaction, int d) {
		for(Component component: wfg.outgoing(interaction)) {
			if(!scViolationsComponents.containsKey(component) ||
					scViolationsComponents.get(component) >= d)  
				return false;
		}
		return true; 
	}
	
	private boolean isInteriorInteraction(BIPInteraction interaction) {
		return !bordersInteraction.contains(interaction);
	}


	private int longestSimplePathOverApproximation() {
		return wfg.getComponents().size() + wfg.getInteractions().size() - 1;
	}


}
