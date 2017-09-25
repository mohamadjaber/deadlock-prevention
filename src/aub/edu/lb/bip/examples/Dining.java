package aub.edu.lb.bip.examples;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Dining {
	private Integer nbOfPhilosophers;
	private String FileName; 

	private PrintStream BIPFile ; 
	
	public void generateDiningBIP(int nbOfPhilosophers) {
		try {
			this.nbOfPhilosophers = nbOfPhilosophers;
			FileName = "BIPExamples/dining"+ nbOfPhilosophers + ".bip"; 
	
			BIPFile = new PrintStream(new File(FileName));
			BIPFile.println("model dining");
			CreateConnectors();
			CreateAtomics();
			CreateCompoundType();
			
	
			BIPFile.println("component DiningPhilosopher top");
			BIPFile.write("end\n".getBytes());
			BIPFile.close();
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
	
	

	private void CreateConnectors() throws IOException {
		BIPFile.println("  connector type SyncThree(Port p1, Port p2, Port p3)");
		BIPFile.println("    define  p1 p2 p3");
		BIPFile.println("    on p1 p2 p3 provided true ");
		BIPFile.println("      up   {}");
		BIPFile.println("      down {}");
		BIPFile.println("  end");
	}
	
	private void CreateAtomics() throws IOException {
		BIPFile.println("  //Atomic component for Philosopher");
		BIPFile.println("  atomic type Philosopher(int id)");
		BIPFile.println("    export port Port get()");
		BIPFile.println("	 export port Port release()");
		    
		BIPFile.println("    place Hungry, Eating");
		BIPFile.println("    initial to Hungry do {}");
		   
		BIPFile.println("    on get from Hungry to Eating ");
		BIPFile.println("      provided true ");
		BIPFile.println("      do { printf(\"Philosopher %d starts eating\\n\", id); }");
		 
		BIPFile.println("    on release from Eating to Hungry ");
		BIPFile.println("      provided true ");
		BIPFile.println("      do { printf(\"Philosopher %d finishes eating\\n\", id); }");
		BIPFile.println("  end");

		BIPFile.println("  // Atomic component for Fork");
		BIPFile.println("  atomic type Fork(int id)");
		BIPFile.println("    export port Port get()");
		BIPFile.println("    export port Port release()");
		    
		BIPFile.println("    place Available, Occupied");
		BIPFile.println("   initial to Available do {}");
		    
		BIPFile.println("    on get from Available to Occupied ");
		BIPFile.println("      provided true ");
		BIPFile.println("     do { printf(\"Fork %d has been reserved by the philo in the Right\\n\", id); }");
		
	
		BIPFile.println("    on release from Occupied to Available ");
		BIPFile.println("      provided true ");
		BIPFile.println("     do { printf(\"Fork %d has been released from the philo in the Right\\n\", id); }");

		BIPFile.println("  end");
	}
	
	

	private void CreateCompoundType() throws IOException {
		BIPFile.println("\n\n  compound type DiningPhilosopher");
		for(int i = 0 ; i < nbOfPhilosophers ; i++)
		{
			BIPFile.println("    component Philosopher p"+i+"("+i+")");
			BIPFile.println("    component Fork f"+i+"("+i+")");
		}
		for(int i = 0 ; i < nbOfPhilosophers ; i++)
		{
			BIPFile.println("    connector SyncThree connGet"+ i + "(f"+ i + ".get, p"+ i + ".get, f"+ (i+1)%nbOfPhilosophers + ".get)");
			BIPFile.println("    connector SyncThree connRelease"+ i + "(f"+ i + ".release, p"+ i + ".release, f"+ (i+1)%nbOfPhilosophers + ".release)");
		}

		BIPFile.println("end\n");
	}



}
