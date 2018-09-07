package pers.arjay.serializer;

import java.security.Key;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

public class SecurityKeySerializer implements RedisSerializer<Key> {

	private final String algorithm;

	public SecurityKeySerializer(String algorithm) {
		Assert.hasText(algorithm, "algorithm must have value!");
		
		this.algorithm = algorithm;
	}

	@Override
	public byte[] serialize(Key key) throws SerializationException {
		return (key == null ? null : Base64.getEncoder().encode(key.getEncoded()));
	}

	@Override
	public Key deserialize(byte[] bytes) throws SerializationException {
		return (bytes == null ? null : new SecretKeySpec(Base64.getDecoder().decode(bytes), algorithm));
	}

}
