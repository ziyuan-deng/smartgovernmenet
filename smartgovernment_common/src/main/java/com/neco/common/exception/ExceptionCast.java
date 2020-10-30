package com.neco.common.exception;


import com.neco.common.response.ResultCode;

/**
 * 自定义异常抛出类
 *
 * @author ziyuan_deng
 * @create 2020-07-23 22:58
 */
public class ExceptionCast {
    //使用此静态方法抛出自定义异常
    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
