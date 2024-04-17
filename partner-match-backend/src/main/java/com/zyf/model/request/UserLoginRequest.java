package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author zyf
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -6364963771055942865L;
    private String userAccount;
    private String userPassword;
}
