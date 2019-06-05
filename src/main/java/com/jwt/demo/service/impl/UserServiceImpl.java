package com.jwt.demo.service.impl;

import com.jwt.demo.common.Constant;
import com.jwt.demo.enity.UserEntity;
import com.jwt.demo.service.UserService;
import com.jwt.demo.utils.JwtUtils;
import com.jwt.demo.utils.RedisUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pengllrn
 * @since <pre>2019/6/4 15:33</pre>
 */
@Service
@CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService {
    @Autowired
    RedisUtils redisUtils;

    @Override
    public UserEntity findUserById(int userId) {
        System.out.println("uuuu");
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(10086);
        userEntity.setUsername("zhangsan");
        userEntity.setPassword("123456");
        userEntity.setEncryptPwd("123456");
        return userEntity;
    }

    @Override
    public UserEntity getUserEntity(String userId) {
        return findUserById(Integer.valueOf(userId));
    }

    @Override
    public List<UserEntity> findUserByUsername(String username) {
        List<UserEntity> userEntities = new ArrayList<>();
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(10086);
        userEntity.setUsername("zhangsan");
        userEntity.setPassword("123456");
        userEntity.setEncryptPwd("123456");
        userEntities.add(userEntity);
        return userEntities;
    }


    /**
     * 保存user登录信息，返回token
     */
    @Override
    public String generateJwtToken(int userId) {
        String salt = JwtUtils.generateSalt();
        //将salt保存到数据库或者缓存
        redisUtils.set("token" + userId, salt, Constant.SALT_EXPIRE_TIME);
        return JwtUtils.sign("" + userId, salt, Constant.TOKEN_EXPIRE_TIME);//生成jwt token,设置过期时间为1小时
    }

    /**
     * 清除token信息，主要是缓存中保存的salt以及 数据库缓存信息
     * @todo 删除数据库换粗信息
     * @param userId
     */
    @Override
    public void deleteLoginInfo(int userId) {
        redisUtils.del("token" + userId,"user::"+userId);
    }

    @Override
    public UserEntity getJwtTokenInfo(String userId) {
        String salt = redisUtils.getString("token" + userId);
        UserEntity user = findUserById(Integer.valueOf(userId));
        if(user != null)
            user.setSalt(salt);
        return user;
    }
}
