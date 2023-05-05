
package com.javainuse.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration

@EnableCaching

//@EnableRedisRepositories
//(value = "org.javainuse.repository")

public class RedisCacheConfig extends CachingConfigurerSupport {

	//private final RedisConnectionFactory redisConnectionFactory;

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	//public RedisCacheConfig(RedisConnectionFactory redisConnectionFactory) {
	//	this.redisConnectionFactory = redisConnectionFactory;
	//}

/*	@Bean public CacheManager cacheManager() 
	{ 
		RedisCacheConfiguration
		cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
		.entryTtl(Duration.ofSeconds(JWT_TOKEN_VALIDITY)) // set cache TTL to 30 minutes
		.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer
				(new StringRedisSerializer()));

	return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(cacheConfiguration) .build(); }

	@Override public KeyGenerator keyGenerator()
	{ // customize the key generation strategy 
		return (target, method, params) -> { 
			// generate a key based on the method name and parameter values 
			StringBuilder sb = new
					StringBuilder(); sb.append(target.getClass().getName()); sb.append(".");
					sb.append(method.getName()); for (Object param : params) { sb.append(".");
					sb.append(param.toString()); } return sb.toString(); }; }

	// @Override // public CacheErrorHandler errorHandler() {
	// // customize the error handling strategy
	// return new RedisCacheErrorHandler(); // }

*/
/*	@Bean
	public JedisConnectionFactory connectionFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName("localhost");
		configuration.setPort(6379);
		return new JedisConnectionFactory(configuration);
	}*/
	
	@Bean 
	JedisConnectionFactory jedisConnectionFactory() {
	    JedisConnectionFactory jedisConFactory = new  JedisConnectionFactory();
	 jedisConFactory.setHostName("localhost");
	 jedisConFactory.setPort(6379); 
	 return jedisConFactory; 
	}

	@Bean
	public RedisTemplate<String, String> template() {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new JdkSerializationRedisSerializer());
		template.setValueSerializer(new JdkSerializationRedisSerializer());
		template.setEnableTransactionSupport(true);
		template.afterPropertiesSet();
		return template;
	}

}
