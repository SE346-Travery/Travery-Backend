package com.travery.traverybackend.configs;

import com.travery.traverybackend.listeners.BookingKeyExpirationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisKeyspaceConfig {

  /**
   * Subscribe to Redis keyspace notifications for expired keys. When a key expires (e.g.,
   * booking:hold:{id} after 15 min TTL), Redis publishes an event to "__keyevent@0__:expired"
   * channel.
   */
  @Bean
  public RedisMessageListenerContainer keyExpirationListenerContainer(
      RedisConnectionFactory connectionFactory,
      BookingKeyExpirationListener bookingKeyExpirationListener) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.addMessageListener(
        bookingKeyExpirationListener, new PatternTopic("__keyevent@*__:expired"));
    return container;
  }
}
