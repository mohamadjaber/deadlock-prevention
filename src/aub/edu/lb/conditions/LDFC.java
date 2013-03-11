package aub.edu.lb.conditions;


import aub.edu.lb.configuration.Configuration;
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
		// Debug
		Configuration.startTime = System.currentTimeMillis();

		Kripke kripke = new Kripke(subSystem);
		for(KripkeState state : kripke.getStates()) {
			for(Transition transition: state.getTransitions()) {
				if(transition.getLabel().equals(interaction)) {
					WaitForGraph wfg = new WaitForGraph(transition.getEndState().getState());
					if(!wfg.checkNoInNoOut(interaction.getComponents(), subSystem.getLength() + 1)) {
						return false;
					}
					
				}
			}
		}
		// Debug
		Configuration.stopTime = System.currentTimeMillis();
		Configuration.totalTime += (Configuration.stopTime - Configuration.startTime);
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
					Configuration.println("Increasing -> Length = " + subSystem.getLength());
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
			// Debug
			Configuration.print("\nInteraction: " + interaction);

			SubSystemDepth subSystem = new SubSystemDepth(interaction);
			
			// Debug
			Configuration.println(" - Length = "+ subSystem.getLength());
			
			if(!checkLocalLDFC(subSystem, interaction)) {
				return false;
			}
			else {
				// Debug
				Configuration.println("locLDFC(a,"+subSystem.getLength() +") = true");
			}
		}
		return true;
	}
	
}
