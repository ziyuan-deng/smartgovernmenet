package com.neco.common.constants;

/**
 * 返回代码常量
 * @author ziyuan_deng
 * @date 2020/09/6
 */
public class CodeConstant {

    //成功
    public final static Integer SUCCESS = 0;
    //未知错误
    public final static Integer UNKNOWN_ERROR = -1;
    //保存失败
    public final static Integer SAVE_FAILD = -2;
    //更新失败
    public final static Integer UPDATE_FAILD = -3;
    //删除失败
    public final static Integer DELETE_FAILD = -4;
    //参数不合法
    public final static Integer PARAMS_ERROR = -1001;
    //参数不完整
    public final static Integer PARAMS_INCOMPLETENESS = -1002;
    //查询结果异常
    public final static Integer QUERY_RESULT_ERROR = -1003;
    //操作 对象不存在
    public final static Integer OPERATION_OBJECT_NOT_EXIST = -1004;
    //操作 对象无效
    public final static Integer OPERATION_OBJECT_INVALID = -1005;
    //操作异常
    public final static Integer OPERATION_SYSTEM_EXCEPTION = -1006;
    //不符合条件
    public final static Integer NON_CONFORMITY_CONDITIONON = -1007;
    //存在关联关联无法操作
    public final static Integer EXIST_ASSOCIATION = -1008;
    //用户为空
    public final static Integer LOGIN_USERNAME_ISNULL = -1100;
    //密码为空
    public final static Integer LOGIN_PASSWORD_ISNULL = -1101;
    //验证码为空
    public final static Integer LOGIN_VERIFY_CODE_ISNULL = -1102;
    //用户不存在
    public final static Integer LOGIN_USER_NO_EXIST = -1103;
    //密码错误
    public final static Integer LOGIN_PASSWORD_ERROR = -1104;
    //用户已停用
    public final static Integer LOGIN_USER_IS_STOP = -1105;
    //用户已被锁定
    public final static Integer LOGIN_USER_IS_LOCK = -1106;
    //验证码为错误
    public final static Integer LOGIN_VERIFY_CODE_ERROR = -1107;
    //session失效
    public final static Integer SESSION_INVALID = -1201;
    //session key为空
    public final static Integer SESSION_KEY_ISNULL = -1202;
    //session value为空
    public final static Integer SESSION_VALUE_ISNULL = -1203;
    //时间转换类型错误
    public final static Integer DATE_FORMAT_ERROR = -1204;
    /** cron表达式不合法 */
    public static final Integer CRON_EXPRESSION_INVALID = -1205;
    //刷新令牌为空
    public static final Integer LOGIN_REFRESHTOKEN_ISNULL = -1206;



    /**
     * ***********************Redis错误常量*****************
     */

    /**
     * Redis连接失败
     */
    public final static Integer REDIS_CONNECTOR_FAILED = -1303;
    /**
     * Redis key 为空
     */
    public final static Integer REDIS_KEY_ISNULL = -1301;
    /**
     * Redis value为空
     */
    public final static Integer REDIS_VALUE_ISNULL = -1302;


    /**
     * 权限类错误常量
     */

    /**
     * 无权限
     */
    public final static Integer PERMISSION_UNAUTHORIZED = -1401;
    /**
     * 未登录
     */
    public final static Integer PERMISSION_UNLOGIN = -1402;

    /***
     * 未登录 html 请求
     */
    public final static Integer PERMISSION_UNLOGIN_PAGE = -1402-1;
    /***
     * 未登录 ajax 请求
     */
    public final static Integer PERMISSION_UNLOGIN_AJAX = -1402-2;
    

    /**
     * 用户管理
     */

    public final static Integer USER_OLD_PASSWORD_ISNULL = -1501;

    public final static Integer USER_NEW_PASSWORD_ISNULL = -1502;


    /**
     * smartBi
     *
     */
    public final static Integer SMARTBI_TARGET_ID_ISNULL = -10101;

    public final static Integer SMARTBI_ORIGIN_ID_ISNULL = -10102;

    public final static Integer SMARTBI_RESOURCE_NOT_EXIST = -10103;
    
    public final static Integer SMARTBI_CONNECTOR_ERROR = -10104;
    
    public final static Integer SMARTBI_QUERY_ERROR = -10105;
    /**
     * 文件操作部分
     */
    public final static Integer EXCEL_FORMAT_ERROR = -1601;     //excel文件格式错误
    public final static Integer EXCEL_EXPORT_ERROR = -1602;     //excel导出错误
    public final static Integer EXCEL_IMPORT_ERROR = -1603;     //excel导入错误

    /**
     * 自定义部分
     */
    public final static Integer CUSTOM_CATALOG_NOT_EXIST = -10201;
    
    public final static Integer SPLITASK_RESOURCE_NOT_EXIST=-90001;

    /**
    *@author cRyann
    *@Description 指标属性 1 : 共享指标; 2 :体系指标 ; 3 :个人指标 ;
    **/
    public final static Integer TARGET_PROPERTY_SHARED = 1;

    public final static Integer TARGET_PROPERTY_SYSTEM =2;

    public final static Integer TARGET_PROPERTY_PERSONAL =3;
}
