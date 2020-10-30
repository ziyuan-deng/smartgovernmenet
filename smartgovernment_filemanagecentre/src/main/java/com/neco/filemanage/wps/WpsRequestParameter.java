package com.neco.filemanage.wps;

import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * WPS服务请求参数
 * @author 冉登寺
 * @date 2020/8/5 11:42
 */
public class WpsRequestParameter {
    // 加签名参数前缀
    public static final String PARAMETER_PREFIX_STR = "_w_";
    private Map<String, String> param = new HashMap<>();
    private WpsRequestParameter(){ }

    /**
     * 添加参数
     * @param name 参数名称
     * @param value 参数值
     * @return
     */
    public WpsRequestParameter add(String name, String value){
        param.put(name, value);
        return this;
    }

    /**
     * 添加传递给WPS专用的参数
     * @param name 参数名
     * @param value 参数值
     * @return
     */
    public WpsRequestParameter addWps(String name, String value){
        Assert.hasText(name, "参数名不能为空");
        if(!name.startsWith(PARAMETER_PREFIX_STR))
            name = PARAMETER_PREFIX_STR + name;
        return add(name, value);
    }

    /**
     * 删除指定参数
     * @param name 参数名
     * @return
     */
    public WpsRequestParameter remove(String name){
        param.remove(name);
        return this;
    }

    /**
     * 删除指定的WPS参数
     * @param name 参数名
     * @return
     */
    public WpsRequestParameter removeWps(String name){
        Assert.hasText(name, "参数名不能为空");
        if(!name.startsWith(PARAMETER_PREFIX_STR))
            name = PARAMETER_PREFIX_STR + name;
        return remove(name);
    }

    /**
     * 获取指定参数的值
     * @param name 参数名
     * @return
     */
    public String get(String name){
        Assert.hasText(name, "参数名不能为空");
        return param.get(name);
    }

    /**
     * 获取WPS类型的参数
     * @param name 简写参数名
     * @return
     */
    public String getWps(String name){
        Assert.hasText(name, "参数名不能为空");
        if(!name.startsWith(PARAMETER_PREFIX_STR))
            name = PARAMETER_PREFIX_STR + name;
        return get(name);
    }

    /**
     * 获取参数键值对
     * @return
     */
    public String getKeyValueString(){
        return SignatureHelper.getKeyValueString(param);
    }

    /**
     * 对参数进行签名
     * @param secret 签名秘钥
     * @return
     */
    public String signature(String secret){
        Assert.hasText(secret, "签名秘钥不能为空");
        return SignatureHelper.getSignature(param, secret);
    }

    /**
     * 获取签名的URL
     * @param secret
     * @return
     */
    public String signatureUrl(String secret){
        return getKeyValueString() + SignatureHelper.SIGNATURE_NAME + "=" + signature(secret);
    }

    /**
     * 创建请求参数对象
     * @return
     */
    public static WpsRequestParameter create(){
        return new WpsRequestParameter();
    }

    /**
     * 通过请求对象创建WPS请求参数
     * @param request 请求
     * @return
     */
    public static WpsRequestParameter create(HttpServletRequest request){
        WpsRequestParameter p = create();
        Enumeration<String> enumeration = request.getParameterNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            p.add(name, request.getParameter(name));
        }
        return p;
    }
}