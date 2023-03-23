package com.gundam.gdapiinterface.controller;

import com.gundam.gdapiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 查询名称接口
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(String name,HttpServletRequest request){
        System.out.println(request.getHeader("gaoda"));
        return "Get 你的名字是:" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name){
        return "Post 你的名字是:" + name;
    }




    @PostMapping("/user")
        public String getUserNameByPost(@RequestBody User user, HttpServletRequest request){
//        String accessKey = request.getHeader("accessKey");
////        String secretKey = request.getHeader("secretKey");
////        if (!accessKey.equals("gaoda") || !secretKey.equals("abcdefgh")){
////            throw new RuntimeException("无权限!");
////        }
//        String nonce = request.getHeader("nonce");
//        String timestamp = request.getHeader("timestamp");
//        String sign = request.getHeader("sign");
//        String body = request.getHeader("body");
//
//        //todo 实际上应该是去数据库查询是否已分配给用户
//        if (!accessKey.equals("gaoda")){
//            throw new RuntimeException("无权限!");
//        }
//        //不校验随机数
//        if (Long.parseLong(nonce) > 10000){
//            throw new RuntimeException("无权限!");
//        }
//        //todo 时间和当前时间不能超过5分钟
//        //if (timestamp){}
//        //todo 实际情况是从数据库查出
//        String serverSign = SignUtils.getSign(body, "abcdefgh");
//        if (!sign.equals(serverSign)){
//            throw new RuntimeException("无权限!");
//        }

        return "Post 用户名字是:" + user.getUserName();
    }



}
