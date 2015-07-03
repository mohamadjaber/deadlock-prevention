package aub.edu.lb.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ujf.verimag.bip.Core.Interactions.Component;

/**
 * 
 * @author Mohamad Jaber OVERVIEW: this class constructs a subsystem with a
 *         specific depth given an interaction. Initially, we construct the
 *         system of depth 1. The set of components connected to the interaction
 *         are stored in the components variable
 */

public class SubSystemDepth extends SubSystem {

	private int length;
	private BIPInteraction interaction;

	private ArrayList<BIPInteraction> boundInteractions;

	/**
	 * We start by length = 1. That is, for an interaction a we take the
	 * components of a and their interactions. 
	 * a -> B -> a' (length = 1). 
	 * a -> B -> a' -> B'->a'' (length = 2). and so on. 
	 * See paper - Journal - for more information.
	 * D(a,l) = G(a, 2*l)
	 * @param interaction
	 */
	public SubSystemDepth(BIPInteraction interaction) {
		super(interaction.getComponents(), getInteractions(interaction));
		length = 1;
		boundInteractions = new ArrayList<BIPInteraction>(interactions);
	}

	/**
	 * 
	 * @param interaction
	 * @return: Let B1, ..., B2 are the components connected to the interaction
	 *          This method return all the interactions connected to these
	 *          components (the interaction given as input belongs to the
	 *          returned interactions)
	 */

	private static ArrayList<BIPInteraction> getInteractions(
			BIPInteraction interaction) {
		ArrayList<BIPInteraction> interactionsSub = new ArrayList<BIPInteraction>();
		for (BIPInteraction inter : BIPAPI.getInteractions()) {
			for (Component component : interaction.getComponents()) {
				if (inter.getComponents().contains(component)) {
					interactionsSub.add(inter);
					break;
				}
			}
		}
		return interactionsSub;
	}

	public boolean increase() {		
		return increaseInteractions(increaseComponents());
	}

	/**
	 * Add interactions
	 * 
	 * @return
	 */
	private boolean increaseInteractions(List<Component> boundComponents) {
		boolean isIncreased = false;
		boundInteractions.clear();
		for (BIPInteraction inter : BIPAPI.getInteractions()) {
			for (Component comp : boundComponents) {
				if (inter.getComponents().contains(comp)) {
					if (!interactions.contains(inter)) {
						addInteraction(inter);
						boundInteractions.add(inter);
						isIncreased = true;
					}
				}
			}
		}
		if(isIncreased) length++;
		return isIncreased;
	}

	/**
	 * Add components
	 * 
	 * @return
	 */
	private List<Component> increaseComponents() {
		List<Component> boundComponents = new ArrayList<Component>();
		for (BIPInteraction inter : boundInteractions) {
			for (Component comp : inter.getComponents()) {
				if (!components.contains(comp)) {
					addComponent(comp);
					boundComponents.add(comp);
				}
			}
		}
		return boundComponents;
	}

	/**
	 * 
	 * @param interaction of the subsystem
	 * @return true if the interaction given as input is a border node. 
	 * That is, there exists a component connected to the interaction and 
	 * that component is not in the subsystem. Clearly that if the interaction 
	 * is not in boundInteractions, then it is not border node. 
	 */
	public boolean isBorderInteraction(BIPInteraction interaction) {
		if (boundInteractions.contains(interaction)) {
			for (Component comp : interaction.getComponents()) {
				if (!components.contains(comp)) {
					return true;
				}
			}
		}
		return false;
	}
		
	public List<BIPInteraction> bordersInteraction() {
		List<BIPInteraction> borderInteractions = new LinkedList<BIPInteraction>();
		for(BIPInteraction interaction: interactions) {
			if(isBorderInteraction(interaction))
				borderInteractions.add(interaction);	
		}
		return borderInteractions;
	}

	public int getLength() {
		return length;
	}

	public BIPInteraction getInteraction() {
		return interaction;
	}

}
