package com.zyf.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author zyf
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -56899043570752005L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
