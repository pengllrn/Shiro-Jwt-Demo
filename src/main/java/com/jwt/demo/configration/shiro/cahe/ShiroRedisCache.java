package com.jwt.demo.configration.shiro.cahe;

import com.jwt.demo.common.Constant;
import com.jwt.demo.enity.UserEntity;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ShiroRedisCache<K, V> implements Cache<K, V> {


    private String prefix = "shiro_cache:";

    private int hours = Constant.SHIRO_CACHE_TIME;

    private RedisTemplate<String ,Object> redisTemplate;


    public ShiroRedisCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public V get(K k) throws CacheException {
        if (k == null)
            return null;
        SimplePrincipalCollection spc = (SimplePrincipalCollection) k;
        UserEntity userEntity = (UserEntity) spc.getPrimaryPrincipal();
        return (V) redisTemplate.opsForValue().get(prefix + userEntity.getUserId());
    }

    @Override
    public V put(K k, V v) throws CacheException {
        if (k == null || v == null) {
            return null;
        }
        SimplePrincipalCollection spc = (SimplePrincipalCollection) k;
        UserEntity userEntity = (UserEntity) spc.getPrimaryPrincipal();

        redisTemplate.opsForValue().set(prefix + userEntity.getUserId(), v, hours,TimeUnit.HOURS);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        if (k == null) {
            return null;
        }

        SimplePrincipalCollection spc = (SimplePrincipalCollection) k;
        UserEntity userEntity = (UserEntity) spc.getPrimaryPrincipal();
        redisTemplate.delete(prefix + userEntity.getUserId());
        V v = get(k);
        return v;
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public int size() {
        return redisTemplate.getConnectionFactory().getConnection().dbSize().intValue();
    }

    @Override
    public Set<K> keys() {
        Set<Object> keys = null;
        Set<K> sets = new HashSet<>();
        for (Object key : keys) {
            sets.add((K) key);
        }
        return sets;
    }

    @Override
    public Collection<V> values() {
        Set<K> keys = keys();
        List<V> values = new ArrayList<>(keys.size());
        for (K k : keys) {
            values.add(get(k));
        }
        return values;
    }
}
