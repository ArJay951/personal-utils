package pers.arjay.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 
 * compile group: 'org.springframework.security', name: 'spring-security-core', version: '5.0.7.RELEASE'
 * 
 * @author jay.kuo
 *
 */
public class AuthorityUtils {
	public static Collection<GrantedAuthority> mapToGrantedAuthorities(Collection<String> authorities) {
		return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
	}
}
