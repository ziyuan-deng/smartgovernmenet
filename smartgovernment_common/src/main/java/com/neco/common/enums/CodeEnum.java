package com.neco.common.enums;


import com.neco.common.constants.CodeConstant;

/**
 * 通用性异常枚举
 * @author ziyuan_deng
 */
public enum CodeEnum {

    SUCCESS(false,CodeConstant.SUCCESS,"成功"),
    OPERATION_SUCCESS(false,CodeConstant.SUCCESS,"操作成功"),
    SAVE_FAILD(false,CodeConstant.SAVE_FAILD,"保存失败"),
    UPDATE_FAILD(false,CodeConstant.UPDATE_FAILD,"更新错误"),
    DELETE_FAILD(false,CodeConstant.DELETE_FAILD,"删除错误"),
    DELETE_DISABLED(false,CodeConstant.DELETE_FAILD,"存在关联数据，删除失败"),
    UNKNOWN_ERROR(false,CodeConstant.UNKNOWN_ERROR,"未知错误"),
    PARAMS_ERROR(false,CodeConstant.PARAMS_ERROR,"参数不合法!"),
    PARAMS_INCOMPLETENESS(false,CodeConstant.PARAMS_INCOMPLETENESS,"参数不完整"),
    QUERY_RESULT_ERROR(false,CodeConstant.QUERY_RESULT_ERROR,"查询结果异常"),
    NON_CONFORMITY_CONDITIONON(false,CodeConstant.NON_CONFORMITY_CONDITIONON,"不符合操作条件！"),
    DATABASE_OPERATION_ERROR(false,CodeConstant.OPERATION_SYSTEM_EXCEPTION,"数据库操作失败"),
    PERMISSION_UNLOGIN(false,CodeConstant.PERMISSION_UNLOGIN,"用户未登录"),
    LOGIN_USER_NO_EXIST(false,CodeConstant.LOGIN_USER_NO_EXIST,"用户不存在"),
    LOGIN_PASSWORD_ERROR(false,CodeConstant.LOGIN_PASSWORD_ERROR,"密码错误"),
    SERVER_ERROR(false,CodeConstant.UNKNOWN_ERROR,"服务异常"),
    CRON_EXPRESSION_INVALID(false,CodeConstant.CRON_EXPRESSION_INVALID,"服务异常"),
    PERMISSION_DENIED(false,CodeConstant.PERMISSION_UNAUTHORIZED,"没有权限"),
    AUTH_USERNAME_NONE(false,CodeConstant.LOGIN_USERNAME_ISNULL,"用户名为空！"),
    AUTH_PASSWORD_NONE(false,CodeConstant.LOGIN_PASSWORD_ISNULL,"密码为空！"),
    AUTH_LOGIN_REFRESHTOKEN_NONE(false,CodeConstant.LOGIN_REFRESHTOKEN_ISNULL,"刷新令牌为空！"),

    ;
    private boolean success;
    private Integer code;
    private String msg;

    private CodeEnum(boolean success,Integer code, String msg){
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }

    public boolean getSuccess(){
        return success;
    }

}
