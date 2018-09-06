package pers.arjay.generator.serial;

import java.security.NoSuchAlgorithmException;

public interface SerialNumberGenerator {

	public String generateSerial() throws NoSuchAlgorithmException;
	
}
