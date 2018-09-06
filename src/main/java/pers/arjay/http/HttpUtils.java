package pers.arjay.http;

import static java.util.Optional.ofNullable;

import javax.servlet.http.HttpServletRequest;

/**
 * compileOnly group: 'javax.servlet', name: 'javax.servlet-api', version: '3.1.0'
 * 
 * @author jay.kuo
 *
 */
public class HttpUtils {

	public static String getClientIp(HttpServletRequest request) {
		return request != null 
			? ofNullable(request.getHeader("X-FORWARDED-FOR")).orElse(request.getRemoteAddr()) 
			: "";
	}

}
