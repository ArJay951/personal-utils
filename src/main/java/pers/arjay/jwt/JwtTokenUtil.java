package pers.arjay.jwt;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * compile group: 'io.jsonwebtoken', name: 'jjwt', version: '0.9.0'
 * 
 * @author jay.kuo
 *
 */
@Slf4j
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -3384744415959929106L;
	/**
	 * 密鑰
	 */
	private final String secret = "boya-kai-room";

	/**
	 * 從數據聲明生成令牌
	 *
	 * @param claims
	 *            數據聲明
	 * @return 令牌
	 */
	private String generateToken(Map<String, Object> claims) {
		/* 30 * 24 * 60 * 60 * 1000 */
		Date expirationDate = new Date(System.currentTimeMillis() + 2592000L * 1000); // 2592000L * 1000
		return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	/**
	 * 從令牌中獲取數據聲明
	 *
	 * @param token
	 *            令牌
	 * @return 數據聲明
	 */
	private Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			log.info("getClaimsFromToken error ", e);
			claims = null;
		}
		return claims;
	}

	/**
	 * 生成令牌
	 *
	 * @param userDetails
	 *            用户
	 * @return 令牌
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>(2);
		claims.put("sub", userDetails.getUsername());
		claims.put("created", new Date());
		return generateToken(claims);
	}

	/**
	 * 從令牌中獲取用户名
	 *
	 * @param token
	 *            令牌
	 * @return 用户名
	 */
	public String getUsernameFromToken(String token) {
		String username;
		try {
			Claims claims = getClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			log.error("getUsernameFromToken error:", e);
			username = null;
		}
		return username;
	}

	/**
	 * 判斷令牌是否過期
	 *
	 * @param token
	 *            令牌
	 * @return 是否過期
	 */
	public Boolean isTokenExpired(String token) {
		try {
			Claims claims = getClaimsFromToken(token);
			Date expiration = claims.getExpiration();
			return expiration.before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 刷新令牌
	 *
	 * @param token
	 *            原令牌
	 * @return 新令牌
	 */
	public String refreshToken(String token) {
		String refreshedToken;
		try {
			Claims claims = getClaimsFromToken(token);
			claims.put("created", new Date());
			refreshedToken = generateToken(claims);
		} catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}

	/**
	 * 驗證令牌
	 *
	 * @param token
	 *            令牌
	 * @param userDetails
	 *            用户
	 * @return 是否有效
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		String username = getUsernameFromToken(token);
		return (Objects.equals(username, userDetails.getUsername()) && !isTokenExpired(token));
	}

	public Boolean canTokenBeRefreshed(String token, LocalDateTime modifyDate) {
		return true;
	}
	
}
