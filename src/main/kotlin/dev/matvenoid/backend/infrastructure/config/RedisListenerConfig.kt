package dev.matvenoid.backend.infrastructure.config

import dev.matvenoid.backend.infrastructure.messaging.redis.RedisExpiryListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

@Configuration
class RedisListenerConfig(
    private val expiryListener: RedisExpiryListener,
) {
    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory
    ): RedisMessageListenerContainer {
        val listener = RedisMessageListenerContainer()
        listener.setConnectionFactory(connectionFactory)
        listener.addMessageListener(
            expiryListener,
            PatternTopic("__keyevent@*__:expired")
        )
        return listener
    }
}
