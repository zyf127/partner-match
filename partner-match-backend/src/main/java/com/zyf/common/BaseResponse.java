package com.zyf.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @author zyf
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 2772493293998549178L;
    private int code;
    private T data;
    private String message;
    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, String message, String description) {
        this(code, null, message, description);
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
    public BaseResponse(ErrorCode errorCode, String message, String description) {
        this(errorCode.getCode(), null, message, description);
    }
}
