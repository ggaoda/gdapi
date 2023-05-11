package com.gundam.gdapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.gundam.gdapiclientsdk.model.User;
import com.gundam.gdapiclientsdk.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用第三方接口的客户端
 * @author Gundam
 */
public class GdApiClient {

    private String accessKey;
    private String secretKey;

    private static final String GATEWAY_HOST = "http://localhost:8090";

    public GdApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result= HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result= HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }




    private Map<String,String> getHeader(String body){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("accessKey",accessKey);
        //不安全↓
//        headers.put("secretKey",secretKey);
        headers.put("nonce", RandomUtil.randomNumbers(4));
        headers.put("body", body);
        headers.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        headers.put("sign", SignUtils.getSign(body,secretKey));
        return headers;
    }



    public String getUserNameByPost(User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeader(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }


}
