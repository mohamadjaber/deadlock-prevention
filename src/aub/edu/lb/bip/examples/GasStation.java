package aub.edu.lb.bip.examples;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class GasStation {
	private int nbOfPumps;
	private int nbOfCustomers; 
	private String FileName; 
	private String FileNameInc; 

	private PrintStream BIPFile ; 
	private PrintStream INCFile ; 

	
	public String generateGasStation(int nbOfCustomers, int nbOfPumps) {
		try {
			this.nbOfCustomers = nbOfCustomers;
			this.nbOfPumps = nbOfPumps;

			
			FileName = "BIPExamples/gas_station" + nbOfCustomers + ".bip";
			FileNameInc = "BIPExamples/gas_station" + nbOfCustomers + ".incr"; 

	
			BIPFile = new PrintStream(new File(FileName));
			INCFile = new PrintStream(new File(FileNameInc));
			BIPFile.println("model gas");
			CreateConnectors();
			CreateAtomics();
			CreateCompoundType();
			
	
			BIPFile.println("component GasStation_Type top");
			BIPFile.write("end\n".getBytes());
			BIPFile.close();
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return FileName;
	}
	
	

	private void CreateConnectors() throws IOException {
		BIPFile.println("  connector type rendezvous2(Port p1, Port p2)");
		BIPFile.println("    define [ p1 p2 ]");
		BIPFile.println("  end");
		
		BIPFile.println("  connector type rendezvous3(Port p1, Port p2, Port p3)");
		BIPFile.println("    define [ p1 p2 p3]");
		BIPFile.println("  end");
		
		BIPFile.println("  connector type single(Port p1)");
		BIPFile.println("    define [ p1]");
		BIPFile.println("  end");
	}
	
	private void CreateAtomics() throws IOException {
		BIPFile.println("\n  atomic type Customers");
		BIPFile.println("    export port Port prepay");
		BIPFile.println("    export port Port start");
		BIPFile.println("    export port Port finish");
		BIPFile.println("    export port Port change");
		BIPFile.println("    place S0, S1, S2, S3");
		BIPFile.println("    initial to S0");
		BIPFile.println("    on prepay from S0 to S1");
		BIPFile.println("    on start from S1 to S2");
		BIPFile.println("    on finish from S2 to S3");
		BIPFile.println("    on change from S3 to S0");
		BIPFile.println("  end\n\n");

		BIPFile.println("  atomic type Pumps");
		BIPFile.println("  	export port Port activate");
		BIPFile.println("  	export port Port start");
		BIPFile.println("  	export port Port finish");
		BIPFile.println("  	place S0, S1, S2");
		BIPFile.println("  	initial to S0");
		BIPFile.println("  		on activate from S0 to S1");
		BIPFile.println("  		on start from S1 to S2");
		BIPFile.println("  		on finish from S2 to S0");
		BIPFile.println("  end\n");
		
		BIPFile.println("  atomic type Operator");
		BIPFile.println("    export port Port finish");
		BIPFile.println("    export port Port change");

		for(int i = 0; i < nbOfPumps; i++) {
			BIPFile.println("    export port Port prepay" + i);
		}
		BIPFile.println("    place S0, S1");
		BIPFile.println("    initial to S0");
		for(int i = 0; i < nbOfPumps; i++) {
			BIPFile.println("    on prepay" + i+  " from S0 to S0");
		}
		BIPFile.println("    on finish from S0 to S1");
		BIPFile.println("    on change from S1 to S0");
		BIPFile.println("  end");
	}
	
	

	private void CreateCompoundType() throws IOException {
		BIPFile.println("\n\n  compound type GasStation_Type");
		for(int i = 0 ; i < nbOfCustomers ; i++)
		{
			BIPFile.println("    component Customers custom"+i);
		}
		
		for(int i = 0 ; i < nbOfPumps ; i++)
		{
			BIPFile.println("    component Pumps pump"+i);
		}
		
		BIPFile.println("    component Operator op");

		
		int pumpNb = 0;
		for(int i = 0 ; i < nbOfCustomers ; i++)
		{
			pumpNb = (pumpNb + 1) % nbOfPumps;
			//if(i%nbOfPumps == 0) pumpNb = (pumpNb + 1)%nbOfPumps;
			BIPFile.println("    connector rendezvous3 prepayCustom"+  i +" (custom"+  i +".prepay, op.prepay"+  pumpNb +" , pump"+  pumpNb +".activate)");
			BIPFile.println("    connector rendezvous2 startCustom"+  i +" (custom"+  i +".start, pump"+  pumpNb +".start)");
			BIPFile.println("    connector rendezvous3 finishCustom"+  i +" (custom"+  i +".finish, pump"+  pumpNb +".finish, op.finish)");
			BIPFile.println("    connector rendezvous2 changeCustom"+  i +" (custom"+  i +".change, op.change)");
		}

		BIPFile.println("end\n");
		
		String partition1 = "pump0 op";
		String partition2 = "pump1 op";
		String partition3 = "pump2 op";
		for(int i = 0 ; i < nbOfCustomers ; i++)
		{
			if(i%9 >= 0 && i%9 < 3) partition1 += " custom" +i;
			else if(i%9 >= 3 && i%9 < 6) partition2 += " custom" +i;
			else if(i%9 >= 6 && i%9 < 9) partition3 += " custom" +i;

		}
		INCFile.println(partition1);
		INCFile.println(partition2);
		INCFile.println(partition3);
	}



}
