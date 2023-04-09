package com.atguigu.aclservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.aclservice.service.IndexService;
import com.atguigu.utils.utils.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/acl/index")
//@CrossOrigin
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 根据token获取用户信息
     */
    @GetMapping("info")
    public BaseResponse info(){
        //获取当前登录用户用户名
        //类似于threadLocal了，即：可以直接从springsecurity的上下文中获取当前用户的username！！！
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //再查库，获取详细信息！
        Map<String, Object> userInfo = indexService.getUserInfo(username);
        return BaseResponse.ok().data(userInfo);
    }

    /**
     * 获取菜单
     * @return
     */
    @GetMapping("menu")
    public BaseResponse getMenu(){
        //获取当前登录用户用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<JSONObject> permissionList = indexService.getMenu(username);
        return BaseResponse.ok().data("permissionList", permissionList);
    }

    @PostMapping("logout")
    public BaseResponse logout(){
        return BaseResponse.ok();
    }

}
