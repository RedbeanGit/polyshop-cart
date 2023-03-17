package fr.dopolytech.polyshop.cart.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class PurchaseRedisConfig {
  @Bean
  ReactiveRedisOperations<String, Long> redisOperations(ReactiveRedisConnectionFactory factory) {
    Jackson2JsonRedisSerializer<Long> serializer = new Jackson2JsonRedisSerializer<>(Long.class);

    RedisSerializationContext.RedisSerializationContextBuilder<String, Long> builder =
        RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

    RedisSerializationContext<String, Long> context = builder.value(serializer).build();

    return new ReactiveRedisTemplate<>(factory, context);
  }

}

