package aub.edu.lb.kripke;

import java.util.ArrayList;
import java.util.LinkedList;

import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.GlobalState;
import aub.edu.lb.model.LocalState;
import aub.edu.lb.model.SubSystem;
import ujf.verimag.bip.Core.Interactions.Component;


public class WaitForGraph {
	private int lengthNoInNoOut;
	
	private ArrayList<Component> components;
	private ArrayList<BIPInteraction> interactions; 
	
	private GlobalState state; 
	
	private LinkedList<Edge> waitEdges = new LinkedList<Edge>();
	private LinkedList<Edge> readyEdges = new LinkedList<Edge>();
	
	
	public WaitForGraph(GlobalState state) {
		this.state = state; 
		buildWaitForGraph();
	}
	
	private void addWaitEdge(Edge e) {
		waitEdges.add(e);		
	}
	
	private void addReadyEdge(Edge e) {
		readyEdges.add(e);
	}
	
	public ArrayList<Component> getComponents() {
		return components;
	}
	
	public ArrayList<BIPInteraction> getInteractions() {
		return interactions;
	}
	
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	private void buildWaitForGraph() {
		
		SubSystem subSystem = state.getSubSystem();
		// computes interactions' nodes in the wait-for graph
		ArrayList<BIPInteraction> leastOneReadyInteractions = new ArrayList<BIPInteraction>();
		for(BIPInteraction interaction: subSystem.getInteractions()) {
			for(LocalState ls: state.getLocalStates()) {
				if(ls.readies(interaction)) {
					leastOneReadyInteractions.add(interaction);
					break;
				}
			}
			
		}
		
		// creates wait-for graph's edges
		components = subSystem.getComponents();
		interactions =  leastOneReadyInteractions;
		

		// creates edges
		for(LocalState localState : state.getLocalStates()) {
			for(BIPInteraction interaction: interactions) {
				if(localState.readies(interaction)) {
					addReadyEdge(new Edge(localState.getComponent(), interaction));
				}
				else {
					if(interaction.getComponents().contains(localState.getComponent()))
						addWaitEdge(new Edge(localState.getComponent(),interaction));
				}
			}
		}
	}
	

	
	/**
	 * 
	 * @param node is a component or BIP interaction
	 * @param length
	 * @return
	 */
	public boolean noIn(Object node, int length) {
		// stop condition
		if(length == lengthNoInNoOut) return false;
		
		if(node instanceof Component) {
			length++;
			boolean noIn = true;
			for(Edge edge: waitEdges) {
				if(edge.getComponent().equals((Component) node)) {
					noIn = noIn && noIn(edge.getInteraction(), length);
				}
			}
			return noIn;
		}
		else { // node is an instance of BIPInteraction
			boolean noIn = true;
			length++;
			for(Edge edge: readyEdges) {
				if(edge.getInteraction().equals((BIPInteraction) node)) {
					noIn = noIn && noIn(edge.getComponent(), length);
				}
			}
			return noIn;
		}
	}
	
	/**
	 * 
	 * @param node is a component or BIP interaction
	 * @param length
	 * @return
	 */
	public boolean noOut(Object node, int length) {
		// stop condition
		if(length == lengthNoInNoOut) return false;
		
		if(node instanceof Component) {
			length++;
			boolean noOut = true;
			for(Edge edge: readyEdges) {
				if(edge.getComponent().equals((Component) node)) {
					noOut = noOut && noOut(edge.getInteraction(), length);
				}
			}
			return noOut;
		}
		else { // node is an instance of BIPInteraction
			boolean noOut = true;
			length++;
			for(Edge edge: waitEdges) {
				if(edge.getInteraction().equals((BIPInteraction) node)) {
					noOut = noOut && noOut(edge.getComponent(), length);
				}
			}
			return noOut;
		}
	}
	

	

	
	public boolean checkNoInNoOut(ArrayList<Component> components, int length) {
		lengthNoInNoOut = length; 
		for(Component component: components) {
			if(!noIn(component, 0) && !noOut(component, 0)) {
				return false;
			}
		}
		return true;
	}
	
	public String toString() {
		String wfgName = "WFG:\nState: " + state + "\n" ;
		wfgName += "Component nodes: [ ";
		for(Component comp : components)
			wfgName += comp.getName() + "  ";
		wfgName += "] \n";
		wfgName += "Interaction nodes: [ ";
		for(BIPInteraction inter : interactions)
			wfgName += inter + "  ";
		wfgName += "] \n";

		wfgName += "Wait Edges: [";

		for(Edge edge: waitEdges) 
			wfgName += edge.getInteraction() + " --> " + edge.getComponent().getName()  + "  ";
		wfgName += "] \n";

		wfgName += "Ready Edges: [";
		for(Edge edge: readyEdges) 
			wfgName += edge.getComponent().getName() + " --> " + edge.getInteraction()  + "  ";
		wfgName += "] \n";

		return wfgName;
	}
	
	
}
