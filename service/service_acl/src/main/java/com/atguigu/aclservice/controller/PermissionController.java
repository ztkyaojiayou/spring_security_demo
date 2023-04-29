package com.atguigu.aclservice.controller;


import com.atguigu.aclservice.entity.Permission;
import com.atguigu.aclservice.service.PermissionService;
import com.atguigu.base.utils.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 权限 菜单管理
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@RestController
@RequestMapping("/admin/acl/permission")
//@CrossOrigin
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    //获取全部菜单
    @ApiOperation(value = "查询所有菜单")
    @GetMapping
    public BaseResponse indexAllPermission() {
        List<Permission> list =  permissionService.queryAllMenu();
        return BaseResponse.ok().data("children",list);
    }

    /**
     * 核心接口
     * @param roleId
     * @param permissionId
     * @return
     */
    @ApiOperation(value = "给角色分配权限")
    @PostMapping("/doAssign")
    public BaseResponse doAssign(String roleId, String[] permissionId) {
        permissionService.saveRolePermissionRealtionShipGuli(roleId,permissionId);
        return BaseResponse.ok();
    }

    @ApiOperation(value = "根据角色获取菜单")
    @GetMapping("toAssign/{roleId}")
    public BaseResponse toAssign(@PathVariable String roleId) {
        List<Permission> list = permissionService.selectAllMenu(roleId);
        return BaseResponse.ok().data("children", list);
    }

}

