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
	
	static String generateTokenRingConflict(boolean token) {
		String output = "";
		
		if(token )
			output += "atomic type TokenRingConflictHasToken\n";
		else 
			output += "atomic type TokenRingConflictNoToken\n";

		output += space + "export port Port releaseTokenToken()" + "\n"; 
		output += space + "export port Port getTokenToken()" + "\n"; 
		output += space + "export port Port releaseTokenResource()" + "\n"; 
		output += space + "export port Port getTokenResource()" + "\n"; 

		output += space + "place lHasToken, lNoToken, lTokenWithResource, lTokenTokenWithResource\n";

		if(token)
			output += space + "initial to lHasToken do {}\n"; 
		else
			output += space + "initial to lNoToken do {}\n"; 

		output += space + "on releaseTokenToken from lHasToken to lNoToken provided true do {}\n";
		output += space + "on releaseTokenToken from lTokenTokenWithResource to lTokenWithResource provided true do {}\n";

		output += space + "on getTokenToken from lNoToken to lHasToken provided true do {}\n"; 
		output += space + "on getTokenToken from lTokenWithResource to lTokenTokenWithResource provided true do {}\n"; 

		output += space + "on releaseTokenResource from lHasToken to lTokenWithResource provided true do {}\n"; 
		// TO BE REMOVED -- CHECK...
		// output += space + "on getTokenResource from lNoToken to lHasToken provided true do {}\n"; 
		output += space + "on getTokenResource from lTokenWithResource to lHasToken provided true do {}\n"; 
		
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
	
	static String generateResourceConflictTokenConnections(int[][] conflictingResources) {
		String output = "";
		for(int i = 0; i < conflictingResources.length; i++) {
			for(int j = 0; j < conflictingResources[i].length; j++) {
				output += space + "connector Sync2 conn" + (connectorCounter++) + "(" +
	 					"r" + conflictingResources[i][j] + ".rcvToken" + ", t" + i + ".releaseTokenResource" + ")\n";
				output += space + "connector Sync2 conn" + (connectorCounter++) + "(" +
						"r" + conflictingResources[i][j] + ".sendToken" + ", t" + i + ".getTokenResource" + ")\n";
			}
		}
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
	
	static String generateTokenTokenConnectionsConflict(int numberOfResources) {
		String output = "";

		for(int i = 0; i < numberOfResources; i++) {
			output += space + "connector Sync2 conn" + (connectorCounter++) + "(" +
 					"t" + i + ".releaseTokenToken" + ", t" + ((i+1) % numberOfResources) + ".getTokenToken" + ")\n";
		}
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
	
	
	public static String resourceAllocationConflicting(int nbOfClients, int nbOfResources, int[][] resourceMapping, 
			int availableTokens, int[][] conflictingResources)  {
		String output = "model resourceAllocation\n\n";
		
		int max = 2; 
		// generating atomic types
		for(int i = 0; i < nbOfClients; i++) {
			output += generateClient(resourceMapping[i], i);
			if(max < resourceMapping[i].length) max = resourceMapping[i].length;
			
		}
		output += generateResource();
		output += generateTokenRingConflict(true);
		output += generateTokenRingConflict(false);

		
		// generate connector types
		for(int i = 2; i <= max + 1; i++)
			output += generateConnectorType(i); 
		
		output += "\n";
		
		output += "compound type ResourceAllocationTokenRing\n";
		
		for(int i = 0; i < nbOfClients; i++) {
			output += space + "component Client" + i + " c" + i + "\n";
		}
		
		int nbOfTokenComponents = conflictingResources.length; 
		
		// split tokens randomly initially 
		boolean[] tokens = new boolean[nbOfTokenComponents];
		int counter = 0;
		while(counter < availableTokens) {
			int i = (int) (Math.random() * nbOfTokenComponents); 
			if(!tokens[i]) {
				tokens[i] = true; 
				counter++; 
			}
		}
		
		for(int i = 0; i < nbOfResources; i++) {
			output += space + "component Resource" + " r" + i + "\n";
		}
		for(int i = 0; i < nbOfTokenComponents; i++) {
			if(tokens[i])
				output += space + "component TokenRingConflictHasToken" + " t" + i + "\n";
			else 
				output += space + "component TokenRingConflictNoToken" + " t" + i + "\n";
		}
		
		
		output += generateResourceConflictTokenConnections(conflictingResources);

		
		for(int i = 0; i < nbOfClients; i++) {
			output += generateClientResourceConnections(i, resourceMapping[i]);
		}
		
		output += generateTokenTokenConnectionsConflict(nbOfTokenComponents) + "\n";
		output += "end\n\n";
		
		output += "component ResourceAllocationTokenRing top\n";
		output += "end\n\n";

		return output; 
	}
	
	
	public static String resourceAllocation(int nbOfClients, int nbOfResources, int[][] resourceMapping, int availableTokens) {
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
	
	public static void generateTokenRing() throws FileNotFoundException {
		int nbOfClients = 6; 
		int nbOfResources = 10; 
		int[][] resourceMapping = {{0, 1}, {0,1}, {2} , {3}, {4}, {5, 6, 7, 8, 9}};
		int nbOfTokens = 7; 
		String fileName = "BIPExamples/resourceAllocation_" + nbOfClients + "_" + nbOfResources + "_" + nbOfTokens + ".bip";
		
		bipFile = new PrintStream(new File(fileName));
		bipFile.print(resourceAllocation(nbOfClients, nbOfResources, resourceMapping, nbOfTokens));
	}
	
	public static void generateTokenRing1() throws FileNotFoundException {
		int nbOfClients = 2; 
		int nbOfResources = 2; 
		int[][] resourceMapping = {{0, 1}, {1,0}};
		int nbOfTokens = 2; 
		String fileName = "BIPExamples/resourceAllocation_" + nbOfClients + "_" + nbOfResources + "_" + nbOfTokens + ".bip";
		
		bipFile = new PrintStream(new File(fileName));
		bipFile.print(resourceAllocation(nbOfClients, nbOfResources, resourceMapping, nbOfTokens));
	}
	
	// No local deadlock - although some components (e.g., c0, may block for ever
	// However, there is no sub system that can detect this deadlock. 
	public static void generateTokenRingConflict() throws FileNotFoundException {
		int nbOfClients = 5; 
		int nbOfResources = 5; 
		int[][] resourceMapping = {{0, 1}, {1, 0}, {2} , {3}, {4}};
		int[][] conflictingResources = {{0, 1}, {2, 3, 4}};
		int nbOfTokens = 2; 
		String fileName = "BIPExamples/resourceAllocationConflict_" + nbOfClients + "_" + nbOfResources + "_" + nbOfTokens + "xxx.bip";
		
		bipFile = new PrintStream(new File(fileName));
		bipFile.print(resourceAllocationConflicting(nbOfClients, nbOfResources, resourceMapping, nbOfTokens, conflictingResources));
	}
	// Local deadlock exist
	public static void generateTokenRingConflict1() throws FileNotFoundException {
		int nbOfClients = 5; 
		int nbOfResources = 5; 
		int[][] resourceMapping = {{0, 2}, {2, 0}, {2} , {3}, {4}};
		int[][] conflictingResources = {{0, 1}, {2, 3, 4}};
		int nbOfTokens = 2; 
		String fileName = "BIPExamples/resourceAllocationConflict_" + nbOfClients + "_" + nbOfResources + "_" + nbOfTokens + ".bip";
		
		bipFile = new PrintStream(new File(fileName));
		bipFile.print(resourceAllocationConflicting(nbOfClients, nbOfResources, resourceMapping, nbOfTokens, conflictingResources));
	}
	
	// Local deadlock exist
	public static void generateTokenRingConflict2() throws FileNotFoundException {
		int nbOfClients = 15; 
		int nbOfResources = 15; 
		int[][] resourceMapping = {{0, 2}, {1}, {2}, {3}, {0, 2}, {3, 11} , {4, 12}, {5, 13}, {6, 14}, {7}, {8}, {9, 10}, {11}, {2}, {0}};
		int[][] conflictingResources = {{0, 1}, {2, 3, 4}, {5, 6, 7, 8, 9, 10}, {11, 12, 13, 14}};
		int nbOfTokens = 2; 
		String fileName = "BIPExamples/resourceAllocationConflict_" + nbOfClients + "_" + nbOfResources + "_" + nbOfTokens + ".bip";
		
		bipFile = new PrintStream(new File(fileName));
		bipFile.print(resourceAllocationConflicting(nbOfClients, nbOfResources, resourceMapping, nbOfTokens, conflictingResources));
	}

	
	public static void main(String[] args) throws FileNotFoundException {
		generateTokenRing();
		generateTokenRing1();
		generateTokenRingConflict();
	}

}
