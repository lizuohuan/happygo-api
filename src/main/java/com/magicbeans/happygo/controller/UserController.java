package com.magicbeans.happygo.controller;


import com.magicbeans.base.Pages;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.Admin;
import com.magicbeans.happygo.service.IAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author magic-beans
 * @since 2017-07-28
 */
@RestController
@RequestMapping("user")
@Api(value = "user", description = "用户管理")
public class UserController extends BaseController {

    @Autowired
    private IAdminService adminService;


    /**
     * 分页查询
     * @param pages
     * @return
     */
    @RequestMapping(value = "list")
    @ApiOperation(value = "测试接口")
    public ResponseData adminList(Pages<Admin> pages) {
        return ResponseData.success(adminService.findPage(pages, null, null));
    }


    /**
     * 根据Id删除
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除")
    @RequestMapping(value = "del/{id}")
    public ResponseData deleteById(@PathVariable String id) {
        adminService.delete(id);
        return ResponseData.success();
    }

    /**
     * 根据ID查询实体
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询实体")
    @RequestMapping(value = "get/{id}")
    public ResponseData findById(@PathVariable String id){
        return ResponseData.success(adminService.find(id));
    }


    /**
     * 增加修改
     * @param admin
     * @return
     */
    @ApiOperation(value = "增加修改")
    @RequestMapping(value = "save")
    public ResponseData save(@RequestBody  Admin admin){
        if(StringUtils.isEmpty(admin.getId())){
            adminService.save(admin);
        }else{
            adminService.update(admin);
        }
        return ResponseData.success();
    }



}
