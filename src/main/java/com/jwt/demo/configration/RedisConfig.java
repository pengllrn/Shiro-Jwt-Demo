package com.jwt.demo.configration;

import com.jwt.demo.common.Constant;
import com.jwt.demo.utils.RedisUtils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import lombok.Data;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Pengllrn
 * @since <pre>2019/6/3 20:11</pre>
 */
@Configuration
@PropertySource("classpath:/config/redis.properties")
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisConfig {
    private String host;
    private int port;
    private int database;
//    private String password;
    private int maxActive;
    private int maxIdle;
    private int maxWait;
    private int minIdle;

    private int timeout;

    /**
     * Redis数据库单机版基本配置
     * 1.连接器，设置redis数据库基本信息
     */
    @Bean
    public RedisConnectionFactory JedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        //连接池
        //IP地址
        redisConfiguration.setHostName(host);
        //端口号
        redisConfiguration.setPort(port);
        redisConfiguration.setDatabase(database);
        //如果Redis设置有密码
        //redisConfiguration.setPassword(password);
        //客户端超时时间单位是毫秒
        return new JedisConnectionFactory(redisConfiguration);//Jedis是java对redis的一种封装，也集成到了spring-redis里
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 连接池的最大数据库连接数
        jedisPoolConfig.setMaxTotal(maxActive);
        // 最大空闲数
        jedisPoolConfig.setMaxIdle(maxIdle);
        // 最大建立连接等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        // 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        jedisPoolConfig.setEvictorShutdownTimeoutMillis(timeout);
//        jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        // 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
//        jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        // 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
//        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        // 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
        jedisPoolConfig.setTestOnBorrow(true);
        // 在空闲时检查有效性, 默认false
//        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        return jedisPoolConfig;
    }

    /**
     * 2.实例化 RedisTemplate 对象，来替换掉spring默认的RedisTemplate
     * @retur
     */
    @Bean
    public RedisTemplate<String, Object> functionDomainRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //1.连接池
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //2.配置序列化方式
        redisTemplate.setKeySerializer(keySerializer());
        redisTemplate.setHashKeySerializer(keySerializer());
        redisTemplate.setHashValueSerializer(valueSerializer());
        redisTemplate.setValueSerializer(valueSerializer());
        redisTemplate.setEnableTransactionSupport(true);// 开启事务
        return redisTemplate;
    }

    @Bean(name = "redisUtil")
    public RedisUtils redisUtil(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils redisUtil = new RedisUtils();
        redisUtil.setRedisTemplate(redisTemplate);
        return redisUtil;
    }

    /**
     * 配置缓存管理器：结合Cacheable使用
     * 用来替换spring默认的CacheManager(基于ehcache实现，是基于jvm缓存)
     * 即用RedisCacheManager来替换CacheManager
     */
    @Primary
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        //缓存配置对象
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.entryTtl(Duration.ofMinutes(Constant.SPRING_CACHE_TIME)) //设置缓存的默认超时时间：60分钟
                .disableCachingNullValues()             //如果是空值，不缓存
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))         //设置key序列化器
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer((valueSerializer())));  //设置value序列化器
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration).build();
    }

    //字符串序列器
    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }
    //Gson序列器，序列化Obejct类
    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
