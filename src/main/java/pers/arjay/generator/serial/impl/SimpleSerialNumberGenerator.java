package pers.arjay.generator.serial.impl;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.web.filter.DelegatingFilterProxy;

import pers.arjay.generator.serial.SerialNumberGenerator;

public class SimpleSerialNumberGenerator implements SerialNumberGenerator {

	public static final String DEFAULT = "SN";

	private String prefix;
	
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	public SimpleSerialNumberGenerator() {
		DelegatingFilterProxy
		this.prefix = DEFAULT;
	}

	public SimpleSerialNumberGenerator(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String generateSerial() throws NoSuchAlgorithmException {
		final long millSec = System.currentTimeMillis();
		final long nanoSec = System.nanoTime();
		final String source = "" + millSec + nanoSec + UUID.randomUUID();
		
		return prefix + LocalDate.now().format(dateTimeFormatter) + MD5Util.hash(source);
	}

}
