package media.utils;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;
import java.util.Date;

/**
 * Very simple Formatter for log entries. The difference between the this one
 * and the VerySimpleFormatter is: this one prints out the class name too.
 */
public class VerySimpleClassNameFormatter extends Formatter {
	public String format(LogRecord record) {
		StringBuffer sb = new StringBuffer();

		// time in format hh:mm:ss
		DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);

		sb.append(dateFormat.format(new Date(System.currentTimeMillis())));
		sb.append(" ");
		sb.append(record.getLevel().getLocalizedName());
		sb.append(": ");
		sb.append(record.getLoggerName().replaceFirst("([a-z]\\w*\\.)*", ""));
		sb.append(": ");
		sb.append(formatMessage(record));
		sb.append("\n");
		// exception handling
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
			}
		}

		return sb.toString();
	}
}
