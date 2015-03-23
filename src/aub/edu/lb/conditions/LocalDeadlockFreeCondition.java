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

public class LocalDeadlockFreeCondition {
	
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
		if(!debug) 
			log.setLevel(Level.OFF);
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
	
	protected boolean checkLocalLDFC(BIPInteraction interaction) {
		while(true) {
			if(localLDFC(interaction)) {
				return true;
			}
			else {
				boolean isIncreased = subSystem.increase();
				if(isIncreased) {
					log.info("Increasing -> Length = " + subSystem.getLength() + "\n");
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
	public boolean check() {
		if(initialSuperCycle()) {
			return false;
		}
		
		for(BIPInteraction interaction: BIPAPI.getInteractions()) {
			log.info("\nInteraction: " + interaction);

		
			
			subSystem = new SubSystemDepth(interaction);
			
			log.info(" - Length = "+ subSystem.getLength() + "\n");
			
			if(!checkLocalLDFC(interaction)) {
				return false;
			}
			else {
				log.info(getName() + "(a,"+subSystem.getLength() +") = true\n");
			}
		}
		return true;
	}
	
	public boolean initialSuperCycle() {
		SubSystem system = new SubSystem(BIPAPI.getComponents(), BIPAPI.getInteractions());
		WaitForGraph wfg = new WaitForGraph(system.getInitialState());
		return wfg.superCycle();
	}
	
	public String getName() {
		return "locLDFC";
	}
	
}
