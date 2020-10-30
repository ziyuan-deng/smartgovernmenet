package com.neco.gateway.filter.response;

import org.springframework.ui.ModelMap;

/**
 * @author ziyuan_deng
 * @date 2020/10/14
 */
public class ResponseDataUtils {

    public static ModelMap retErrorInfo(int code, String message){
        ModelMap modelMap = new ModelMap();
        modelMap.put("code",code);
        modelMap.put("msg",message);
        return modelMap;
    }
    public static ModelMap retCorrectInfo(int code, String message, Object data){
        ModelMap modelMap = new ModelMap();
        modelMap.put("code",code);
        modelMap.put("msg",message);
        modelMap.put("data",data);
        return modelMap;
    }

}
