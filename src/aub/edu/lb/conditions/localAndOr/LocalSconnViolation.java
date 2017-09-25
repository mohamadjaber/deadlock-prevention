package aub.edu.lb.conditions.localAndOr;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ujf.verimag.bip.Core.Interactions.Component;
import aub.edu.lb.architectures.TarjanSCC;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.model.BIPInteraction;

public class LocalSconnViolation {
	private LocalScViolation localScViolation; 
	
	private WaitForGraph subWFGWithoutViolationNodes;
	
	public LocalSconnViolation(LocalScViolation localScViolation) {
		this.localScViolation = localScViolation;
		// keep all nodes with NO super-cycle violation
		subWFGWithoutViolationNodes = removeNodesLocalScViolation();
	}
	
	/**
	 * WL = graph obtained by removing all nodes with no super-cycle violation (WL = subWFGWithoutViolationNodes)
	 * Bi is in strongly connected super-cycle which is in WL:
	 * 1. compute strong connected parts of WL = C1, C2, ..., Cn
	 * 2. if Bi is in trivial strongly connected component -> violation -> return true
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
					List<Component> currentmMarkedComponents = new LinkedList<Component>();
					for(Component component:  nonMarkedComponents) {
						for(BIPInteraction interaction: subWFGWithoutViolationNodes.outgoing(component)) {
							if(markedInteractions.contains(interaction) || 
									!strongConnectedPart.contains(wfgMatrix.getStateId(interaction))) {
								// mark component
								markedComponents.add(component);
								currentmMarkedComponents.add(component);
								marking = true;
							}
						}
					}
					nonMarkedComponents.removeAll(currentmMarkedComponents);
					
					// for all non marked interactions a check if:
					// all B where a -> B we have: B not in C or B is marked THEN mark a
					List<BIPInteraction> currentMarkedInteractions = new LinkedList<BIPInteraction>();
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
							currentMarkedInteractions.add(interaction);
						}
					}
					nonMarkedInteractions.removeAll(currentMarkedInteractions);
				} while(marking);
				
			}
		}
		return markedComponents.contains(localScViolation.component);
	}
	
	/**
	 * for all wait for paths pi: from Bi to a border node v 
	 * 		if pi contains a node with local super-cycle violation return true
	 * return false;
	 * WL = graph obtained by removing all nodes with no super-cycle violation (WL = subWFGWithoutViolationNodes)
	 * For all border nodes v:
	 *   if v is reachable from Bi then there exists a path from Bi to v with all nodes have no super-cycle violation
	 *   in that case return false; 
	 * return true;
	 */
	private boolean isWaitForPathsToBorderLocalScViolation() {
		for(BIPInteraction interaction: localScViolation.bordersInteraction) {
			if(subWFGWithoutViolationNodes.existPath(localScViolation.component, interaction))
				return false;
		}
		return true; 
	}
	
	

	/**
	 * for all wait for paths, pi, from border node to Bi 
	 * 		if pi contains a node with local super-cycle violation return true
	 * return false;
	 * WL = graph obtained by removing all nodes with no super-cycle violation (WL = subWFGWithoutViolationNodes)
	 * For all border nodes v:
	 *   if Bi is reachable from v then there exists a path from v to Bi with all nodes have no super-cycle violation
	 *   in that case return false; 
	 * return true;
	 */
	private boolean isWaitForPathsFromBorder() {
		for(BIPInteraction interaction: localScViolation.bordersInteraction) {
			if(subWFGWithoutViolationNodes.existPath(interaction, localScViolation.component))
				return false;
		}
		return true; 
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
