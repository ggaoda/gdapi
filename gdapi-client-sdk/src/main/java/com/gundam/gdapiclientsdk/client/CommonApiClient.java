package com.gundam.gdapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import com.gundam.gdapiclientsdk.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共的API-SDK，抽取ak,sk并且封装生成签名的过程
 */
public class CommonApiClient {

    protected final String accessKey;
    protected final String secretKey;

    protected static final String GATEWAY_HOST ="http://localhost:8090";

    public CommonApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 负责将数字签名的相关参数填入请求头中
     * @param body 请求体
     * @param accessKey ak
     * @param secretKey sk
     * @return map
     */
    protected static Map<String,String> getHeadMap(String body, String accessKey, String secretKey){
        //六个参数
        Map<String,String> headMap = new HashMap<>();
        headMap.put("accessKey",accessKey);
        headMap.put("body",body);
        headMap.put("sign", SignUtils.getSign(body,secretKey));
        headMap.put("nonce", RandomUtil.randomNumbers(5));
        //当下时间/1000，时间戳大概10位
        headMap.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        return headMap;
    }


}