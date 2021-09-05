package com.azoft.usersdb.db;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public final class StackTraceStringifierTest {
	@Test
	void stringify_shouldReturnPrintedStackTrace() {
		final Exception mockException = mock(Exception.class);
		final String mockStackTrace = "Mock Stack Trace";
		doAnswer(invocation -> {
			final PrintWriter writer = invocation.getArgument(0);
			writer.write(mockStackTrace);
			return null;
		}).when(mockException).printStackTrace(any(PrintWriter.class));
		final StackTraceStringifier stringifier = new StackTraceStringifier();
		assertEquals(mockStackTrace, stringifier.stringify(mockException));
	}
}
