package aub.edu.lb.conditions;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ujf.verimag.bip.Core.Interactions.CompoundType;
import BIPTransformation.TransformationFunction;
import aub.edu.lb.configuration.Configuration;
import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.logging.LogFormatter;
import aub.edu.lb.model.BIPAPI;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.SubSystem;
import aub.edu.lb.model.SubSystemDepth;

public class LocalDeadlockFreeCondition implements CheckableCondition {
	
	protected SubSystemDepth subSystem; 
	private static Logger log = Logger.getLogger(LocalCompleteDeadlockFreeCondition.class.getName());
	
	static {
		LogManager.getLogManager().reset();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new LogFormatter());
		log.addHandler(handler);
	}
	
	public LocalDeadlockFreeCondition(String fileName, boolean debug) {
		CompoundType ct = TransformationFunction.ParseBIPFile(fileName);
		BIPAPI.initialize(ct);
		if(!debug) log.setLevel(Level.OFF);
	}
	
	/**
	 * 
	 * @param subSystem
	 * @param interaction
	 * @return
	 */
	protected boolean localLDFC(BIPInteraction interaction) {
		// Debug
		Configuration.startTime = System.currentTimeMillis();
		Kripke kripke = new Kripke(subSystem);

		for(KripkeState state : kripke.getStates()) {
			for(Transition transition: state.getTransitions()) {
				if(transition.getLabel().equals(interaction)) {
					WaitForGraph wfg = new WaitForGraph(transition.getEndState().getState());
					if(!wfg.checkNoInNoOut(interaction.getComponents(), subSystem.getLength())) {
						Configuration.stopTime = System.currentTimeMillis();
						Configuration.totalTime += (Configuration.stopTime - Configuration.startTime);
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
	
	private void printLog() {
		log.info("\nLength = " + subSystem.getLength() + "\n");
		log.info("Number states of the subSystem: " + subSystem.getNumberStates() + "\n");
		// log.info(subSystem.toString() + "\n");
		log.info("Components: " + subSystem.getComponents().size() + " out of " + BIPAPI.getComponents().size() + "\n");
	}
	
	protected boolean checkLocalLDFC(BIPInteraction interaction) {
		while(true) {
			printLog();
			if(localLDFC(interaction)) 	return true;
			if(!subSystem.increase()) return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean check() {
		if(initialSuperCycle()) {
			return false;
		}
		
		int maxLength = 0;
		
		log.info("Number states of the full system: " + BIPAPI.getNumberStates() + "\n");

		for(BIPInteraction interaction: BIPAPI.getInteractions()) {
			log.info("\nInteraction: " + interaction);
			subSystem = new SubSystemDepth(interaction);			
			if(!checkLocalLDFC(interaction)) return false;
			else {
				maxLength = Math.max(maxLength, subSystem.getLength());
				log.info(getName() + "(" + interaction + "," + subSystem.getLength() +") = true\n");
			}
		}
		log.info("Max Length: " + maxLength);
		return true;
	}
	
	public boolean initialSuperCycle() {
		SubSystem system = new SubSystem(BIPAPI.getComponents(), BIPAPI.getInteractions());
		WaitForGraph wfg = new WaitForGraph(system.getInitialState());
		return wfg.superCycle();
	}
	
	public String getName() {
		return "LLIN";
	}
	
}
