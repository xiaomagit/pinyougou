package com.itheima.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dubbo.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public String getName() {
        return "itcast";
    }
}
