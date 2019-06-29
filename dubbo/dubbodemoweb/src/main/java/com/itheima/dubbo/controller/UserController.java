package com.itheima.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.dubbo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @ResponseBody
    @RequestMapping("/showName")
    public String showName() {
        return userService.getName();
    }

}
