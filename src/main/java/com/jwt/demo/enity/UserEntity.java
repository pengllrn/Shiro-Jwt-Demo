package com.jwt.demo.enity;

import lombok.Data;

/**
 * @author Pengllrn
 * @since <pre>2019/6/4 15:28</pre>
 */
@Data
public class UserEntity {
    private int userId;

    private String username;
    private String password;
    private String encryptPwd;
    private String salt;

}
