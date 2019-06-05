package com.jwt.demo;

import com.jwt.demo.enity.UserEntity;
import com.jwt.demo.service.UserService;
import com.jwt.demo.utils.RedisUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Pengllrn
 * @since <pre>2019/6/4 15:23</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRdis {
    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserService userService;

    @Test
    public void setOb(){
        redisUtils.set("8989","dsadsa");
    }

    @Test
    public void testCache(){
        UserEntity userById = userService.findUserById(10);
        System.out.println(userById);
    }
}
