package aub.edu.lb.cmd;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;
import jcmdline.VersionCmdLineHandler;



public class CmdLineFactory {
	private static final String defaultInputBIP = "input.bip";
	private static final String helpInputBIPFile = "input BIP file name";
	private static final String cmdLineTool = "java -jar deadlockFree.jar";
	
	private static final String VERSION = "V 1.0";
	
	public static final String LocalAndOrName = "LALT";
	public static final String LocalLinearName = "LLIN";
	public static final String conditionNotSupported = "Condition not supported";

	private static final String defaultConditionName = LocalAndOrName;
	
	private static final String cmdLineDescription = "A tool for local and global deadlock-free check!";
	
	private static final String helpConditionType = LocalAndOrName + " (local linear check) or " + 
											LocalAndOrName + " (local and/or check - default)";
	
	private static final String helpDebugging = "Prints useful information at each iteration of checking. \n" +
				" Example: selected interaction, depth length, etc.\n" +
				" This information could be useful in case when the condition fails.\n";

	private static final String helpText = "Have Fun!";
		
	
	private FileParam inputBIP; 
	private BooleanParam debugging;
	private StringParam conditionType;
	
	
	private CmdLineHandler cmdLineHandler;
	


	
	public CmdLineFactory(String[] args) {
		inputBIP = new FileParam(defaultInputBIP,	helpInputBIPFile,
				FileParam.EXISTS & FileParam.IS_READABLE,
				!FileParam.OPTIONAL,
				!FileParam.MULTI_VALUED
			);
		
		
		conditionType = new StringParam("condition", helpConditionType);
		
		debugging = new BooleanParam("debug", helpDebugging);
 

		cmdLineHandler = new VersionCmdLineHandler(VERSION,
				(CmdLineHandler) new HelpCmdLineHandler(helpText, cmdLineTool, cmdLineDescription,
						new Parameter[] {  conditionType, debugging },
						new Parameter[] { inputBIP } ));
		
		cmdLineHandler.parse(args);	
	}
	

	public String getInputBIPFile() {
		return inputBIP.getValue().getAbsolutePath();
	}
	
	public boolean isDebugOn() {
		return debugging.isTrue();
	}
	

	public String getConditionType() {
		if(!conditionType.isSet()) return defaultConditionName;
		return conditionType.getValue();
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
