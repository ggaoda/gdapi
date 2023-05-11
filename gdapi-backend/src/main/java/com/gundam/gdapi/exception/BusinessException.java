package com.gundam.gdapi.exception;

import com.gundam.gdapi.common.ErrorCode;

/**
 * 自定义异常类
 * @author Gundam
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 自定义code message
     * @param code
     * @param message
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 用common里的ErrorCode和默认信息
     * @param errorCode
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 用common里的ErrorCode并重写信息
     * @param errorCode
     * @param message
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
