package aub.edu.lb.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {
	
	public String format(LogRecord record) {
		if (record.getLevel() == Level.INFO) {
			return "";
		} else {
			return super.format(record);
		}
	}
}
