package pers.arjay.generator.serial.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	public static String hash(String source) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(source.getBytes());
		byte[] byteData = md.digest();
		
		final StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			final String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}
	
}
