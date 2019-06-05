package com.jwt.demo.configration.shiro.realm;

import com.jwt.demo.enity.UserEntity;
import com.jwt.demo.service.UserService;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Pengllrn
 * @since <pre>2019/6/4 15:41</pre>
 */
public class DbShiroRealm extends AuthorizingRealm {
    private final Logger logger = LoggerFactory.getLogger(DbShiroRealm.class);

    private static final String encryptSalt = "F12839WhsnnEV$#23b";
    private UserService userService;

    public DbShiroRealm(UserService userService) {
        this.userService = userService;
//        this.setCredentialsMatcher(new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME));
    }

    @Override
    public boolean supports(AuthenticationToken token) {//DbShiroRealm处理 UsernamePasswordToken类型的token
        return token instanceof UsernamePasswordToken;
    }

    /**
     * 登录认证
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken userpasswordToken = (UsernamePasswordToken) token;
        String username = userpasswordToken.getUsername();
        char[] pwd = userpasswordToken.getPassword();
        List<UserEntity> users = userService.findUserByUsername(username);
        if (users != null && users.size() != 0) {
            UserEntity user = users.get(0);
            logger.info("---------------- Shiro 凭证认证结束 ----------------------");
            return new SimpleAuthenticationInfo(
                    user, //数据库用户,可以让它保存在Redis缓存里面
                    user.getEncryptPwd(), //数据库密码密文,应该是用户输入密码的加密
                    ByteSource.Util.bytes(encryptSalt),//加盐
                    "dbRealm"  //realm 名字： "DbShiroRealm" 和ShiroConfig里面的Bean一致就行
            );
        }
        throw new UnknownAccountException();
    }


    /**
     * 授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        logger.info("---------------- 执行 Shiro 权限获取 ---------------------");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRole("admin");
        authorizationInfo.addStringPermission("all");
//        UserEntity user = (UserEntity) principals.getPrimaryPrincipal();
//        List<String> roles = userRoleService.findRolesByUserId(user.getUserId());
//        List<String> permissions = userPermissionService.findPermissions(user.getUserId());
//        if (roles != null)
//            authorizationInfo.addRoles(roles);
//        if (permissions != null)
//            authorizationInfo.addStringPermissions(permissions);
//        if (roles != null)
//            authorizationInfo.addRoles(roles);
//        logger.info("---- 获取到以下权限和角色 ----");
//        logger.info("" + authorizationInfo.getStringPermissions());
//        logger.info("" + authorizationInfo.getRoles());
//        logger.info("---------------- Shiro 权限获取成功 ----------------------");
        return authorizationInfo;
    }


}
