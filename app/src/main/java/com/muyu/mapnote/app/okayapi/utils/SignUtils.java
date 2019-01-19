package com.muyu.mapnote.app.okayapi.utils;

import com.muyu.mapnote.app.okayapi.OkayApi;
import com.muyu.minimalism.utils.MD5Utils;
import com.muyu.minimalism.utils.StringUtils;

import java.util.Map;
import java.util.SortedMap;


public class SignUtils {

    /**
     * @param params 所有的请求参数都会在这里进行排序加密
     * @return 得到签名
     */
    public static String getSign(SortedMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : params.entrySet()) {
            if (!entry.getKey().equals("sign")) { //拼装参数,排除sign
                if (!StringUtils.isEmpty((String)entry.getKey()) && !StringUtils.isEmpty((String)entry.getValue()))
                    sb.append(entry.getValue());
            }
        }
        sb.append(OkayApi.get().getAppSecret());
        return MD5Utils.md5(sb.toString()).toUpperCase();
    }

    /**
     * @param params 所有的请求参数都会在这里进行排序加密
     * @return 验证签名结果
     */
    public static boolean verifySign(SortedMap<String, String> params) {
        if (params == null || StringUtils.isEmpty(params.get("sign"))) return false;
        String sign = getSign(params);
        return !StringUtils.isEmpty(sign) && params.get("sign").equals(sign);
    }

}
