package com.neco.filemanage.wps;

import org.apache.commons.codec.digest.HmacUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 * WPS签名服务加密工具
 * @author 冉登寺
 * @date 2020/8/5 11:40
 */
public class SignatureHelper {
    // 签名的字段名
    public static final String SIGNATURE_NAME = "_w_signature";
    // secret
    private static final String SECRET_KEY_NAME = "_w_secretkey";

    public static String getKeyValueString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(){
            {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if(entry.getKey().startsWith(WpsRequestParameter.PARAMETER_PREFIX_STR))
                        add(entry.getKey());
                }
            }
        };
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key) + "&";
            sb.append(key).append("=").append(value);
        }
        return sb.toString();
    }

    public static String getSignature(Map<String, String> params, String appSecret) {
        if(params == null)
            return null;
        List<String> keys = new ArrayList();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if(key.startsWith(WpsRequestParameter.PARAMETER_PREFIX_STR))
                keys.add(key);
        }

        // 将所有参数按key的升序排序
        keys.sort(String::compareTo);

        // 构造签名的源字符串
        StringBuilder contents = new StringBuilder();
        for (String key : keys) {
            if (SIGNATURE_NAME.equals(key))
                continue;
            contents.append(key).append("=").append(params.get(key));
        }
        contents.append(SECRET_KEY_NAME).append("=").append(appSecret);

        // 进行hmac sha1 签名
        byte[] bytes = HmacUtils.hmacSha1(appSecret.getBytes(), contents.toString().getBytes());

        //字符串经过Base64编码
        String sign = encodeBase64String(bytes);
        try {
            return URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> paramToMap(String paramStr) {
        String[] params = paramStr.split("&");
        return new HashMap<String, String>(){
            {
                for (String param1 : params) {
                    String[] param = param1.split("=");
                    if (param.length >= 2) {
                        String key = param[0];
                        StringBuilder value = new StringBuilder(param[1]);
                        for (int j = 2; j < param.length; j++) {
                            value.append("=").append(param[j]);
                        }
                        put(key, value.toString());
                    }
                }
            }
        };
    }
}