package aub.edu.lb.conditions;


import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.model.BIPAPI;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.SubSystemDepth;

public class LDFC {
	/**
	 * 
	 * @param subSystem
	 * @param interaction
	 * @return
	 */
	private static boolean localLDFC(SubSystemDepth subSystem, BIPInteraction interaction) {
		Kripke kripke = new Kripke(subSystem);
		for(KripkeState state : kripke.getStates()) {
			for(Transition transition: state.getTransitions()) {
				if(transition.getLabel().equals(interaction)) {
					WaitForGraph wfg = new WaitForGraph(transition.getEndState().getState());
					if(!wfg.checkNoInNoOut(interaction.getComponents(), subSystem.getLength())) {
						return false;
					}
				}
			}
		}
		return true; 
	}
	
	private static boolean checkLocalLDFC(SubSystemDepth subSystem, BIPInteraction interaction) {
		while(true) {
			if(localLDFC(subSystem, interaction)) {
				return true;
			}
			else {
				boolean isIncreased = subSystem.increase();
				if(isIncreased) {
					// Print Debug
				}
				else  {
					return false;
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public static boolean check() {
		for(BIPInteraction interaction: BIPAPI.getInteractions()) {
			SubSystemDepth subSystem = new SubSystemDepth(interaction);
			if(!checkLocalLDFC(subSystem, interaction))
				return false;	
		}
		return true;
	}
	
}
