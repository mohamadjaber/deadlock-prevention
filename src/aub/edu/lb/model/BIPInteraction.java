package aub.edu.lb.model;

import java.util.ArrayList;


import ujf.verimag.bip.Core.Behaviors.Port;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.Connector;
import ujf.verimag.bip.Core.Interactions.InnerPortReference;

public class BIPInteraction {
	
	private ArrayList<Component> components;
	private ArrayList<String> ports;
	private int size;
	
	
	/**
	 * 
	 * @param c
	 */
	public BIPInteraction(Connector c) {
		size = c.getActualPort().size();
		components = new ArrayList<Component>(size);
		ports = new ArrayList<String>(size);
		for(Object o: c.getActualPort()) {
			InnerPortReference ipr = (InnerPortReference) o;
			//No hierarchical connectors
			assert(ipr.getTargetInstance().getTargetPart() instanceof Component);
			
			components.add((Component)ipr.getTargetInstance().getTargetPart());
			ports.add(ipr.getTargetPort().getName());
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Component> getComponents() {
		return components;
	}
	
	
	public ArrayList<String> getPorts() {
		return ports;
	}
	
	public String getPort(Component c) {
		int index = components.indexOf(c);
		if(index == -1)
			return null;
		else {
			return ports.get(components.indexOf(c));
		}
	}
	
	public Component getComponent(Port p) {
		return components.get(ports.indexOf(p));
	}
	
	public int size() {
		return size; 
	}
	
	
	 /**
	  * @param gs
	  * @return
	  */
	 @Override
	 public boolean equals(Object obj) {
		if(obj instanceof GlobalState) {
			BIPInteraction interaction = (BIPInteraction) obj;
			if(interaction.size() != size)
				return false;
			else {
				for(Component component: interaction.components) {
					if(!components.contains(component) || 
							(components.contains(component) && 
									!interaction.getPort(component).equals(getPort(component)))) {
						return false;
					}
				}
				return true;
			}
		}
		return super.equals(obj);
	 }
	 
	 
	 public String toString() {
		 String bipInteractionName = "[";
		 for(int i = 0; i < components.size(); i++) 
			 bipInteractionName += components.get(i).getName() + "." + ports.get(i) + " ";
		 return bipInteractionName + "]";
	 }
	
	
	
}
