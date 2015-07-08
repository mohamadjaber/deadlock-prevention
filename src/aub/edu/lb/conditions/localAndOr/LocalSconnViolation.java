package aub.edu.lb.conditions.localAndOr;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ujf.verimag.bip.Core.Interactions.Component;
import aub.edu.lb.architectures.TarjanSCC;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.model.BIPInteraction;

public class LocalSconnViolation {
	private LocalScViolation localScViolation; 
	
	public LocalSconnViolation(LocalScViolation localScViolation) {
		this.localScViolation = localScViolation;		
	}
	
	/**
	 * WL = graph obtained by removing all nodes with no super-cycle violation (WL = subWFGWithoutViolationNodes)
	 * Bi is in strongly connected super-cycle which is in WL:
	 * 1. compute strong connected parts of WL = C1, C2, ..., Cn
	 * 2. if Bi is trivial strongly connected component -> violation -> return true
	 * 3. if Bi is in non trivial strongly connected component C: 
	 *			3.1 Mark all nodes in C that cannot be in strongly connected super-cycle:
	 *				Repeat until no change:
	 *					for all B: if exist a such that B -> a and ( a not in C Or a is marked) THEN mark B
	 *					for all a: if for all B: a -> B we have: B not in C or B is marked THEN mark a
	 *
	 * Let C' = C - marked nodes
	 * If Bi in C' return false; true otherwise. 
	 * Note that, C' cannot contain only Bi as a node since if so it would be marked and removed. 
	 * 
	 * @return
	 */
	private boolean isSSCInSubWFGWithNoViolation() {
		// remove all nodes with no super-cycle violation
		WaitForGraph subWFGWithoutViolationNodes = removeNodesLocalScViolation();
		WFGMatrix wfgMatrix = new WFGMatrix(subWFGWithoutViolationNodes);
		List<List<Integer>> stronglyConnectedParts = new TarjanSCC().scc(wfgMatrix.getGraph());
		
		Set<Component> markedComponents = new HashSet<Component>();
		Set<BIPInteraction> markedInteractions = new HashSet<BIPInteraction>();
		Set<Component> nonMarkedComponents = new HashSet<Component>(subWFGWithoutViolationNodes.getComponents());
		Set<BIPInteraction> nonMarkedInteractions = new HashSet<BIPInteraction>(subWFGWithoutViolationNodes.getInteractions());
		
		
		for(List<Integer> strongConnectedPart: stronglyConnectedParts) {
			int idComponent = wfgMatrix.getStateId(localScViolation.component);
			if(strongConnectedPart.contains(idComponent)) {
				// Bi is in a trivial strongly connected component
				if(strongConnectedPart.size() == 1) return true;
				
				// Bi is in non trivial strongly connected component
				
				boolean marking; 
				
				// Repeat until no marking:
				do {
					marking = false;
					
					// for all non marked components B check if:
					// exist a such that B -> a and ( a not in C Or a is marked) THEN mark B
					for(Component component: nonMarkedComponents) {
						for(BIPInteraction interaction: subWFGWithoutViolationNodes.outgoing(component)) {
							if(markedInteractions.contains(interaction) || 
									!strongConnectedPart.contains(wfgMatrix.getStateId(interaction))) {
								// mark component
								markedComponents.add(component);
								nonMarkedComponents.remove(component);
								marking = true;
							}
						}
					}
					
					// for all non marked interactions a check if:
					// all B where a -> B we have: B not in C or B is marked THEN mark a
					for(BIPInteraction interaction: nonMarkedInteractions) {
						boolean allOutgoingMarkedOrOutside = true;
						for(Component component: subWFGWithoutViolationNodes.outgoing(interaction)) {
							if(strongConnectedPart.contains(wfgMatrix.getStateId(component)) && 
									nonMarkedComponents.contains(component))
								allOutgoingMarkedOrOutside = false;
								break;
						}
						// mark interaction
						if(allOutgoingMarkedOrOutside) {
							markedInteractions.add(interaction);
							nonMarkedInteractions.remove(interaction);
						}
					}
				} while(marking);
				
			}
		}
		return nonMarkedComponents.contains(localScViolation.component);
	}
	
	/**
	 * for all wait for paths pi: from Bi to a border node v 
	 * 		if pi contains a node with local super-cycle violation return true
	 * return false;
	 * 
	 * This can be computed as follows: 
	 * 		1. compute reachable nodes from Bi; reachable(Bi) = BFS(Bi) or DFS(Bi)
	 *      2. for each border interaction node v
	 * 			2.1. compute reachable nodes from v to Bi in the transpose graph; reachableT(v) 
	 *      	2.2 if all the nodes in reachable(Bi) intersects reachableT(v) do not have local sc violation 
	 *      		return false; 
	 *      return true; 
	 */
	private boolean isWaitForPathsToBorderLocalScViolation() {
		Set<Object> reachableFromComp = localScViolation.wfg.bfs(localScViolation.component);
		for(BIPInteraction interaction: localScViolation.bordersInteraction) {
			Set<Object> backwardReachableBorder = localScViolation.wfg.inverseBfs(interaction);
			backwardReachableBorder.retainAll(reachableFromComp);
			for(Object node: backwardReachableBorder) {
				if(localScViolation.scViolationsComponents.contains(node) || 
						localScViolation.scViolationsInteractions.contains(node))
					return true;
			}
		}
		return false; 
	}
	
	

	/**
	 * for all wait for paths pi a border node to Bi 
	 * 		if pi contains a node with local super-cycle violation return true
	 * return false;
	 * 
	 * This can be computed as follows: 
	 * 		1. compute backward reachable nodes from Bi; reachableT(Bi)
	 *      2. for each border interaction node v
	 * 			2.1. compute reachable nodes from v to Bi; reachable(v) 
	 *      	2.2 if all the nodes in reachableT(Bi) intersects reachable(v) do not have local sc violation 
	 *      		return false; 
	 *      return true; 
	 */
	private boolean isWaitForPathsFromBorder() {
		Set<Object> backwardReachableFromComp = localScViolation.wfg.inverseBfs(localScViolation.component);
		for(BIPInteraction interaction: localScViolation.bordersInteraction) {
			Set<Object> reachableBorder = localScViolation.wfg.bfs(interaction);
			reachableBorder.retainAll(backwardReachableFromComp);
			for(Object node: reachableBorder) {
				if(localScViolation.scViolationsComponents.contains(node) || 
						localScViolation.scViolationsInteractions.contains(node))
					return true;
			}
		}
		return false; 
	}
	
	private WaitForGraph removeNodesLocalScViolation() {
		return new WaitForGraph(localScViolation.wfg, 
				localScViolation.scFormationComponents, 
				localScViolation.scFormationInteractions);
	}
	
	public boolean isLocalSconnViolation() {
		return isSSCInSubWFGWithNoViolation() && 
				(isWaitForPathsToBorderLocalScViolation() || isWaitForPathsFromBorder());
	}

}
