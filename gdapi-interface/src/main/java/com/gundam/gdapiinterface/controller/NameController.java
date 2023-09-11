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

        return "Post 用户名字是:" + user.getUserName();
    }



}
