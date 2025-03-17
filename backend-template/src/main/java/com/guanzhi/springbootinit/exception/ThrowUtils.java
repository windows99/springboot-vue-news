package com.guanzhi.springbootinit.exception;

import com.guanzhi.springbootinit.common.ErrorCode;

/**
 * 抛异常工具类
 *
 * @author sk

 *  

 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }


    /**
     * 条件成立则抛异常
     *
     * @param data
     */
    public static void throwIfNull(Object data) {
        throwIf(data == null, ErrorCode.PARAMS_NULL);
    }
}
