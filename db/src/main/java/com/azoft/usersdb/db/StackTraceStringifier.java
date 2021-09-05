package com.azoft.usersdb.db;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceStringifier {
	public String stringify(final Exception exception) {
		final StringWriter stackTraceStringWriter = new StringWriter();
		try (final PrintWriter stackTraceWriter = new PrintWriter(stackTraceStringWriter)) {
			exception.printStackTrace(stackTraceWriter);
		}
		return stackTraceStringWriter.toString();
	}
}
