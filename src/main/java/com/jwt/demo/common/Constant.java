package com.jwt.demo.common;

/**
 * @author Pengllrn
 * @since <pre>2019/6/5 14:11</pre>
 */
public class Constant {
    public static final int TOKEN_EXPIRE_TIME = 3600;//毫秒 token的过期时间
    public static final int SALT_EXPIRE_TIME = 3600;//毫秒 盐的过期时间
    public static final int SPRING_CACHE_TIME = 60;//分钟 spring普通对象缓存时间
    public static final int SHIRO_CACHE_TIME = 24;//小时 shiro的角色和权限信息缓存时间
}
