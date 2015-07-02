package aub.edu.lb.conditions.localAndOr;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import BIPTransformation.TransformationFunction;
import aub.edu.lb.conditions.LocalCompleteDeadlockFreeCondition;
import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.logging.LogFormatter;
import aub.edu.lb.model.BIPAPI;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.SubSystem;
import aub.edu.lb.model.SubSystemDepthLocalAndOr;

public class LALT {
	private static Logger log = Logger.getLogger(LocalCompleteDeadlockFreeCondition.class.getName());
	protected SubSystemDepthLocalAndOr subSystem; 

	static {
		LogManager.getLogManager().reset();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new LogFormatter());
		log.addHandler(handler);
	}
	
	public LALT(String fileName, boolean debug) {
		CompoundType ct = TransformationFunction.ParseBIPFile(fileName);
		BIPAPI.initialize(ct);
		if(!debug) 
			log.setLevel(Level.OFF);
	}
	
	public boolean check() {
		if(initialSuperCycle()) return false;
		
		for(BIPInteraction interaction: BIPAPI.getInteractions()) {
			log.info("\nInteraction: " + interaction);
			
			if(!laltInteraction(interaction)) return false;
		}
	
		return true;
	}
	
	
	private boolean laltInteraction(BIPInteraction interaction) {
		while(true) {
			// initially, length, i.e. l, is equal to 1
			subSystem = new SubSystemDepthLocalAndOr(interaction);
			if(laltInteractionDistance(interaction)) return true;
			if(!subSystem.increase()) return false;
			log.info("Increasing -> Length = " + subSystem.getLength() + "\n");
		}
	}
	
	
	private boolean laltInteractionDistance(BIPInteraction interaction) {
		Kripke kripke = new Kripke(subSystem);
		
		for(Transition transition: kripke.getTransitions()) {
			if(transition.getLabel().equals(interaction)) {
				for(Component component: interaction.getComponents()) {
					if(!LocalFormViolation(component, kripke, transition.getEndState())) return false;
				}
			}
		}
		return true;
	}

	private boolean LocalFormViolation(Component component, Kripke kripke, KripkeState state) {
		WaitForGraph wfg = new WaitForGraph(state.getState());

		return false;
	}

	public boolean initialSuperCycle() {
		SubSystem system = new SubSystem(BIPAPI.getComponents(), BIPAPI.getInteractions());
		WaitForGraph wfg = new WaitForGraph(system.getInitialState());
		return wfg.superCycle();
	}
	
}
