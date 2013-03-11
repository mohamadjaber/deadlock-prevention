package aub.edu.lb.model;

import java.util.ArrayList;

import ujf.verimag.bip.Core.Interactions.Component;

/**
 * 
 * @author Mohamad Jaber
 * OVERVIEW: this class constructs a subsystem with a specific depth given an interaction. 
 * Initially, we construct the system of depth 0. The set of components connected to the interaction
 * are stored in the components variable
 */

public class SubSystemDepth extends SubSystem{

	private int length;
	private BIPInteraction interaction; 
	
	private ArrayList<BIPInteraction> boundInteractions;
	private ArrayList<Component> boundComponents;
	
	/**
	 * We start by length = 0. 
	 * That is, for an interaction a we take the components of a and 
	 * their interactions. 
	 * a -> B -> a' (length = 0).
	 * a -> B -> a' -> B' (length = 1). 
	 * and so on.
	 * See paper for more information. 
	 * @param interaction
	 */
	public SubSystemDepth(BIPInteraction interaction) {
		super(interaction.getComponents(), getInteractions(interaction));
		length = 0;	
		boundInteractions = new ArrayList<BIPInteraction>(interactions);
		boundComponents = new ArrayList<Component>();
	}
	

	/**
	 * 
	 * @param interaction
	 * @return: Let B1, ..., B2 are the components connected to the interaction 
	 * This method return all the interactions connected to these components 
	 * (the interaction given as input belongs to the returned interactions)
	 */
	
	private static ArrayList<BIPInteraction> getInteractions(BIPInteraction interaction) {
		ArrayList<BIPInteraction> interactionsSub = new ArrayList<BIPInteraction>();
		for(BIPInteraction inter: BIPAPI.getInteractions()) {
			for(Component component: interaction.getComponents()) {
				if(inter.getComponents().contains(component)) {
					interactionsSub.add(inter);
					break;
				}
			}
		}
		return interactionsSub;
	}
	
	public boolean increase() {
		if(length % 2 == 1) 
			return increaseOdd();
		else 
			return increaseEven();
	}

	
	/**
	 * 
	 * @return
	 */
	private boolean increaseEven() {
		boolean isIncreased = false;		
		boundInteractions.clear();
		for(BIPInteraction inter: BIPAPI.getInteractions()) {
			for(Component comp: boundComponents) {
				if(inter.getComponents().contains(comp)) {
					if(!interactions.contains(inter)) {
						addInteraction(inter);
						boundInteractions.add(inter);
						isIncreased = true;
					}
				}
			}
		}
		length++;
		return isIncreased;
	}


	/**
	 * 
	 * @return
	 */
	private boolean increaseOdd() {
		boolean isIncreased = false;		
		boundComponents.clear();
		for(BIPInteraction inter: boundInteractions) {
			for(Component comp: inter.getComponents()) {
				if(!components.contains(comp)) {
					addComponent(comp);
					boundComponents.add(comp);
					isIncreased  = true;
				}
			}
		}
		length++;
		return isIncreased;
	}

	
	
	
	
	
	
	public int getLength() {
		return length;
	}
	
	public BIPInteraction getInteraction() {
		return interaction;
	}
	

}
