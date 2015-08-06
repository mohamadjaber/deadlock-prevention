package aub.edu.lb.bip.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class ResourceAllocation {
	private static PrintStream bipFile ; 

	static String space = "\t";
	static int connectorCounter = 0;

	
	static String generateClient(int[] resources, int id) {
		String output = "";
		output += "atomic type Client" + id + "\n";
		
		for(int i = 0; i < resources.length; i++) {
			output += space + "export port Port pRequest" + resources[i] + "()\n"; 
		}
		
		for(int i = 0; i < resources.length; i++) {
			output += space + "export port Port pGrant" + resources[i] + "()\n"; 
		}
		
		output += space + "export port Port releaseAll()" + "\n"; 

		output += space + "place l0";
		
		for(int i = 0; i < resources.length; i++) {
			output += ", lR" + resources[i]; 
			output += ", lG" + resources[i]; 
		}
		
		output += "\n";
		output += space + "initial to l0 do {}\n"; 
		
		output += space + "on pRequest" + resources[0] + " from l0 to lR" + resources[0] + " provided true do {}\n";
		output += space + "on pGrant" + resources[0] + " from lR" + resources[0] + " to lG" + resources[0] + " provided true do {}\n";

		
		for(int i = 1; i < resources.length; i++) {
			output += space + "on pRequest" + resources[i] + " from lG" + resources[i-1] + " to lR" + resources[i] + " provided true do {}\n";
			output += space + "on pGrant" + resources[i] + " from lR" + resources[i] + " to lG" + resources[i] + " provided true do {}\n";

		}
		
		output += space + "on releaseAll from lG" + resources[resources.length - 1] + " to l0 provided true do {}\n";

		output += "end\n";
		return output;
	}
	
	static String generateResource() {
		String output = "";
		output += "atomic type Resource\n";
		
		output += space + "export port Port rcvRequest()" + "\n"; 
		output += space + "export port Port rcvToken()" + "\n"; 
		output += space + "export port Port sendResource()" + "\n"; 
		output += space + "export port Port rcvResource()" + "\n"; 
		output += space + "export port Port sendToken()" + "\n"; 

		output += space + "place l0, l1, l2, l3, l4\n";

		output += space + "initial to l0 do {}\n"; 

		output += space + "on rcvRequest from l0 to l1 provided true do {}\n"; 
		output += space + "on rcvToken from l1 to l2 provided true do {}\n"; 
		output += space + "on sendResource from l2 to l3 provided true do {}\n"; 
		output += space + "on rcvResource from l3 to l4 provided true do {}\n"; 
		output += space + "on sendToken from l4 to l0 provided true do {}\n";
		
		output += "end\n";
		return output;
	}
	
	static String generateTokenRing(boolean token) {
		String output = "";
		
		if(token )
			output += "atomic type TokenRingHasToken\n";
		else 
			output += "atomic type TokenRingNoToken\n";

		output += space + "export port Port releaseToken()" + "\n"; 
		output += space + "export port Port getToken()" + "\n"; 

		output += space + "place lHasToken, lNoToken\n";

		if(token)
			output += space + "initial to lHasToken do {}\n"; 
		else
			output += space + "initial to lNoToken do {}\n"; 

		output += space + "on releaseToken from lHasToken to lNoToken provided true do {}\n"; 
		output += space + "on getToken from lNoToken to lHasToken provided true do {}\n"; 
	
		output += "end\n";
		return output;
	}
	
	
	static String generateResourceTokenConnections(int id) {
		String output = "";

		output += space + "connector Sync2 conn" + (connectorCounter++) + "(" +
 					"r" + id + ".rcvToken" + ", t" + id + ".releaseToken" + ")\n";
		
		output += space + "connector Sync2 conn" + (connectorCounter++) + "(" +
					"r" + id + ".sendToken" + ", t" + id + ".getToken" + ")\n";
		
		return output;
	}
	
	static String generateClientResourceConnections(int clientId, int[] resources) {
		String output = "";

		for(int i = 0; i < resources.length; i++) {
			output += space + "connector Sync2 conn" + (connectorCounter++) + "(" +
 					"c" + clientId + ".pRequest" + resources[i] + ", r" + resources[i] + ".rcvRequest" + ")\n";
			output += space + "connector Sync2 conn" + (connectorCounter++) + "(" +
					"c" + clientId + ".pGrant" + resources[i] + ", r" + resources[i] + ".sendResource" + ")\n";
		}
		
		output += space + "connector Sync" + (resources.length + 1) + " conn" + (connectorCounter++) + "(c" + clientId + ".releaseAll, ";
		for(int i = 0; i < resources.length - 1; i++) {
			output += "r" + resources[i] + "." + "rcvResource" + ", ";
		}	
		output += "r" + resources[resources.length - 1] + "." + "rcvResource" + ")\n";
		
		return output;
	}
	
	static String generateTokenTokenConnections(int numberOfResources) {
		String output = "";

		for(int i = 0; i < numberOfResources; i++) {
			output += space + "connector Sync2 conn" + (connectorCounter++) + "(" +
 					"t" + i + ".releaseToken" + ", t" + ((i+1) % numberOfResources) + ".getToken" + ")\n";
		}
		return output;
	}
	
	static String generateConnectorType(int n) {
		String output = "";
		
		output += "connector type Sync" + n + "("; 
		for(int i = 0; i < n - 1; i++) {
			output += "Port p" + i + ",";
		}
		
		output += "Port p" + (n - 1) + ")\n" + space;
		
		output += "define ";
		for(int i = 0; i < n; i++) {
			output += "p" + i + " ";
		}
		
		output += "\n" + space + "on ";
		
		for(int i = 0; i < n; i++) {
			output += "p" + i + " ";
		}
		output += "provided true up {} down {}\n";
		
		output += "end\n";
		return output; 
	}
	
	
	public static String generateResourceAllocation(int nbOfClients, int nbOfResources, int[][] resourceMapping, int availableTokens) {
		String output = "model resourceAllocation\n\n";
		
		int max = 2; 
		// generating atomic types
		for(int i = 0; i < nbOfClients; i++) {
			output += generateClient(resourceMapping[i], i);
			if(max < resourceMapping[i].length) max = resourceMapping[i].length;
			
		}
		output += generateResource();
		output += generateTokenRing(true);
		output += generateTokenRing(false);

		
		// generate connector types
		for(int i = 2; i <= max + 1; i++)
			output += generateConnectorType(i); 
		
		output += "\n";
		
		output += "compound type ResourceAllocationTokenRing\n";
		
		for(int i = 0; i < nbOfClients; i++) {
			output += space + "component Client" + i + " c" + i + "\n";
		}
		
		
		// split tokens randomly initially 
		boolean[] tokens = new boolean[nbOfResources];
		int counter = 0;
		while(counter < availableTokens) {
			int i = (int) (Math.random() * nbOfResources); 
			if(!tokens[i]) {
				tokens[i] = true; 
				counter++; 
			}
		}
		
		for(int i = 0; i < nbOfResources; i++) {
			output += space + "component Resource" + " r" + i + "\n";
			
			if(tokens[i])
				output += space + "component TokenRingHasToken" + " t" + i + "\n";
			else 
				output += space + "component TokenRingNoToken" + " t" + i + "\n";
		}
		
		for(int i = 0; i < nbOfResources; i++) {
			output += generateResourceTokenConnections(i);
		}
		
		for(int i = 0; i < nbOfClients; i++) {
			output += generateClientResourceConnections(i, resourceMapping[i]);
		}
		
		output += generateTokenTokenConnections(nbOfResources) + "\n";
		output += "end\n\n";
		
		output += "component ResourceAllocationTokenRing top\n";
		output += "end\n\n";

		return output; 
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		int nbOfClients = 5; 
		int nbOfResources = 5; 
		int[][] resourceMapping = {{0, 1}, {0,1}, {2, 3}, {2, 3}, {4}};
		int nbOfTokens = 4; 
		String fileName = "BIPExamples/resourceAllocation_" + nbOfClients + "_" + nbOfResources + "_" + nbOfTokens + ".bip";
		
		bipFile = new PrintStream(new File(fileName));
		bipFile.print(generateResourceAllocation(nbOfClients, nbOfResources, resourceMapping, nbOfTokens));
	}

}
