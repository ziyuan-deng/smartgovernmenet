package com.neco.common.exception;


import com.neco.common.response.ResultCode;

/**
 * 自定义异常类
 *
 * @author ziyuan_deng
 * @create 2020-07-23 22:55
 */
public class CustomException extends RuntimeException {

    private ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        //异常信息为错误代码+异常信息
        super("错误代码："+resultCode.code()+"错误信息："+resultCode.message());
        this.resultCode = resultCode;
    }
    public ResultCode getResultCode(){
        return this.resultCode;
    }
}
