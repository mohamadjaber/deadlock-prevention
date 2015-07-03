package aub.edu.lb.kripke;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

	public WaitForGraph() {
		components = new ArrayList<Component>();
		interactions = new ArrayList<BIPInteraction>();
	}
	
	/**
	 * Create a wfg C which is subgraph of the given wfg C'
	 * by keeping only components and interactions given as parameters
	 * and the edges connecting them.
	 * 
	 * @param wfg
	 * @param subComponents
	 * @param subInteractions
	 */
	public WaitForGraph(WaitForGraph wfg, Set<Component> subComponents, 
			Set<BIPInteraction> subInteractions) {
		components = new ArrayList<>(subComponents);
		interactions = new ArrayList<>(subInteractions);
		
		for(Edge e: wfg.waitEdges) {
			if(subComponents.contains(e.getComponent()) && subInteractions.contains(e.getInteraction()))
					waitEdges.add(e);
		}
		
		for(Edge e: wfg.readyEdges) {
			if(subComponents.contains(e.getComponent()) && subInteractions.contains(e.getInteraction()))
					readyEdges.add(e);
		}
		
		this.state = wfg.state; 
	}
	

	public WaitForGraph(GlobalState state) {
		this.state = state;
		buildWaitForGraph();
	}

	public void addWaitEdge(Edge e) {
		waitEdges.add(e);
	}

	public void addReadyEdge(Edge e) {
		readyEdges.add(e);
	}

	public ArrayList<Component> getComponents() {
		return components;
	}

	public ArrayList<BIPInteraction> getInteractions() {
		return interactions;
	}

	// not used
	public Set<Set<Object>> paths(Object node1, Object node2) {
		Set<Set<Object>> paths = new HashSet<Set<Object>>();
		Set<Object> visitedNodes = new HashSet<Object>();
		paths(node1, node2, visitedNodes, new HashSet<Object>(), paths);
		return paths;
	}

	// not used
	private void paths(Object node1, Object node2, Set<Object> visitedNodes,
			Set<Object> path, // current partial path
			Set<Set<Object>> paths) { // current paths

		Set<Object> succNodes = succ(node1);
		if (succNodes.contains(node2)) {
			path.add(node2);
			paths.add(path);
		} else {
			for (Object node : succNodes) {
				if (!visitedNodes.contains(node)) {
					paths(node, node2, visitedNodes, new HashSet<Object>(path),
							paths);
				}
			}
		}
	}

	public boolean existPath(Object node1, Object node2) {
		Set<Object> visitedNodes = new HashSet<Object>();
		return existPath(node1, node2, visitedNodes);
	}

	private boolean existPath(Object node1, Object node2,
			Set<Object> visitedNodes) {
		Set<Object> succNodes = succ(node1);
		if (succNodes.contains(node2))
			return true;
		visitedNodes.add(node1);
		for (Object node : succNodes) {
			if (!visitedNodes.contains(node))
				if (existPath(node, node2, visitedNodes))
					return true;
		}
		return false;
	}

	private Set<Object> succ(Object node) {
		Set<Object> succNodes = new HashSet<Object>();
		if (node instanceof BIPInteraction) {
			BIPInteraction interaction = (BIPInteraction) node;
			for (Edge edge : waitEdges) {
				if (edge.getInteraction().equals(interaction))
					succNodes.add(edge.getComponent());
			}
		} else {
			Component component = (Component) node;
			for (Edge edge : readyEdges) {
				if (edge.getComponent().equals(component))
					succNodes.add(edge.getInteraction());
			}
		}
		return succNodes;
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
		// if no local state readies that interaction then the interaction is a
		// source state
		// and then cannot belong to a supercycle.
		for (BIPInteraction interaction : subSystem.getInteractions()) {
			for (LocalState ls : state.getLocalStates()) {
				if (ls.readies(interaction)) {
					leastOneReadyInteractions.add(interaction);
					break;
				}
			}

		}

		// creates wait-for graph's edges
		components = subSystem.getComponents();
		interactions = leastOneReadyInteractions;

		// creates edges
		for (LocalState localState : state.getLocalStates()) {
			for (BIPInteraction interaction : interactions) {
				if (localState.readies(interaction)) {
					addReadyEdge(new Edge(localState.getComponent(),
							interaction));
				} else {
					if (interaction.getComponents().contains(
							localState.getComponent()))
						addWaitEdge(new Edge(localState.getComponent(),
								interaction));
				}
			}
		}
	}

	/**
	 * 
	 * @param node
	 *            is a component or BIP interaction
	 * @param length
	 * @return: true if there exists in path of length equal to bound. false
	 *          otherwise.
	 */
	private boolean inPathDepth(Object node, int length, int bound) {
		// stop condition
		if (length == bound)
			return true;

		if (node instanceof Component) {
			length++;
			boolean inPath = false;
			for (Edge edge : waitEdges) {
				if (edge.getComponent().equals((Component) node)) {
					inPath = inPath
							|| inPathDepth(edge.getInteraction(), length, bound);
				}
			}
			return inPath;
		} else { // node is an instance of BIPInteraction
			boolean inPath = false;
			length++;
			for (Edge edge : readyEdges) {
				if (edge.getInteraction().equals((BIPInteraction) node)) {
					inPath = inPath
							|| inPathDepth(edge.getComponent(), length, bound);
				}
			}
			return inPath;
		}
	}

	/**
	 * 
	 * @param node
	 *            is a component or an interaction
	 * @param length
	 * @return true if there exists a out path of length bound, false otherwise.
	 *         When we rich the bound we check: 1. if it is an interaction and
	 *         all its participants are in the sub-system and readies the
	 *         interaction. 2. if if is a component and all the interactions
	 *         connected to this component are in the sub-system and the
	 *         component and are waiting for the component. If (1) and (2) we
	 *         return false; true otherwise.
	 * 
	 */
	private boolean outPathDepth(Object node, int length, int bound) {
		// stop condition
		// We reach the bound length of the subsystem
		if (length == bound) {
			return checkOutBoundOptimization(node);
		}

		if (node instanceof Component) {
			length++;
			boolean outPath = false;
			for (Edge edge : readyEdges) {
				if (edge.getComponent().equals((Component) node)) {
					outPath = outPath
							|| outPathDepth(edge.getInteraction(), length,
									bound);
				}
			}
			return outPath;
		} else { // node is an instance of BIPInteraction
			boolean outPath = false;
			length++;
			for (Edge edge : waitEdges) {
				if (edge.getInteraction().equals((BIPInteraction) node)) {
					outPath = outPath
							|| outPathDepth(edge.getComponent(), length, bound);
				}
			}
			return outPath;
		}
	}

	private boolean checkOutBoundOptimization(Object node) {
		if (node instanceof BIPInteraction) {
			return checkOutBoundOptimization((BIPInteraction) node);
		} else { // node is instance of component
			return checkOutBoundOptimization((Component) node);
		}
	}

	private boolean checkOutBoundOptimization(BIPInteraction interaction) {
		// all the participants of the interaction are in the subsystem
		// and no out edges, in this case we can say that there is no out
		if (components.containsAll(interaction.getComponents())) {
			for (Edge edge : waitEdges) {
				if (edge.getInteraction().equals(interaction)) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	private boolean checkOutBoundOptimization(Component component) {
		// all the interactions of the component are in the subsystem
		// and no out edges, in this case we can say that there is no out
		if (interactions.containsAll(BIPAPI.getInteractions(component))) {
			for (Edge edge : readyEdges) {
				if (edge.getComponent().equals(component))
					return true;
			}
			return false;
		} else
			return true;
	}

	private boolean outPathDepthL(Object node, int length) {
		return outPathDepth(node, 0, length);
	}

	private boolean inPathDepthL(Object node, int length) {
		return inPathDepth(node, 0, length);
	}

	/**
	 * 
	 * @param components
	 * @param length
	 * @return For a given l we check if there exist out path and in path of
	 *         length = l + 1. The condition is satisfied if for all
	 *         participants of the interaction (e.g., components)
	 *         inDepth(component) <= l or outDepth(component) <= l For this, we
	 *         check: inDepth(component) == l + 1 and outDepth(component) == l +
	 *         1 If this condition is not satisfied we can say that: either 1.
	 *         component has in-depth at most l, 2. or out-depth at most l The
	 *         actual value of length is equal l + 1.
	 */
	public boolean checkNoInNoOut(ArrayList<Component> components, int length) {
		for (Component component : components) {
			if (outPathDepthL(component, length)
					&& inPathDepthL(component, length))
				return false;
		}
		return true;
	}

	private Set<BIPInteraction> enabledInteractions() {
		Set<BIPInteraction> enabledInteractions = new HashSet<BIPInteraction>();
		for (BIPInteraction interaction : interactions) {
			boolean enabled = true;

			for (Edge waitEdge : waitEdges) {
				if (waitEdge.getInteraction().equals(interaction)) {
					enabled = false;
					break;
				}
			}
			if (enabled)
				enabledInteractions.add(interaction);
		}
		return enabledInteractions;
	}

	public boolean superCycle() {
		Set<Object> marked = new HashSet<Object>(enabledInteractions());

		boolean changeMarking;
		do {
			changeMarking = false;
			// if exists interaction a such that every outgoing edge from a is
			// to a marked node B, mark a
			for (BIPInteraction interaction : interactions) {
				if (marked.contains(interaction))
					continue;
				boolean allOutgoingMarked = true;
				for (Edge waitEdge : waitEdges) {
					if (!marked.contains(waitEdge.getComponent())) {
						allOutgoingMarked = false;
						break;
					}
				}
				if (allOutgoingMarked) {
					marked.add(interaction);
					changeMarking = true;
				}
			}

			// if exists non-marked component B such that some outgoing edge
			// from B is to a
			// marked node a, mark B
			for (Component component : components) {
				if (marked.contains(component))
					continue;
				for (Edge readyEdge : readyEdges) {
					if (marked.contains(readyEdge.getInteraction())) {
						changeMarking = true;
						marked.add(component);
						break;
					}
				}
			}
		} while (changeMarking);

		return !(marked.containsAll(interactions) && marked
				.containsAll(components));
	}
	
	
	public boolean hasOutgoing(BIPInteraction interaction) {
		for(Edge e: waitEdges) {
			if(e.getInteraction().equals(interaction))
				return true;
		}
		return false;
	}
	
	public List<Component> outgoing(BIPInteraction interaction) {
		List<Component> components = new LinkedList<Component>();
		for(Edge e: waitEdges) {
			if(e.getInteraction().equals(interaction))
				components.add(e.getComponent());
		}
		return components; 
	}
	
	public List<BIPInteraction> outgoing(Component component) {
		List<BIPInteraction> interactions = new LinkedList<BIPInteraction>();
		for(Edge e: readyEdges) {
			if(e.getComponent().equals(component))
				interactions.add(e.getInteraction());
		}
		return interactions; 
	}

	public String toString() {
		String wfgName = "WFG:\nState: " + state + "\n";
		wfgName += "Component nodes: [ ";
		for (Component comp : components)
			wfgName += comp.getName() + "  ";
		wfgName += "] \n";
		wfgName += "Interaction nodes: [ ";
		for (BIPInteraction inter : interactions)
			wfgName += inter + "  ";
		wfgName += "] \n";

		wfgName += "Wait Edges: [";

		for (Edge edge : waitEdges)
			wfgName += edge.getInteraction() + " --> "
					+ edge.getComponent().getName() + "  ";
		wfgName += "] \n";

		wfgName += "Ready Edges: [";
		for (Edge edge : readyEdges)
			wfgName += edge.getComponent().getName() + " --> "
					+ edge.getInteraction() + "  ";
		wfgName += "] \n";

		return wfgName;
	}

}
