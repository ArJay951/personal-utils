package pers.arjay.exception;

import org.slf4j.helpers.MessageFormatter;

public class SampleException extends RuntimeException {
	
	private static final long serialVersionUID = 4892064115976876151L;

	public SampleException(String format, Object arg1) {
		super(MessageFormatter.format(format, arg1).getMessage());
	}

	public SampleException(String format, Object arg1, Object arg2) {
		super(MessageFormatter.format(format, arg1, arg2).getMessage());
	}

	public SampleException(String format, Object... args) {
		super(MessageFormatter.format(format, args).getMessage());
	}
	
}
