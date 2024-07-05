package com.mistra.skeleton.redis.lock;

import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = "com.mistra.skeleton.redis.lock")
public class DistributedLockAutoConfiguration {

    @Autowired
    private RedisProperties properties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        if (Objects.nonNull(properties.getCluster()) && CollectionUtils.isNotEmpty(properties.getCluster().getNodes())) {
            // ClusterServers
            for (String node : properties.getCluster().getNodes()) {
                config.useClusterServers().addNodeAddress("redis://" + node);
            }
            config.useClusterServers().setMasterConnectionPoolSize(200);
            config.useClusterServers().setSlaveConnectionMinimumIdleSize(200);
            config.useClusterServers().setMasterConnectionMinimumIdleSize(200);
            config.useClusterServers().setSlaveConnectionPoolSize(200);
            config.useClusterServers().setScanInterval(3000);
            config.useClusterServers().setTimeout((int) properties.getTimeout().toMillis());
            if (StringUtils.isNotBlank(properties.getPassword())) {
                config.useClusterServers().setPassword(properties.getPassword());
            }
            return Redisson.create(config);
        } else if (Objects.nonNull(properties.getHost()) && Objects.nonNull(properties.getPort())) {
            // SingleServer
            String redisHost = properties.getHost().startsWith("redis://") ? properties.getHost() : "redis://" + properties.getHost();
            SingleServerConfig serverConfig = config.useSingleServer()
                    .setAddress(redisHost + ":" + properties.getPort())
                    .setTimeout((int) properties.getTimeout().toMillis());
            if (StringUtils.isNotBlank(properties.getPassword())) {
                serverConfig.setPassword(properties.getPassword());
            }
            return Redisson.create(config);
        } else {
            throw new RuntimeException("Please add Redis hosting configuration");
        }
    }

    @Bean
    @ConditionalOnBean(value = RedissonClient.class)
    public DLEnvironment dlEnvironment(@Value(value = "${spring.application.name}") String prefix, RedissonClient redissonClient) {
        final boolean nxLockSupported = redissonClient != null;
        Config config;
        final boolean redLockSupported = nxLockSupported && (config = redissonClient.getConfig()) != null && config.isClusterConfig();
        return new DLEnvironment() {

            @Override
            public String prefix() {
                return prefix;
            }

            @Override
            public boolean nxLockSupported() {
                return nxLockSupported;
            }

            @Override
            public boolean redLockSupported() {
                return redLockSupported;
            }

            @Override
            public DLUnderlyingSupport underlying() {
                return () -> redissonClient;
            }
        };
    }

    @Bean
    @ConditionalOnBean(value = DLEnvironment.class)
    public DistributedLockFactory distributedLockFactory(DLEnvironment dlEnvironment) {
        return new DefaultDistributedLockFactory(dlEnvironment);
    }

    @Bean
    @ConditionalOnBean(value = RedissonClient.class)
    public ReentrantLockUtil reentrantLockUtil(RedissonClient redissonClient) {
        return new ReentrantLockUtil(redissonClient);
    }
}
