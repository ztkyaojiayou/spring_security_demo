package com.atguigu.aclservice.controller;


import com.atguigu.aclservice.entity.User;
import com.atguigu.aclservice.service.RoleService;
import com.atguigu.aclservice.service.UserService;
import com.atguigu.base.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@RestController
@RequestMapping("/admin/acl/user")
//@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "获取管理用户分页列表")
    @GetMapping("{page}/{limit}")
    public BaseResponse index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "courseQuery", value = "查询对象", required = false)
             User userQueryVo) {
        Page<User> pageParam = new Page<>(page, limit);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(userQueryVo.getUsername())) {
            wrapper.like("username",userQueryVo.getUsername());
        }

        IPage<User> pageModel = userService.page(pageParam, wrapper);
        return BaseResponse.ok().data("items", pageModel.getRecords()).data("total", pageModel.getTotal());
    }

    @ApiOperation(value = "新增管理用户")
    @PostMapping("save")
    public BaseResponse save(@RequestBody User user) {
        user.setPassword(MD5.encrypt(user.getPassword()));
        userService.save(user);
        return BaseResponse.ok();
    }

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public BaseResponse toAssign(@PathVariable String userId) {
        Map<String, Object> roleMap = roleService.findRoleByUserId(userId);
        return BaseResponse.ok().data(roleMap);
    }

    /**
     * 核心接口
     * @param userId
     * @param roleId
     * @return
     */
    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("/doAssign")
    public BaseResponse doAssign(@RequestParam String userId, @RequestParam String[] roleId) {
        roleService.saveUserRoleRealtionShip(userId,roleId);
        return BaseResponse.ok();
    }
}

