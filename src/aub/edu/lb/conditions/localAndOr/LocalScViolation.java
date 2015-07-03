package aub.edu.lb.conditions.localAndOr;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ujf.verimag.bip.Core.Interactions.Component;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.SubSystemDepth;

public class LocalScViolation {
	
	private Component component; 
	private WaitForGraph wfg;
	private SubSystemDepth subSystem; 
 
	
	private Set<Component> scViolationsComponents;
	private Set<BIPInteraction> scViolationsInteractions;
	
	private Set<Component> scFormationComponents;
	private Set<BIPInteraction> scFormationInteractions;
	
	List<BIPInteraction> bordersInteraction;

	
	public LocalScViolation(Component component, WaitForGraph wfg, SubSystemDepth subSystem) {
		this.component = component; 
		this.wfg = wfg; 
		
		this.subSystem = subSystem; 
		
		scViolationsComponents = new HashSet<Component>();
		scViolationsInteractions = new HashSet<BIPInteraction>();
		
		scFormationComponents = new HashSet<Component>(subSystem.getComponents());
		scFormationInteractions = new HashSet<BIPInteraction>(subSystem.getInteractions());
		
		bordersInteraction = subSystem.bordersInteraction();
		computeScViolations();
	}


	private void computeScViolations() {
		int dd = longestSimplePathOverApproximation();
		
		computeBottomScViolations();
		
		for(int i = 2; i <= dd; i++) {
			updateScViolations();
		}
	}
	
	
	private void computeBottomScViolations() {
		for(BIPInteraction interaction : scFormationInteractions) {
			if(isInteriorInteraction(interaction) && !wfg.hasOutgoing(interaction)) {
				scViolationsInteractions.add(interaction);
				scFormationInteractions.remove(interaction);
			}
		}
	}
	
	private void updateScViolations() {
		for(BIPInteraction interaction : scFormationInteractions) {
			if(isInteriorInteraction(interaction) && scViolateInteraction(interaction)) {
				scViolationsInteractions.add(interaction);
				scFormationInteractions.remove(interaction);
			}
		}
		
		for(Component component : scFormationComponents) {
			if(scViolateComponent(component)) {
				scViolationsComponents.add(component);
				scFormationComponents.remove(component);
			}
		}	
	}
	
	private boolean scViolateInteraction(BIPInteraction interaction) {
		for(Component component: wfg.outgoing(interaction)) {
			if(!scViolationsComponents.contains(component))	return false;
		}
		return true; 
	}
	
	private boolean scViolateComponent(Component component) {
		for(BIPInteraction interaction : wfg.outgoing(component)) {
			if(scViolationsInteractions.contains(interaction))	return true;
		}
		return false; 
	}
	
	private boolean isInteriorInteraction(BIPInteraction interaction) {
		return !bordersInteraction.contains(interaction);
	}


	private int longestSimplePathOverApproximation() {
		return wfg.getComponents().size() + wfg.getInteractions().size() - 1;
	}

	
	public boolean islocalScViolation() {
		return scViolationsComponents.contains(component);
	}
	
	public SubSystemDepth getSubSystem() {
		return subSystem;
	}

}
