package aub.edu.lb.conditions.localAndOr;

import java.util.List;

import aub.edu.lb.architectures.TarjanSCC;
import aub.edu.lb.kripke.WaitForGraph;

public class LocalSconnViolation {
	private LocalScViolation localScViolation; 
	
	public LocalSconnViolation(LocalScViolation localScViolation) {
		this.localScViolation = localScViolation;	
		
		

		
	}
	
	private boolean isSSCInSubWFGWithNoViolation() {
		WaitForGraph subWFGWithoutViolationNodes = removeNodesLocalScViolation();
		WFGMatrix wfgMatrix = new WFGMatrix(subWFGWithoutViolationNodes);
		List<List<Integer>> stronglyConnectedParts = new TarjanSCC().scc(wfgMatrix.getGraph());
		
		/*
		 * strongly connected parts = C1, C2, ..., Cn
		 * Ci contains strongly connected super-cycle iff
		 * 		1. for all interactions a in Ci: exist B in Ci such that a -> B
		 * 		2. for all components B in Ci: if B -> a (in the full wait-for-graph) then a in C
		 */
		for(List<Integer> strongConnectedPart: stronglyConnectedParts) {
			int idComponent = wfgMatrix.getStateId(localScViolation.component);
			if(strongConnectedPart.contains(idComponent)) {
				
			}
		}
		
		
		return false;
	}
	
	private boolean isWaitForPathsToBorderLocalScViolation() {
		/*
		 * for all wait for paths pi: from Bi to a border node v 
		 * 		if pi contains a node with local super-cycle violation return true
		 * return false;
		 * 
		 * This can be computed as follows: 
		 * 		1. compute reachable nodes from Bi; reachable(Bi) = BFS(Bi) or DFS(Bi)
		 * 		2. compute reachable nodes from v to Bi in the transpose graph; reachableT(v) 
		 *      3. if all the nodes in reachable(Bi) intersects reachableT(v) do not have local sc violation 
		 *      	return false; 
		 *      return true; 
		 */
		return false; 
	}
	
	private WaitForGraph removeNodesLocalScViolation() {
		return new WaitForGraph(localScViolation.wfg, 
				localScViolation.scFormationComponents, 
				localScViolation.scFormationInteractions);
	}
	
	
}
