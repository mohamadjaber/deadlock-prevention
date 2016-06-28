package aub.edu.lb.conditions.localAndOr;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import BIPTransformation.TransformationFunction;
import aub.edu.lb.conditions.CheckableCondition;
import aub.edu.lb.conditions.LocalCompleteDeadlockFreeCondition;
import aub.edu.lb.configuration.Configuration;
import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;
import aub.edu.lb.kripke.WaitForGraph;
import aub.edu.lb.logging.LogFormatter;
import aub.edu.lb.model.BIPAPI;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.GlobalState;
import aub.edu.lb.model.SubSystem;
import aub.edu.lb.model.SubSystemDepth;

public class LALT implements CheckableCondition {
	private static Logger log = Logger.getLogger(LocalCompleteDeadlockFreeCondition.class.getName());
	protected SubSystemDepth subSystem; 
	int maxLength = 0;
	long maxStates = 0;
	
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

		log.info("Number states of the full system: " + BIPAPI.getNumberStates() + "\n");
		
		for(BIPInteraction interaction: BIPAPI.getInteractions()) {
			log.info("\nInteraction: " + interaction);
			subSystem = new SubSystemDepth(interaction);
			if(!laltInteraction(interaction)) return false;
			else {
				maxLength = Math.max(maxLength, subSystem.getLength());
				log.info(getName() + "(" + interaction + ","+ subSystem.getLength() +") = true\n");
			}
			log.info("------------------------------------------");
		}
		
		log.info("\n------------------------------------------\n");
		log.info("Max Length: " + maxLength + "\n");
		log.info("Max States: " + maxStates + "\n");
		return true;
	}
	
	private void printLog() {
		log.info("\nLength = " + subSystem.getLength() + "\n");
		long nbStates = subSystem.getNumberStates();
		maxStates = Math.max(maxStates, nbStates);
		log.info("Number states of the subSystem: " + nbStates + "\n");
		// log.info(subSystem.toString() + "\n");
		log.info("Components: " + subSystem.getComponents().size() + " out of " + BIPAPI.getComponents().size() + "\n");
	}
	
	private boolean laltInteraction(BIPInteraction interaction) {
		while(true) {
			printLog();
			// initially, length, i.e. l, is equal to 1
			if(laltInteractionDistance(interaction)) return true;
			if(!subSystem.increase()) return false;
		}
	}
	
	/*
	 * 
	 */
	private boolean laltInteractionDistance(BIPInteraction interaction) {
		// Debug
		Configuration.startTime = System.currentTimeMillis();

		Kripke kripke = new Kripke(subSystem);
		
		for(KripkeState state : kripke.getStates()) {
			for(Transition transition: state.getTransitions()) {
				if(transition.getLabel().equals(interaction)) {
					if(!localFormViolation(interaction.getComponents(), kripke, transition.getEndState().getState())) {
						// Debug
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
	

	private boolean localFormViolation(List<Component> components, Kripke kripke, GlobalState state) {
		for (Component component : components) {
			if (!localFormViolation(component, kripke, state)) 
				return false;
		}
		return true;
	}

	private boolean localFormViolation(Component component, Kripke kripke, GlobalState state) {
		WaitForGraph wfg = new WaitForGraph(state);
		LocalScViolation localScViolation = new LocalScViolation(component, wfg, subSystem);
		if(localScViolation.islocalScViolation()) return true;
		LocalSconnViolation localSconnViolation = new LocalSconnViolation(localScViolation);
		return localSconnViolation.isLocalSconnViolation();
	}

	public boolean initialSuperCycle() {
		SubSystem system = new SubSystem(BIPAPI.getComponents(), BIPAPI.getInteractions());
		WaitForGraph wfg = new WaitForGraph(system.getInitialState());
		return wfg.superCycle();
	}
	
	public String getName() {
		return "LALT";
	}
	
}
