package aub.edu.lb.kripke;

import java.util.ArrayList;






import java.util.LinkedList;
import aub.edu.lb.model.BIPAPI;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.GlobalState;
import aub.edu.lb.model.LocalState;
import aub.edu.lb.model.SubSystem;
import ujf.verimag.bip.Core.Interactions.Component;


public class WaitForGraph {
	
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
	 * @return: true if there exists in path of length equal to bound.
	 *   false otherwise. 
	 */
	private boolean inPathLength(Object node, int length, int bound) {
		// stop condition
		if(length == bound) return true;
		
		if(node instanceof Component) {
			length++;
			boolean inPath = false;
			for(Edge edge: waitEdges) {
				if(edge.getComponent().equals((Component) node)) {
					inPath = inPath || inPathLength(edge.getInteraction(), length, bound);
				}
			}
			return inPath;
		}
		else { // node is an instance of BIPInteraction
			boolean inPath = false;
			length++;
			for(Edge edge: readyEdges) {
				if(edge.getInteraction().equals((BIPInteraction) node)) {
					inPath = inPath || inPathLength(edge.getComponent(), length, bound);
				}
			}
			return inPath;
		}
	}
	
	/**
	 * 
	 * @param node is a component or an interaction
	 * @param length
	 * @return true if there exists a out path of length bound, 
	 *    false otherwise. When we rich the bound we check:
	 *    1. if it is an interaction and all its participants are in 
	 *       the sub-system and readies the interaction.
	 *    2. if if is a component and all the interactions connected
	 *       to this component are in the sub-system and the component
	 *       and are waiting for the component. 
	 *    If (1) and (2) we return false; true otherwise. 
	 *        
	 */
	private boolean outPathLength(Object node, int length, int bound) {
		// stop condition
		// We reach the bound length of the subsystem
		if(length == bound) {
			return checkOutBoundOptimization(node);
		}

		if(node instanceof Component) {
			length++;
			boolean outPath = false;
			for(Edge edge: readyEdges) {
				if(edge.getComponent().equals((Component) node)) {
					outPath = outPath || outPathLength(edge.getInteraction(), length, bound);
				}
			}
			return outPath;
		}
		else { // node is an instance of BIPInteraction
			boolean outPath = false;
			length++;
			for(Edge edge: waitEdges) {
				if(edge.getInteraction().equals((BIPInteraction) node)) {
					outPath = outPath || outPathLength(edge.getComponent(), length, bound);
				}
			}
			return outPath;
		}
	}
	
	
	
	private boolean checkOutBoundOptimization(Object node) {
		if(node instanceof BIPInteraction) {
			return checkOutBoundOptimization((BIPInteraction) node);
		}
		else { // node is instance of component 
			return checkOutBoundOptimization((Component) node);
		}
	}
	
	
	private boolean checkOutBoundOptimization(BIPInteraction interaction) {
		// all the participants of the interaction are in the subsystem
		// and no out edges, in this case we can say that there is no out
		if(components.containsAll(interaction.getComponents())) {
			for(Edge edge: waitEdges) {
				if(edge.getInteraction().equals(interaction)) {
					return true;
				}
			}
			return false;
		}
		else {
			return true;
		}
	}
	
	private boolean checkOutBoundOptimization(Component component) {
		// all the interactions of the component are in the subsystem
		// and no out edges, in this case we can say that there is no out
		if(interactions.containsAll(BIPAPI.getInteractions(component))) {
			for(Edge edge: readyEdges) {
				if(edge.getComponent().equals(component))
					return true;
			}
			return false;
		}
		else
			return true;
	}
	

	
	private boolean outPathLengthL(Object node, int length) {
		return outPathLength(node, 0, length); 
	}
	
	private boolean inPathLengthL(Object node, int length) {
		return inPathLength(node, 0, length); 
	}

	
	public boolean checkNoInNoOut(ArrayList<Component> components, int length) {
		for(Component component: components) {
			if(outPathLengthL(component,length) && inPathLengthL(component, length))
				return false;
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
