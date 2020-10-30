package com.neco.common.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.neco.common.constants.CommonConstant;
import org.springframework.ui.ModelMap;

/**
 * @author ziyuan_deng
 * @create 2020-09-06 21:18
 */
public class ResponseUtil {


    //成功
    private final static Integer SUCCESS = 0;
    //统一错误
    private final static Integer ERROR = 500;
    private final static String REQUEST_SUCCESS = "操作成功!";//"Request success!";

    /***
     * 请求数据成功、正确信息返回
     * @param data
     * @return
     */
    public static ModelMap retCorrectModel(Object data) {
        return retInfo(SUCCESS, data, REQUEST_SUCCESS);
    }

    /***
     * 请求数据成功、正确信息返回
     * @param data
     * @return
     */
    public static String retCorrectJson(Object data) {
        return retInfoJson(SUCCESS, data, REQUEST_SUCCESS);
    }

    /***
     * 请求数据成功、正确信息返回
     * @param msg
     * @return
     */
    public static ModelMap retCorrectModel(Object data, String msg) {
        return retInfo(SUCCESS, data, msg);
    }

    /***
     * 请求数据成功、正确信息返回
     * @param data
     * @return
     */
    public static String retCorrectJson(Object data, String msg) {
        return retInfoJson(SUCCESS, data, msg);
    }

    /***
     * 请求数据成功、正确信息返回
     * @param msg
     * @return
     */
    public static ModelMap retCorrectInfo(String msg) {
        return retInfo(SUCCESS, null, msg);
    }

    /***
     * 请求数据成功、正确信息返回
     * @param msg
     * @return
     */
    public static String retCorrectInfoJson(String msg) {
        return retInfoJson(SUCCESS, null, msg);
    }

    /***
     * 校验错误信息返回
     * @param msg
     * @return
     */
    public static ModelMap retErrorInfo(String msg) {
        return retInfo(ERROR, null, msg);
    }

    /***
     * 校验错误信息返回
     * @param msg
     * @return
     */
    public static String retErrorJson(String msg) {
        return retInfoJson(ERROR, null, msg);
    }


    /***
     * 校验错误信息返回
     * @param msg
     * @return
     */
    public static ModelMap retErrorInfo(Integer errCode, String msg) {
        return retInfo(errCode, null, msg);
    }

    /***
     * 校验错误信息返回
     * @param msg
     * @return
     */
    public static String retErrorInfoJson(Integer errCode, String msg) {
        return retInfoJson(errCode, null, msg);
    }

    /**
     * @param code
     * @param data
     * @param msg
     * @return
     * @notes: 返回格式标准
     */
    private static ModelMap retInfo(Integer code, Object data, String msg) {
        ModelMap result = new ModelMap();
        result.put(CommonConstant.RESULT_CODE, code);
        result.put(CommonConstant.RESULT_DATA, data);
        result.put(CommonConstant.RESULT_MSG, msg);
        return result;
    }

    /**
     * @param code
     * @param data
     * @param msg
     * @return
     * @notes: 返回格式标准, json
     */
    public static String retInfoJson(Integer code, Object data, String msg) {
        return JSON.toJSONString(retInfo(code, data, msg), SerializerFeature.WriteMapNullValue);
    }
}
