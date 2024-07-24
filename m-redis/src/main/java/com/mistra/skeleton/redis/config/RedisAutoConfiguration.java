package com.mistra.skeleton.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/9/21 15:47
 * @ Description:
 */
@Configuration
@ComponentScan(value = "com.mistra.skeleton.redis")
public class RedisAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(RedisAutoConfiguration.class);
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database:0}")
    private int database;

    public RedisAutoConfiguration() {
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMinIdle(this.minIdle);
        jedisPoolConfig.setMaxIdle(this.maxIdle);
        jedisPoolConfig.setMaxWaitMillis(this.maxWaitMillis);
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jedisPool(JedisPoolConfig jedisPoolConfig) {
        log.info("----------JedisPool init success----------");
        return new JedisPool(jedisPoolConfig, this.host, this.port, this.timeout, this.password, this.database, (String) null);
    }

    @Bean
    @ConditionalOnProperty(
            name = {"spring.redis.connection-factory.enabled"}
    )
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        log.info("----------JedisConnectionFactory init success----------");
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(this.host);
        configuration.setPort(this.port);
        configuration.setDatabase(this.database);
        if (!StringUtils.isEmpty(this.password)) {
            configuration.setPassword(this.password);
        }
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().poolConfig(jedisPoolConfig).build();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(configuration, jedisClientConfiguration);
        return jedisConnectionFactory;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = ((SingleServerConfig) config.useSingleServer().setAddress("redis://" + this.host + ":" + this.port).setConnectionMinimumIdleSize(this.minIdle).setConnectionPoolSize(this.maxIdle).setTimeout(this.timeout)).setDatabase(this.database).setDnsMonitoringInterval(-1L);
        config.setCodec(new StringCodec());
        if (!StringUtils.isEmpty(this.password)) {
            serverConfig.setPassword(this.password);
        }
        return Redisson.create(config);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(connectionFactory);
        return redisMessageListenerContainer;
    }

    /**
     * 当某个Redis的key过期之后，Redis内部会发布一个事件到__keyevent@<db>__:expired这个channel上，只要监听这个事件，那么就可以获取到过期的key
     * 将延迟任务作为key，过期时间设置为延迟时间
     * 监听__keyevent@<db>__:expired这个channel，那么一旦延迟任务到了过期时间（延迟时间），那么就可以获取到这个任务
     * Spring已经实现了监听__keyevent@*__:expired这个channel这个功能，__keyevent@*__:expired中的*代表通配符的意思，监听所有的数据库。
     *
     * 任务存在延迟：Redis过期事件的发布不是指key到了过期时间就发布，而是key到了过期时间被清除之后才会发布事件。 惰性清除 定时清除
     * 丢消息太频繁：Redis实现的发布订阅模式，消息是没有持久化机制，当消息发布到某个channel之后，如果没有客户端订阅这个channel，那么这个消息就丢了
     * 消息消费只有广播模式：所谓的广播模式就是多个消费者订阅同一个channel，那么每个消费者都能消费到发布到这个channel的所有消息。
     * @param redisMessageListenerContainer
     * @return
     */
    @Bean
    public KeyExpirationEventMessageListener redisKeyExpirationListener(RedisMessageListenerContainer redisMessageListenerContainer) {
        return new KeyExpirationEventMessageListener(redisMessageListenerContainer);
    }
}
