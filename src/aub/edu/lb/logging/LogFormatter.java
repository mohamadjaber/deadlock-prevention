package aub.edu.lb.logging;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {

	public LogFormatter() {
		super();
	}

	@Override
	public String format(final LogRecord record) {
		return record.getMessage();
	}

}
