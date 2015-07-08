package aub.edu.lb.conditions.localAndOr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ujf.verimag.bip.Core.Interactions.Component;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.model.BIPInteraction;



public class WFGMatrix  {

	private Map<BIPInteraction, Integer> mapBIPInteractionId;
	private Map<Integer, BIPInteraction> mapIdBIPInteraction;
	private Map<Component, Integer> mapComponentId;
	private Map<Integer, Component> mapIdComponent;
	

	private List<Integer>[] graph; 
	private WaitForGraph wfg;
	
	public WFGMatrix(WaitForGraph wfg) {
		this.wfg = wfg;
		initializaMapStateId();
		setGraph();
	}
	
	@SuppressWarnings("unchecked")
	private void setGraph() {
		graph = new List[wfg.getComponents().size() + wfg.getInteractions().size()];
		
		for (int i = 0; i < graph.length; i++) {
			graph[i] = new ArrayList<>();
		}
		
		for(Component component: wfg.getComponents()) {
			for(BIPInteraction interaction: wfg.outgoing(component)) {
				graph[getStateId(component)].add(getStateId(interaction));
			}
		}
		
		for(BIPInteraction interaction: wfg.getInteractions()) {
			for(Component component: wfg.outgoing(interaction)) {
				graph[getStateId(interaction)].add(getStateId(component));
			}
		}
	}
	
	public int getStateId(BIPInteraction interaction) {
		return mapBIPInteractionId.get(interaction);
	}
	
	public int getStateId(Component component) {
		return mapComponentId.get(component);
	}
	


	private void initializaMapStateId() {
		int counter = 0; 
		mapBIPInteractionId = new HashMap<BIPInteraction, Integer>();
		mapIdBIPInteraction = new HashMap<Integer, BIPInteraction>();
		mapComponentId = new HashMap<Component, Integer>();
		mapIdComponent = new HashMap<Integer, Component>();
		
		for(BIPInteraction interaction: wfg.getInteractions()) {
			mapBIPInteractionId.put(interaction, counter);
			mapIdBIPInteraction.put(counter, interaction);
			counter++;
		}

		for(Component component: wfg.getComponents()) {
			mapComponentId.put(component, counter);
			mapIdComponent.put(counter, component);
			counter++;
		}
	}
	
	
	
	public List<Integer>[] getGraph() {
		return graph; 
	}
	

}
