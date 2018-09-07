package pers.arjay.serializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

/**
 * 用於存放redis key時產生前綴。
 * 
 * @author jay.kuo
 *
 */
public class PrefixKeyRedisSerializer implements RedisSerializer<String> {

	private final String cacheName;

	private final Charset charset;

	public PrefixKeyRedisSerializer(String cacheName) {
		this(cacheName, StandardCharsets.UTF_8);
	}

	public PrefixKeyRedisSerializer(String cacheName, Charset charset) {
		Assert.hasText(cacheName, "cacheName must not be null!");
		Assert.notNull(charset, "Charset must not be null!");

		this.charset = charset;
		this.cacheName = cacheName;
	}

	@Override
	public byte[] serialize(String bindedSerialNumber) throws SerializationException {
		return bindedSerialNumber == null ? null : (cacheName + bindedSerialNumber).getBytes(charset);
	}

	@Override
	public String deserialize(byte[] bytes) throws SerializationException {
		return bytes == null ? null : new String(bytes, charset).substring(cacheName.length());
	}

}
