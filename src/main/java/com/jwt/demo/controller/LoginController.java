package com.jwt.demo.controller;

import com.jwt.demo.common.ResponseMsg;
import com.jwt.demo.enity.UserEntity;
import com.jwt.demo.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Pengllrn
 * @since <pre>2019/6/4 15:55</pre>
 */
@RestController
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    @Autowired
    private UserService userService;

    /**
     * 用户名登录
     * @param username
     * @param password
     * @param response
     * @return
     */
    @PostMapping(value = "/login")
    public String login(String username,String password,
                                      HttpServletResponse response){
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username,password);
            if(subject.isAuthenticated())
                subject.logout();
            subject.login(token);
            UserEntity user = (UserEntity) subject.getPrincipal();
            String headerToken = userService.generateJwtToken(user.getUserId());
            response.setHeader(ResponseMsg.TOKEN_NAME,headerToken);
//            return ResponseEntity.ok().build();
            return "ok";
        }catch (UnknownAccountException e){
            logger.error("User {} login failed,Reason:{}",username, ResponseMsg.UNKNOWN_ACCOUNT);
//            return ResponseEntity.status(-1).build();
            return ResponseMsg.UNKNOWN_ACCOUNT;
        }catch (IncorrectCredentialsException e){
            logger.error("User {} login failed,Reason:{}",username, ResponseMsg.INCORRECT_CREDENTIALS);
//            return ResponseEntity.status(-2).build();
            return ResponseMsg.INCORRECT_CREDENTIALS;
        }catch (AuthenticationException e){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping(value = "/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        if(subject.getPrincipal() != null){
            UserEntity user = (UserEntity) subject.getPrincipal();
            userService.deleteLoginInfo(user.getUserId());
        }
        SecurityUtils.getSubject().logout();
        return "退出成功";
    }
}
