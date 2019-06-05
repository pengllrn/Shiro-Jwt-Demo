package com.jwt.demo.service;

import com.jwt.demo.enity.UserEntity;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * @author Pengllrn
 * @since <pre>2019/6/4 15:32</pre>
 */
@CacheConfig
public interface UserService {

    /**
     * 根据用户Id找到用户
     * @param userId
     * @return
     */
    @Cacheable(key = "#p0")
    UserEntity findUserById(int userId);

    @Cacheable(key = "#p0")
    UserEntity getUserEntity(String userId);

    /**
     * 根据用户账号找到用户，用于登录
     * @param username
     * @return
     */
    List<UserEntity> findUserByUsername(String username);

    String generateJwtToken(int userId);

    @Cacheable(key = "#p0")
    UserEntity getJwtTokenInfo(String userId);

    void deleteLoginInfo(int userId);
}
