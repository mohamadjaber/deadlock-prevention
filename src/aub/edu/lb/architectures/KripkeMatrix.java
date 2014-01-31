package aub.edu.lb.architectures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;



public class KripkeMatrix  {

	private Map<KripkeState, Integer> mapStateId;
	private Map<Integer, KripkeState> mapIdState;

	private List<Integer>[] graph; 
	private Kripke kripke;
	
	public KripkeMatrix(Kripke kripke) {
		this.kripke = kripke;
		initializaMapStateId();
		setGraph();
		
	}
	
	@SuppressWarnings("unchecked")
	private void setGraph() {
		graph = new List[kripke.getStates().size()];
		
		for (int i = 0; i < graph.length; i++) {
			graph[i] = new ArrayList<>();
		}
		
		for(KripkeState fromState: kripke.getStates()) {
			for(Transition transition: fromState.getTransitions()) {
				graph[getStateId(fromState)].add(getStateId(transition.getEndState()));
			}
		}
	}

	private void initializaMapStateId() {
		int counter = 0; 
		mapStateId = new HashMap<KripkeState, Integer>();
		mapIdState = new HashMap<Integer, KripkeState>();

		for(KripkeState state: kripke.getStates()) {
			mapStateId.put(state, counter);
			mapIdState.put(counter, state);
			counter++;
		}
	}
	
	
	
	public int getStateId(KripkeState state) {
		return mapStateId.get(state);
	}
	
	public KripkeState getKripkeState(int id) {
		return mapIdState.get(id);
	}
	
	
	
	
	public List<Integer>[] getGraph() {
		return graph; 
	}
	

}
