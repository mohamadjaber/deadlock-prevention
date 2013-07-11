package aub.edu.lb.configuration;

public class Configuration {
	public static boolean debug = false;
	public static double startTime, stopTime, totalTime;

	
	public static void println(String message) {
		if(debug)
			System.out.println(message);
	}
	
	public static void print(String message) {
		if(debug)
			System.out.print(message);
	}
	
	public static String cmdHelp() {
		return "java -jar LDFCBIP.jar example.bip [--debug]\n\n" +
				"--debug Prints useful information at each iteration of the algorithm. \n" +
				"        Example: selected interaction, depth length, etc.\n" +
				"        This information could be useful in case when the LDFC condition fails.\n";
	}
	
	public static String deadlockFreeMessage() {
		return  "\n" +
				"*****************************************\n" +
				"* Local and global deadlock-free system *\n" +
				"*****************************************";
	}
	
	public static String deadlockMessage() {
		return  "\n" +
				"*************************************\n" +
				"* The system might contain dealdock *\n" +
				"*************************************";
	}

}
